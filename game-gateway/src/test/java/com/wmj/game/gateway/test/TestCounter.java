package com.wmj.game.gateway.test;

import java.util.concurrent.atomic.AtomicLong;

public class TestCounter {
    public static void main(String[] args) {
        AtomicLong al = new AtomicLong(Long.MAX_VALUE);
        System.err.println(Math.abs(al.incrementAndGet())%2);
    }
}
