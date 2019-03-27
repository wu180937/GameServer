package com.wmj.game.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @Auther: wumingjie
 * @Date: 2019/3/26
 * @Description:
 */
@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
public class WebBootstrap {
    public static void main(String[] args) {
        SpringApplication.run(WebBootstrap.class, args);
    }
}
