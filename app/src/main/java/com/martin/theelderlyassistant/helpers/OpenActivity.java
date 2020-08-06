package com.martin.theelderlyassistant.helpers;

import android.app.Activity;
import android.content.Intent;

public class OpenActivity extends Activity {

    /**
     * Open an activity when called
     */
    public void openAnActivityScreen(Class<?> destinationActivity){
        Intent intent = new Intent(this, destinationActivity);
        startActivity(intent);
    }
}
