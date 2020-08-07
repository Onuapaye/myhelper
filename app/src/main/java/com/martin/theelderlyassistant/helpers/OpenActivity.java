package com.martin.theelderlyassistant.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.martin.theelderlyassistant.R;
import com.martin.theelderlyassistant.views.ElderlyLoginActivity;
import com.martin.theelderlyassistant.views.ElderlyRegistrationActivity;


public class OpenActivity extends Activity {

    /**
     * Opens an activity when called
     */
    public static void openAnActivityScreen(Context sourceActivity, Class destinationActivity){
        Intent intent = new Intent(sourceActivity, destinationActivity);
        sourceActivity.startActivity(intent);
    }

}

