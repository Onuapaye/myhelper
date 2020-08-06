package com.martin.theelderlyassistant;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.martin.theelderlyassistant.views.ElderlyLoginActivity;

public class MainActivity extends AppCompatActivity {
    public Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.openElderlyLoginScreen();
    }

    /**
     * Opens the `Elderly Login` screen when the button of
     * `I am an Elderly` is clicked
     */
    private void openElderlyLoginScreen(){
        button = (Button) findViewById(R.id.iAmAnElderlyButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ElderlyLoginActivity.class);
                startActivity(intent);
            }
        });
    }
}