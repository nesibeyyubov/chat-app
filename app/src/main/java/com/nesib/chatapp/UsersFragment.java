package com.nesib.chatapp;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nesib.chatapp.adapters.ChatsAdapter;
import com.nesib.chatapp.adapters.UsersAdapter;
import com.nesib.chatapp.model.Chat;
import com.nesib.chatapp.model.User;

import java.util.ArrayList;
import java.util.List;


public class UsersFragment extends Fragment {
    private Context context;
    private UsersAdapter usersAdapter;
    private RecyclerView recyclerView;
    private RelativeLayout progressContainer;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private ArrayList<User> users;

    public UsersFragment(Context context){
        this.context = context;
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        users = new ArrayList<>();
    }
    public UsersFragment() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        recyclerView = view.findViewById(R.id.recylcerView);
        progressContainer = view.findViewById(R.id.progressContainer);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(context,2));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        progressContainer.setVisibility(View.VISIBLE);
        db.collection("users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                users.clear();
                List<DocumentSnapshot> snapshotList = value.getDocuments();
                for(DocumentSnapshot snapshot : snapshotList){
                    User user = snapshot.toObject(User.class);
                    if(!user.getId().equals(auth.getCurrentUser().getUid())){
                        users.add(user);
                    }
                }
                progressContainer.setVisibility(View.GONE);
                usersAdapter = new UsersAdapter(context,users);
                recyclerView.setAdapter(usersAdapter);
            }
        });

    }
}