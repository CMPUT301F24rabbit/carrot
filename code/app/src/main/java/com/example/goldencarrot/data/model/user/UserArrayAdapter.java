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
 * Array adapter for users
 */
public class UserArrayAdapter extends ArrayAdapter<UserImpl> {
    public UserArrayAdapter(@NonNull Context context, ArrayList<UserImpl> users) {
        super(context, 0, users);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.user_list_item, parent, false);
        } else {
            view = convertView;
        }
        UserImpl user = getItem(position);
        TextView username = view.findViewById(R.id.userNameView);
        username.setText(user.getUsername());
        return view;
    }
}
