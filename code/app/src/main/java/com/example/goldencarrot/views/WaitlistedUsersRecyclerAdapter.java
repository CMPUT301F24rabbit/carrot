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

public class WaitlistedUsersRecyclerAdapter extends RecyclerView.Adapter<WaitlistedUsersRecyclerAdapter.WaitlistedUsersViewHolder> {

    private ArrayList<User> waitlistedUsersList;

    public WaitlistedUsersRecyclerAdapter(ArrayList<User> waitlistedUsersList) {
        this.waitlistedUsersList = waitlistedUsersList != null ? waitlistedUsersList : new ArrayList<>();
    }

    @NonNull
    @Override
    public WaitlistedUsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item, parent, false);
        return new WaitlistedUsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WaitlistedUsersViewHolder holder, int position) {
        User user = waitlistedUsersList.get(position);
        holder.usernameTextView.setText(user.getName());
        holder.emailTextView.setText(user.getEmail());
    }

    @Override
    public int getItemCount() {
        return waitlistedUsersList != null ? waitlistedUsersList.size() : 0;
    }

    // Method to update the list and notify the adapter of data changes
    public void updateWaitlistedUsersList(ArrayList<User> newUsersList) {
        this.waitlistedUsersList = newUsersList != null ? newUsersList : new ArrayList<>();
        notifyDataSetChanged();
    }

    public static class WaitlistedUsersViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        TextView emailTextView;

        public WaitlistedUsersViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.nameListView);
            emailTextView = itemView.findViewById(R.id.emailListView);
        }
    }
}

