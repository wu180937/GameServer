package com.wmj.game.web.service;

import com.wmj.game.web.dao.AccountDao;
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

}
