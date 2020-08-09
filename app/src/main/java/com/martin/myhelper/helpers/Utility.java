package com.martin.myhelper.helpers;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.martin.myhelper.model.ElderlyModel;

import java.util.UUID;

public class Utility {

    private static final String REQUIRED_FIELD_TITLE = "Required Field Empty";
    private static final String MIN_PASSWORD_INVALID = "Minimum Required Length";
    private static final  int MIN_PASSWORD_LENGTH_VALUE = 8;


    public static void showMessage(Context _context, String _message){
        Toast.makeText(_context, _message, Toast.LENGTH_SHORT).show();
    }

    // this validates some fields to check for their emptiness or nullability
    public static boolean validateInputsOnCreate(AppCompatActivity appCompatActivity, EditText... editTexts ) {
        EditText firstNameText = editTexts[0];
        EditText lastNameText = editTexts[1];
        EditText emailText = editTexts[2];
        EditText mobileNumberText = editTexts[3];
        EditText password = editTexts[4];
        EditText retypePassword = editTexts[5];

        if (firstNameText.getText() == null || firstNameText.getText().toString().isEmpty()){
            Utility.showInformationDialog(REQUIRED_FIELD_TITLE, "Please enter your first name.", appCompatActivity);
//            firstNameText.setError("First name is required.");
            return false;
        }

        if (lastNameText.getText() == null || lastNameText.getText().toString().isEmpty()){
            Utility.showInformationDialog(REQUIRED_FIELD_TITLE, "Please enter your last name.", appCompatActivity);
//            lastNameText.setError("Last name is required.");
            return  false;
        }

        if (emailText.getText() == null || emailText.getText().toString().isEmpty()){
//            emailText.setError("E-mail address is required");
            Utility.showInformationDialog(REQUIRED_FIELD_TITLE, "Please enter your e-mail address.", appCompatActivity);
            return false;
        }

        if (mobileNumberText.getText() == null || mobileNumberText.getText().toString().isEmpty()){
            Utility.showInformationDialog(REQUIRED_FIELD_TITLE, "Please enter your mobile number.", appCompatActivity);
//            mobileNumberText.setError("Mobile number is required");
            return false;
        }

        if (password.getText() == null || password.getText().toString().isEmpty()){
            Utility.showInformationDialog(REQUIRED_FIELD_TITLE, "Please enter your password.", appCompatActivity);
//            password.setError("Password is required");
            return false;
        }

        if (!retypePassword.getText().toString().equals(password.getText().toString())){
            Utility.showInformationDialog(REQUIRED_FIELD_TITLE, "Your password do not match. Please try again", appCompatActivity);
//            password.setError("Password does not match");
            return false;
        }

        if (password.length() < MIN_PASSWORD_LENGTH_VALUE){
            Utility.showInformationDialog(MIN_PASSWORD_INVALID, "Password must be greater or equals to " + MIN_PASSWORD_LENGTH_VALUE + " characters long", appCompatActivity);
            return false;
        }

        if (password.length() < MIN_PASSWORD_LENGTH_VALUE){
            Utility.showInformationDialog(MIN_PASSWORD_INVALID, "Re-type password must be greater or equals to " + MIN_PASSWORD_LENGTH_VALUE + " characters long", appCompatActivity);
            return false;
        }

        return true;
    }

    /***
     * Validates user inputs during login
     * @param appCompatActivity
     * @param email
     * @param password
     * @return a boolean of true if validation is successful else false
     */
    public static boolean validateInputsOnUserLogin(AppCompatActivity appCompatActivity, String email, String password ) {

        if (email == null || email.isEmpty()){
            Utility.showInformationDialog(REQUIRED_FIELD_TITLE, "Please enter your e-mail address.", appCompatActivity);
            return false;
        }

        if (password == null || password.isEmpty()){
            Utility.showInformationDialog(REQUIRED_FIELD_TITLE, "Please enter your password.", appCompatActivity);
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

    /***
     * Gets a reference to the Firebase Realtime Database
     * @return
     */
    /*public static DatabaseReference getFirebaseDatabaseReference(){
        return FirebaseDatabase.getInstance().getReference();
    }*/

    /***
     * Gets the instance of the FirebaseAuth for user authentication processes
     * @return
     */
    public static FirebaseAuth getFirebaseAuthenticationReference(){
        return FirebaseAuth.getInstance();
    }

    /***
     * Gets the instance of the FirebaseStore cloud database
     * @return
     */
    public static FirebaseFirestore getFirebaseFireStore(){
        return FirebaseFirestore.getInstance();
    }

    /***
     * Gets the reference of the Firebase Storage for uploading and retrieving images
     * @return
     */
    public static StorageReference getFirebaseStorage(){
        return FirebaseStorage.getInstance().getReference();
    }

    /***
     *
     * @param _msgTitle
     * @param _msgContent
     * @param _appCompactActivity
     */
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

    /***
     * Show a message to the user if the account e-mail is not verified. It gives the Yes and No options
     * to the User and when yes is clicked, a verification e-mail is resent else prevents the user from accessing the app
     * @param _appCompactActivity
     */
    public static void promptUserBeforeEmailResetLinkIsSent(final AppCompatActivity _appCompactActivity){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(_appCompactActivity);
        final FirebaseDatabaseCRUDHelper crudHelper = new FirebaseDatabaseCRUDHelper();

        // set dialog title
        alertDialogBuilder.setTitle("RESEND VERIFICATION LINK?");

        // set dialog message
        alertDialogBuilder.setMessage("Your account or e-mail is not yet verified. Do you want to resend the verification code or link now?");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Yes! Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // send verification e-mail if the user clicks on yes
                crudHelper.sendVerificationEmail(_appCompactActivity);
                return;
            }
        });
        alertDialogBuilder.setNegativeButton("No! Don't Send",(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showInformationDialog("ACCESS DENIED", "Sorry you cannot login to your account till you verify it.", _appCompactActivity);
                return;
            }
        }));

        // create the alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show the alert dialog
        alertDialog.show();
    }

    public static void promptUserBeforePasswordResetLinkIsSent(final String _email, final AppCompatActivity _appCompactActivity, final Class destinationClass){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(_appCompactActivity);
        final FirebaseDatabaseCRUDHelper crudHelper = new FirebaseDatabaseCRUDHelper();

        // set dialog title
        alertDialogBuilder.setTitle("RESET PASSWORD LINK?");

        // set dialog message
        alertDialogBuilder.setMessage("Do you want to send a PASSWORD RESET link now?");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Yes! Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // send verification e-mail if the user clicks on yes
                crudHelper.sendPasswordResetLink(_email, _appCompactActivity, destinationClass);
                return;
            }
        });
        alertDialogBuilder.setNegativeButton("No! Don't Send",(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showInformationDialog("PASSWORD RESET", "You have cancelled the PASSWORD RESET successfully.", _appCompactActivity);
                return;
            }
        }));

        // create the alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show the alert dialog
        alertDialog.show();
    }

    public static String getUUID(){
        return UUID.randomUUID().toString();
    }
}
