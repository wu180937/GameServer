package com.wmj.game.web.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * @Auther: wumingjie
 * @Date: 2019/4/1
 * @Description:
 */
@Component
public class AccountDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;
}
