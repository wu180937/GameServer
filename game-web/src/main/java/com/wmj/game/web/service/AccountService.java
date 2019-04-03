package com.wmj.game.web.service;

import com.wmj.game.web.dao.AccountDao;
import com.wmj.game.web.entity.Account2Player;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author: wumj
 * @Date: 2019/4/1 23:53
 * @Description:
 */
@Service
@Transactional(readOnly = true)
public class AccountService {
    @Autowired
    private AccountDao accountDao;

    @Transactional
    public Account2Player findPlayerByAccount(String account) {
        if (StringUtils.isEmpty(account)) {
            throw new IllegalArgumentException("account cannot be empty.");
        }
        return this.accountDao.getByAccount(account);
    }
}
