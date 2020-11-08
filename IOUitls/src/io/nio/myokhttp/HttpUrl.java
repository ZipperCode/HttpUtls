package io.nio.myokhttp;

import java.net.MalformedURLException;
import java.net.URL;

public final class HttpUrl {

    private String urlString;

    private URL url;

    private String host;

    private String scheme;

    private int port;

    public HttpUrl(String urlString) {
        this.urlString = urlString;
    }

    public String urlString() {
        return urlString;
    }

    public URL url() {
        if (url == null) {
            parse();
        }
        return url;
    }

    public String host() {
        if (host == null) {
            parse();
        }
        return host;
    }

    public String scheme() {
        if (scheme == null) {
            parse();
        }
        return scheme;
    }

    public int port() {
        if (port == 0) {
            parse();
        }
        return port;
    }

    private void parse() {
        try {
            url = new URL(urlString);
            scheme = url.getProtocol();
            host = url.getHost();
            if ((port = url.getPort()) == -1) {
                port = url.getDefaultPort();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "HttpUrl{" +
                "urlString='" + urlString + '\'' +
                ", host='" + host + '\'' +
                ", scheme='" + scheme + '\'' +
                ", port=" + port +
                '}';
    }

    public static void main(String[] args) {
        String s = "https://blog.csdn.net:4433/jiang7701037/article/details/86304302";
        HttpUrl httpUrl = new HttpUrl(s);
        httpUrl.parse();
        System.out.println(httpUrl);
    }
}
