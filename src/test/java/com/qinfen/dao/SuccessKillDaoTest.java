package com.qinfen.dao;

import com.qinfen.entity.SuccessKill;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * @author QinFen
 * @date 2019/11/2 0002 14:50
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKillDaoTest {
    @Resource
    private SuccessKillDao successKillDao;

    @Test
    public void insertSuccessKill() {
        long id = 1000L;
        long phone = 13122332142L;
        //联合主键保证了不能重复秒杀
        int i = successKillDao.insertSuccessKill(id, phone);
        System.out.println(i);
    }

    @Test
    public void queryByIdWithSeckill() {
        long id = 1000L;
        long phone = 13122332142L;
        SuccessKill successKill = successKillDao.queryByIdWithSeckill(id, phone);
        System.out.println(successKill);
        System.out.println(successKill.getSeckill());
    }
}