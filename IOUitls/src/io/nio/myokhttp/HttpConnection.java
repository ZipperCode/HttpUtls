package io.nio.myokhttp;

import java.io.Closeable;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;

public final class HttpConnection implements Closeable {

    /**
     * 最大的连接闲置时间
     */
    private static final long MAX_OVER_TIME = 60 * 1000 * 1000;

    private WeakReference<HttpURLConnection> realHttpConnection;

    private final ConnectionPool connectionPool;
    /**
     * 最后一次的运行时间
     */
    private long lastNanoTime;
    /**
     * 当前连接的url，用于close方法关闭时，释放连接池
     */
    private String url;

    public HttpConnection(ConnectionPool connectionPool, String url, HttpURLConnection httpConnection) {
        this.connectionPool = connectionPool;
        this.url = url;
        this.realHttpConnection = new WeakReference<>(httpConnection);
        this.lastNanoTime = System.nanoTime();
    }

    /**
     * 获取最后一次的使用时间
     *
     * @return 时间
     */
    public long lastNanoTime() {
        return lastNanoTime;
    }

    /**
     * 重置最后一个的使用事件
     */
    public void restLastNanoTime() {
        lastNanoTime = System.nanoTime();
    }

    private String url() {
        return url;
    }

    public HttpURLConnection connection() {
        return realHttpConnection.get();
    }

    public boolean isOverTime() {
        long currentNanoTime = System.nanoTime();
        System.out.println(this + ":currentNanoTime = " + currentNanoTime
                + ",lastNanoTime = " + lastNanoTime);
        if (currentNanoTime - lastNanoTime > MAX_OVER_TIME) {
            System.out.println(this + ": sub = " + (currentNanoTime - lastNanoTime));
            return true;
        }
        return false;
    }

    @Override
    public void close() throws IOException {
        HttpURLConnection httpURLConnection = realHttpConnection.get();
        if (httpURLConnection != null) {
            httpURLConnection.disconnect();
        }
        connectionPool.removeConnection(url);
        realHttpConnection.clear();
        realHttpConnection = null;
        lastNanoTime = -1L;

    }
}
