package com.example.susa.database.requests.tasks;

import android.os.AsyncTask;

import com.example.susa.database.requests.DatabaseRequest;
import com.google.gson.Gson;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

import okhttp3.Cookie;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

public class LoginRequestTask implements Callable<String> {
    LoginEntity entity;

    public LoginRequestTask(String email, String password){
        entity = new LoginEntity(email, password);
    }

    @Override
    public String call() throws Exception {

        Gson gson = new Gson();

        RequestBody body = RequestBody.create(gson.toJson(entity), DatabaseRequest.JSON);
        Request request = new Request.Builder()
                .url(DatabaseRequest.getRequestAddress() + "auth/login")
                .post(body)
                .build();


        System.out.println(request.url().toString());

        try (Response response = DatabaseRequest.getHttpClient().newCall(request).execute()){
            System.out.println(response.headers().get("Set-Cookie"));
            return Objects.requireNonNull(response.body()).string();
        }
    }

    private class LoginEntity {
        private String email;
        private String password;

        public LoginEntity(String email, String password) {
            this.email = email;
            this.password = password;
        }
    }
}
