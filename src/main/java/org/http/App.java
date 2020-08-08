package org.http;

import okhttp3.Request;
import org.http.callback.JsonResponseHandler;
import org.http.callback.StringResponseHandler;
import org.http.request.HttpMethod;
import org.http.request.RequestBuilder;
import org.http.response.TextResponse;
import org.http.util.HttpUtil;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;

/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ) {
        test1();
    }


    public static class Demo1<T>{

        public void c(){
            System.out.println(getClass().getEnclosingClass().getTypeName());
            Type genericSuperclass = getClass().getGenericSuperclass();
            System.out.println(genericSuperclass instanceof ParameterizedType);
            System.out.println(genericSuperclass instanceof TypeVariable);
            System.out.println(genericSuperclass instanceof Class);
            System.out.println(((Class)(genericSuperclass)).getName());
        }
        public void t(){
            Field[] declaredFields = getClass().getDeclaredFields();
            for (int i = 0; i < declaredFields.length; i++) {
                Type genericType = declaredFields[i].getGenericType();
                if(genericType instanceof ParameterizedType){
                    System.out.println(declaredFields[i].getName() + "-->ParameterizedType:(RawType)"+((ParameterizedType) genericType).getRawType());
                    System.out.println(declaredFields[i].getName() + "-->ParameterizedType:(OwnerType)"+((ParameterizedType) genericType).getOwnerType());
                    System.out.println(declaredFields[i].getName() +
                            "-->ParameterizedType:(ActualTypeArguments)"
                            + Arrays.toString(((ParameterizedType) genericType).getActualTypeArguments()));

                }else if(genericType instanceof TypeVariable){
                    System.out.println(declaredFields[i].getName() + "-->TypeVariable:(Bounds)"+ Arrays.toString(((TypeVariable) genericType).getBounds()));
                    System.out.println(declaredFields[i].getName() + "-->TypeVariable:(GenericDeclaration)"+((TypeVariable) genericType).getGenericDeclaration());
                }

//                Class <T> entityClass = (Class <T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
//                System.out.println(entityClass);

            }
        }
    }


    public static void test1(){
//        Request request = RequestUtil.createGetRequest("https://httpbin.org/status/500");
//        HttpUtil.asyncGet(request, new Callback() {
//            @Override
//            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                System.out.println("onFailure");
//            }
//
//            @Override
//            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                System.out.println("onResponse");
//            }
//        });

        Request jsonRequest = new RequestBuilder()
                .url("https://httpbin.org/post")
                .method(HttpMethod.POST)
                .addParam("username","helloworld")
                .addParam("password","123456")
                .build();
        HttpUtil.asyncPost(jsonRequest, new StringResponseHandler() {
            @Override
            public void onSuccess(String result) {
                System.out.println(result);
            }

            @Override
            public void onFailure(int code, String message) {
                System.out.println(code + ":" + message);
            }
        });
    }
}
