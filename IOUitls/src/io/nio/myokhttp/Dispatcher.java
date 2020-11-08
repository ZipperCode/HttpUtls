package io.nio.myokhttp;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.*;

public final class Dispatcher {
    private int maxRequests = 64;

    private int maxRequestPerHost = 5;

    private ExecutorService executorService;

    private Deque<RealCall> readyAsyncCalls = new ArrayDeque<>();

    private Deque<RealCall> runningAsyncCalls = new ArrayDeque<>();

    private Deque<RealCall> runningSyncCalls = new ArrayDeque<>();

    public synchronized ExecutorService executorService() {
        if (this.executorService == null) {
            this.executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setName("Custom OKHttp");
                    return thread;
                }
            });
        }
        return this.executorService;
    }

    public synchronized void setMaxRequests(int maxRequests){
        if(maxRequests < 1){
            throw new IllegalArgumentException("max < 1: " + maxRequests);
        }

        this.maxRequests = maxRequests;
        // 调度任务执行
    }

    void executed(RealCall call){
        this.runningSyncCalls.add(call);
    }

    void finished(RealCall call) {
        synchronized (this){
            if(!this.runningSyncCalls.remove(call)){
                throw new RuntimeException("the call cant't remove, it ");
            }
        }
    }
}
