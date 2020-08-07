package com.martin.theelderlyassistant;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.martin.theelderlyassistant.helpers.OpenActivity;
import com.martin.theelderlyassistant.views.ElderlyLoginActivity;
import com.martin.theelderlyassistant.views.ElderlyRegistrationActivity;

import static androidx.core.content.ContextCompat.startActivity;

public class MainActivity extends AppCompatActivity {

    // a variable of type button
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