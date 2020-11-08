package io.nio.myokhttp;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ConnectionPool {

    private final LinkedHashMap<String, HttpConnection> urlConnectionCaches;

    private int limit;

    public ConnectionPool(int capacity, int limit) {
        this.limit = limit;
        urlConnectionCaches = new LinkedHashMap<String, HttpConnection>(capacity) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, HttpConnection> eldest) {
                HttpConnection value = eldest.getValue();
                return size() > limit || value.isOverTime();
            }
        };
    }

    public void limit(int limit) {
        this.limit = limit;
    }

    public synchronized void addConnection(String url, HttpConnection httpConnection) {
        this.urlConnectionCaches.put(url, httpConnection);
    }

    public HttpConnection getConnection(String url){
        return this.urlConnectionCaches.get(url);
    }

    public synchronized void removeConnection(String url){
        this.urlConnectionCaches.remove(url);
    }
}
