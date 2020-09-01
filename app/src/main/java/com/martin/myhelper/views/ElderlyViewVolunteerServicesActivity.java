package com.martin.myhelper.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.martin.myhelper.R;
import com.martin.myhelper.helpers.ElderlyViewVolunteerServicesAdapter;
import com.martin.myhelper.helpers.ElderlyViewVolunteerServicesToMakeRequestAdapter;
import com.martin.myhelper.helpers.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.martin.myhelper.helpers.Utility.ASSIST_WITH_ERRANDS;
import static com.martin.myhelper.helpers.Utility.ASSIST_WITH_GARDENING;
import static com.martin.myhelper.helpers.Utility.ASSIST_WITH_GROCERY_SHOPPING;
import static com.martin.myhelper.helpers.Utility.ASSIST_WITH_HOUSE_CLEANING;
import static com.martin.myhelper.helpers.Utility.ASSIST_WITH_HOUSE_MAINTENANCE;
import static com.martin.myhelper.helpers.Utility.PROVIDE_LIFT_TO_SHOP;
import static com.martin.myhelper.helpers.Utility.PROVIDE_LIFT_TO_SOCIAL;
import static com.martin.myhelper.helpers.Utility.TEACH_USAGE_MOBILE_DEVICES;
import static com.martin.myhelper.helpers.Utility.TEACH_USAGE_WEB_APPS;
import static com.martin.myhelper.helpers.Utility.WALK_WITH_U;

public class ElderlyViewVolunteerServicesActivity extends AppCompatActivity {

    private List<String> servicesList;
    private List<String> _tempServicesList;

    /*variable for loading system times*/
    private List<String> tempSystemTimeList;
    private List<String> systemTimeList = new ArrayList<>();
    private List<String> lstFinalSystemTimes = new ArrayList<>();
    private String[] availableSystemTimesArray = null;

    private HashMap<Integer, String> _tempServiceTypeHash;
    private HashMap<Integer, String> serviceTypesHash = new HashMap<>();
    private HashMap<Integer, String> finalServiceTypesHash = new HashMap<>() ;
    private String[] serviceArrayForSpinner;
    private ArrayAdapter<String> serviceTypesArrayAdapter;

    private Spinner spnServiceType;
    private String selectedServiceTypeID = "";

    private ArrayList<ArrayList<String>> volunteerProfilesLists, volunteerAccountList;
    private ArrayList<String> _tempList1;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elderly_view_volunteer_service);

        this.getServiceTypesRecordsForSpinner();

        this.handleSelectServiceTypeSpinnerOnItemChange();
    }

    /***
     * Loads all the service types from an array into the spinner control
     */
    private void getServiceTypesRecordsForSpinner(){

        spnServiceType = findViewById(R.id.spnServiceType);
        firebaseFirestore = Utility.getFirebaseFireStoreInstance();

        servicesList = new ArrayList<>();

        final CollectionReference collection = firebaseFirestore.collection("service_types");
        collection.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                int key = 0;
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots){

                    _tempServiceTypeHash = new HashMap<>();
                    serviceTypesHash.put(key, snapshot.getString("id"));

                    _tempServicesList = new ArrayList<>();
                    _tempServicesList.add(snapshot.getString("service_name") );

                    servicesList.add(_tempServicesList.toString().replaceAll("(^\\[|\\]$)", ""));
                    key++;
                }

                // convert the list into a normal array
                serviceArrayForSpinner = new String[servicesList.size()];
                servicesList.toArray(serviceArrayForSpinner);

                // call the callback method to set the hash results
                setServiceTypesHashCallBack(serviceTypesHash);

                serviceTypesArrayAdapter = new ArrayAdapter<>(ElderlyViewVolunteerServicesActivity.this,
                        R.layout.support_simple_spinner_dropdown_item, serviceArrayForSpinner);

                serviceTypesArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

                spnServiceType.setAdapter(serviceTypesArrayAdapter);
            }
        });

    }

    // a callback method to get the results of the hash map
    private void setServiceTypesHashCallBack(HashMap<Integer, String> _hash){
        this.finalServiceTypesHash = _hash;
    }

    private void handleSelectServiceTypeSpinnerOnItemChange(){

        spnServiceType = findViewById(R.id.availableServiceTypeSpinner);
        spnServiceType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String _spinnerKey;
                _spinnerKey = finalServiceTypesHash.get(spnServiceType.getSelectedItemPosition());

                // set the selected service time key for saving later
                selectedServiceTypeID = _spinnerKey;

                RecyclerView recyclerView = findViewById(R.id.rcvViewProvidedServices);
                recyclerView.removeAllViewsInLayout();

                // load the list by showing the recycler view
                loadVolunteerServicesForRecyclerView(selectedServiceTypeID);
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
    private void loadVolunteerServicesForRecyclerView(final String serviceTypeID) {

        firebaseFirestore = Utility.getFirebaseFireStoreInstance();

        final CollectionReference volunteersColsRef = firebaseFirestore.collection("volunteers");

        // instantiate the array list of volunteers
        volunteerProfilesLists = new ArrayList<>();

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
                    CollectionReference profileReference = firebaseFirestore.collection("volunteer_profiles")
                            .document(docPath).collection("profiles");

                    profileReference.whereEqualTo("serviceTypeId", serviceTypeID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            if (task.getResult() != null){

                                QuerySnapshot documentSnapshot = task.getResult();

                                for (DocumentSnapshot ds1 : documentSnapshot) {

                                    // instantiate the temporal array list of volunteer services
                                    _tempList1 = new ArrayList<>();

                                    _tempList1.add(ds1.getString("id"));
                                    _tempList1.add(ds1.getString("serviceTypeId"));
                                    _tempList1.add(ds1.getString("volunteerId"));
                                    _tempList1.add(ds1.getString("description"));

                                    _tempList1.add(String.valueOf(ds1.get("monCalls")));
                                    _tempList1.add(String.valueOf(ds1.get("monTimes")));

                                    _tempList1.add(String.valueOf(ds1.get("tueCalls")));
                                    _tempList1.add(String.valueOf(ds1.get("tueTimes")));

                                    _tempList1.add(String.valueOf(ds1.get("wedCalls")));
                                    _tempList1.add(String.valueOf(ds1.get("wedTimes")));

                                    _tempList1.add(String.valueOf(ds1.get("thuCalls")));
                                    _tempList1.add(String.valueOf(ds1.get("thuTimes")));

                                    _tempList1.add(String.valueOf(ds1.get("friCalls")));
                                    _tempList1.add(String.valueOf(ds1.get("friTimes")));

                                    _tempList1.add(String.valueOf(ds1.get("satCalls")));
                                    _tempList1.add(String.valueOf(ds1.get("satTimes")));

                                    _tempList1.add(String.valueOf(ds1.get("sunCalls")));
                                    _tempList1.add(String.valueOf(ds1.get("sunTimes")));

                                    volunteerProfilesLists.add(_tempList1);
                                }
                            }
                        }
                        RecyclerView viewVolunteerServices = findViewById(R.id.rcvViewProvidedServices);

                        ElderlyViewVolunteerServicesToMakeRequestAdapter
                                servicesToMakeRequestAdapter = new ElderlyViewVolunteerServicesToMakeRequestAdapter(
                                        ElderlyViewVolunteerServicesActivity.this, volunteerProfilesLists);
                        viewVolunteerServices.
                                setAdapter(servicesToMakeRequestAdapter);
                        viewVolunteerServices.
                                setLayoutManager(new LinearLayoutManager(ElderlyViewVolunteerServicesActivity.this));
                        }
                    });
                }
            }
            }
        });
    }

}