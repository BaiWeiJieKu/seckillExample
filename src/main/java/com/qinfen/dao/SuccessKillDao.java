package com.qinfen.dao;

import com.qinfen.entity.SuccessKill;
import org.apache.ibatis.annotations.Param;

/**
 * @author QinFen
 * @date 2019/11/2 0002 11:53
 */
public interface SuccessKillDao {

    /**
     * 插入购买明细，可过滤重复
     *
     * @param seckillId
     * @param userPhone
     * @return
     */
    int insertSuccessKill(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone);

    /**
     * 根据id和手机号查询明细信息和库存信息
     *
     * @param seckillId
     * @return
     */
    SuccessKill queryByIdWithSeckill(@Param("seckillId") long seckillId ,@Param("userPhone") long userPhone);

}
