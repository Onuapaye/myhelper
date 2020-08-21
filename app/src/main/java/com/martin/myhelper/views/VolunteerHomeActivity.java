package com.martin.myhelper.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.martin.myhelper.R;
import com.martin.myhelper.helpers.Utility;

import static com.martin.myhelper.helpers.Utility.CREATE_RECORD_SUCCESS_TITLE;
import static com.martin.myhelper.helpers.Utility.CREATE_VOLUNTEER_PROFILE_SUCCESS_MSG;
import static com.martin.myhelper.helpers.Utility.UPDATE_RECORD_SUCCESS_MSG;
import static com.martin.myhelper.helpers.Utility.UPDATE_RECORD_SUCCESS_TITLE;

public class VolunteerHomeActivity extends AppCompatActivity {

    private TextView createServiceProfile, editServiceProfile, viewElderlyRequests, viewFeedback;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_home);

        this.setOnClicks();
        this.showCreateSuccessMessage();
        this.showUpdateSuccessMessage();
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
                intent = new Intent(VolunteerHomeActivity.this, VolunteerProfilesActivity.class);
                startActivity(intent);
            }
        });
    }

    private  void showCreateSuccessMessage(){
        Intent intent = getIntent();
        intent.getExtras();

        if (intent.hasExtra("recordCreated")){
            // show a message for successful record recreation
            Utility.showInformationDialog(CREATE_RECORD_SUCCESS_TITLE,
                    CREATE_VOLUNTEER_PROFILE_SUCCESS_MSG , this);
        }
    }

    private void showUpdateSuccessMessage(){
        Intent intent = getIntent();
        intent.getExtras();

        if (intent.hasExtra("recordUpdate")){
            Utility.showInformationDialog(UPDATE_RECORD_SUCCESS_TITLE, UPDATE_RECORD_SUCCESS_MSG, this);
        }
    }
}