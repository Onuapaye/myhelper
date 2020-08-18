package com.martin.myhelper.helpers;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Patterns;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.Spinner;
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

import static com.martin.myhelper.model.GenericModel.PICK_IMAGE_REQUEST;

public class Utility extends Activity {

    public static final String REQUIRED_FIELD_TITLE = "REQUIRED FIELD";
    private static final String MIN_PASSWORD_INVALID = "Minimum Required Length";
    private static final  int MIN_PASSWORD_LENGTH_VALUE = 8;

    public static final String NULL_OBJECT_DETECTED = "Null Object Detected";
    public static final String NULL_FIELD_MESSAGE = "Field validation failed. The passed model is null";
    public static final String CREATE_RECORD_SUCCESS_TITLE = "RECORD CREATED";
    public static final String UPDATE_RECORD_SUCCESS_TITLE = "RECORD UPDATED";
    public static final String CREATE_RECORD_SUCCESS_MSG = "Record created successfully!";
    public static final String UPDATE_RECORD_SUCCESS_MSG = "Record updated successfully!";
    public static final String CREATE_RECORD_FAILED_TITLE = "RECORD CREATION FAILED";
    public static final String CREATE_RECORD_FAILED_MSG = "Record NOT created successfully!";
    public static final String CREATE_RECORD_EMAIL_SUCCESS_MSG = "An email is sent to your inbox for verification";
    public static final String CREATE_RECORD_EMAIL_FAILURE_MSG = "We could not send you a verification link. You can request for verification later.";
    public static final String CREATE_VOLUNTEER_PROFILE_SUCCESS_MSG = "You have added Service Type successfully to your profile.";

    public static final String PASSWORD_RESET_TITLE = "PASSWORD RESET";
    public static final String INVALID_EMAIL_TITLE = "INVALID E-MAIL";
    public static final String INVALID_EMAIL_MSG = "Invalid e-mail address entered. The e-mail should be Gmail,  Yahoo, Hotmail, or Outlook";
    public static final String INVALID_PASSWORD_TITLE = "INVALID PASSWORD";
    public static final String INVALID_PASSWORD_LENGTH_MSG = "Password must be greater or equals to " + MIN_PASSWORD_LENGTH_VALUE + " characters long";
    public static final String INVALID_PASSWORD_UPPERCASE_MSG = "Password must contain at least one UPPERCASE character.";
    public static final String INVALID_PASSWORD_LOWERCASE_MSG = "Password must contain at least one LOWERCASE character.";
    public static final String INVALID_PASSWORD_NUMBER_SYMBOL_MSG = "Password must contain at least one NUMERIC and SYMBOL character.";
    public static final String INVALID_PASSWORD_ALL_MSG = "Password should at least " + MIN_PASSWORD_LENGTH_VALUE + " characters and must contain at least one UPPERCASE, LOWERCASE, NUMBER, and a SYMBOL";
    public static final String SELECT_SERVICE_TYPE = "Select a service type to offer";
    public static final String SELECT_DAYS_FOR_SERVICE_MSG = "Please select at least one or more DAYS FOR SERVICE PROVISION from the list";
    public static final String SELECT_TIMES_FOR_SERVICE_MSG = "Please select at least one or more TIME ON DAYS FOR SERVICE PROVISION from the list";
    public static final String SELECT_TIMES_FOR_CALLS_MSG = "Please select at least one or more TIME OF DAYS FOR CALLS from the list";
    public static final String SELECT_SERVICE_TYPE_MSG = "Please select a SERVICE TYPE from the list.";

    public static final String TEACH_USAGE_MOBILE_DEVICES = "To teach you how to use a mobile phone/tablet/laptop";
    public static final String TEACH_USAGE_WEB_APPS = "To teach you how to use mobile/web applications";
    public static final String WALK_WITH_U = "To walk with you in a park";
    public static final String PROVIDE_LIFT_TO_SOCIAL = "To provide lift to social event";
    public static final String ASSIST_WITH_HOUSE_CLEANING = "To assist you with house cleaning";
    public static final String ASSIST_WITH_HOUSE_MAINTENANCE = "To assist you with house maintenance (if certified in a trade (painting, electrical, plumbing, carpentry, etc.))";
    public static final String ASSIST_WITH_GARDENING = "To assist you with gardening";
    public static final String ASSIST_WITH_ERRANDS = "To assist you with errands";
    public static final String ASSIST_WITH_GROCERY_SHOPPING = "To do grocery shopping for you";
    public static final String PROVIDE_LIFT_TO_SHOP = "To provide lift to shops";
    public static final String TAKE_CARE_OF_PETS = "To take care of your pet(s) (walking dogs outside)";

