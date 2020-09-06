package io.vincenzopalazzo.lightning.testutil;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.Objects;

public class HttpRequestFactory {
    private static HttpRequestFactory SINGLETON;

    public static HttpRequestFactory getInstance(){
        if(SINGLETON == null){
            SINGLETON = new HttpRequestFactory();
        }
        return SINGLETON;
    }

    private OkHttpClient client;

    private HttpRequestFactory(){
        client = new OkHttpClient();
    }

    public Request buildGetRequest(String withUrl){
        if(withUrl == null || withUrl.isEmpty()){
            throw new IllegalArgumentException("Url null or empty");
        }
        return new Request.Builder().url(withUrl).build();
    }

    public String execRequest(Request request){
        if(request == null){
            throw new IllegalArgumentException("Request null");
        }
        try{
            Response response = client.newCall(request).execute();
            return Objects.requireNonNull(response.body()).string();
        }catch (Exception ex){
            throw new RuntimeException(ex.getCause());
        }
    }

}
