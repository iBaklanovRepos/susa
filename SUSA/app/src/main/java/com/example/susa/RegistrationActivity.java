package com.example.susa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.susa.database.requests.tasks.RegisterRequestTask;
import com.example.susa.database.requests.RequestTaskRunner;
import com.example.susa.models.User;
import com.example.susa.models.jsonmodels.UserJson;
import com.example.susa.utils.AndroidUtil;
import com.google.gson.Gson;

public class RegistrationActivity extends AppCompatActivity implements RequestTaskRunner.Callback<String> {
    private EditText emailField, passwordField, nameField;
    private Button registrationBtn;

    private ProgressBar progressBar;
    private RadioGroup radioGroup;
    private RadioButton sportsmanRadio, trainerRadio, refereeRadio, medicRadio;
    private LinearLayout registerLayout;
    private ImageView backIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password);
        nameField = findViewById(R.id.userName);

        progressBar = findViewById(R.id.registrProgressBar);
        progressBar.setVisibility(ProgressBar.INVISIBLE);

        registrationBtn = findViewById(R.id.registration);

        radioGroup = findViewById(R.id.registerRadioGroup);

        registerLayout = findViewById(R.id.registerLayout);

        registerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidUtil.hideKeyboard(RegistrationActivity.this);
            }
        });
        registrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = emailField.getText().toString();
                final String password = passwordField.getText().toString();
                final String userName = nameField.getText().toString();
                RadioButton selectedRole = findViewById(radioGroup.getCheckedRadioButtonId());
                final String role = selectedRole.getText().toString();
                progressBar.setVisibility(ProgressBar.VISIBLE);
                AndroidUtil.hideKeyboard(RegistrationActivity.this);
                new RequestTaskRunner().executeAsync(new RegisterRequestTask(email, password, userName, role), RegistrationActivity.this);
            }
        });

        findViewById(R.id.toolbar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationActivity.this, MapActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });

        backIV = findViewById(R.id.toolbar_back);

        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public void onComplete(String result) {
        Gson gson = new Gson();
        UserJson userJson = gson.fromJson(result, UserJson.class);
        if(userJson.success){
            User.loginUser(true, userJson.token);
            RadioButton selectedRole = findViewById(radioGroup.getCheckedRadioButtonId());
            progressBar.setVisibility(ProgressBar.INVISIBLE);
//            switch (selectedRole.getText().toString()){
//                case "Спортсмен":{
//                    Intent intent = new Intent(RegistrationActivity.this, SportsmanRegistrationActivity.class);
//                    startActivity(intent);
//                    finish();
//                    break;
//                }
//                case "Медик":{
//                    Intent intent = new Intent(RegistrationActivity.this, MedicRegistraitionActivity.class);
//                    startActivity(intent);
//                    finish();
//                    break;
//                }
//                case "Тренер":{
//                    Intent intent = new Intent(RegistrationActivity.this, TrainerRegistrationActivity.class);
//                    startActivity(intent);
//                    finish();
//                    break;
//                } case "Судья":{
//                    Intent intent = new Intent(RegistrationActivity.this, RefereeRegistraitionActivity.class);
//                    startActivity(intent);
//                    finish();
//                    break;
//                } default:{
//                    System.out.println("Invalid role");
//                    break;
//                }
//            }

            Intent intent = new Intent(RegistrationActivity.this, MapActivity.class);
            startActivity(intent);
            finish();


        }else{
            progressBar.setVisibility(ProgressBar.INVISIBLE);
            Toast.makeText(RegistrationActivity.this, userJson.error, Toast.LENGTH_SHORT).show();
        }
        System.out.println(result);
    }
}