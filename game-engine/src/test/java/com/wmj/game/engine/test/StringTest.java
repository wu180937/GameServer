package com.wmj.game.engine.test;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Auther: wumingjie
 * @Date: 2019/3/12
 * @Description:
 */
public class StringTest {
    public static void main(String[] args) {
        List<String> list = Lists.newArrayList("1", "2", "3", "4", "5", "6", "7", "8", "9");
        List<String> list2 = Lists.newArrayList("5", "6", "7", "8", "9", "11", "12", "13", "14");
        System.err.println(list.stream().filter(str -> !list2.contains(str)).collect(Collectors.toList()));
        System.err.println(list2.stream().filter(str -> !list.contains(str)).collect(Collectors.toList()));
        List<Pair<String, Integer>> pairList = new ArrayList<>();
        pairList.add(Pair.of("a", 1));
        pairList.add(Pair.of("b", 2));
        System.err.println(pairList.stream().map(Pair::getLeft).collect(Collectors.toSet()));
    }
}
