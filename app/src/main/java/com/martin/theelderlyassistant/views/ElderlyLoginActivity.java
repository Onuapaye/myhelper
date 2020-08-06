package com.martin.theelderlyassistant.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.martin.theelderlyassistant.MainActivity;
import com.martin.theelderlyassistant.R;

import org.w3c.dom.Text;

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