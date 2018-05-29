package com.qlk.jdk8;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.LongStream;

public class ForkJionPoolTest {
    class MyTask extends RecursiveTask<Long>{

         private AtomicInteger atomicInteger;
        private long start;
        private long end;

        public MyTask(long start, long end,AtomicInteger atomicInteger) {
            this.start = start;
            this.end = end;
            this.atomicInteger=atomicInteger;
        }


        @Override
        protected Long compute() {
            long sum=0;
            if(end-start<=100){
               // System.out.println(Thread.currentThread().getName());
                atomicInteger.addAndGet(1);
                /*try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
                for (long i=start;i<=end;i++){
                    sum+=i;
                }
            }else {
                long middle=(end+start)/2;
                MyTask left=new MyTask(start,middle,atomicInteger);
                MyTask right=new MyTask(middle+1,end,atomicInteger);
                left.fork();
                right.fork();
                sum=left.join()+right.join();
            }
            return  sum;
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
//        AtomicInteger atomicInteger =new AtomicInteger();
//        RecursiveTask task=new MyTask(0,100000000,atomicInteger);
//        Future future=new ForkJoinPool().submit(task);
        long start=System.nanoTime();
//        System.out.println(future.get());
//        System.out.println((System.nanoTime()-start)/1_000_000);
//        System.out.println(atomicInteger.get());
        long result=0;
        result= LongStream.rangeClosed(0,1_000_000).parallel().reduce(0,(a,b)->{
            System.out.println(Thread.currentThread().getName());
            return Long.sum(a,b);});
        System.out.println((System.nanoTime()-start)/1000_000);
        System.out.println(result);
    }

}
