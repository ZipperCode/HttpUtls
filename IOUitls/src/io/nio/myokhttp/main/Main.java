package io.nio.myokhttp.main;

import io.nio.myokhttp.HttpClient;
import io.nio.myokhttp.body.Request;
import io.nio.myokhttp.body.Response;
import io.nio.myokhttp.body.ResponseBody;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        HttpClient httpClient = new HttpClient.Builder().build();

        Request request = new Request.Builder()
                .url("https://www.baidu.com")
                .build();

        try {
            Response response = httpClient.newCall(request).execute();
            ResponseBody body = response.body();
            if(body != null){
                System.out.println(body.string());
            }
            System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
