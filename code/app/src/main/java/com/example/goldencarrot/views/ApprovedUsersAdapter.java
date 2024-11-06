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

public class ApprovedUsersAdapter extends RecyclerView.Adapter<ApprovedUsersAdapter.ApprovedUsersViewHolder> {

    private ArrayList<User> approvedUsersList;

    // Constructor to initialize the list
    public ApprovedUsersAdapter(ArrayList<User> approvedUsersList) {
        this.approvedUsersList = approvedUsersList;
    }

    @NonNull
    @Override
    public ApprovedUsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each approved user item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.approved_user_item,
                parent, false);
        return new ApprovedUsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApprovedUsersViewHolder holder, int position) {
        // Get the approved user at the current position
        User user = approvedUsersList.get(position);

        // Bind the data (name and email) to the view holder
        holder.usernameTextView.setText(user.getName());
        holder.emailTextView.setText(user.getEmail());
    }

    @Override
    public int getItemCount() {
        return approvedUsersList.size();
    }

    // ViewHolder to represent each item
    public static class ApprovedUsersViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        TextView emailTextView;

        public ApprovedUsersViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.userNameTextView);
            emailTextView = itemView.findViewById(R.id.userEmailTextView);
        }
    }
}
