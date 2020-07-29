package com.nesib.chatapp;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.nesib.chatapp.adapters.ChatsAdapter;
import com.nesib.chatapp.model.Chat;
import com.nesib.chatapp.model.Message;
import com.nesib.chatapp.model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ChatsFragment extends Fragment {
    private Context context;
    private ChatsAdapter chatsAdapter;
    private RecyclerView recyclerView;
    private RelativeLayout progressContainer;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private List<Message> messages;
    private List<Chat> chats;

    public ChatsFragment() {

    }

    public ChatsFragment(Context context) {
        this.context = context;
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        messages = new ArrayList<>();
        chats = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        recyclerView = view.findViewById(R.id.recylcerView);
        progressContainer = view.findViewById(R.id.progressContainer);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(new ChatsAdapter(context, chats));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recyclerView.setVisibility(View.INVISIBLE);
        progressContainer.setVisibility(View.VISIBLE);
        db.collection("messages")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        messages.clear();
                        chats.clear();

                        List<DocumentSnapshot> snapshots = value.getDocuments();
                        for (DocumentSnapshot snapshot : snapshots) {
                            Message message = snapshot.toObject(Message.class);
                            messages.add(message);
                        }

                        getRecentChats();
                    }
                });
    }

    public void getAllMessages() {
        recyclerView.setVisibility(View.GONE);
        db.collection("messages")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        List<DocumentSnapshot> snapshots = value.getDocuments();
                        for (DocumentSnapshot snapshot : snapshots) {
                            Message message = snapshot.toObject(Message.class);
                            messages.add(message);
                        }

                        getRecentChats();
                    }
                });
    }

    public void getRecentChats() {
        ArrayList<String> senderList = new ArrayList<>();
        ArrayList<String> receiverList = new ArrayList<>();

        ArrayList<String> uniqueSenderList = new ArrayList<>();
        ArrayList<String> uniqueReceiverList = new ArrayList<>();

        String currentId = auth.getCurrentUser().getUid();
        for (Message message : messages) {
            if (message.getReceiverId().equals(currentId)) {
                senderList.add(message.getSenderId());
            } else if (message.getSenderId().equals(currentId)) {
                receiverList.add(message.getReceiverId());
            }
        }

        for (int i = 0; i < senderList.size(); i++) {
            int count = 0;
            for (int j = i + 1; j < senderList.size(); j++) {
                if (senderList.get(i).equals(senderList.get(j))) {
                    count = count + 1;
                }
            }
            if (count == 0) {
                uniqueSenderList.add(senderList.get(i));
            }
        }

        for (int i = 0; i < receiverList.size(); i++) {
            int count = 0;
            for (int j = i + 1; j < receiverList.size(); j++) {
                if (receiverList.get(i).equals(receiverList.get(j))) {
                    count = count + 1;
                }
            }
            if (count == 0) {
                uniqueReceiverList.add(receiverList.get(i));
            }
        }


        ArrayList<String> uniqueChatList = new ArrayList<>(uniqueSenderList);
        for (int i = 0; i < uniqueReceiverList.size(); i++) {
            boolean flag = false;
            for (int j = 0; j < uniqueSenderList.size(); j++) {
                if (uniqueReceiverList.get(i).equals(uniqueSenderList.get(j))) {
                    flag = true;
                }
            }
            if (!flag) {
                uniqueChatList.add(uniqueReceiverList.get(i));
            }
        }
        getAllUsers(uniqueChatList);

    }

    private void getAllUsers(final ArrayList<String> uniqueChatList) {
        final ArrayList<User> allUsers = new ArrayList<>();
        db.collection("users")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> snapshots = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot snapshot : snapshots) {
                            User user = snapshot.toObject(User.class);
                            allUsers.add(user);
                        }

                        chats.clear();
                        for (String userId : uniqueChatList) {
                            for (int i = 0; i < allUsers.size(); i++) {
                                if (userId.equals(allUsers.get(i).getId())) {
                                    Chat chat = new Chat();
                                    chat.setProfileImage(allUsers.get(i).getImageUrl());
                                    chat.setUserName(allUsers.get(i).getUserName());
                                    chat.setReceiverId(userId);
                                    chat.setLastMessage(getLastMessage(userId).getMessage());
                                    chat.setLastMessageDate(getLastMessage(userId).getDate());
                                    chats.add(chat);

                                    break;
                                }
                            }
                        }
                        Collections.reverse(chats);
                        recyclerView.setVisibility(View.VISIBLE);
                        progressContainer.setVisibility(View.GONE);
                        chatsAdapter = new ChatsAdapter(context, chats);
                        recyclerView.setAdapter(chatsAdapter);
                    }
                });
    }

    private Message getLastMessage(String uniqueChatId) {
        Message lastMessage = new Message();
        String currentId = auth.getCurrentUser().getUid();

        ArrayList<Long> allMessageDatesOfRecentChat = new ArrayList<>();
        ArrayList<Message> allMessagesOfRecentChat = new ArrayList<>();
        for (Message message : messages) {
            if ((uniqueChatId.equals(message.getSenderId()) && currentId.equals(message.getReceiverId())) ||
                    (uniqueChatId.equals(message.getReceiverId()) && currentId.equals(message.getSenderId()))
            ) {
                allMessageDatesOfRecentChat.add(message.getDate());
                allMessagesOfRecentChat.add(message);
            }
        }
        long lastMessageDate = -1;
        for (int i = 0; i < allMessageDatesOfRecentChat.size(); i++) {
            if (lastMessageDate < allMessageDatesOfRecentChat.get(i)) {
                lastMessageDate = allMessageDatesOfRecentChat.get(i);
            }
        }

        for (int i = 0; i < allMessagesOfRecentChat.size(); i++) {
            if (lastMessageDate == allMessagesOfRecentChat.get(i).getDate()) {
                lastMessage = allMessagesOfRecentChat.get(i);
                break;
            }
        }

        return lastMessage;
    }
}