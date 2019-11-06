package com.qinfen.service;

import com.qinfen.dto.Exposer;
import com.qinfen.dto.SeckillExecution;
import com.qinfen.entity.Seckill;
import com.qinfen.exception.RepeatKillException;
import com.qinfen.exception.SeckillCloseException;
import com.qinfen.exception.SeckillException;

import java.util.List;

/**
 * @author QinFen
 * @date 2019/11/3 0003 10:48
 */
public interface SeckillService {

    /**
     * 查询所有秒杀记录
     *
     * @return
     */
    List<Seckill> getSeckillList();

    /**
     * 查询单个秒杀记录
     *
     * @param seckillId
     * @return
     */
    Seckill getById(long seckillId);

    /**
     * 秒杀开启时输出秒杀接口地址
     * 否则输出系统时间和秒杀时间
     *
     * @param seckillId
     */
    Exposer exportSeckillUrl(long seckillId);

    /**
     * 执行秒杀操作，抛出异常，方便spring事务管理进行回滚
     *
     * @param seckillId
     * @param userPhone
     * @param md5
     */
    SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException, RepeatKillException, SeckillCloseException;


    /**
     * 调用存储过程执行秒杀，不需要抛出异常
     * 有关事务的提交或回滚已经在存储过程中完成了，也就是说用不到spring的事务管理了
     *
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     */
    SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5);

}
