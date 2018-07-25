package com.shuiyes.video.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池辅助类，用来管理整个程序的下载线程
 */
public class ThreadPoolUtil {

    private ThreadPoolUtil() {

    }

    /**
     * 线程池核心线程数
     */
    private static int CORE_POOL_SIZE = 5;
    /**
     * 线程池最大线程数
     */
    private static int MAX_POOL_SIZE = 200;
    /**
     * 额外线程空状态生存时间
     */
    private static int KEEP_ALIVE_TIME = 5000;
    // 阻塞队列。当核心线程都被占用，且阻塞队列已满的情况下，才会开启额外线程
    private static BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(
            10);

    // 线程工厂
    private static ThreadFactory threadFactory = new ThreadFactory() {

        private final AtomicInteger integer = new AtomicInteger();

        @Override
        public Thread newThread(Runnable r) {

            return new Thread(r, "myThreadPool thread:"
                    + integer.getAndIncrement());
        }

    };

    private static RejectedExecutionHandler handler = new RejectedExecutionHandler() {

        @Override
        public void rejectedExecution(Runnable arg0, ThreadPoolExecutor arg1) {

        }

    };

    // 线程池
    private static ThreadPoolExecutor threadPool;

    static {
        threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE,
                KEEP_ALIVE_TIME, TimeUnit.SECONDS, workQueue, threadFactory,
                handler);
    }

    /**
     * 从线程池中抽取线程，执行指定的Runnable对象
     *
     * @param runnable
     */
    public static void execute(Runnable runnable) {
        threadPool.execute(runnable);
    }

}
