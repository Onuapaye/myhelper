package com.martin.theelderlyassistant.helpers;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.martin.theelderlyassistant.R;


public class OpenActivity extends Activity {

    /**
     * Open an activity when called
     */
    public void openAnActivityScreen(Class<?> destinationActivity){
        Intent intent = new Intent(this, destinationActivity);
        startActivity(intent);
    }

}
