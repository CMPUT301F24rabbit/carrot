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
 * RecyclerView adapter for displaying a list of users who are waitlisted for an event.
 * Each item in the list shows the user's name and email.
 * The adapter updates dynamically as the waitlisted users list changes.
 */
public class WaitlistedUsersRecyclerAdapter extends RecyclerView.Adapter<WaitlistedUsersRecyclerAdapter.WaitlistedUsersViewHolder> {

    // List of users who are waitlisted for an event
    private ArrayList<User> waitlistedUsersList;

    /**
     * Constructor for initializing the adapter with a list of waitlisted users.
     *
     * @param waitlistedUsersList The list of waitlisted users. If null, an empty list is used.
     */
    public WaitlistedUsersRecyclerAdapter(ArrayList<User> waitlistedUsersList) {
        this.waitlistedUsersList = waitlistedUsersList != null ? waitlistedUsersList : new ArrayList<>();
    }

    /**
     * Called when the RecyclerView needs a new ViewHolder for a list item.
     * Inflates the layout for each item in the RecyclerView.
     *
     * @param parent   The ViewGroup into which the new view will be added.
     * @param viewType The type of the view.
     * @return A new instance of WaitlistedUsersViewHolder.
     */
    @NonNull
    @Override
    public WaitlistedUsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item, parent, false);
        return new WaitlistedUsersViewHolder(view);
    }

    /**
     * Called when the RecyclerView needs to bind data to a list item view.
     * Sets the user's name and email on the corresponding TextViews.
     *
     * @param holder   The ViewHolder to bind data to.
     * @param position The position of the item within the list.
     */
    @Override
    public void onBindViewHolder(@NonNull WaitlistedUsersViewHolder holder, int position) {
        User user = waitlistedUsersList.get(position);
        holder.usernameTextView.setText(user.getName());
        holder.emailTextView.setText(user.getEmail());
    }

    /**
     * Returns the number of items in the waitlisted users list.
     *
     * @return The size of the waitlisted users list.
     */
    @Override
    public int getItemCount() {
        return waitlistedUsersList != null ? waitlistedUsersList.size() : 0;
    }

    /**
     * Updates the list of waitlisted users and notifies the adapter of the change.
     *
     * @param newUsersList The new list of waitlisted users. If null, an empty list is used.
     */
    public void updateWaitlistedUsersList(ArrayList<User> newUsersList) {
        this.waitlistedUsersList = newUsersList != null ? newUsersList : new ArrayList<>();
        notifyDataSetChanged();
    }

    /**
     * ViewHolder class for holding references to the views for each list item.
     */
    public static class WaitlistedUsersViewHolder extends RecyclerView.ViewHolder {
        // TextViews for displaying the user's name and email
        TextView usernameTextView;
        TextView emailTextView;

        /**
         * Constructor for initializing the ViewHolder with the list item view.
         *
         * @param itemView The view for the individual list item.
         */
        public WaitlistedUsersViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.nameListView);
            emailTextView = itemView.findViewById(R.id.emailListView);
        }
    }
}
