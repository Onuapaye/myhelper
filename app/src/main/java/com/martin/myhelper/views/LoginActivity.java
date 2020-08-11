package com.martin.myhelper.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.martin.myhelper.R;
import com.martin.myhelper.helpers.FirebaseDatabaseCRUDHelper;
import com.martin.myhelper.helpers.Utility;
import com.martin.myhelper.model.GenericModel;

import static com.martin.myhelper.helpers.Utility.CREATE_RECORD_EMAIL_SUCCESS_MSG;
import static com.martin.myhelper.helpers.Utility.CREATE_RECORD_SUCCESS_MSG;
import static com.martin.myhelper.helpers.Utility.CREATE_RECORD_SUCCESS_TITLE;

public class LoginActivity extends AppCompatActivity {

    private TextView registerLinkTextView;
    private EditText email, password;
    private Button _loginButton;

    private FirebaseDatabaseCRUDHelper crudHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // monitor onclick event of a text to open elderly registration scree
        this.openElderlyRegistrationScreen();

        // login to elderly homepage
        this.loginUserToFireStore();

        // show message for user record creation
        this.checkAndShowUserCreationSuccessMessage();
    }

    /**
     * Open the elderly registration screen when clicked
     */
    private void openElderlyRegistrationScreen(){

        registerLinkTextView = findViewById(R.id.registerLink);

        registerLinkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent intent = new Intent(LoginActivity.this, ElderlyRegistrationActivity.class);
            startActivity(intent);
            }
        });
    }

    private void loginUserToFireStore(){

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        _loginButton = findViewById(R.id.loginButton);

        _loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //crudHelper = new FirebaseDatabaseCRUDHelper();
                //crudHelper.getUserType(GenericModel.ELDERS);
                //crudHelper.getCurrentUserID();
                //boolean loginValidationPassed = Utility.validateInputsOnUserLogin(LoginActivity.this,
                 //       email.getText().toString(), password.getText().toString());


                //if(loginValidationPassed){

                    crudHelper = new FirebaseDatabaseCRUDHelper();
                    crudHelper.loginFireStoreUser(LoginActivity.this,
                            ElderlyHomeActivity.class, LoginActivity.this, email.getText().toString(), password.getText().toString());

                if (GenericModel.GLOBAL_USERTYPE == GenericModel.USER_TYPE_ELDER){

                }
                System.out.println("----------->>> " + GenericModel.GLOBAL_USERTYPE);
            }
        });
    }

    private void checkAndShowUserCreationSuccessMessage(){
        Intent intent = getIntent();
        intent.getExtras();

        if (intent.hasExtra("recordCreated")){
            // show a message for successful record recreation
            Utility.showInformationDialog(CREATE_RECORD_SUCCESS_TITLE,
                    CREATE_RECORD_SUCCESS_MSG + "\n" + CREATE_RECORD_EMAIL_SUCCESS_MSG, LoginActivity.this);
        }

        if (intent.hasExtra("loginPageHeaderTitle")){
            TextView textView = findViewById(R.id.pageHeaderTitle);
            textView.setText(intent.getStringExtra("loginPageHeaderTitle"));
        }
    }

    /*private void setLoginFields(){
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);

        ElderlyModel elderlyModel = new ElderlyModel();
        elderlyModel.setEmail(email.getText().toString());
        elderlyModel.setPassword(password.getText().toString());
    }*/
}