    public static final int MODEL_ARRAY_LENGTH = 5;
    public static final int REQUEST_CODE = 1000;
    private static OpenActivity openActivity;

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
            Utility.showInformationDialog(REQUIRED_FIELD_TITLE, "Please enter your first name.", appCompatActivity);return false;
        }

        if (lastNameText.getText() == null || lastNameText.getText().toString().isEmpty()){
            Utility.showInformationDialog(REQUIRED_FIELD_TITLE, "Please enter your last name.", appCompatActivity);
            return  false;
        }

        if (emailText.getText() == null || emailText.getText().toString().isEmpty()){
            Utility.showInformationDialog(REQUIRED_FIELD_TITLE, "Please enter your e-mail address.", appCompatActivity);
            return false;
        }

        if (mobileNumberText.getText() == null || mobileNumberText.getText().toString().isEmpty()){
            Utility.showInformationDialog(REQUIRED_FIELD_TITLE, "Please enter your mobile number.", appCompatActivity);
            return false;
        }

        if (mobileNumberText.getText().toString().trim().length() != 10){
            Utility.showInformationDialog(REQUIRED_FIELD_TITLE, "Please enter a mobile number NOT less/greater than 10 characters", appCompatActivity);
            return false;
        }

        if (password.getText() == null || password.getText().toString().isEmpty()){
            Utility.showInformationDialog(REQUIRED_FIELD_TITLE, "Please enter your password.", appCompatActivity);return false;
        }

        if (!retypePassword.getText().toString().equals(password.getText().toString())){
            Utility.showInformationDialog(REQUIRED_FIELD_TITLE, "Your password do not match. Please try again", appCompatActivity);
            return false;
        }

        if (password.length() < MIN_PASSWORD_LENGTH_VALUE){
            Utility.showInformationDialog(MIN_PASSWORD_INVALID, INVALID_PASSWORD_LENGTH_MSG, appCompatActivity);
            return false;
        }

        /*if (retypePassword.length() < MIN_PASSWORD_LENGTH_VALUE){
            Utility.showInformationDialog(MIN_PASSWORD_INVALID, "Re-type password must be greater or equals to " + MIN_PASSWORD_LENGTH_VALUE + " characters long", appCompatActivity);
            return false;
        }*/

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

    public static boolean validateInputsOnCreateServiceType(AppCompatActivity appCompatActivity, EditText... editTexts ) {
        EditText serviceTypeNameText = editTexts[0];
        EditText serviceTypeText = editTexts[1];

        if (serviceTypeNameText.getText() == null || serviceTypeNameText.getText().toString().isEmpty()) {
            Utility.showInformationDialog(REQUIRED_FIELD_TITLE, "Please enter service type name.", appCompatActivity);
            return false;
        }

        if (serviceTypeText.getText() == null || serviceTypeText.getText().toString().isEmpty()) {
            Utility.showInformationDialog(REQUIRED_FIELD_TITLE, "Please enter service type description.", appCompatActivity);
            return false;
        }

        return true;
    }

    public static boolean validateInputsOnCreateVolunteerProfile(AppCompatActivity appCompatActivity, EditText description, Spinner... spinners) {
        String availableServiceSpinner = (String) spinners[0].getSelectedItem();
        String availableDaysSpinner = (String) spinners[1].getSelectedItem();
        String availableDaysTimeSpinner = (String) spinners[1].getSelectedItem();
        String availableDaysForCallsSpinner = (String) spinners[2].getSelectedItem();
        EditText serviceDescription = description;

        if (availableServiceSpinner == null || availableServiceSpinner.isEmpty()) {
            Utility.showInformationDialog(REQUIRED_FIELD_TITLE, "Please select an available service type", appCompatActivity);
            return false;
        }

        if (availableDaysSpinner == null || availableDaysSpinner.isEmpty()) {
            Utility.showInformationDialog(REQUIRED_FIELD_TITLE, "Please select your available day(s) for the service type", appCompatActivity);
            return false;
        }

        if (availableDaysTimeSpinner == null || availableDaysTimeSpinner.isEmpty()) {
            Utility.showInformationDialog(REQUIRED_FIELD_TITLE, "Please select your available time(s) for the service type", appCompatActivity);
            return false;
        }

        if (availableDaysForCallsSpinner == null || availableDaysForCallsSpinner.isEmpty()) {
            Utility.showInformationDialog(REQUIRED_FIELD_TITLE, "Please select your available time(s) for calls", appCompatActivity);
            return false;
        }

        if (serviceDescription.getText() == null || serviceDescription.getText().toString().isEmpty()) {
            Utility.showInformationDialog(REQUIRED_FIELD_TITLE, "Please enter service type description.", appCompatActivity);
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
    public static FirebaseAuth getFirebaseAuthenticationInstance(){
        return FirebaseAuth.getInstance();
    }

    /***
     * Gets the instance of the FirebaseStore cloud database
     * @return
     */
    public static FirebaseFirestore getFirebaseFireStoreInstance(){
        return FirebaseFirestore.getInstance();
    }

    /***
     * Gets the reference of the Firebase Storage for uploading and retrieving images
     * @return
     */
    public static StorageReference getFirebaseStorageReference(){
        //FirebaseStorage firebaseStorage = getFirebaseStorageInstance();
        //StorageReference storageReference = firebaseStorage.getReference();
        return FirebaseStorage.getInstance().getReference();
    }

    public static FirebaseStorage getFirebaseStorageInstance(){
        return FirebaseStorage.getInstance();
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
                //return;
            }
        });

        // create the alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show the alert dialog
        alertDialog.show();
    }

    /*public static void showInformationDialogOnCreateRecord(String _msgTitle, String _msgContent, final AppCompatActivity _appCompatActivity){

        openActivity = new OpenActivity();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(_appCompatActivity);

        // set dialog title
        alertDialogBuilder.setTitle(_msgTitle);

        // set dialog message
        alertDialogBuilder.setMessage(_msgContent);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //openActivity.openAnActivityScreen(_appCompatActivity, _destinationActivity);
                //return;
            }
        });

        // create the alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show the alert dialog
        alertDialog.show();
    }*/

    /***
     * Show a message to the user if the account e-mail is not verified. It gives the Yes and No options
     * to the User and when yes is clicked, a verification e-mail is resent else prevents the user from accessing the app
     * @param _appCompactActivity
     */
    public static void promptUserBeforeEmailResetLinkIsSent(final AppCompatActivity _appCompactActivity){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(_appCompactActivity);
        final ElderlyVolunteerCRUDHelper crudHelper = new ElderlyVolunteerCRUDHelper();

        // set dialog title
        alertDialogBuilder.setTitle("ACCOUNT NOT VERIFIED");

        // set dialog message
        alertDialogBuilder.setMessage("Your account or e-mail is not yet verified. Do you want to resend the verification code or link now?");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Yes!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // send verification e-mail if the user clicks on yes
                crudHelper.reSendVerificationEmail(_appCompactActivity);
                return;
            }
        });
        alertDialogBuilder.setNegativeButton("No",(new DialogInterface.OnClickListener() {
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
        final ElderlyVolunteerCRUDHelper crudHelper = new ElderlyVolunteerCRUDHelper();

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

    public static boolean isEmailAddressValid(CharSequence _emailAddress){

        return !TextUtils.isEmpty(String.valueOf(_emailAddress)) && Patterns.EMAIL_ADDRESS.matcher(String.valueOf(_emailAddress)).matches();
    }

    public static boolean isPasswordLengthValid(CharSequence data) {
        return String.valueOf(data).length() >= MIN_PASSWORD_LENGTH_VALUE;
    }

    public static boolean isPasswordHavingNumberAndSymbol(CharSequence data) {
        String password = String.valueOf(data);
        return !password.matches("[A-Za-z0-9 ]*");
    }

    public static boolean isPasswordHavingUpperCase(CharSequence data) {
        String password = String.valueOf(data);
        return !password.equals(password.toLowerCase());
    }

    public static boolean isPasswordHavingLowerCase(CharSequence data) {
        String password = String.valueOf(data);
        return !password.equals(password.toUpperCase());
    }

    public static String getUUID(){
        return UUID.randomUUID().toString();
    }
}
