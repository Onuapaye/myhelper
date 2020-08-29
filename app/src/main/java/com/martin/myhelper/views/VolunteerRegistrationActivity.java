package com.martin.myhelper.views;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.martin.myhelper.MainActivity;
import com.martin.myhelper.R;
import com.martin.myhelper.helpers.OpenActivity;
import com.martin.myhelper.helpers.Utility;
import com.martin.myhelper.helpers.VolunteerCRUDHelper;
import com.martin.myhelper.model.GenericModel;
import com.martin.myhelper.model.VolunteerModel;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import static com.martin.myhelper.helpers.Utility.CREATE_RECORD_EMAIL_SUCCESS_MSG;
import static com.martin.myhelper.helpers.Utility.CREATE_RECORD_SUCCESS_MSG;
import static com.martin.myhelper.helpers.Utility.REQUIRED_FIELD_TITLE;
import static com.martin.myhelper.model.GenericModel.PICK_IMAGE_REQUEST;

public class VolunteerRegistrationActivity extends AppCompatActivity {

    // global variables
    private EditText firstName, lastName, email, mobileNumber, password, retypePassword;
    private CircularImageView profileImage;
    private Uri imageUri;
    private String imageExtension;

    public static ProgressBar progressBar;

    private int userType;
    private Context context = VolunteerRegistrationActivity.this;
    private AppCompatActivity appCompatActivity = VolunteerRegistrationActivity.this;

    private VolunteerCRUDHelper crudHelper;
    private StorageReference storageReference;
    public static StorageTask storageTask;

    private Button button;

