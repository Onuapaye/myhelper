package com.martin.myhelper.helpers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.martin.myhelper.model.ServiceTypeModel;

import java.util.HashMap;
import java.util.Map;

import static com.martin.myhelper.helpers.Utility.CREATE_RECORD_FAILED_MSG;
import static com.martin.myhelper.helpers.Utility.CREATE_RECORD_FAILED_TITLE;

public class VolunteerProfileCRUDHelper {

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private ElderlyVolunteerCRUDHelper crudHelper;

    public void createVolunteerServiceProfile(final AppCompatActivity appCompatActivity, final ServiceTypeModel serviceTypeModel){

        // create an instance of the DocumentReference class of FirebaseStore
        serviceTypeModel.setId(this.generateServiceTypeID());
        firebaseFirestore = Utility.getFirebaseFireStoreInstance();
        crudHelper = new ElderlyVolunteerCRUDHelper();

        // create a hash map of the object to be stored
        Map<String, Object> modelMap = new HashMap<>();
        modelMap.put("id", serviceTypeModel.getId());
        modelMap.put("volunteerId", crudHelper.getCurrentUserID());
        modelMap.put("serviceTypeName", serviceTypeModel.getServiceTypeName());
        modelMap.put("serviceTypeDescription", serviceTypeModel.getServiceTypeDescription());
        modelMap.put("createdAt", FieldValue.serverTimestamp().toString());
        modelMap.put("updatedAt", FieldValue.serverTimestamp().toString());

        DocumentReference documentReference = firebaseFirestore.collection("service_types").document(serviceTypeModel.getId());
        documentReference.set(modelMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Utility.showInformationDialog(CREATE_RECORD_FAILED_TITLE, CREATE_RECORD_FAILED_MSG + e.getMessage(), appCompatActivity);
                return;
            }
        });

    }

    private String generateServiceTypeID(){
        return Utility.getUUID();
    }
}
