package com.example.goldencarrot.data.model.user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.goldencarrot.R;

import java.util.ArrayList;

/**
 * Array adapter for User Objects, used to inflate and display a list of Users
 * in different views like Cancelled waitlist, regular waitlist, chosen waitlist etc...
 */
public class UserArrayAdapter extends ArrayAdapter<User> {
    private ArrayList<User> users;
    private Context context;
    public UserArrayAdapter(@NonNull Context context, ArrayList<User> users) {
        super(context, 0, users);
        this.users = users;
        this.context = context;
    }

    /** This method can be used to display the inflated list of Users.
     *
     * @param position index
     * @param convertView convertview
     * @param parent parent view
     * @return
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.user_list_item, parent, false);
        } else {
            view = convertView;
        }
        User user = getItem(position);
        TextView username = view.findViewById(R.id.nameListView);
        TextView email = view.findViewById(R.id.emailListView);
        username.setText(user.getName());
        email.setText(user.getEmail());
        return view;
    }

}
