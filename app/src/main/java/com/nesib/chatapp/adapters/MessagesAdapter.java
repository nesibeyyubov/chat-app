package com.nesib.chatapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.nesib.chatapp.R;
import com.nesib.chatapp.model.Message;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
    private ArrayList<Message> messages;
    private Context context;
    private FirebaseAuth auth;

    public MessagesAdapter(Context context,ArrayList<Message> messages){
        this.context = context;
        this.messages = messages;
        auth = FirebaseAuth.getInstance();
    }

    public void setMessages(ArrayList<Message> messages){
        this.messages = messages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        Message message = messages.get(position);
        View view = null;
        if(message.getReceiverId().equals("") || message.getSenderId().equals("")){
            view = LayoutInflater.from(context).inflate(R.layout.message_left_typing,parent,false);
        }
        else if(auth.getCurrentUser().getUid().equals(message.getReceiverId())){
            view = LayoutInflater.from(context).inflate(R.layout.message_left,parent,false);
        }
        else if(auth.getCurrentUser().getUid().equals(message.getSenderId())){
            view = LayoutInflater.from(context).inflate(R.layout.message_right,parent,false);
        }

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = messages.get(position);
        if(!message.getSenderId().equals("")){
            holder.messageText.setText(message.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView messageText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message);
        }

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
