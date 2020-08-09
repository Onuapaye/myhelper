package com.martin.myhelper.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.martin.myhelper.R;
import com.martin.myhelper.helpers.FirebaseDatabaseCRUDHelper;
import com.martin.myhelper.helpers.Utility;
import com.martin.myhelper.model.ElderlyModel;

public class ElderlyLoginActivity extends AppCompatActivity {

    private TextView registerLinkTextView;
    private EditText email, password;
    private Button _loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elderly_login);

        // monitor onclick event of a text to open elderly registration scree
        this.openElderlyRegistrationScreen();

        // login to elderly homepage
        this.loginUserToFireStore();
    }

    /**
     * Open the elderly registration screen when clicked
     */
    private void openElderlyRegistrationScreen(){

        registerLinkTextView = (TextView) findViewById(R.id.registerLink);

        registerLinkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent intent = new Intent(ElderlyLoginActivity.this, ElderlyRegistrationActivity.class);
            startActivity(intent);
            }
        });
    }

    private void loginUserToFireStore(){

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);

        /*ElderlyModel elderlyModel = new ElderlyModel();
        elderlyModel.setEmail(email.getText().toString());
        elderlyModel.setPassword(password.getText().toString());*/

        Log.d("EMAIL", email.getText().toString());
        _loginButton = (Button) findViewById(R.id.loginButton);
        _loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                boolean loginValidationPassed = Utility.validateInputsOnUserLogin(ElderlyLoginActivity.this,
                        email.getText().toString(), password.getText().toString());

                if(loginValidationPassed){
                    //Utility.showInformationDialog("VALIDATION FAILED", "Validation failed and you can't continue login.", ElderlyLoginActivity.this);
                //} else {
                    FirebaseDatabaseCRUDHelper crudHelper = new FirebaseDatabaseCRUDHelper();
                    crudHelper.loginFireStoreUser(ElderlyHomeActivity.class, ElderlyLoginActivity.this,
                            ElderlyLoginActivity.this, email.getText().toString(), password.getText().toString());
                }
            }
        });
    }

    /*private void setLoginFields(){
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);

        ElderlyModel elderlyModel = new ElderlyModel();
        elderlyModel.setEmail(email.getText().toString());
        elderlyModel.setPassword(password.getText().toString());
    }*/
}