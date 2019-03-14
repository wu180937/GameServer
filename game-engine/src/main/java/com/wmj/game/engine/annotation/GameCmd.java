package com.wmj.game.engine.annotation;

import com.google.protobuf.GeneratedMessageV3;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Auther: wumingjie
 * @Date: 2019/3/14
 * @Description:
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface GameCmd {
    int cmd();

    Class<GeneratedMessageV3> protoClazz();
}
