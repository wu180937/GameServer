package com.wmj.game.web.controller;

import com.alibaba.fastjson.JSONObject;
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
    @RequestMapping(value = "/ulogin", method = RequestMethod.POST)
    public JSONObject login(String userName, String pwd) {
        JSONObject result = new JSONObject();
        return result;
    }

}
