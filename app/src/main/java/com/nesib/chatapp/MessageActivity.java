package com.nesib.chatapp;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.nesib.chatapp.adapters.MessagesAdapter;
import com.nesib.chatapp.model.Message;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MessageActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private MessagesAdapter messagesAdapter;
    private RecyclerView recyclerView;
    private RelativeLayout progressContainer;
    private EditText messageInput;
    private ImageButton sendButton, backButton;
    private ImageView receiverImage;
    private TextView receiverName, statusText;
    private View statusIcon;

    private ArrayList<Message> messages;

    private int onStartCallCount = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        progressContainer = findViewById(R.id.progressContainer);
        recyclerView = findViewById(R.id.recylcerView);
        sendButton = findViewById(R.id.sendButton);
        messageInput = findViewById(R.id.messageInput);
        receiverImage = findViewById(R.id.receiverImage);
        receiverName = findViewById(R.id.receiverName);
        backButton = findViewById(R.id.backButton);
        statusText = findViewById(R.id.statusText);
        statusIcon = findViewById(R.id.statusIcon);

        receiverName.setText(getIntent().getStringExtra("receiverName"));
        Picasso.get()
                .load(getIntent().getStringExtra("receiverImage"))
                .centerCrop()
                .fit()
                .into(receiverImage);

        messages = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        final String currentUserId = auth.getCurrentUser().getUid();
        final String receiverId = getIntent().getStringExtra("receiverId");

        recyclerView.setVisibility(View.GONE);
        db.collection("messages")
                .orderBy("date")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        List<DocumentSnapshot> snapshotList = value.getDocuments();
                        messages.clear();

                        for (DocumentSnapshot snapshot : snapshotList) {
                            Message message = snapshot.toObject(Message.class);

                            if (message.getSenderId().equals(currentUserId) && message.getReceiverId().equals(receiverId) ||
                                    message.getSenderId().equals(receiverId) && message.getReceiverId().equals(currentUserId)
                            ) {
                                messages.add(message);
                            }
                        }
                        messagesAdapter = new MessagesAdapter(MessageActivity.this, messages);
                        recyclerView.setAdapter(messagesAdapter);
                        recyclerView.scrollToPosition(messages.size() - 1);

                        if (progressContainer.getVisibility() == View.VISIBLE) {
                            progressContainer.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }


                    }
                });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageTextValue = messageInput.getText().toString().trim();
                if (!messageTextValue.isEmpty()) {
                    Message message = new Message();
                    message.setMessage(messageTextValue);
                    message.setSenderId(currentUserId);
                    message.setReceiverId(receiverId);
                    message.setDate(System.currentTimeMillis());

                    messageInput.setText("");

                    db.collection("messages")
                            .document("" + System.currentTimeMillis())
                            .set(message)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                }
                            });
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        messageInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String receiverId = getIntent().getStringExtra("receiverId");
                String senderId = auth.getCurrentUser().getUid();
                if (charSequence.length() > 0) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("receiverId", receiverId);
                    hashMap.put("senderId", senderId);
                    db.collection("typing").document(receiverId + senderId).set(hashMap);
                } else {
                    db.collection("typing").document(receiverId + senderId).delete();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        final String receiverId = auth.getCurrentUser().getUid();
        final String senderId = getIntent().getStringExtra("receiverId");

        db.collection("users")
                .document(receiverId)
                .update("status","online");

        db.collection("typing").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                List<DocumentSnapshot> snapshots = value.getDocuments();

                if(messages.size()>0){
                    if(messages.get(messages.size()-1).getSenderId().equals("")){
                        messages.remove(messages.size()-1);
                        messagesAdapter.setMessages(messages);
                        messagesAdapter.notifyDataSetChanged();
                    }
                }

                onStartCallCount++;

                for (DocumentSnapshot snapshot : snapshots) {
                    if (snapshot.getString("receiverId").equals(receiverId) &&
                            snapshot.getString("senderId").equals(senderId)
                    ) {
                        Message message = new Message("", "", "", 0L);
                        messages.add(message);
                        messagesAdapter.setMessages(messages);
                        messagesAdapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(messages.size() - 1);
                        break;
                    }
                }
            }
        });

        db.collection("users").document(senderId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                String status = value.getString("status");
                if (status == null) {
                    status = "offline";
                }
                statusText.setText(status);
                if (status.equals("online")) {
                    statusIcon.setBackgroundResource(R.drawable.online_bg);
                } else {
                    statusIcon.setBackgroundResource(R.drawable.offline_bg);
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        db.collection("users")
                .document(auth.getCurrentUser().getUid())
                .update("status","offline");
    }
}
