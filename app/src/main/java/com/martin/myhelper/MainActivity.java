package com.martin.myhelper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.martin.myhelper.model.GenericModel;
import com.martin.myhelper.views.CreateVolunteerProfileActivity;
import com.martin.myhelper.views.ElderlyViewProvidedServiceActivity;
import com.martin.myhelper.views.LoginActivity;

public class MainActivity extends AppCompatActivity {

    // a variable of type button
    public Button elderlyLoginButton, volunteerLoginButton, administratorButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // open the login screen
        this.openLoginScreen();
    }

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

        // onClick event for volunteer login button
        administratorButton = (Button) findViewById(R.id.iAmAnAdminButton);
        administratorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Intent i = new Intent(MainActivity.this, ElderlyViewProvidedServiceActivity.class);
                //i.putExtra("loginPageHeaderTitle", "VOLUNTEER");
                //i.putExtra("userType", GenericModel.USER_TYPE_ADMIN);
                //startActivity(i);
            }
        });
    }

}