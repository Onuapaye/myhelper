package com.martin.myhelper.helpers;

import android.app.Activity;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import com.martin.myhelper.model.VolunteerModel;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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


    public void createVolunteerUserRecord(final AppCompatActivity appCompatActivity, final VolunteerModel volunteerModel, final Uri profileImageUri){

        // create the elderly record after the user account is created
        firebaseAuth = Utility.getFirebaseAuthenticationInstance();

        // 1. create the user account via firebase auth if not null
        firebaseAuth.createUserWithEmailAndPassword(volunteerModel.getEmail(), volunteerModel.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    // set the model user id
                    firebaseUser = firebaseAuth.getCurrentUser();
                    volunteerModel.setId(firebaseUser.getUid());

                    // 2. create the image record for the volunteer
                    storageReference = FirebaseStorage.getInstance().getReference("images");
                    StorageReference fileReference = storageReference.child(firebaseUser.getUid() + "." + volunteerModel.getImageType());

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

                            modelMap.put("id", volunteerModel.getId());
                            modelMap.put("firstName",volunteerModel.getFirstName());
                            modelMap.put("lastName", volunteerModel.getLastName());
                            modelMap.put("email", volunteerModel.getEmail());
                            modelMap.put("mobileNumber", volunteerModel.getMobileNumber());
                            modelMap.put("userType",volunteerModel.getUserType());
                            modelMap.put("imageType", volunteerModel.getImageType());
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
                                    });

                                    Utility.showInformationDialog(CREATE_RECORD_SUCCESS_TITLE,
                                            CREATE_RECORD_SUCCESS_MSG + "\n" + CREATE_RECORD_EMAIL_SUCCESS_MSG, appCompatActivity);
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


    public void createVolunteerServiceProfile(final AppCompatActivity appCompatActivity, final VolunteerModel volunteerModel){

            // create an instance of the DocumentReference class of FirebaseStore
            firebaseAuth = Utility.getFirebaseAuthenticationInstance();
            firebaseUser = firebaseAuth.getCurrentUser();
            firebaseFirestore = Utility.getFirebaseFireStoreInstance();

            final String profileID = generateProfileId();

            // create a hash map of the object to be stored
            Map<String, Object> modelMap = new HashMap<>();
            modelMap.put("id", profileID);
            modelMap.put("volunteerId", volunteerModel.getId());
            modelMap.put("serviceTypeId", volunteerModel.getServiceTypeId());
            modelMap.put("daysForService", volunteerModel.getDaysForService());
            modelMap.put("timesForService", volunteerModel.getTimesForService());
            modelMap.put("timesForCalls", volunteerModel.getTimesForCalls());
            modelMap.put("description", volunteerModel.getDescriptionOfService());
            modelMap.put("createdAt", FieldValue.serverTimestamp());
            modelMap.put("updatedAt", FieldValue.serverTimestamp());

            DocumentReference documentReference = firebaseFirestore.collection("volunteer_profiles")
                                                  .document(firebaseAuth.getCurrentUser().getUid()).collection("profiles").document(profileID);

            documentReference.set(modelMap).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Utility.showInformationDialog(CREATE_RECORD_FAILED_TITLE, CREATE_RECORD_FAILED_MSG + e.getMessage(), appCompatActivity);
                    return;
                }
            });

    }

    public void updateVolunteerServiceProfile(final AppCompatActivity appCompatActivity, final VolunteerModel volunteerModel){

        // create an instance of the DocumentReference class of FirebaseStore
        firebaseAuth = Utility.getFirebaseAuthenticationInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = Utility.getFirebaseFireStoreInstance();

        // create a hash map of the object to be stored
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("serviceTypeId", volunteerModel.getServiceTypeId());
        modelMap.put("daysForService", volunteerModel.getDaysForService());
        modelMap.put("timesForService", volunteerModel.getTimesForService());
        modelMap.put("timesForCalls", volunteerModel.getTimesForCalls());
        modelMap.put("description", volunteerModel.getDescriptionOfService());
        modelMap.put("updatedAt", FieldValue.serverTimestamp());

        DocumentReference documentReference = firebaseFirestore.collection("volunteer_profiles")
                                                               .document(firebaseAuth.getCurrentUser().getUid()).collection("profiles")
                                                               .document(volunteerModel.getProfileId());

        // documentReference.set(modelMap, SetOptions.merge());
        documentReference.update(modelMap).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Utility.showInformationDialog(CREATE_RECORD_FAILED_TITLE, CREATE_RECORD_FAILED_MSG + e.getMessage(), appCompatActivity);
                return;
            }
        });

    }

    public void deleteVolunteerServiceProfile(final AppCompatActivity appCompatActivity, final String profileID){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(appCompatActivity);
        alertDialogBuilder.setTitle("DELETE RECORD?");
        alertDialogBuilder.setMessage("Are you sure to DELETE the record?");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                // create an instance of the DocumentReference class of FirebaseStore
                firebaseAuth = Utility.getFirebaseAuthenticationInstance();
                firebaseUser = firebaseAuth.getCurrentUser();
                firebaseFirestore = Utility.getFirebaseFireStoreInstance();

                DocumentReference documentReference = firebaseFirestore.collection("volunteer_profiles")
                        .document(firebaseAuth.getCurrentUser().getUid()).collection("profiles")
                        .document(profileID);

                // documentReference.set(modelMap, SetOptions.merge());
                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Utility.showInformationDialog("RECORD DELETED", "You have successfully deleted a record from your profile", appCompatActivity);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Utility.showInformationDialog(CREATE_RECORD_FAILED_TITLE, CREATE_RECORD_FAILED_MSG + e.getMessage(), appCompatActivity);
                        return;
                    }
                });
            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Utility.showInformationDialog("RECORD NOT DELETED", "You have cancelled the record DELETE action successfully", appCompatActivity);
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private String generateProfileId(){
        return UUID.randomUUID().toString();
    }
}
