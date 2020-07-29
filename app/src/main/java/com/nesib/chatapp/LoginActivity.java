package com.nesib.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private LinearLayout loginButton;
    private TextView registerLink;

    private EditText email, password;
    private FirebaseAuth firebaseAuth;
    private TextView loginError,authButtonText;
    private ProgressBar authProgressBar;
    private final String TAG="mytag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginButton = findViewById(R.id.loginButton);
        registerLink = findViewById(R.id.registerLink);
        email = findViewById(R.id.emailInput);
        password = findViewById(R.id.passwordInput);
        loginError = findViewById(R.id.loginError);
        authButtonText = findViewById(R.id.authButtonText);
        authProgressBar = findViewById(R.id.authProgressBar);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            openMainActivity();
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailValue = email.getText().toString().trim();
                String passwordValue = password.getText().toString().trim();
                if (emailValue.isEmpty()) {
                    loginError.setText("Email field can't be empty !");
                    loginError.setVisibility(View.VISIBLE);
                } else if (passwordValue.isEmpty()) {
                    loginError.setText("Password field can't be empty !");
                    loginError.setVisibility(View.VISIBLE);
                }
                else if(passwordValue.isEmpty() && emailValue.isEmpty()){
                    loginError.setText("These fields can't be empty !");
                    loginError.setVisibility(View.VISIBLE);
                }else {
                    authProgressBar.setVisibility(View.VISIBLE);
                    authButtonText.setVisibility(View.GONE);
                    firebaseAuth.signInWithEmailAndPassword(emailValue, passwordValue)
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    loginError.setVisibility(View.GONE);
                                    openMainActivity();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    authProgressBar.setVisibility(View.GONE);
                                    authButtonText.setVisibility(View.VISIBLE);
                                    loginError.setVisibility(View.VISIBLE);
                                    loginError.setText("Email or password is not correct !");
                                }
                            });
                }

            }
        });

        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

    }

    public void openMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}