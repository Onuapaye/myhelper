package com.martin.myhelper.views;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.martin.myhelper.R;
import com.martin.myhelper.helpers.ElderlyVolunteerCRUDHelper;
import com.martin.myhelper.helpers.OpenActivity;
import com.martin.myhelper.helpers.Utility;
import com.martin.myhelper.model.GenericModel;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import static com.martin.myhelper.helpers.Utility.CREATE_RECORD_EMAIL_SUCCESS_MSG;
import static com.martin.myhelper.helpers.Utility.CREATE_RECORD_SUCCESS_MSG;
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

    private ElderlyVolunteerCRUDHelper crudHelper;
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
        this.createFiresStoreUserRecord();

        // monitor the text change event of the email field
        this.validateEmailOnEditTextChange();

        // monitor the text change event of the password field
        this.validatePasswordOnEditTextChange();

        // imageViewOnClick event
        this.monitorOnClickEventForImageView();
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

    private void createFiresStoreUserRecord(){

        button = findViewById(R.id.confirmButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // set values for validation
                setFieldValues();

                //  validate fields
                boolean validationSucceeded = Utility.validateInputsOnCreate(appCompatActivity, firstName, lastName, email, mobileNumber, password, retypePassword);

                if ( !validationSucceeded ) {
                    return;
                } else {

                    // create an array to hold the data
                    String[] modelArray = new String[]{
                        firstName.getText().toString(),
                        lastName.getText().toString(),
                        email.getText().toString(),
                        mobileNumber.getText().toString(),
                        password.getText().toString(),
                        String.valueOf(userType),
                        String.valueOf(imageUri),
                        imageExtension = getFileExtension(imageUri)
                    };

                   // call method to create the record for the volunteer
                   crudHelper = new ElderlyVolunteerCRUDHelper();
                   crudHelper.createUserRecord(appCompatActivity, modelArray);

                    // redirect to login activity
                    intent = new Intent(context, LoginActivity.class);
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
                        //email.getFocusable();
                        email.setFocusable(true);
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

                        if (!Utility.isPasswordLengthValid(password.getText().toString().trim())){

                            // show message that password length is invalid
                            Utility.showInformationDialog(Utility.INVALID_PASSWORD_TITLE, Utility.INVALID_PASSWORD_LENGTH_MSG, appCompatActivity);
                            password.getFocusable();
                            return;
                        }

                        if (!Utility.isPasswordHavingNumberAndSymbol(password.getText().toString().trim())){

                            // show message that password is not containing a number or symbol
                            Utility.showInformationDialog(Utility.INVALID_PASSWORD_TITLE, Utility.INVALID_PASSWORD_NUMBER_SYMBOL_MSG, appCompatActivity);
                            password.getFocusable();
                            return;
                        }

                        if (!Utility.isPasswordHavingLowerCase(password.getText().toString().trim())){

                            // show a message that password does not contain an upper case
                            Utility.showInformationDialog(Utility.INVALID_PASSWORD_TITLE, Utility.INVALID_PASSWORD_LOWERCASE_MSG, appCompatActivity);
                            password.getFocusable();
                            return;
                        }

                        if (!Utility.isPasswordHavingUpperCase(password.getText().toString().trim())) {

                            // show a message that password does not contain a lower case
                            Utility.showInformationDialog(Utility.INVALID_PASSWORD_TITLE, Utility.INVALID_PASSWORD_UPPERCASE_MSG, appCompatActivity);
                            password.getFocusable();
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

        profileImage = (CircularImageView) findViewById(R.id.profileImage);
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
            Log.d("URI_MAIN", data.getData().toString());
            imageUri = data.getData();

            Log.d("URI_MAIN2", imageUri.toString());

            // show image into the imageView using Picasso or direct into the image view
            Picasso.get().load(this.imageUri).into(profileImage);
            //profileImage.setImageURI(profileImageUri);
        }
    }


    // Get values from some EditTexts and assign or sets their values to
    // other variables
    private void setFieldValues(){
        firstName = (EditText) findViewById(R.id.firstName);
        lastName = (EditText) findViewById(R.id.lastName);
        email = (EditText) findViewById(R.id.email);
        mobileNumber = (EditText) findViewById(R.id.mobileNumber);
        password = (EditText) findViewById(R.id.password);
        retypePassword = (EditText) findViewById(R.id.retypePassword);
        userType = GenericModel.USER_TYPE_VOLUNTEER;
        profileImage = (CircularImageView) findViewById(R.id.profileImage);
    }

}