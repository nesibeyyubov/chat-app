package com.nesib.chatapp;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {
    private Context context;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    public ProfileFragment(Context context){
        this.context = context;
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        final ImageView profileImage = view.findViewById(R.id.profileImage);
        final TextView userName = view.findViewById(R.id.userName);
        final TextView fullName = view.findViewById(R.id.fullName);
        final RelativeLayout progressContainer = view.findViewById(R.id.progressContainer);
        final LinearLayout profileContainer = view.findViewById(R.id.profileContainer);
        Button logoutButton = view.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                Intent intent = new Intent(context,LoginActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.incoming,R.anim.going);
                getActivity().finish();
            }
        });
        FirebaseUser user = auth.getCurrentUser();

        progressContainer.setVisibility(View.VISIBLE);
        profileContainer.setVisibility(View.GONE);
        db.collection("users").document(user.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String userNameValue = documentSnapshot.getString("userName");
                        String fullNameValue = documentSnapshot.getString("fullName");
                        String imageUrl = documentSnapshot.getString("imageUrl");

                        Picasso.get().load(imageUrl).into(profileImage);
                        userName.setText(userNameValue);
                        fullName.setText(fullNameValue);
                        progressContainer.setVisibility(View.GONE);
                        profileContainer.setVisibility(View.VISIBLE);
                    }
                });

        return view;
    }
}