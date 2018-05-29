package com.qlk.jdk8;

import java.util.List;
import java.util.stream.IntStream;
import static java.util.stream.Collectors.*;

public class OtherTest {

    public static void main(String[] args) {
        List list= IntStream.rangeClosed(0,1000000).boxed().collect(toList());
        Long start=System.nanoTime();
//        forTest(list);
//        foreachTest(list);
//        streamTest(list);
//        parallelStreamTest(list);
        System.out.println(forTest(list)+"用时："+(System.nanoTime()-start)/1_000_000);
    }
    public static Integer forTest(List<Integer> list){
        int num=0;
        for (int i = 0; i < list.size(); i++) {
            num+=list.get(i);
        }
        return num;
    }
    public static void foreachTest(List list){
        int num=0;
        list.forEach(System.out::println);
    }
    public static void streamTest(List list){
        list.stream().forEach(System.out::println);
    }
    public static Integer parallelStreamTest(List<Integer> list){
        return list.parallelStream().mapToInt(Integer::intValue).sum();
    }
}
