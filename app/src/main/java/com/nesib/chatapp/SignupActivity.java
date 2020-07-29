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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nesib.chatapp.model.User;

import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {
    private LinearLayout signupButton;
    private TextView loginLink;

    private EditText email, password,fullName,userName;
    private TextView signupError,authButtonText;
    private ProgressBar authProgressBar;
    private final String TAG="mytag";

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupButton = findViewById(R.id.signupButton);
        loginLink = findViewById(R.id.loginLink);

        email = findViewById(R.id.emailInput);
        fullName = findViewById(R.id.fullName);
        userName = findViewById(R.id.userName);
        password = findViewById(R.id.passwordInput);
        signupError = findViewById(R.id.signupError);
        authButtonText = findViewById(R.id.authButtonText);
        authProgressBar = findViewById(R.id.authProgressBar);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailValue = email.getText().toString().trim();
                String passwordValue = password.getText().toString().trim();
                final String fullNameValue = fullName.getText().toString().trim();
                final String userNameValue = userName.getText().toString().trim();
                if(passwordValue.isEmpty()
                        && emailValue.isEmpty()
                        && fullNameValue.isEmpty()
                        && userNameValue.isEmpty()){
                    signupError.setText("Please fill all the fields !");
                    signupError.setVisibility(View.VISIBLE);
                }
                else if (emailValue.isEmpty()) {
                    signupError.setText("Email field can't be empty !");
                    signupError.setVisibility(View.VISIBLE);
                } else if (passwordValue.isEmpty()) {
                    signupError.setText("Password field can't be empty !");
                    signupError.setVisibility(View.VISIBLE);
                }
                else{
                    authProgressBar.setVisibility(View.VISIBLE);
                    authButtonText.setVisibility(View.GONE);
                    firebaseAuth.createUserWithEmailAndPassword(emailValue,passwordValue)
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    openPhotoActivity(fullNameValue,userNameValue);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    signupError.setVisibility(View.VISIBLE);
                                    signupError.setText(e.getMessage());
                                    authProgressBar.setVisibility(View.GONE);
                                    authButtonText.setVisibility(View.VISIBLE);
                                }
                            });
                }

            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }



    public void openPhotoActivity(String _fullName,String _userName){
        Intent intent = new Intent(SignupActivity.this,UploadPhotoActivity.class);
        intent.putExtra("fullName",_fullName);
        intent.putExtra("userName",_userName);
        startActivity(intent);
        overridePendingTransition(R.anim.incoming,R.anim.going);
        finish();
    }
}