package com.nesib.chatapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nesib.chatapp.MessageActivity;
import com.nesib.chatapp.R;
import com.nesib.chatapp.model.Chat;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> {
    private Context context;
    private List<Chat> chatList;

    public ChatsAdapter(Context context, List<Chat> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat = chatList.get(position);
        holder.userName.setText(chat.getUserName());
        holder.profileImage.setBackgroundResource(R.drawable.user);
        Picasso.get().load(chat.getProfileImage()).centerCrop().fit().into(holder.profileImage);
        holder.lastMessage.setText(chat.getLastMessage());

        holder.lastDate.setText(getFormattedDate(chat.getLastMessageDate()));
        holder.messageCount.setText("6");
    }

    public String getFormattedDate(long timestamp){
        DateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
        String date = simpleDateFormat.format(new Date(timestamp));

        return date;
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView profileImage;
        private TextView userName,messageCount,lastMessage,lastDate;
        private RelativeLayout messageCountContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profileImage);
            userName = itemView.findViewById(R.id.userName);
            messageCount = itemView.findViewById(R.id.messageCount);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            lastDate = itemView.findViewById(R.id.lastDate);
            messageCountContainer = itemView.findViewById(R.id.messageCountContainer);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, MessageActivity.class);
            Chat chat = chatList.get(getAdapterPosition());
            intent.putExtra("receiverId",chat.getReceiverId());
            intent.putExtra("receiverImage",chat.getProfileImage());
            intent.putExtra("receiverName",chat.getUserName());
            context.startActivity(intent);
        }
    }


}
