package com.martin.myhelper.helpers;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.martin.myhelper.views.LoginActivity;

import java.util.HashMap;
import java.util.Map;

import static com.martin.myhelper.helpers.Utility.CREATE_RECORD_EMAIL_SUCCESS_MSG;
import static com.martin.myhelper.helpers.Utility.CREATE_RECORD_FAILED_MSG;
import static com.martin.myhelper.helpers.Utility.CREATE_RECORD_FAILED_TITLE;
import static com.martin.myhelper.helpers.Utility.CREATE_RECORD_SUCCESS_MSG;
import static com.martin.myhelper.helpers.Utility.CREATE_RECORD_SUCCESS_TITLE;

public class VolunteerCRUDHelper extends Activity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;

    private Utility utility;

    public void createVolunteerUserRecord(final AppCompatActivity appCompatActivity, final String[] _modelArray, final Uri profileImageUri){

        // create the elderly record after the user account is created
        firebaseAuth = Utility.getFirebaseAuthenticationInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        // 1. create the user account via firebase auth if not null
        firebaseAuth.createUserWithEmailAndPassword(_modelArray[2], _modelArray[4]).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    // 2. create the image record for the volunteer
                    storageReference = FirebaseStorage.getInstance().getReference("images");
                    StorageReference fileReference = storageReference.child(firebaseUser.getUid() + "." + _modelArray[6]);

                    // upload the image file into FireStorage
                    fileReference.putFile(profileImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //VolunteerRegistrationActivity.progressBar.setProgress(0);
                                }
                            }, 500);

                            // create a hash map of the object to be stored
                            Map<String, Object> modelMap = new HashMap<>();

                            modelMap.put("userID", firebaseUser.getUid());
                            modelMap.put("firstName", _modelArray[0]);
                            modelMap.put("lastName", _modelArray[1]);
                            modelMap.put("email", _modelArray[2]);
                            modelMap.put("mobileNumber", _modelArray[3]);
                            modelMap.put("userType", _modelArray[5]);
                            modelMap.put("createdAt", FieldValue.serverTimestamp());
                            modelMap.put("updatedAt", FieldValue.serverTimestamp());
                            modelMap.put("profilePhotoUrl", taskSnapshot.getUploadSessionUri().toString());// set the image path from the taskSnapshot

                            // 3. create the volunteer record including adding the profileUrl after saving the image
                            // create an instance of the DocumentReference class of FirebaseStore
                            firebaseFirestore = Utility.getFirebaseFireStoreInstance();
                            DocumentReference documentReference = firebaseFirestore.collection("volunteers").document(firebaseUser.getUid());
                            documentReference.set(modelMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    // send an e-mail for verification
                                    firebaseUser.sendEmailVerification().addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Utility.showInformationDialog("E-MAIL NOT DELIVERED",
                                                    Utility.CREATE_RECORD_EMAIL_FAILURE_MSG, appCompatActivity);
                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Utility.showInformationDialog(CREATE_RECORD_SUCCESS_TITLE, CREATE_RECORD_SUCCESS_MSG + "\n" + CREATE_RECORD_EMAIL_SUCCESS_MSG, appCompatActivity);

                                            Intent intent = new Intent(appCompatActivity, LoginActivity.class);
                                            intent.putExtra("recordCreated", CREATE_RECORD_SUCCESS_MSG + "\n" + CREATE_RECORD_EMAIL_SUCCESS_MSG);
                                            intent.putExtra("loginPageHeaderTitle", "VOLUNTEER");
                                            startActivity(intent);
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Utility.showInformationDialog(CREATE_RECORD_FAILED_TITLE, CREATE_RECORD_FAILED_MSG + e.getMessage(), appCompatActivity);
                                    return;
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // show error message when it fails to save the image
                            Utility.showInformationDialog("IMAGE UPLOAD ERROR", "Your photograph is not uploaded due to error." + e.getMessage(), appCompatActivity);
                            return;
                        }
                    });/*.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            progressBar  = findViewById(R.id.volunteerRegistrationProgressBar);
                            progressBar.setVisibility(View.VISIBLE);
                            double progressValue = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressBar.setProgress((int) progressValue);
                        }
                    });*/

                } else {
                    Utility.showInformationDialog("ERROR!", task.getException().getMessage(), appCompatActivity);
                    return;
                }
            }
        });
    }

}
