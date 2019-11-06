package com.qinfen.service.impl;

import com.qinfen.dao.SeckillDao;
import com.qinfen.dao.SuccessKillDao;
import com.qinfen.dao.cache.RedisDao;
import com.qinfen.dto.Exposer;
import com.qinfen.dto.SeckillExecution;
import com.qinfen.entity.Seckill;
import com.qinfen.entity.SuccessKill;
import com.qinfen.enums.SeckillStatEnum;
import com.qinfen.exception.RepeatKillException;
import com.qinfen.exception.SeckillCloseException;
import com.qinfen.exception.SeckillException;
import com.qinfen.service.SeckillService;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author QinFen
 * @date 2019/11/3 0003 11:12
 */
@Service
public class SeckillServiceImpl implements SeckillService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String SLAT = "sdgfweoir230Fgfdklvnc";

    @Resource
    private SeckillDao seckillDao;

    @Resource
    private SuccessKillDao successKillDao;

    @Autowired
    private RedisDao redisDao;

    @Override
    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0, 4);
    }

    @Override
    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    @Override
    public Exposer exportSeckillUrl(long seckillId) {

        //优化点：缓存优化,超时的基础上维护一致性
        //1：访问Redis
        Seckill seckill = redisDao.getSeckill(seckillId);
        if (seckill == null) {
            //2:访问数据库
            //确认是否有该商品
            seckill = seckillDao.queryById(seckillId);
            if (seckill == null) {
                return new Exposer(false, seckillId);
            } else {
                //3:放入Redis
                redisDao.putSeckill(seckill);
            }
        }
        //获取该商品的开始秒杀时间和结束秒杀时间
        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        //系统当前时间
        Date nowTime = new Date();
        //如果当前时间在商品的秒杀时间范围内，则返回商品的秒杀信息
        if (nowTime.getTime() < startTime.getTime()
                || nowTime.getTime() > endTime.getTime()) {
            return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(),
                    endTime.getTime());
        }
        String md5 = getMd5(seckillId);
        return new Exposer(true, seckillId, md5);

    }

    /**
     * MD5加密
     *
     * @param seckillId
     * @return
     */
    private String getMd5(long seckillId) {
        String base = seckillId + "/" + SLAT;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    /**
     * 执行秒杀
     *
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     * @throws SeckillException
     * @throws RepeatKillException
     * @throws SeckillCloseException
     * @Transactional注解是表明此测试类的事务启用， 这样所有的测试方案都会自动的 rollback，即不用自己清除自己所做的任何对数据库的变更了。
     */
    @Transactional
    @Override
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException, RepeatKillException, SeckillCloseException {
        //验证用户传的MD5是不是服务器发送的那个，防止恶意秒杀
        if (md5 == null || !md5.equals(getMd5(seckillId))) {
            throw new SeckillException("seckill data rewrite");
        }

        //执行秒杀逻辑，减库存，记录购买行为
        Date nowTime = new Date();
        try {
            //记录购买行为
            int insertCount = successKillDao.insertSuccessKill(seckillId, userPhone);
            if (insertCount <= 0) {
                //重复秒杀
                throw new RepeatKillException("seckill repeated");
            } else {
                //减少库存，热点商品竞争
                int updateCount = seckillDao.reduceNumber(seckillId, nowTime);
                if (updateCount <= 0) {
                    //没有更新到记录，秒杀结束，rollback
                    throw new SeckillCloseException("seckill is closed");
                } else {
                    //秒杀成功，返回秒杀产品信息，commit
                    SuccessKill successKill = successKillDao.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, successKill);
                }

            }

        } catch (SeckillCloseException e1) {
            throw e1;
        } catch (RepeatKillException e2) {
            throw e2;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            //所有编译期异常，转化为运行期异常
            throw new SeckillException("seckill inner error:" + e.getMessage());
        }
    }

    @Override
    public SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5) {
        if (md5 == null || !md5.equals(getMd5(seckillId))) {
            return new SeckillExecution(seckillId,SeckillStatEnum.DATA_REWRITE);
        }
        Date killTime = new Date();
        Map<String, Object> map = new HashMap<>();
        map.put("seckillId", seckillId);
        map.put("phone", userPhone);
        map.put("killTime", killTime);
        map.put("result", null);
        //执行存储过程，result被赋值
        seckillDao.killByProcedure(map);
        //获取result
        Integer result = MapUtils.getInteger(map, "result", -2);
        if (result == 1) {
            //秒杀成功
            SuccessKill successKilled = successKillDao.queryByIdWithSeckill(seckillId, userPhone);
            return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, successKilled);
        } else {
            return new SeckillExecution(seckillId, Objects.requireNonNull(SeckillStatEnum.stateOf(result)));
        }
    }
}
