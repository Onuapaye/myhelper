package com.martin.myhelper.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.martin.myhelper.R;
import com.martin.myhelper.helpers.ElderlyProvideFeedbackAdapter;
import com.martin.myhelper.helpers.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ElderlyProvideFeedBackActivity extends AppCompatActivity {

    private ArrayList<ArrayList<String>> volunteerProfilesLists, elderlyRequestsList, volunteerAccountList;
    private ArrayList<String> _tempList1, _tempList2;
    private ArrayList<String> list;

    private ArrayList<String> servicesList;
    private List<String> _tempServicesList;
    private String[] serviceTypeIDs;
    private String selectedServiceTypeID = "";
    private Spinner spnServiceTypes;
    private ArrayAdapter<String> serviceTypesArrayAdapter;
    private String[] serviceArrayForSpinner;
    private HashMap<Integer, String> _tempServiceTypeHash;
    private HashMap<Integer, String> serviceTypesHash = new HashMap<>();
    private HashMap<Integer, String> finalServiceTypesHash = new HashMap<>() ;
    private HashMap<String, String> dummyHashMap = new HashMap<>() ;
    private HashMap<String, String> _dummy = new HashMap<>();

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private int key = 0;

    private RecyclerView recyclerView;
    private ElderlyProvideFeedbackAdapter provideFeedbackAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elderly_provide_feed_back);

        this.initializeWidgets();

        this.loadElderlyRequestedServicesTypes();
        this.handleSelectServiceTypeSpinnerOnItemChange();
    }


    /***
     * Loads all the service types from an array into the spinner control
     */
    private void loadElderlyRequestedServicesTypes(){

        final CollectionReference elderlyCollection = firebaseFirestore.collection("elderly_requests")
                .document(firebaseAuth.getCurrentUser().getUid()).collection("requests");

        elderlyCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
              @Override
              public void onComplete(@NonNull Task<QuerySnapshot> task) {
                  if (task.isSuccessful()) {
                      if (task.getResult() != null) {

                          QuerySnapshot snapshot = task.getResult();
                          for (QueryDocumentSnapshot qDoc : snapshot) {

                              if(!_dummy.containsKey(qDoc.getString("requestServiceTypeId"))) {
                                  _dummy.put(qDoc.getString("requestServiceTypeId"), qDoc.getString("requestMessage"));

                                  getServiceTypesRecordsForSpinner(qDoc.getString("requestServiceTypeId"));
                              }
                          }
                      }
                  }
              }
        });
    }

    private void getServiceTypesRecordsForSpinner(String serviceTypeID){

        servicesList = new ArrayList<>();

        final CollectionReference collection = firebaseFirestore.collection("service_types");
        collection.whereEqualTo("id", serviceTypeID).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots){

                    _tempServiceTypeHash = new HashMap<>();
                    _tempServicesList = new ArrayList<>();

                    if (!dummyHashMap.containsKey(snapshot.getString("id"))) {

                        dummyHashMap.put(snapshot.getString("id"), snapshot.getString("service_name"));
                        serviceTypesHash.put(key, snapshot.getString("id"));

                        _tempServicesList.add(snapshot.getString("service_name"));
                        servicesList.add(_tempServicesList.toString().replaceAll("(^\\[|\\]$)", ""));

                        key++;
                    }

                }

                // convert the list into a normal array
                serviceArrayForSpinner = new String[servicesList.size()];
                servicesList.toArray(serviceArrayForSpinner);

                // call the callback method to set the hash results
                setServiceTypesHashCallBack(serviceTypesHash);

                serviceTypesArrayAdapter = new ArrayAdapter<String>(ElderlyProvideFeedBackActivity.this,
                        R.layout.support_simple_spinner_dropdown_item, serviceArrayForSpinner);

                serviceTypesArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

                spnServiceTypes.setAdapter(serviceTypesArrayAdapter);
            }
        });

    }

    // a callback method to get the results of the hash map
    private void setServiceTypesHashCallBack(HashMap<Integer, String> _hash){
        this.finalServiceTypesHash = _hash;
    }

    private void handleSelectServiceTypeSpinnerOnItemChange(){

        spnServiceTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String _spinnerKey = finalServiceTypesHash.get(spnServiceTypes.getSelectedItemPosition());

                // set the selected service time key for saving later
                selectedServiceTypeID = _spinnerKey;

                // load the system times for dialog builder selection
                loadVolunteerServicesRecyclerView(_spinnerKey);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    /**
     * Loads all volunteer with their profiles based on the service type selected
     * from the drop-down list
     * @param serviceTypeID
     */
    private void loadVolunteerServicesRecyclerView(final String serviceTypeID) {

        // instantiate the array list of volunteers
        volunteerProfilesLists = new ArrayList<>();
        elderlyRequestsList = new ArrayList<>();

        // STEP 1. get the elderly requests;
        final CollectionReference elderlyCollection = firebaseFirestore.collection("elderly_requests")
                                                      .document(firebaseAuth.getCurrentUser().getUid()).collection("requests");

        elderlyCollection.whereEqualTo("requestServiceTypeId", serviceTypeID)
                .whereEqualTo("elderlyId", firebaseAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult() != null){

                        QuerySnapshot snapshot = task.getResult();
                        for (QueryDocumentSnapshot qDoc : snapshot){

                            // instantiate the temporal array list of volunteer services
                            _tempList1 = new ArrayList<>();

                            _tempList1.add(qDoc.getString("id"));
                            _tempList1.add(qDoc.getString("elderlyId"));
                            _tempList1.add(qDoc.getString("requestVolunteerId"));
                            _tempList1.add(qDoc.getString("requestServiceTypeId"));

                            _tempList1.add(String.valueOf(qDoc.get("monCalls")));
                            _tempList1.add(String.valueOf(qDoc.get("monTimes")));

                            _tempList1.add(String.valueOf(qDoc.get("tueCalls")));
                            _tempList1.add(String.valueOf(qDoc.get("tueTimes")));

                            _tempList1.add(String.valueOf(qDoc.get("wedCalls")));
                            _tempList1.add(String.valueOf(qDoc.get("wedTimes")));

                            _tempList1.add(String.valueOf(qDoc.get("thuCalls")));
                            _tempList1.add(String.valueOf(qDoc.get("thuTimes")));

                            _tempList1.add(String.valueOf(qDoc.get("friCalls")));
                            _tempList1.add(String.valueOf(qDoc.get("friTimes")));

                            _tempList1.add(String.valueOf(qDoc.get("satCalls")));
                            _tempList1.add(String.valueOf(qDoc.get("satTimes")));

                            _tempList1.add(String.valueOf(qDoc.get("sunCalls")));
                            _tempList1.add(String.valueOf(qDoc.get("sunTimes")));

                            _tempList1.add(qDoc.getString("requestMessage"));

                            elderlyRequestsList.add(_tempList1);
                        }
                    }
                }

                provideFeedbackAdapter = new ElderlyProvideFeedbackAdapter(elderlyRequestsList,
                        ElderlyProvideFeedBackActivity.this );

                recyclerView.setAdapter(provideFeedbackAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(ElderlyProvideFeedBackActivity.this));

            }
        });
    }


    void initializeWidgets(){

        firebaseFirestore = Utility.getFirebaseFireStoreInstance();
        firebaseAuth = Utility.getFirebaseAuthenticationInstance();

        recyclerView = findViewById(R.id.rcvViewProvidedServices);
        spnServiceTypes = findViewById(R.id.availableServiceTypeSpinner);

    }
}