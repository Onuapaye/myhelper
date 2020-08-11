package com.martin.myhelper.helpers;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class OpenActivity extends Activity {

    /**
     * Opens an activity when called
     */
    public void openActivityScreen(Context sourceActivity, Class destinationActivity){
        Intent intent = new Intent(sourceActivity, destinationActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void openAnActivityScreen(Context sourceActivity, Class destinationActivity){
        Intent intent = new Intent(sourceActivity, destinationActivity);
        startActivity(intent);
    }

    /*public void openAnActivityScreen(Class destinationActivity){
       startActivity(new Intent(getApplicationContext(), destinationActivity));
    }*/
}

