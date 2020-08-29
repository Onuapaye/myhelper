package com.martin.myhelper.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.martin.myhelper.MainActivity;
import com.martin.myhelper.R;
import com.martin.myhelper.helpers.ElderlyVolunteerCRUDHelper;
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

        this.handleViewElderlyRequests();
        this.handleSignOutOnClick();
    }

    private void handleSignOutOnClick(){
        TextView signOut = findViewById(R.id.logOutButton);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ElderlyVolunteerCRUDHelper.signOutUser(VolunteerHomeActivity.this, MainActivity.class);
            }
        });
    }

    private void  setOnClicks(){
        this.handleCreateProfileOnClick();
        this.handleEditProfileOnClick();
        this.handleViewElderlyFeedBacks();
    }
    private  void handleCreateProfileOnClick(){
        createServiceProfile = findViewById(R.id.createProfileTextView);
        createServiceProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(VolunteerHomeActivity.this,
                        VolunteerProfileCreateActivity.class);
                startActivity(intent);
            }
        });
    }

    private  void handleEditProfileOnClick(){
        editServiceProfile = findViewById(R.id.editProfileTextView);
        editServiceProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(VolunteerHomeActivity.this,
                        VolunteerProfilesActivity.class);
                startActivity(intent);
            }
        });
    }

    private void handleViewElderlyRequests(){
        viewElderlyRequests = findViewById(R.id.viewElderlyRequestsTextView);
        viewElderlyRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(VolunteerHomeActivity.this,
                        VolunteerViewElderlyRequestsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void handleViewElderlyFeedBacks(){
        viewFeedback = findViewById(R.id.provideFeedBackTextView);
        viewFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(VolunteerHomeActivity.this,
                        VolunteerViewElderlyFeedbackActivity.class);
                startActivity(intent);
            }
        });
    }

    //
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