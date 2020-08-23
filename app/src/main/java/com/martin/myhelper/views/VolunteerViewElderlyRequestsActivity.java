package com.martin.myhelper.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.martin.myhelper.R;
import com.martin.myhelper.helpers.Utility;
import com.martin.myhelper.helpers.VolunteerViewElderlyRequestsAdapter;

import java.util.ArrayList;

public class VolunteerViewElderlyRequestsActivity extends AppCompatActivity {

    private Spinner spnServiceTypes;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private ArrayList<ArrayList<String>> volunteerProfileList, listOfElders;
    private ArrayList<String> _tempVolunteerProfileList, _tempListOfElders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_view_elderly_requests);

        this.getElderlyRequestsFromVolunteer();
    }

    private void getElderlyRequestsFromVolunteer() {

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
                        CollectionReference profileReference = firebaseFirestore.collection("elderly_requests")
                                .document(docPath).collection("requests");

                        profileReference
                            .whereEqualTo("requestVolunteerId", firebaseAuth.getCurrentUser().getUid())
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

                                    VolunteerViewElderlyRequestsAdapter viewElderlyRequestsAdapter = new VolunteerViewElderlyRequestsAdapter(VolunteerViewElderlyRequestsActivity.this, listOfElders);
                                    rcvViewElderlyRequest.setAdapter(viewElderlyRequestsAdapter);
                                    rcvViewElderlyRequest.setLayoutManager(new LinearLayoutManager(VolunteerViewElderlyRequestsActivity.this));
                                }
                            });
                    }
                }
            }
        });
    }

}