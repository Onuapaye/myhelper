package com.martin.myhelper.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;


public class OpenActivity extends Activity {

    /**
     * Opens an activity when called
     */
    public static void openAnActivityScreen(Context sourceActivity, Class destinationActivity){
        Intent intent = new Intent(sourceActivity, destinationActivity);
        sourceActivity.startActivity(intent);
    }

}

