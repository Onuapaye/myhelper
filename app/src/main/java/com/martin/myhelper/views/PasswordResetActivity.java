package com.martin.myhelper.views;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.martin.myhelper.MainActivity;
import com.martin.myhelper.R;
import com.martin.myhelper.helpers.Utility;

public class PasswordResetActivity extends AppCompatActivity {

    private TextView email;
    private AppCompatActivity appCompatActivity = PasswordResetActivity.this;
    private FirebaseAuth firebaseAuth;
    private int userType;
    private Button confirmResetButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        this.validateEmailOnEditTextChange();

        this.handleRequestPasswordResetButtonOnClick();
    }

    private void handleRequestPasswordResetButtonOnClick(){
        firebaseAuth = Utility.getFirebaseAuthenticationInstance();

        confirmResetButton = findViewById(R.id.btnPasswordReset);
        email = findViewById(R.id.email);

        Intent _intent = getIntent();
        _intent.getExtras();

        if (_intent.hasExtra("userType")){
            userType = _intent.getIntExtra("userType", 0);
        }

        confirmResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (email.getText().toString() == "" || email.getText().toString().isEmpty()) {
                    Utility.showInformationDialog(Utility.REQUIRED_FIELD_TITLE, "E-mail field cannot be empty.", PasswordResetActivity.this);
                    return;
                }

                firebaseAuth.sendPasswordResetEmail(email.getText().toString().trim()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // sign the user out but first check if the user has signed in or not
                        if (firebaseAuth.getCurrentUser() != null ) {
                            firebaseAuth.signOut();
                        }

                        // redirect user to login
                        Intent intent = new Intent(PasswordResetActivity.this, MainActivity.class);
                        intent.putExtra("userType", userType);
                        intent.putExtra("passwordResetSuccess", "You have successfully requested for a Password Reset.\n Please check your inbox for the RESET LINK");
                        startActivity(intent);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Utility.showInformationDialog("RESET PASSWORD", "Error sending PASSWORD RESET link. " + e.getMessage(), appCompatActivity);
                    }
                });
            }
        });
    }

    private void validateEmailOnEditTextChange(){

        email = findViewById(R.id.email);
        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
            if (!hasFocus){
                if (!Utility.isEmailAddressValid(email.getText().toString().trim())){
                    Utility.showInformationDialog(Utility.INVALID_EMAIL_TITLE, Utility.INVALID_EMAIL_MSG, appCompatActivity);
                    email.getFocusable();
                    return;
                }
            }
            }
        });
    }

}