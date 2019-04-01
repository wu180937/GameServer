package com.wmj.game.web.service;

import com.wmj.game.web.dao.AccountDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: wumj
 * @Date: 2019/4/1 23:53
 * @Description:
 */
@Service
public class AccountService {
    @Autowired
    private AccountDao accountDao;
}
