package com.martin.myhelper.helpers;

import android.util.Log;

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
            Utility.showInformationDialog("Null Object Detected", "Field validation failed. The passed model is null", appCompatActivity);
//            Utility.showInfoDialog(appCompatActivity, "ERROR: Field validation failed. Elderly is null");
            return;
        } else {

            // push data to Firebase table or child called Elders
            modelDatabaseReference.child("elders").push().setValue(elderlyModel).addOnCompleteListener( new OnCompleteListener<Void>(){
                    @Override
                    public void onComplete(@NonNull Task<Void> task){
                        if (task.isSuccessful()){
                            OpenActivity.openAnActivityScreen(appCompatActivity, ElderlyLoginActivity.class);
                            Utility.showInformationDialog("Record Created", "Elderly record created successfully", appCompatActivity);
                        } else {
                            Log.e( "Create Elder", task.getException().getMessage());
                            Utility.showInformationDialog("Create Record Error", task.getException().getMessage().toString(), appCompatActivity);
                        }
                    }
                }
            );
        }
    }

    public void selectRecord(final AppCompatActivity appCompatActivity, DatabaseReference modelDatabaseReference){}
}
