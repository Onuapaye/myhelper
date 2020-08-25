package com.martin.myhelper.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

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
import com.google.firebase.storage.StorageReference;
import com.martin.myhelper.model.ElderlyModel;
import com.martin.myhelper.model.GenericModel;
import com.martin.myhelper.model.ServiceTypeModel;
import com.martin.myhelper.model.VolunteerModel;
import com.martin.myhelper.views.ElderlyHomeActivity;
import com.martin.myhelper.views.ElderlyProvideFeedBackActivity;
import com.martin.myhelper.views.ElderlyRegistrationActivity;
import com.martin.myhelper.views.LoginActivity;
import com.martin.myhelper.views.VolunteerHomeActivity;
import com.martin.myhelper.views.VolunteerProfileCreateActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.martin.myhelper.helpers.Utility.CREATE_RECORD_EMAIL_SUCCESS_MSG;
import static com.martin.myhelper.helpers.Utility.CREATE_RECORD_FAILED_MSG;
import static com.martin.myhelper.helpers.Utility.CREATE_RECORD_FAILED_TITLE;
import static com.martin.myhelper.helpers.Utility.CREATE_RECORD_SUCCESS_MSG;
import static com.martin.myhelper.helpers.Utility.CREATE_RECORD_SUCCESS_TITLE;

public class ElderlyCRUDHelper extends Activity {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private Intent intent;

