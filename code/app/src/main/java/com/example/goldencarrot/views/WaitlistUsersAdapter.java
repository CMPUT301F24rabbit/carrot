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



/**
 * This is an adapter for the waitlist recycler view.
 *
 */
public class WaitlistUsersAdapter extends
        RecyclerView.Adapter<WaitlistUsersAdapter.WaitlistViewHolder> {
        private ArrayList<User> waitlist;
        private String waitlistType; // "approved", "cancelled", "rejected"
        public WaitlistUsersAdapter(ArrayList<User> waitlist, String waitlistType) {
            this.waitlist = waitlist;
            this.waitlistType = waitlistType;
        }
        @NonNull
            @Override
            public WaitlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.waitlist_user_item,
                        parent, false);
                return new WaitlistViewHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull WaitlistViewHolder holder, int position) {
                User user = waitlist.get(position);
                holder.usernameTextView.setText(user.getName());
                holder.emailTextView.setText(user.getEmail());
            }

            @Override
            public int getItemCount() {
                return waitlist.size();
            }

            public static class WaitlistViewHolder extends RecyclerView.ViewHolder {
                TextView usernameTextView;
                TextView emailTextView;

                public WaitlistViewHolder(@NonNull View itemView) {
                    super(itemView);
                    usernameTextView = itemView.findViewById(R.id.nameListView);
                    emailTextView = itemView.findViewById(R.id.emailListView);
                }
            }
        }
