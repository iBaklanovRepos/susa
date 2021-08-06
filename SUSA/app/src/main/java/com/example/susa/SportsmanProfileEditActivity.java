package com.example.susa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class SportsmanProfileEditActivity extends AppCompatActivity {

    ImageView backIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sportsman_profile_edit);
        backIV = findViewById(R.id.toolbar_back);
        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SportsmanProfileEditActivity.this, SettingsActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}