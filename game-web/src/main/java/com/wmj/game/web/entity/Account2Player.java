package com.wmj.game.web.entity;

import java.util.Date;

/**
 * @Auther: wumingjie
 * @Date: 2019/4/2
 * @Description:
 */
public class Account2Player {
    private String account;
    private String pwd;
    private long playerId;
    private Date CreateTime;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public Date getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(Date createTime) {
        CreateTime = createTime;
    }
}
