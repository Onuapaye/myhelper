package com.martin.myhelper.helpers;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.martin.myhelper.model.ElderlyModel;

import java.util.UUID;

public class Utility {

    public static void showMessage(Context _context, String _message){
        Toast.makeText(_context, _message, Toast.LENGTH_SHORT).show();
    }

    // this validates some fields to check for their emptiness or nullability
    public static boolean validateElement(AppCompatActivity appCompatActivity, EditText... editTexts ) {
        EditText firstNameText = editTexts[0];
        EditText lastNameText = editTexts[1];
        EditText emailText = editTexts[2];
        EditText mobileNumberText = editTexts[3];
        EditText password = editTexts[4];
        EditText retypePassword = editTexts[5];

        if (firstNameText.getText() == null || firstNameText.getText().toString().isEmpty()){
            Utility.showInformationDialog("Required Field Empty", "Please enter your first name.", appCompatActivity);
//            firstNameText.setError("First name is required.");
            return false;
        }

        if (lastNameText.getText() == null || lastNameText.getText().toString().isEmpty()){
            Utility.showInformationDialog("Required Field Empty", "Please enter your last name.", appCompatActivity);
//            lastNameText.setError("Last name is required.");
            return  false;
        }

        if (emailText.getText() == null || emailText.getText().toString().isEmpty()){
//            emailText.setError("E-mail address is required");
            Utility.showInformationDialog("Required Field Empty", "Please enter your e-mail address.", appCompatActivity);
            return false;
        }

        if (mobileNumberText.getText() == null || mobileNumberText.getText().toString().isEmpty()){
            Utility.showInformationDialog("Required Field Empty", "Please enter your mobile number.", appCompatActivity);
//            mobileNumberText.setError("Mobile number is required");
            return false;
        }

        if (password.getText() == null || password.getText().toString().isEmpty()){
            Utility.showInformationDialog("Required Field Empty", "Please enter your password.", appCompatActivity);
//            password.setError("Password is required");
            return false;
        }

        if (!retypePassword.getText().toString().equals(password.getText().toString())){
            Utility.showInformationDialog("Required Field Empty", "Your password do not match. Please try again", appCompatActivity);
//            password.setError("Password does not match");
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

    public static void showInformationDialog(String _msgTitle, String _msgContent, final AppCompatActivity _appCompactActivity){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(_appCompactActivity);

        // set dialog title
        alertDialogBuilder.setTitle(_msgTitle);

        // set dialog message
        alertDialogBuilder.setMessage(_msgContent);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                Toast.makeText(_appCompactActivity, "Action cancelled.", Toast.LENGTH_SHORT).show();
                return;
            }
        });

        // create the alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show the alert dialog
        alertDialog.show();
    }

    public static String getUUID(){
        return UUID.randomUUID().toString();
    }
}
