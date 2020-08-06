package com.martin.theelderlyassistant.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.martin.theelderlyassistant.R;

public class ElderlyRegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elderly_registration);

        this.openElderlyLoginScreen();
    }

    /***
     * Open the elderly login screen when clicked
     */
    private void openElderlyLoginScreen(){

        TextView textView = (TextView) findViewById(R.id.loginHereLink);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ElderlyRegistrationActivity.this, ElderlyLoginActivity.class);
                startActivity(intent);
            }
        });
    }
}