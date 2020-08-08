package com.martin.myhelper.helpers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.martin.myhelper.model.ElderlyModel;
import com.martin.myhelper.views.ElderlyLoginActivity;

public class FirebaseDatabaseCRUDHelper {

    // create a record
    public void createRecord(final AppCompatActivity appCompatActivity, final DatabaseReference modelDatabaseReference, final ElderlyModel elderlyModel){

        //check if the object passed is an elderly model type
        if (elderlyModel == null){
            Utility.showInfoDialog(appCompatActivity, "ERROR: Field validation failed. Elderly is null");
            return;
        } else {

            // push data to Firebase table or child called Elders
            modelDatabaseReference.child("elders").push().setValue(elderlyModel).addOnCompleteListener( new OnCompleteListener<Void>(){
                    @Override
                    public void onComplete(@NonNull Task<Void> task){
                        if (task.isSuccessful()){
                            OpenActivity.openAnActivityScreen(appCompatActivity, ElderlyLoginActivity.class);
                            Utility.showMessage(appCompatActivity, "INFO! Elderly record created successfully.");
                        } else {
                            Utility.showInfoDialog(appCompatActivity, "Error: " + task.getException().getMessage());
                        }
                    }
                }
            );
        }
    }

    public void selectRecord(final AppCompatActivity appCompatActivity, DatabaseReference modelDatabaseReference){}
}
