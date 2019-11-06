package com.qinfen.entity;

import java.util.Date;

/**
 * @author QinFen
 * @date 2019/11/2 0002 11:43
 */
public class SuccessKill {
    private long seckillId;
    private long userPhone;
    private short state;
    private Date createTime;
    //多对一
    private Seckill seckill;

    public Seckill getSeckill() {
        return seckill;
    }

    public void setSeckill(Seckill seckill) {
        this.seckill = seckill;
    }

    public long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(long seckillId) {
        this.seckillId = seckillId;
    }
    public short getState() {
        return state;
    }

    public void setState(short state) {
        this.state = state;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public long getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(long userPhone) {
        this.userPhone = userPhone;
    }

    @Override
    public String toString() {
        return "SuccessKill{" +
                "seckillId=" + seckillId +
                ", userPhome=" + userPhone +
                ", state=" + state +
                ", createTime=" + createTime +
                '}';
    }
}
