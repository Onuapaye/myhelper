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
import com.martin.myhelper.helpers.VolunteerViewElderlyRequestsAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VolunteerViewElderlyFeedbackActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private ArrayList<ArrayList<String>> listOfElderlyFeedBacks, _elderlyAccountList;
    private ArrayList<String> _tempListOfElders;
    private List<String> elders;
    private List<String> _tempElderly;

    private HashMap<Integer, String> _tempEldersHash;
    private HashMap<Integer, String>  eldersHash = new HashMap<>();
    private HashMap<Integer, String>  finalHash = new HashMap<>() ;

    private ArrayAdapter<String> arrayAdapter;
    private String[] array;
    private Spinner elderlySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_view_elderly_feedback);

        //this.getElderlyRecordsForSpinner();
        this.getFeedBackElders();
        this.handleSelectElderlySpinnerOnItemChange();
    }

    private void handleSelectElderlySpinnerOnItemChange(){

        elderlySpinner = findViewById(R.id.availableElderlySpinner);
        elderlySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String _spinnerKey;
                _spinnerKey = finalHash.get(elderlySpinner.getSelectedItemPosition());

                // clear the recyclerview before loading new records
                RecyclerView recyclerView = findViewById(R.id.rcvFeedbacks);
                recyclerView.removeAllViewsInLayout();

                getElderlyFeedBacksFromVolunteer(_spinnerKey);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    int key = 0;

    private void getFeedBackElders(){
        firebaseFirestore = Utility.getFirebaseFireStoreInstance();
        firebaseAuth = Utility.getFirebaseAuthenticationInstance();
        elderlySpinner = findViewById(R.id.availableElderlySpinner);

        elders = new ArrayList<>();

        CollectionReference reference = firebaseFirestore.collection("elders");
        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    if (task != null){
                        final QuerySnapshot snapshots = task.getResult();

                        for (int i = 0; i < snapshots.size(); i++){

                            // get the elderly id used as the path to the feedback document
                            final String docPath = snapshots.getDocuments().get(i).getId();

                            // STEP 2 : Get the collections and documents from the volunteer profiles
                            final CollectionReference colRef = firebaseFirestore.collection("elderly_feedbacks").document(docPath).collection("feedbacks");

                            colRef.whereEqualTo("elderlyId", snapshots.getDocuments().get(i).getString("id"))
                                    .whereEqualTo("volunteerId", firebaseAuth.getCurrentUser().getUid())
                                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                    for (final QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {

                                        if (firebaseAuth.getCurrentUser().getUid().equals(snapshot.getString("volunteerId"))) {

                                            final DocumentReference doc = firebaseFirestore.collection("elders").document(snapshot.getString("elderlyId"));
                                            doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot snp) {

                                                    _tempEldersHash = new HashMap<>();
                                                    eldersHash.put(key, snapshot.getString("elderlyId"));

                                                    _tempElderly = new ArrayList<>();

                                                    _tempElderly.add(snp.getString("firstName") + " " +
                                                            snp.getString("lastName").toUpperCase());
                                                    elders.add(_tempElderly.toString().replaceAll("(^\\[|\\]$)", ""));

                                                    key++;

                                                    // convert the list into a normal array
                                                    array = new String[elders.size()];
                                                    elders.toArray(array);

                                                    // call the callback method to set the hash results
                                                    callBackHashValue(eldersHash);

                                                    arrayAdapter = new ArrayAdapter<>(VolunteerViewElderlyFeedbackActivity.this,
                                                            R.layout.support_simple_spinner_dropdown_item, array);
                                                    arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                                                    elderlySpinner.setAdapter(arrayAdapter);
                                                }
                                            });
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    private void getTheFeedbackList(final QueryDocumentSnapshot qds) {

        elderlySpinner = findViewById(R.id.availableElderlySpinner);
        firebaseFirestore = Utility.getFirebaseFireStoreInstance();

        elders = new ArrayList<>();

        final CollectionReference colRef = firebaseFirestore.collection("elderly_feedbacks").document(qds.getString("id")).collection("feedbacks");

        colRef.whereEqualTo("elderlyId", qds.getString("id"))
              .whereEqualTo("volunteerId", firebaseAuth.getCurrentUser().getUid())
              .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int key = 0;

                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {

                    _tempEldersHash = new HashMap<>();
                    eldersHash.put(key, qds.getString("id"));

                    _tempElderly = new ArrayList<>();
                    _tempElderly.add(qds.getString("firstName") + " " +
                            qds.getString("lastName").toUpperCase());

                    elders.add(_tempElderly.toString().replaceAll("(^\\[|\\]$)", ""));
                    key++;
                }

                // convert the list into a normal array
                array = new String[elders.size()];
                elders.toArray(array);

                // call the callback method to set the hash results
                callBackHashValue(eldersHash);

                arrayAdapter = new ArrayAdapter<>(VolunteerViewElderlyFeedbackActivity.this,
                        R.layout.support_simple_spinner_dropdown_item, array);
                arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                elderlySpinner.setAdapter(arrayAdapter);
            }
        });
    }

   /* private void setBismark(){
        firebaseFirestore = Utility.getFirebaseFireStoreInstance();
        firebaseAuth = Utility.getFirebaseAuthenticationInstance();

        final CollectionReference volunteersColsRef = firebaseFirestore.collection("elders");

        // instantiate the array list of volunteers
        listOfElders = new ArrayList<>();

        volunteersColsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){

                    // get the query snapshot from the task
                    QuerySnapshot snapshot = task.getResult();

                    // loop through the snapshot and get the ids
                    for(int i=0; i < snapshot.size(); i++){

                        // get the volunteer id used as the path to the document
                        final String docPath = snapshot.getDocuments().get(i).getId();

                        // STEP 2 : Get the collections and documents from the volunteer profiles
                        CollectionReference profileReference = firebaseFirestore.collection("elderly_feedbacks")
                                .document(docPath).collection("feedbacks");

                        profileReference
                                .whereEqualTo("volunteerId", firebaseAuth.getCurrentUser().getUid())
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
                                                    _tempListOfElders.add(ds.getString("requestVolunteerId"));
                                                    _tempListOfElders.add(ds.getString("requestServiceTypeId"));
                                                    _tempListOfElders.add(String.valueOf(ds.get("requestDaysForService")));
                                                    _tempListOfElders.add(String.valueOf(ds.get("requestTimesForService")));
                                                    _tempListOfElders.add(ds.getString("requestMessage"));

                                                    listOfElders.add(_tempListOfElders);
                                                }
                                            }
                                        }

                                        RecyclerView rcvViewElderlyRequest = findViewById(R.id.rcvProvidedServices);

                                        VolunteerViewElderlyRequestsAdapter viewElderlyRequestsAdapter =
                                                new VolunteerViewElderlyRequestsAdapter(
                                                        VolunteerViewElderlyRequestsActivity.this, listOfElders);

                                        rcvViewElderlyRequest.setAdapter(viewElderlyRequestsAdapter);
                                        rcvViewElderlyRequest.setLayoutManager(new LinearLayoutManager(
                                                VolunteerViewElderlyRequestsActivity.this));
                                    }
                                });
                    }
                }
            }
        });
    }*/

    /*private void getElderlyRecordsForSpinner(){

        elderlySpinner = findViewById(R.id.availableElderlySpinner);
        firebaseFirestore = Utility.getFirebaseFireStoreInstance();

        elders = new ArrayList<>();

        final CollectionReference collection = firebaseFirestore.collection("elders");
        collection.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                int key = 0;
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots){

                    _tempEldersHash = new HashMap<>();
                    eldersHash.put(key, snapshot.getString("id"));

                    _tempElderly = new ArrayList<>();
                    _tempElderly.add(snapshot.getString("firstName") + " " +
                            snapshot.getString("lastName").toUpperCase());

                    elders.add(_tempElderly.toString().replaceAll("(^\\[|\\]$)", ""));
                    key++;
                }

                // convert the list into a normal array
                array = new String[elders.size()];
                elders.toArray(array);

                // call the callback method to set the hash results
                callBackHashValue(eldersHash);

                arrayAdapter = new ArrayAdapter<>(VolunteerViewElderlyFeedbackActivity.this,
                        R.layout.support_simple_spinner_dropdown_item, array);
                arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                elderlySpinner.setAdapter(arrayAdapter);
            }
        });

    }*/

    // a callback method to get the results of the hash map
    private void callBackHashValue(HashMap<Integer, String> _hash){
        this.finalHash = _hash;
    }

    private void getElderlyFeedBacksFromVolunteer(String elderlyID) {

        firebaseFirestore = Utility.getFirebaseFireStoreInstance();
        firebaseAuth = Utility.getFirebaseAuthenticationInstance();
        String userID = firebaseAuth.getCurrentUser().getUid();

        // instantiate the array list of volunteers
        listOfElderlyFeedBacks = new ArrayList<>();
        _elderlyAccountList = new ArrayList<>();
        CollectionReference collectionReference = firebaseFirestore.collection("elderly_feedbacks")
                .document(elderlyID).collection("feedbacks");

        collectionReference
            .whereEqualTo("volunteerId", userID)
            .whereEqualTo("elderlyId", elderlyID)
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

                    RecyclerView rcvViewElderlyFeedback = findViewById(R.id.rcvFeedbacks);
                    rcvViewElderlyFeedback.removeAllViewsInLayout();

                    VolunteerViewElderlyFeedbackAdapter feedbackAdapter = new
                            VolunteerViewElderlyFeedbackAdapter(VolunteerViewElderlyFeedbackActivity.this,
                            listOfElderlyFeedBacks);
                    rcvViewElderlyFeedback.setAdapter(feedbackAdapter);
                    rcvViewElderlyFeedback.setLayoutManager(new LinearLayoutManager(
                            VolunteerViewElderlyFeedbackActivity.this));
                }
            });
    }


}