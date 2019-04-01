package com.wmj.game.web.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther: wumingjie
 * @Date: 2019/3/26
 * @Description:
 */
@RestController
@RequestMapping("/ac")
public class AccountController {
    @Value("${game.login.passEncryptKey}")
    private String passEncryptKey;
    @Value("${game.login.tokenEncryptKey}")
    private String tokenEncryptKey;
    @Value("${game.login.tokenEffectiveTimeSec}")
    private int tokenEffectiveTimeSec;

    @RequestMapping(value = "/ulogin", method = RequestMethod.POST)
    public JSONObject login(String userName, String pwd) {
        JSONObject result = new JSONObject();
        result.put("key", passEncryptKey);
        return result;
    }

}
