package com.example.susa.database.requests.tasks;

import com.example.susa.database.requests.DatabaseRequest;
import com.google.gson.Gson;

import java.util.Objects;
import java.util.concurrent.Callable;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterRequestTask implements Callable<String> {

    RegistrationEntity entity;


    public RegisterRequestTask(String email, String password, String username, String role) {
        entity = new RegistrationEntity(email, password, username, role);
    }

    @Override
    public String call() throws Exception {

        Gson gson = new Gson();

        RequestBody body = RequestBody.create(gson.toJson(entity), DatabaseRequest.JSON);
        Request request = new Request.Builder()
                .url(DatabaseRequest.getRequestAddress() + "auth/register")
                .post(body)
                .build();
        System.out.println(request.url().toString());
        try (Response response = DatabaseRequest.getHttpClient().newCall(request).execute()){
            return Objects.requireNonNull(response.body()).string();
        }
    }

    private class RegistrationEntity {
        private String email;
        private String password;
        private String name;
        private String sportrole;

        public RegistrationEntity(String email, String password, String username, String role) {
            this.email = email;
            this.password = password;
            this.name = username;
            this.sportrole = role;
        }
    }
}
