package com.example.goldencarrot.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.goldencarrot.R;
import java.util.ArrayList;

public class WaitlistActivity extends AppCompatActivity {

    private ListView waitingListView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> waitlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_waitlist);


        waitingListView = findViewById(R.id.waitingListView);

        // Mock list of waitlisted events (this should be actual data)
        waitlist = new ArrayList<>();
        waitlist.add("Event 1");
        waitlist.add("Event 2");
        waitlist.add("Event 3");


        adapter = new ArrayAdapter<String>(this, R.layout.entrant_waitlist_item, R.id.EventNameTextView, waitlist) {
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                // Inflate the custom layout if necessary
                if (convertView == null) {
                    LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.entrant_waitlist_item, parent, false);
                }

                // Event name Text View
                TextView eventNameTextView = convertView.findViewById(R.id.EventNameTextView);
                eventNameTextView.setText(getItem(position));

                // Leave button
                Button leaveButton = convertView.findViewById(R.id.leaveButton);
                leaveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Remove the event from the waitlist and notify the adapter
                        waitlist.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                });

                return convertView;
            }
        };

        // Set the adapter on the ListView
        waitingListView.setAdapter(adapter);
    }
}
