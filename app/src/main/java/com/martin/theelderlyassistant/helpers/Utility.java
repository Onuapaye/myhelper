package com.martin.theelderlyassistant.helpers;

import android.content.Context;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.martin.theelderlyassistant.model.ElderlyModel;

public class Utility {

    public static void showMessage(Context _context, String _message){
        Toast.makeText(_context, _message, Toast.LENGTH_SHORT).show();
    }


    // this validates some fields to check for their emptiness or nullability
    public static boolean validateElement(EditText... editTexts) {
        EditText firstNameText = editTexts[0];
        EditText lastNameText = editTexts[1];
        EditText emailText = editTexts[2];
        EditText mobileNumberText = editTexts[3];
        EditText password = editTexts[4];
        EditText retypePassword = editTexts[5];

        if (firstNameText.getText() == null || firstNameText.getText().toString().isEmpty()){
            firstNameText.setError("First name is required.");
            return false;
        }

        if (lastNameText.getText() == null || lastNameText.getText().toString().isEmpty()){
            lastNameText.setError("Last name is required.");
            return  false;
        }

        if (emailText.getText() == null || emailText.getText().toString().isEmpty()){
            emailText.setError("E-mail address is required");
            return false;
        }

        if (mobileNumberText.getText() == null || mobileNumberText.getText().toString().isEmpty()){
            mobileNumberText.setError("Mobile number is required");
            return false;
        }

        if (password.getText() == null || password.getText().toString().isEmpty()){
            password.setError("Password is required");
            return false;
        }

        if (password.getText() != retypePassword.getText()){
            password.setError("Password does not match");
            return false;
        }

        return true;
    }

    public static ElderlyModel getElderly(Intent intent, Context context){
        try {
            return (ElderlyModel) intent.getSerializableExtra("ELDERLY_KEY");
        } catch (Exception exception){
            exception.printStackTrace();
            showMessage(context, "Getting Elderly data error\n" + exception.getMessage());
        }
        return  null;
    }

    public static DatabaseReference getDatabaseReference(){
        return FirebaseDatabase.getInstance().getReference();
    }

    public static void showInfoDialog(final AppCompatActivity appCompatActivity, String _message){

    }
}
