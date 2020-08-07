package com.martin.myhelper.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.martin.myhelper.R;

public class ElderlyLoginActivity extends AppCompatActivity {

    public TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elderly_login);

        // monitor onclick event of a text to open elderly registration scree
        this.openElderlyRegistrationScreen();
    }

    /**
     * Open the elderly registration screen when clicked
     */
    private void openElderlyRegistrationScreen(){

        textView = (TextView) findViewById(R.id.registerLink);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ElderlyLoginActivity.this, ElderlyRegistrationActivity.class);
                startActivity(intent);
            }
        });
    }
}