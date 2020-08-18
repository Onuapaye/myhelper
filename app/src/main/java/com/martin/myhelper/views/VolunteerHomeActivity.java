package com.martin.myhelper.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.martin.myhelper.R;

public class VolunteerHomeActivity extends AppCompatActivity {

    private TextView createServiceProfile, editServiceProfile, viewElderlyRequests, viewFeedback;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_home);

        this.setOnClicks();
    }

    private void  setOnClicks(){
        this.handleCreateElderlyTextViewOnClick();
        this.handleEditElderlyTextViewOnClick();
    }
    private  void handleCreateElderlyTextViewOnClick(){
        createServiceProfile = findViewById(R.id.createProfileTextView);
        createServiceProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(VolunteerHomeActivity.this, VolunteerProfileCreateActivity.class);
                startActivity(intent);
            }
        });
    }

    private  void handleEditElderlyTextViewOnClick(){
        editServiceProfile = findViewById(R.id.editProfileTextView);
        editServiceProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(VolunteerHomeActivity.this, VolunteerProfileActivity.class);
                startActivity(intent);
            }
        });
    }
}