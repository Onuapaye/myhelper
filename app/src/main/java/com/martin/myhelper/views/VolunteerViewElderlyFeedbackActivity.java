package com.martin.myhelper.views;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.martin.myhelper.R;
import com.martin.myhelper.helpers.Utility;
import com.martin.myhelper.helpers.VolunteerViewElderlyFeedbackAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VolunteerViewElderlyFeedbackActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private ArrayList<ArrayList<String>> listOfElderlyFeedBacks;
    private ArrayList<String> _tempListOfElders;
    private List<String> elders, services;

    private HashMap<Integer, String>  finalHash = new HashMap<>() ;
    private HashMap<Integer, String>  finalServiceTypesHash = new HashMap<>() ;
    private String _selectedElderlyID;

    private ArrayAdapter<String> elderlyArrayAdapter, servicesArrayAdapter;
    private String[] eArray, sArray;
    private Spinner spnElderlySpinner, spnServiceTypes;
    private int key = 0;
    private int sKey = 0;

    private RecyclerView rcvViewElderlyFeedback;
    private VolunteerViewElderlyFeedbackAdapter feedbackAdapter;

    private HashMap<Integer, String> elderlyDataHashMap = new HashMap<>();
    private HashMap<Integer, String> servicesDataHashMap = new HashMap<>();
    private HashMap<Integer, String> elderlyIdFieldHashMap = new HashMap<>(), serviceIdFieldHashMap = new HashMap<>();
    private HashMap<Integer, String> tempHashMap;
    private HashMap<String, String> dummyKeys = new HashMap<>();
    private HashMap<String, String> dummySKeys = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_view_elderly_feedback);

        this.initializeWidgets();

        this.getElderlyFeedBackEldersByCurrentVolunteer();

        this.handleSelectElderlySpinnerOnItemChange();
        this.handleSelectServiceTypeSpinnerOnItemChange();
    }

    void initializeWidgets(){
        firebaseFirestore = Utility.getFirebaseFireStoreInstance();
        firebaseAuth = Utility.getFirebaseAuthenticationInstance();
        spnElderlySpinner = findViewById(R.id.availableElderlySpinner);
        spnServiceTypes = findViewById(R.id.spnAvailableFeedbackServices);
        rcvViewElderlyFeedback = findViewById(R.id.rcvFeedbacks);
    }

    private void handleSelectElderlySpinnerOnItemChange(){

        spnElderlySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String _spinnerKey;
                _spinnerKey = finalHash.get(spnElderlySpinner.getSelectedItemPosition());

                // set the global elderly id for reuse
                _selectedElderlyID = _spinnerKey;

                // clear the adapter and recycler view for the service type spinner
                sArray = null;
                spnServiceTypes.setAdapter(null);
                rcvViewElderlyFeedback.removeAllViewsInLayout();
                rcvViewElderlyFeedback.setAdapter(null);

                // load all service types based on the selected elderly feed backs
                getSelectedElderlyFeedBackServiceTypesForVolunteer(_spinnerKey.trim());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void handleSelectServiceTypeSpinnerOnItemChange(){

        spnServiceTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String _spinnerKey;
                _spinnerKey = finalServiceTypesHash.get(spnServiceTypes.getSelectedItemPosition());

                // clear the recyclerview before loading new records
                rcvViewElderlyFeedback.removeAllViewsInLayout();

                getElderlyFeedBacksFromVolunteer(_spinnerKey, firebaseAuth.getCurrentUser().getUid());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    // a callback method to get the results of the hash map
    private void callBackToSetHashValue(HashMap<Integer, String> _hash){
        this.finalHash = _hash;
    }

    private void callBackToSetServiceTypesHashValue(HashMap<Integer, String> _hash){
        this.finalServiceTypesHash = _hash;
    }

    private void getElderlyFeedBacksFromVolunteer(String serviceTypeID, String userID) {

        // instantiate the array list of volunteers
        listOfElderlyFeedBacks = new ArrayList<>();

        CollectionReference collectionReference = firebaseFirestore.collection("elderly_feedbacks")
                .document(_selectedElderlyID).collection("feedbacks");

        collectionReference
            .whereEqualTo("volunteerId", userID)
            .whereEqualTo("elderlyId", _selectedElderlyID)
            .whereEqualTo("serviceTypeId", serviceTypeID)
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()){
                        if (task.getResult() != null){

                            QuerySnapshot documentSnapshot = task.getResult();

                            for (DocumentSnapshot ds : documentSnapshot) {
                                // instantiate the temporal array list of volunteer services
                                _tempListOfElders = new ArrayList<>();
                                _tempListOfElders.add(ds.getString("id"));
                                _tempListOfElders.add(ds.getString("elderlyId"));
                                _tempListOfElders.add(ds.getString("volunteerId"));
                                _tempListOfElders.add(ds.getString("serviceTypeId"));
                                _tempListOfElders.add(ds.getString("feedbackMessage"));
                                _tempListOfElders.add(String.valueOf(ds.getLong("ratingValue").intValue()));

                                listOfElderlyFeedBacks.add(_tempListOfElders);
                            }
                        }
                    }

                    rcvViewElderlyFeedback.removeAllViewsInLayout();

                    feedbackAdapter = new VolunteerViewElderlyFeedbackAdapter(
                                                    VolunteerViewElderlyFeedbackActivity.this,
                                                            listOfElderlyFeedBacks);

                    rcvViewElderlyFeedback.setAdapter(feedbackAdapter);
                    rcvViewElderlyFeedback.setLayoutManager(new LinearLayoutManager(
                            VolunteerViewElderlyFeedbackActivity.this));
                }
            });
    }


    void getElderlyFeedBackEldersByCurrentVolunteer(){

        CollectionReference reference = firebaseFirestore.collection("elders");
        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){

                    final QuerySnapshot snapshots = task.getResult();

                    for (int i = 0; i < snapshots.size(); i++){

                        /*BEGIN*/
                            CollectionReference collectionReference = firebaseFirestore.collection("elderly_feedbacks")
                                    .document(snapshots.getDocuments().get(i).getId()).collection("feedbacks");

                        final int finalI = i;
                        collectionReference
                                    .whereEqualTo("elderlyId", snapshots.getDocuments().get(i).getId())
                                    .whereEqualTo("volunteerId", firebaseAuth.getCurrentUser().getUid())
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                    if (queryDocumentSnapshots.size() > 0){

                                        for (QueryDocumentSnapshot qds : queryDocumentSnapshots){

                                            // get the elderly name and id to be hashed
                                            DocumentReference documentReference = firebaseFirestore.collection("elders")
                                                    .document(snapshots.getDocuments().get(finalI).getId());

                                            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot sn) {
                                                    if (sn.exists()){

                                                        tempHashMap = new HashMap<>();

                                                        if (! dummyKeys.containsKey(sn.getString("id"))){

                                                            dummyKeys.put(sn.getString("id"), sn.getString("firstName") + ", " + sn.getString("lastName").toUpperCase());

                                                            elderlyIdFieldHashMap.put(key, sn.getString("id"));
                                                            tempHashMap.put(key, sn.getString("firstName") + ", "
                                                                    + sn.getString("lastName").toUpperCase());

                                                            key++;
                                                        }

                                                        elderlyDataHashMap.putAll(tempHashMap);

                                                        elders = new ArrayList<>(elderlyDataHashMap.values());

                                                        // convert the list into a normal array
                                                        eArray = new String[elders.size()];
                                                        elders.toArray(eArray);

                                                        // call the callback method to set the hash results
                                                        callBackToSetHashValue(elderlyIdFieldHashMap);

                                                        elderlyArrayAdapter = new ArrayAdapter<>(VolunteerViewElderlyFeedbackActivity.this,
                                                                R.layout.support_simple_spinner_dropdown_item, eArray);

                                                        elderlyArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                                                        spnElderlySpinner.setAdapter(elderlyArrayAdapter);
                                                    }
                                                }
                                            });
                                        }
                                    }

                                }
                            });

                        /*END*/
                    }
                }
            }
        });

    }

    void getSelectedElderlyFeedBackServiceTypesForVolunteer(String elderlyID){

        CollectionReference collectionReference = firebaseFirestore.collection("elderly_feedbacks")
                .document(elderlyID).collection("feedbacks");

        collectionReference
                .whereEqualTo("volunteerId", firebaseAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.size() > 0){

                    for (QueryDocumentSnapshot qds : queryDocumentSnapshots){

                        DocumentReference documentReference = firebaseFirestore.collection("service_types")
                                .document(qds.getString("serviceTypeId"));

                        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot snapshot) {
                                if (snapshot.exists()){

                                    tempHashMap = new HashMap<>();
                                    if (! dummySKeys.containsKey(snapshot.getString("id"))){

                                        dummySKeys.put(snapshot.getString("id"), snapshot.getString("service_name"));

                                        serviceIdFieldHashMap.put(sKey, snapshot.getString("id"));
                                        tempHashMap.put(key, snapshot.getString("service_name"));

                                        sKey++;
                                    }

                                    servicesDataHashMap.putAll(tempHashMap);

                                    services = new ArrayList<>(servicesDataHashMap.values());

                                    // convert the list into a normal array
                                    sArray = new String[services.size()];
                                    services.toArray(sArray);


                                    // call the callback method to set the hash results
                                    callBackToSetServiceTypesHashValue(serviceIdFieldHashMap);

                                    servicesArrayAdapter = new ArrayAdapter<>(VolunteerViewElderlyFeedbackActivity.this,
                                            R.layout.support_simple_spinner_dropdown_item, sArray);

                                    servicesArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                                    spnServiceTypes.setAdapter(servicesArrayAdapter);
                                }
                            }
                        });
                    }
                }
            }
        });
    }
}