package com.wmj.game.web.dao;

import com.wmj.game.web.entity.Account2Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
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

    public Account2Player getByAccount(String account) {
        return DataAccessUtils.singleResult(this.jdbcTemplate.query(
                "SELECT `account`, `pwd`, `playerId`, `createTime` FROM `account_2_player` WHERE `account` = ?",
                BeanPropertyRowMapper.newInstance(Account2Player.class),
                account));
    }

}
