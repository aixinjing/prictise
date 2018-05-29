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
        System.out.println();
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

    public static List forSum(List<Work> works){
        for (int i=0;i<works.size();i++){
            works.get(i).doSomeWork();
        }
        return works;
    }
    public static List forkJoinSum(List<Work> works) {
        ForkJoinTask<List> task = new ForkJoinSumCalculator(works);
        return new ForkJoinPool().invoke(task);
    }
    public static void main(String[] args) {
      List list=new ArrayList(100);
        IntStream.rangeClosed(0,100).forEach((i)->{
            Work work=new Work();
            list.add(work);
        });
        long start=System.nanoTime();
        ForkJoinTask<List> task = new ForkJoinSumCalculator(list);
        new ForkJoinPool().invoke(task);
        long end=System.nanoTime();
        long start1=System.nanoTime();
        forSum(list);
        long end1=System.nanoTime();
        System.out.println("forkJoinSum"+(end-start)/1000_000);
        System.out.println("forSum"+(end1-start1)/1000_000);
    }
    }
@Data
class Work{
    private  boolean workered;

    public void doSomeWork(){
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.workered=true;
        System.out.println(workered);
    }
}