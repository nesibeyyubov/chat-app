package com.nesib.chatapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nesib.chatapp.MessageActivity;
import com.nesib.chatapp.R;
import com.nesib.chatapp.model.Chat;
import com.nesib.chatapp.model.User;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    private Context context;
    private List<User> userList;

    public UsersAdapter(Context context, ArrayList<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.userName.setText(userList.get(position).getUserName());
        holder.fullName.setText(userList.get(position).getFullName());

        String imageUrl = userList.get(position).getImageUrl();
        if (imageUrl == null || imageUrl.equals("none")) {
            holder.profileImage.setBackgroundResource(R.drawable.user);
        } else {
            Picasso
                    .get()
                    .load(imageUrl)
                    .centerCrop()
                    .fit()
                    .into(holder.profileImage);
        }

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView profileImage;
        private TextView userName, fullName;
        private Button messageButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profileImage);
            userName = itemView.findViewById(R.id.userName);
            fullName = itemView.findViewById(R.id.fullName);
            messageButton = itemView.findViewById(R.id.messageButton);

            messageButton.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            User user = userList.get(getAdapterPosition());
            Intent intent = new Intent(context, MessageActivity.class);
            intent.putExtra("receiverId", user.getId());
            intent.putExtra("receiverImage", user.getImageUrl());
            intent.putExtra("receiverName", user.getUserName());
            context.startActivity(intent);
        }
    }


}
