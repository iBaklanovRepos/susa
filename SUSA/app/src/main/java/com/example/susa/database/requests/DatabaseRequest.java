package com.example.susa.database.requests;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DatabaseRequest {
    public final static String PROTOCOL = "http";
    public final static String LOCALHOST = "10.0.2.2";
    public final static String PORT = "5010";
    public final static String ADDRESS = "api/v1";
    public final static MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private static String requestAddress;

    private static OkHttpClient httpClient;

    private static RequestTaskRunner taskRunner;

    public static void initDatabaseRequest() {
        if(httpClient == null) {

            requestAddress = requestHeaderBuilder();
            taskRunner = new RequestTaskRunner();
            httpClient = new OkHttpClient().newBuilder().cookieJar(new CookieJar() {
                private final HashMap<HttpUrl, List<Cookie>> cookieStore = new HashMap<>();

                @Override
                public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                    System.out.println("Here is the cookie " + cookies.get(0));
                    cookieStore.put(url, cookies);
                }

                @Override
                public List<Cookie> loadForRequest(HttpUrl url) {
                    List<Cookie> cookies = cookieStore.get(url);
                    return cookies != null ? cookies : new ArrayList<Cookie>();
                }
            }).build();
        }
    }

    public static OkHttpClient getHttpClient(){
        return httpClient;
    }

    public static String getRequestAddress() {
        return requestAddress;
    }

    private static String requestHeaderBuilder() {
        return String.format("%s://%s:%s/%s/", PROTOCOL, LOCALHOST, PORT, ADDRESS);
    }
}
