package com.martin.myhelper.views;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.hootsuite.nachos.NachoTextView;
import com.martin.myhelper.R;
import com.martin.myhelper.helpers.MultiSelectionSpinner;

public class CreateVolunteerProfileActivity extends AppCompatActivity {
    MultiSelectionSpinner multiSelectionSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_volunteer_profile);

        multiSelectionSpinner = (MultiSelectionSpinner) findViewById(R.id.mySpinner1);

        Button bt = (Button) findViewById(R.id.confirmCreateServiceProfile);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = multiSelectionSpinner.getSelectedItemsAsString();
                Log.e("getSelected ", s);
            }
        });
    }
}