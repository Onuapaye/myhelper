package com.martin.theelderlyassistant.helpers;

import android.app.Activity;
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
    public void openAnActivityScreen(Activity sourceActivity, Class<?> destinationActivity){
        Intent intent = new Intent(sourceActivity, destinationActivity);
        startActivity(intent);
    }

}

//public class GenericClickListener implements View.OnClickListener {
//
//    public GenericClickListener(Activity sourceActivity, Class<? extends AppCompatActivity> destinationActivity) {
//        @Override
//        public void onClick (View view){
//            Intent intent = new Intent(sourceActivity, destinationActivity);
//            startActivity(intent);
//        }
//    }
//}