    public void createElderlyUserRecord(final AppCompatActivity appCompatActivity, final ElderlyModel elderlyModel){

        // create the elderly record after the user account is created
        firebaseAuth = Utility.getFirebaseAuthenticationInstance();

        firebaseAuth.createUserWithEmailAndPassword(elderlyModel.getEmail(), elderlyModel.getPassword())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    firebaseUser = firebaseAuth.getCurrentUser();
                    elderlyModel.setId(firebaseAuth.getCurrentUser().getUid());

                    // create an instance of the DocumentReference class of FirebaseStore
                    firebaseFirestore = Utility.getFirebaseFireStoreInstance();
                    DocumentReference documentReference = firebaseFirestore.collection("elders").document(elderlyModel.getId());

                    // create a hash map of the object to be stored
                    Map<String, Object> modelMap = new HashMap<>();
                    modelMap.put("id", elderlyModel.getId());
                    modelMap.put("firstName", elderlyModel.getFirstName());
                    modelMap.put("lastName", elderlyModel.getLastName());
                    modelMap.put("email", elderlyModel.getEmail());
                    modelMap.put("mobileNumber", elderlyModel.getMobileNumber());
                    modelMap.put("userType", elderlyModel.getUserType());
                    modelMap.put("createdAt", FieldValue.serverTimestamp());
                    modelMap.put("updatedAt", FieldValue.serverTimestamp());

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
                            Utility.showInformationDialog(CREATE_RECORD_FAILED_TITLE,
                                    CREATE_RECORD_FAILED_MSG + e.getMessage(), appCompatActivity);
                            return;
                        }
                    });
                } else {
                    Utility.showInformationDialog("ERROR!", task.getException().getMessage(), appCompatActivity);
                    return;
                }
            }
        });
    }

    public void createElderlyServiceRequest(final AppCompatActivity appCompatActivity, final ElderlyModel elderlyModel){

        // create an instance of the DocumentReference class of FirebaseStore
        //firebaseAuth = Utility.getFirebaseAuthenticationInstance();
        //firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = Utility.getFirebaseFireStoreInstance();

        final String requestID = UUID.randomUUID().toString();
        elderlyModel.setServiceRequestId(requestID);

        // create a hash map of the object to be stored
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("id", elderlyModel.getServiceRequestId());
        modelMap.put("elderlyId", elderlyModel.getId());
        modelMap.put("requestVolunteerId", elderlyModel.getServiceRequestVolunteerId());
        modelMap.put("requestServiceTypeId", elderlyModel.getServiceRequestServiceTypeId());
        modelMap.put("requestDaysForService", elderlyModel.getDaysForService());
        modelMap.put("requestTimesForService", elderlyModel.getTimesForService());
        //modelMap.put("timesForCalls", elderlyModel.getTimesForCalls());
        modelMap.put("requestMessage", elderlyModel.getServiceRequestMessage());
        modelMap.put("createdAt", FieldValue.serverTimestamp());
        modelMap.put("updatedAt", FieldValue.serverTimestamp());

        DocumentReference documentReference = firebaseFirestore.collection("elderly_requests")
                .document(elderlyModel.getId()).collection("requests").document(elderlyModel.getServiceRequestId());

        documentReference.set(modelMap).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Utility.showInformationDialog(CREATE_RECORD_FAILED_TITLE, CREATE_RECORD_FAILED_MSG + e.getMessage(), appCompatActivity);
                return;
            }
        });

    }

    public void createElderlyServiceFeedback(final Context appCompatActivity, final ElderlyModel elderlyModel, final Context context){

        // create an instance of the DocumentReference class of FirebaseStore
        //firebaseAuth = Utility.getFirebaseAuthenticationInstance();
        //firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = Utility.getFirebaseFireStoreInstance();

        final String requestID = UUID.randomUUID().toString();
        elderlyModel.setFeedBackId(requestID);

        // create a hash map of the object to be stored
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("id", elderlyModel.getFeedBackId());
        modelMap.put("elderlyId", elderlyModel.getFeedBackElderlyId());
        modelMap.put("volunteerId", elderlyModel.getFeedBackVolunteerId());
        modelMap.put("serviceTypeId", elderlyModel.getFeedBackServiceTypeId());
        modelMap.put("requestId", elderlyModel.getFeedBackRequestId());
        modelMap.put("ratingValue", elderlyModel.getFeedBackRating());
        modelMap.put("feedbackMessage", elderlyModel.getFeedBackMessage());
        modelMap.put("createdAt", FieldValue.serverTimestamp());
        modelMap.put("updatedAt", FieldValue.serverTimestamp());

        DocumentReference documentReference = firebaseFirestore.collection("elderly_feedbacks")
                .document(elderlyModel.getFeedBackElderlyId()).collection("feedbacks")
                .document(elderlyModel.getFeedBackId());

        documentReference.set(modelMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Utility.showInformationDialog(CREATE_RECORD_FAILED_TITLE, "Feedback NOT provided due to error.\n" + e.getMessage(), appCompatActivity);
                return;
            }
        });

    }

    public void updateElderlyServiceRequest(final AppCompatActivity appCompatActivity, final ElderlyModel elderlyModel){

        // create an instance of the DocumentReference class of FirebaseStore
        firebaseAuth = Utility.getFirebaseAuthenticationInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = Utility.getFirebaseFireStoreInstance();

        // create a hash map of the object to be stored
        Map<String, Object> modelMap = new HashMap<>();
        //modelMap.put("id", elderlyModel.getServiceRequestId());
        //modelMap.put("elderlyId", elderlyModel.getId());
        //modelMap.put("requestVolunteerId", elderlyModel.getServiceRequestVolunteerId());
        //modelMap.put("requestServiceTypeId", elderlyModel.getServiceRequestServiceTypeId());
        modelMap.put("requestDaysForService", elderlyModel.getDaysForService());
        modelMap.put("requestTimesForService", elderlyModel.getTimesForService());
        //modelMap.put("timesForCalls", elderlyModel.getTimesForCalls());
        modelMap.put("requestMessage", elderlyModel.getServiceRequestMessage());
        //modelMap.put("createdAt", FieldValue.serverTimestamp());
        modelMap.put("updatedAt", FieldValue.serverTimestamp());

        DocumentReference documentReference = firebaseFirestore.collection("elderly_requests")
                .document(elderlyModel.getId()).collection("requests").document(elderlyModel.getServiceRequestId());

        // documentReference.set(modelMap, SetOptions.merge());
        documentReference.update(modelMap).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Utility.showInformationDialog(CREATE_RECORD_FAILED_TITLE, CREATE_RECORD_FAILED_MSG + e.getMessage(), appCompatActivity);
                return;
            }
        });

    }

    public void deleteElderlyServiceRequest(final AppCompatActivity appCompatActivity, final String serviceRequestId){

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

                DocumentReference documentReference = firebaseFirestore.collection("elderly_requests")
                        .document(firebaseAuth.getCurrentUser().getUid()).collection("requests")
                        .document(serviceRequestId);

                // documentReference.set(modelMap, SetOptions.merge());
                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Utility.showInformationDialog("RECORD DELETED", "You have successfully deleted a record from your requests", appCompatActivity);
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

}
