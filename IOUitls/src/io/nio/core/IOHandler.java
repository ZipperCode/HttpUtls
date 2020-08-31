package io.nio.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class IOHandler extends Thread {

    private final LinkedBlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();

    private final ExecutorService runTaskExecutorService;

    private final AtomicBoolean lock = new AtomicBoolean(false);

    public IOHandler(){
        runTaskExecutorService = Executors.newCachedThreadPool(
                new IOThreadFactory("IOHandler-Thread-"));
    }

    @Override
    public void run() {
        super.run();
        while (true){
            // 没有任务让出资源
            if(taskQueue.size() == 0){
                synchronized (lock){
                    if(lock.get()){
                        try {
                            lock.wait();
                            System.out.println("任务出现 IOHandler 被唤醒");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                continue;
            }
            Runnable poll = taskQueue.poll();
            if(poll != null && !runTaskExecutorService.isShutdown()){
                System.out.println("开始执行任务 ： " + poll);
                runTaskExecutorService.execute(poll);
            }
        }
    }

    /**
     * 有任务的时候
     */
    public void onTask(Runnable runnable){
//        System.out.println("onTask 出现新任务添加到队列中，唤醒线程");
        synchronized (lock){
            lock.compareAndSet(true,false);
            taskQueue.offer(runnable);
            lock.notifyAll();
        }
    }

    /**
     * 没有任务的时候
     */
    public void onNone(){
//        System.out.println("没有任务，进入等待");
        synchronized (lock){
            lock.compareAndSet(false,true);
        }
    }

    static class IOThreadFactory implements ThreadFactory {
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        IOThreadFactory(String namePrefix) {
            SecurityManager s = System.getSecurityManager();
            this.group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            this.namePrefix = namePrefix;
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
}
