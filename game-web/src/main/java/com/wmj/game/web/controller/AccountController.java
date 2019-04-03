package com.wmj.game.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.wmj.game.common.constant.ErrorCode;
import com.wmj.game.common.util.AESUtil;
import com.wmj.game.web.entity.Account2Player;
import com.wmj.game.web.service.AccountService;
import org.apache.commons.codec.digest.Crypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import sun.security.krb5.internal.crypto.dk.AesDkCrypto;

/**
 * @Auther: wumingjie
 * @Date: 2019/3/26
 * @Description:
 */
@RestController
@RequestMapping("/ac")
public class AccountController {
    @Value("${game.login.tokenEncryptKey}")
    private String tokenEncryptKey;
    @Value("${game.login.tokenEffectiveTimeSec}")
    private int tokenEffectiveTimeSec;
    @Autowired
    private AccountService accountService;

    @RequestMapping(value = "/ulogin", method = RequestMethod.POST)
    public JSONObject login(String userName, String pwd) {
        JSONObject result = new JSONObject();
        Account2Player account2Player = this.accountService.findPlayerByAccount(userName);
        if (account2Player == null || !account2Player.getPwd().equals(pwd)) {
            result.put("c", ErrorCode.AccountOrPwdError);
            return result;
        }
        result.put("c", ErrorCode.Success);
        StringBuilder token = new StringBuilder();
        token.append(account2Player.getAccount());
        token.append("_").append(account2Player.getPlayerId());
        token.append("_").append(System.currentTimeMillis() + (this.tokenEffectiveTimeSec * 1000));
        String cryptToken = AESUtil.encrypt(token.toString(), tokenEncryptKey);
        result.put("token", cryptToken);
        return result;
    }


}
