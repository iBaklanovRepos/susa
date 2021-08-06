package com.example.susa.database.requests.tasks;

import com.example.susa.database.requests.DatabaseRequest;
import com.example.susa.models.jsonmodels.AuthMeJson;
import com.google.gson.Gson;

import java.util.Objects;
import java.util.concurrent.Callable;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AuthMeRequestTask implements Callable<String> {
    AuthMeJson authMeJson;
    String bearerToken;

    public AuthMeRequestTask(String bearerToken) {
        this.bearerToken = bearerToken;
    }

    @Override
    public String call() throws Exception {

        Request request = new Request.Builder()
                .url(DatabaseRequest.getRequestAddress() + "auth/me")
                .get()
                .addHeader("Authorization", "Bearer " + bearerToken)
                .build();


        System.out.println(request.url().toString());

        try (Response response = DatabaseRequest.getHttpClient().newCall(request).execute()){
            return Objects.requireNonNull(response.body()).string();
        }
    }

}
