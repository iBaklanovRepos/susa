package com.example.susa.database.requests.tasks;

import com.example.susa.database.requests.DatabaseRequest;

import java.util.Objects;
import java.util.concurrent.Callable;

import okhttp3.Request;
import okhttp3.Response;

public class LogoutRequestTask implements Callable<String> {
    @Override
    public String call() throws Exception {
        Request request = new Request.Builder()
                .url(DatabaseRequest.getRequestAddress() + "auth/logout")
                .get()
                .build();
        try (Response response = DatabaseRequest.getHttpClient().newCall(request).execute()){
            return Objects.requireNonNull(response.body()).string();
        }
    }
}