    private Intent intent;
    private OpenActivity openActivity;
    private Utility utility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_registration);

        // open the login activity screen
        this.openLoginScreen();

        // create the volunteer record
        this.createVolunteerRegistration();

        // monitor the text change event of the email field
        this.validateEmailOnEditTextChange();

        // monitor the text change event of the password field
        this.validatePasswordOnEditTextChange();

        // imageViewOnClick event
        this.monitorOnClickEventForImageView();

        this.validateOtherInputsOnEditTextChange();
    }

    /***
     * Open the elderly login screen when clicked
     */
    private void openLoginScreen(){
        openActivity = new OpenActivity();
        TextView textView = (TextView) findViewById(R.id.loginHereLink);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(context, LoginActivity.class);
                intent.putExtra("loginPageHeaderTitle", "VOLUNTEER");
                intent.putExtra("userType", GenericModel.USER_TYPE_VOLUNTEER);
                startActivity(intent);
            }
        });
    }

    private void createVolunteerRegistration(){
        button = findViewById(R.id.confirmButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // set values for validation
                setFieldValues();

                //  validate fields
                boolean validationSucceeded = Utility.validateInputsOnCreate(appCompatActivity, firstName,
                        lastName, email, mobileNumber, password, retypePassword);

                if ( !validationSucceeded ) {
                    return;
                } else if (imageUri == null){
                    Utility.showInformationDialog("IMAGE VALIDATION FAILED",
                            "Please upload your image", appCompatActivity);
                    return;
                } else {

                    // create an array to hold the data
                    VolunteerModel model = new VolunteerModel();
                    model.setFirstName(firstName.getText().toString());
                    model.setLastName(lastName.getText().toString());
                    model.setEmail(email.getText().toString());
                    model.setMobileNumber(mobileNumber.getText().toString());
                    model.setPassword(password.getText().toString());
                    model.setUserType(userType);
                    model.setImageType(getFileExtension(imageUri));

                    // call method to create the record for the volunteer
                    crudHelper = new VolunteerCRUDHelper();
                    crudHelper.createVolunteerUserRecord(appCompatActivity, model, imageUri);

                    // redirect to login activity
                    intent = new Intent(context, MainActivity.class);
                    intent.putExtra("recordCreated", CREATE_RECORD_SUCCESS_MSG + "\n" + CREATE_RECORD_EMAIL_SUCCESS_MSG);
                    intent.putExtra("loginPageHeaderTitle", "VOLUNTEER");
                    startActivity(intent);
                }
            }
        });
    }

    private void validateEmailOnEditTextChange(){

        email = findViewById(R.id.email);
        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus){
                    if (!Utility.isEmailAddressValid(email.getText().toString().trim())){
                        Utility.showInformationDialog(Utility.INVALID_EMAIL_TITLE, Utility.INVALID_EMAIL_MSG, appCompatActivity);
                        //email.requestFocus();
                        return;
                    }
                }
            }
        });
    }

    private void validatePasswordOnEditTextChange(){

        password = findViewById(R.id.password);
        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus){
                    if (Integer.parseInt(String.valueOf(password.getText().toString().trim().length())) > 0){

                        if (!Utility.isPasswordLengthValid(password.getText().toString().trim())
                                || !Utility.isPasswordHavingNumberAndSymbol(password.getText().toString().trim())
                                || !Utility.isPasswordHavingLowerCase(password.getText().toString().trim())
                                || !Utility.isPasswordHavingUpperCase(password.getText().toString().trim()) ){

                            Utility.showInformationDialog(Utility.INVALID_PASSWORD_TITLE, Utility.INVALID_PASSWORD_ALL_MSG, appCompatActivity);
                            return;
                        }

                    }
                }
            }
        });
    }

    private void validateOtherInputsOnEditTextChange(){
        this.setFieldValues();

        // first name
        firstName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    if (firstName.getText() == null || firstName.getText().toString().isEmpty()) {
                        Utility.showInformationDialog(REQUIRED_FIELD_TITLE, "Please enter your first name.", appCompatActivity);
                        //firstName.requestFocus();
                        return;
                    }
                }
            }
        });

        // last name
        lastName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(!hasFocus) {
                    if (lastName.getText() == null || lastName.getText().toString().isEmpty()) {
                        Utility.showInformationDialog(REQUIRED_FIELD_TITLE, "Please enter your last name.", appCompatActivity);
                        //lastName.requestFocus();
                        return;
                    }
                }
            }
        });

        // email

        // mobile number
        mobileNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    if (mobileNumber.getText() == null || mobileNumber.getText().toString().isEmpty()) {
                        Utility.showInformationDialog(REQUIRED_FIELD_TITLE, "Please enter your mobile number.", appCompatActivity);
                        //mobileNumber.requestFocus();
                        return;
                    }

                    if (mobileNumber.getText().toString().trim().length() != 10) {
                        Utility.showInformationDialog(REQUIRED_FIELD_TITLE, "Please enter a mobile number NOT less/greater than 10 characters", appCompatActivity);
                        //mobileNumber.requestFocus();
                        return;
                    }
                }
            }
        });

        // password
        retypePassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus){
                    if (Integer.parseInt(String.valueOf(retypePassword.getText().toString().trim().length())) > 0){

                        if (!Utility.isPasswordLengthValid(retypePassword.getText().toString().trim())
                                || !Utility.isPasswordHavingNumberAndSymbol(retypePassword.getText().toString().trim())
                                || !Utility.isPasswordHavingLowerCase(retypePassword.getText().toString().trim())
                                || !Utility.isPasswordHavingUpperCase(retypePassword.getText().toString().trim()) ){

                            Utility.showInformationDialog(Utility.INVALID_PASSWORD_TITLE,
                                    Utility.INVALID_REPASSWORD_ALL_MSG, appCompatActivity);
                            //retypePassword.requestFocus();
                            return;
                        }
                    }
                }

            }
        });
    }

    // choose an image from the app
    public void opeAndChooseImageFile(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // get the file extension from the uploaded file
    public String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    //private val selectedImageUri: Uri? = null;
    private void monitorOnClickEventForImageView(){

        profileImage = findViewById(R.id.profileImage);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                opeAndChooseImageFile();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GenericModel.PICK_IMAGE_REQUEST && resultCode == RESULT_OK
            && data != null && data.getData() != null){

            // set the image uri or path
            imageUri = data.getData();

            // show image into the imageView using Picasso or direct into the image view
            Picasso.get().load(this.imageUri).into(profileImage);
            //profileImage.setImageURI(profileImageUri);
        }
    }


    // Get values from some EditTexts and assign or sets their values to
    // other variables
    private void setFieldValues(){
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.email);
        mobileNumber = findViewById(R.id.mobileNumber);
        password = findViewById(R.id.password);
        retypePassword = findViewById(R.id.retypePassword);
        userType = GenericModel.USER_TYPE_VOLUNTEER;
        profileImage = findViewById(R.id.profileImage);
    }

}