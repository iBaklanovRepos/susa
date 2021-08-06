package com.example.susa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.susa.database.requests.DatabaseRequest;

import java.net.CookieHandler;

public class MainActivity extends AppCompatActivity {
    private Button loginBtn, registerBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        WebView webView = new WebView(this);
//        webView.getSettings().setJavaScriptEnabled(true);
//
//        CookieSyncManager.createInstance(this);
//        CookieSyncManager.getInstance().sync();
//        CookieManager.setAcceptFileSchemeCookies(true);
//        CookieManager cookieManager = CookieManager.getInstance();
//
//
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//            CookieSyncManager.createInstance(this);
//        }
//        cookieManager.setAcceptCookie(true);
//        cookieManager.acceptCookie();

        CookieHandler.setDefault(new java.net.CookieManager());

        DatabaseRequest.initDatabaseRequest();
        loginBtn = findViewById(R.id.login);
        registerBtn = findViewById(R.id.register);


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
    }
}