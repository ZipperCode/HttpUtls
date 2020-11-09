package io.nio.myokhttp;

import java.util.*;
import java.util.concurrent.*;

public final class Dispatcher {
    private int maxRequests = 64;

    private int maxRequestPerHost = 5;

    private ExecutorService executorService;

    private Deque<RealCall.AsyncCall> readyAsyncCalls = new ArrayDeque<>();

    private Deque<RealCall.AsyncCall> runningAsyncCalls = new ArrayDeque<>();

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
        dispatcher();
    }

    void executed(RealCall call){
        this.runningSyncCalls.add(call);
    }

    void finished(RealCall call) {
        synchronized (this){
            if(!this.runningSyncCalls.remove(call)){
                throw new RuntimeException("the call cant't remove, it maybe null");
            }
        }
    }

    public void enqueue(RealCall.AsyncCall call) {
        if (this.runningAsyncCalls.size() < this.maxRequests) {
            this.runningAsyncCalls.add(call);
            this.executorService().execute(call);
        } else {
            this.readyAsyncCalls.add(call);
        }
    }

    synchronized void finished(RealCall.AsyncCall call){
        synchronized (this){
            if(!this.runningAsyncCalls.remove(call)){
                throw new RuntimeException("the call cant't remove, it maybe null");
            }
            dispatcher();
        }
    }

    synchronized void dispatcher(){
        if (this.runningAsyncCalls.size() <= this.maxRequests){
            Iterator<RealCall.AsyncCall> iterator = this.readyAsyncCalls.iterator();
            while (iterator.hasNext() && (this.runningAsyncCalls.size() <= this.maxRequests)){
                RealCall.AsyncCall next = iterator.next();
                this.runningAsyncCalls.add(next);
                iterator.remove();
            }
        }
    }

    public synchronized List<Call> runningCalls() {
        List<Call> result = new ArrayList();
        result.addAll(this.runningSyncCalls);
        Iterator iterator = this.runningAsyncCalls.iterator();

        while(iterator.hasNext()) {
            RealCall.AsyncCall asyncCall = (RealCall.AsyncCall)iterator.next();
            result.add(asyncCall.get());
        }

        return Collections.unmodifiableList(result);
    }

}
