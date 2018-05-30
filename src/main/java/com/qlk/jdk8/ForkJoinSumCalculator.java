package com.qlk.jdk8;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class ForkJoinSumCalculator extends java.util.concurrent.RecursiveTask<List> {
    private final List<Work> works;
    private final int start;
    private final int end;
    public static final long THRESHOLD = 1_0;

    public ForkJoinSumCalculator(List<Work> works) {
        this(works, 0, works.size());
    }

    private ForkJoinSumCalculator(List<Work> works, int start, int end) {
        this.works = works;
        this.start = start;
        this.end = end;
    }

    @Override
    protected List compute() {
        int length = end - start;
        if (length <= THRESHOLD) {
            return computeSequentially();
        }
        ForkJoinSumCalculator leftTask =
                new ForkJoinSumCalculator(works, start, start + length / 2);
         leftTask.fork();
        ForkJoinSumCalculator rightTask =
                new ForkJoinSumCalculator(works, start + length / 2, end);
          rightTask.fork();
        List rightResult = rightTask.join();
        List leftResult = leftTask.join();
        leftResult.addAll(rightResult);
        return leftResult;
    }

    private List<Work> computeSequentially() {
        List<Work> list=new ArrayList<>();
        for (int i = start; i < end; i++) {
            works.get(i).doSomeWork();
            list.add(works.get(i));
            }
            return list;
        }

    public static List forTest(List<Work> works){
        long start=System.currentTimeMillis();
        for (int i=0;i<works.size();i++){
            works.get(i).doSomeWork();
        }
        System.out.println("forTest:"+(System.currentTimeMillis()-start));
        return works;
    }
    public static List forkJoinTest(List<Work> works) {
        long start=System.currentTimeMillis();
        ForkJoinTask<List> task = new ForkJoinSumCalculator(works);
        works=new ForkJoinPool().invoke(task);
        System.out.println("forkJoinTest:"+(System.currentTimeMillis()-start));
        return  works;
    }
    public static List parallelStreamTest(List<Work> works) {
        long start=System.currentTimeMillis();
       works.parallelStream().forEach(work -> work.doSomeWork());
        System.out.println("parallelStreamTest:"+(System.currentTimeMillis()-start));
        return  works;
    }
    public static void main(String[] args) {
      List list=new ArrayList();
        IntStream.rangeClosed(0,100).forEach((i)->{
            Work work=new Work();
            list.add(work);
        });
        forTest(list);
        parallelStreamTest(list);
        forkJoinTest(list);
    }
    }
@Data
class Work{
    private  boolean worked=false;

    public void doSomeWork(){
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.worked=true;
       // System.out.println(Thread.currentThread().getName());
    }
}