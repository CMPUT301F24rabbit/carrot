package com.example.goldencarrot.views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.goldencarrot.R;

import com.example.goldencarrot.data.model.user.User;

import java.util.ArrayList;

public class CancelledUsersAdapter extends RecyclerView.Adapter<CancelledUsersAdapter.CancelledUsersViewHolder> {

    private ArrayList<User> cancelledUsersList;

    public CancelledUsersAdapter(ArrayList<User> cancelledUsersList) {
        this.cancelledUsersList = cancelledUsersList;
    }

    @NonNull
    @Override
    public CancelledUsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cancelled_user_item, parent, false);
        return new CancelledUsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CancelledUsersViewHolder holder, int position) {
        User user = cancelledUsersList.get(position);
        holder.usernameTextView.setText(user.getUsername());
        holder.emailTextView.setText(user.getEmail());
    }

    @Override
    public int getItemCount() {
        return cancelledUsersList.size();
    }

    public static class CancelledUsersViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        TextView emailTextView;

        public CancelledUsersViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.userNameTextView);
            emailTextView = itemView.findViewById(R.id.userEmailTextView);
        }
    }
}
