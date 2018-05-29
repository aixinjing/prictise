### jdk1.8学习总结
#### 1. 上集回顾

1. 函数式接口

1. Optional 的使用 --> OptionalTest

3. default方法的提出原因


### 2. 并行数据处理与性能
并行流就是一个把内容分成多个数据块，并用不同的线程分别处理每个数据块的流。这样一来，你就可以自动把给定操作的工作负荷
分配给多核处理器的所有内核，让它们都忙起来
1. 用并行流并行处理数据   
  只需要用parallelStream替换stream
  
2. 并行流的性能分析  
    ParallelStreamsTest
3. 分支/合并框架   
    分支/合并框架的目的是以递归方式将可以并行的任务拆分成更小的任务，然后将每个子任
务的结果合并起来生成整体结果。它是 ExecutorService 接口的一个实现，它把子任务分配给
线程池（称为 ForkJoinPool ）中的工作线程。首先来看看如何定义任务和子任务  
ForkJoinSumCalculator
![合并框架过程图](images/分支合并过程图.png)  

  要把任务提交到这个池，必须创建RecursiveTask<R> 的一个子类，其中 R 是并行化任务（以
及所有子任务）产生的结果类型，或者如果任务不返回结果，则是 RecursiveAction 类型（当
然它可能会更新其他非局部机构）。要定义RecursiveTask，只需实现它唯一的抽象方法compute

 默认线程数 Runtime.getRuntime().availableProcessors()

### 3. CompletableFuture ：组合式异步编程
#### Future 接口
Future 接口在Java 5中被引入，设计初衷是对将来某个时刻会发生的结果进行建模。它建模
了一种异步计算，返回一个执行运算结果的引用，当运算结束后，这个引用被返回给调用方。在
Future 中触发那些潜在耗时的操作把调用线程解放出来，让它能继续执行其他有价值的工作，
不再需要呆呆等待耗时的操作完成（洗衣房的例子或欠条）。
```
ExecutorService executor = Executors.newCachedThreadPool();
Future<Double> future = executor.submit(new Callable<Double>() {
public Double call() {
return doSomeLongComputation();
}});
doSomethingElse();
try {
Double result = future.get(1, TimeUnit.SECONDS);
} catch (ExecutionException ee) {
// 计算抛出一个异常
} catch (InterruptedException ie) {
// 当前线程在等待过程中被中断
} catch (TimeoutException te) {
// 在Future对象完成之前超过已过期
}
```
CompletableFuture继承了Future 思路一致 增加了方便流式操作的方法
```
 CompletableFuture.supplyAsync(() -> "CF")
            .thenApply(s -> s + ".thenApply")
            .thenCompose(s -> CompletableFuture.supplyAsync(() -> s + ".thenCompose"))
            .thenAccept(System.out::println);
```

#### 实战
业务需求
1. 已经获取一个商品集合，集合中的商品对象包含部分属性（商品名、商品编码）
2. 需要调取接口获取商品原价，依赖商品编码
3. 需要调取接口获取折扣，依赖商品编码
4. 商品真实价格等于原价*商品折扣
4. 需要调取接口获取商品优惠券，优惠券依赖价格

####  线程数计算公式
![线程数计算](images/线程数计算.png) 



