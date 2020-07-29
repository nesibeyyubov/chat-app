package com.nesib.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.nesib.chatapp.model.User;

public class UploadPhotoActivity extends AppCompatActivity implements View.OnClickListener {
    private Button skipButton,uploadButton;
    private ImageView uploadedImage;
    private LinearLayout nextButton;
    private TextView nextButtonText;
    private ProgressBar progressBar,skipProgressBar;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseStorage firebaseStorage;

    private Uri imageUri;

    private final static int PICK_IMAGE_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_photo);

        skipButton = findViewById(R.id.skipButton);
        uploadButton = findViewById(R.id.uploadButton);
        uploadedImage = findViewById(R.id.uploadedImage);
        nextButton = findViewById(R.id.nextButton);
        nextButtonText = findViewById(R.id.nextButtonText);
        progressBar = findViewById(R.id.nextProgressBar);
        skipProgressBar = findViewById(R.id.skipProgressBar);

        nextButton.setOnClickListener(this);
        skipButton.setOnClickListener(this);
        uploadButton.setOnClickListener(this);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
    }

    @Override
    public void onClick(View view) {
        String userId = auth.getCurrentUser().getUid();
        String email = auth.getCurrentUser().getEmail();
        String fullNameValue = getIntent().getStringExtra("fullName");
        String userNameValue = getIntent().getStringExtra("userName");

        final User user = new User();
        user.setId(userId);
        user.setEmail(email);
        user.setFullName(fullNameValue);
        user.setUserName(userNameValue);
        switch (view.getId()){
            case R.id.nextButton:
                progressBar.setVisibility(View.VISIBLE);
                nextButtonText.setVisibility(View.GONE);
                firebaseStorage.getReference("images/").child(user.getId())
                        .putFile(imageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                taskSnapshot.getMetadata().getReference().getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                user.setImageUrl(uri.toString());
                                                saveUser(user);
                                            }
                                        });
                            }
                        });
                break;
            case R.id.skipButton:
                skipProgressBar.setVisibility(View.VISIBLE);
                user.setImageUrl("none");
                saveUser(user);
                break;
            case R.id.uploadButton:
                getImageFromGallery();
                break;
        }
    }

    public void openMainActivity(){
        Intent intent = new Intent(UploadPhotoActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void saveUser(User user){
        db.collection("users")
                .document(user.getId())
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        openMainActivity();
                    }
                });
    }

    public void getImageFromGallery(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,PICK_IMAGE_REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
            uploadedImage.setImageURI(imageUri);
        }
        else if(resultCode == RESULT_CANCELED){
        }
    }
}