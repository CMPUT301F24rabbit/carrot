package com.example.goldencarrot.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.goldencarrot.R;
import com.example.goldencarrot.data.model.user.UserUtils;

public class SelectUserTypeActivity extends AppCompatActivity {
    String userType = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_sign_up_select_user_type);


        // Organizer Button
        findViewById(R.id.select_user_type_organizer_button).
                setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userType = UserUtils.ORGANIZER_TYPE;
            }
        });

        // Participant Button
        findViewById(R.id.select_user_type_participant_button).
                setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userType = UserUtils.PARTICIPANT_TYPE;
            }
        });

        // Next Button
        findViewById(R.id.select_user_type_next_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userType!=null){
                    Intent intent = new Intent(SelectUserTypeActivity.this,
                            SignUpActivity.class);
                    intent.putExtra(UserUtils.USER_TYPE, userType);
                    startActivity(intent);
                }
            }
        });
    }
}
