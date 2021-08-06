package com.example.susa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.susa.database.requests.tasks.LoginRequestTask;
import com.example.susa.database.requests.RequestTaskRunner;
import com.example.susa.models.User;
import com.example.susa.models.jsonmodels.UserJson;
import com.example.susa.utils.AndroidUtil;
import com.google.gson.Gson;

public class LoginActivity extends AppCompatActivity implements RequestTaskRunner.Callback<String> {
    private EditText emailField, passwordField;
    private Button loginBtn;
    private ProgressBar progressBar;
    private LinearLayout loginLayout;
    private ImageView backIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginLayout = findViewById(R.id.loginLayout);
        loginLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidUtil.hideKeyboard(LoginActivity.this);
            }
        });
        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password);

        loginBtn = findViewById(R.id.login);

        progressBar = findViewById(R.id.loginProgressBar);
        progressBar.setVisibility(ProgressBar.INVISIBLE);

        backIV = findViewById(R.id.toolbar_back);
        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = emailField.getText().toString();
                final String password = passwordField.getText().toString();
                progressBar.setVisibility(ProgressBar.VISIBLE);
                AndroidUtil.hideKeyboard(LoginActivity.this);
                new RequestTaskRunner().executeAsync(new LoginRequestTask(email, password), LoginActivity.this);
            }
        });
    }


    @Override
    public void onComplete(String result) {
        Gson gson = new Gson();
        UserJson userJson = gson.fromJson(result, UserJson.class);
        if(userJson.success){
            User.loginUser(true, userJson.token);
            progressBar.setVisibility(ProgressBar.INVISIBLE);
//            CookieManager.getInstance().setCookie("token", userJson.token);
            Intent intent = new Intent(LoginActivity.this, MapActivity.class);
            startActivity(intent);
            finish();
        }else{
            progressBar.setVisibility(ProgressBar.INVISIBLE);
            Toast.makeText(LoginActivity.this, userJson.error, Toast.LENGTH_SHORT).show();
        }


    }
}