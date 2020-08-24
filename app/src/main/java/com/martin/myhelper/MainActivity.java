package com.martin.myhelper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.martin.myhelper.helpers.ElderlyVolunteerCRUDHelper;
import com.martin.myhelper.helpers.Utility;
import com.martin.myhelper.model.GenericModel;
import com.martin.myhelper.views.ElderlyCreateRequestActivity;
import com.martin.myhelper.views.LoginActivity;
import com.martin.myhelper.views.VolunteerProfileCreateActivity;

import static com.martin.myhelper.helpers.Utility.CREATE_RECORD_EMAIL_SUCCESS_MSG;
import static com.martin.myhelper.helpers.Utility.CREATE_RECORD_SUCCESS_MSG;
import static com.martin.myhelper.helpers.Utility.CREATE_RECORD_SUCCESS_TITLE;

public class MainActivity extends AppCompatActivity {
    // a variable of type button
    public Button elderlyLoginButton, volunteerLoginButton, administratorButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // open the login screen
        this.openLoginScreen();

        // show message for user record creation
        this.checkAndShowUserCreationSuccessMessage();
        this.showPasswordResetSuccessMessage();

//        this.handleSignOutOnClick();
    }

//    private void handleSignOutOnClick(){
//        TextView signOut = findViewById(R.id.createProfileLogOutButton);
//        signOut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ElderlyVolunteerCRUDHelper.signOutUser(MainActivity.this, MainActivity.class);
//            }
//        });
//    }

    /**
     * Opens the `Login` screen when the button of
     * `I am an Elderly` or `I am a Volunteer` is clicked
     */
    private void openLoginScreen(){
        // onClick event for elderly login button
        elderlyLoginButton = (Button) findViewById(R.id.iAmAnElderlyButton);
        elderlyLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.putExtra("loginPageHeaderTitle", "ELDERLY PERSON");
                intent.putExtra("userType", GenericModel.USER_TYPE_ELDER);
                startActivity(intent);
            }
        });

        // onClick event for volunteer login button
        volunteerLoginButton = (Button) findViewById(R.id.iAmAVolunteerButton);
        volunteerLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.putExtra("loginPageHeaderTitle", "VOLUNTEER");
                intent.putExtra("userType", GenericModel.USER_TYPE_VOLUNTEER);
                startActivity(intent);
            }
        });
    }

    private void checkAndShowUserCreationSuccessMessage(){

        Intent intent = getIntent();
        intent.getExtras();

        if (intent.hasExtra("recordCreated")){
            // show a message for successful record recreation
            Utility.showInformationDialog(CREATE_RECORD_SUCCESS_TITLE,
                    CREATE_RECORD_SUCCESS_MSG + "\n" + CREATE_RECORD_EMAIL_SUCCESS_MSG, this);
        }

        if (intent.hasExtra("loginPageHeaderTitle")){
            TextView textView = findViewById(R.id.pageHeaderTitle);
            textView.setText(intent.getStringExtra("loginPageHeaderTitle"));
        }
    }

    private  void showPasswordResetSuccessMessage(){
        Intent intent = getIntent();
        intent.getExtras();

        if (intent.hasExtra("passwordResetSuccess")){
            // show a message for successful record recreation
            Utility.showInformationDialog("PASSWORD RESET LINK SENT",
                    intent.getStringExtra("passwordResetSuccess") , this);
        }
    }
}

/*// onClick event for volunteer login button
administratorButton = (Button) findViewById(R.id.iAmAnAdminButton);
administratorButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent i = new Intent(MainActivity.this, VolunteerProfileCreateActivity.class);
        i.putExtra("loginPageHeaderTitle", "VOLUNTEER");
        i.putExtra("userType", GenericModel.USER_TYPE_ADMIN);
        startActivity(i);
    }
});*/