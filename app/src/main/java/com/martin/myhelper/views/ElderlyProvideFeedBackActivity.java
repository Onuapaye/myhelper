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
import android.widget.RatingBar;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
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

import static com.martin.myhelper.R.layout.support_simple_spinner_dropdown_item;
import static com.martin.myhelper.helpers.Utility.ASSIST_WITH_ERRANDS;
import static com.martin.myhelper.helpers.Utility.ASSIST_WITH_GARDENING;
import static com.martin.myhelper.helpers.Utility.ASSIST_WITH_GROCERY_SHOPPING;
import static com.martin.myhelper.helpers.Utility.ASSIST_WITH_HOUSE_CLEANING;
import static com.martin.myhelper.helpers.Utility.ASSIST_WITH_HOUSE_MAINTENANCE;
import static com.martin.myhelper.helpers.Utility.PROVIDE_LIFT_TO_SHOP;
import static com.martin.myhelper.helpers.Utility.PROVIDE_LIFT_TO_SOCIAL;
import static com.martin.myhelper.helpers.Utility.TAKE_CARE_OF_PETS;
import static com.martin.myhelper.helpers.Utility.TEACH_USAGE_MOBILE_DEVICES;
import static com.martin.myhelper.helpers.Utility.TEACH_USAGE_WEB_APPS;
import static com.martin.myhelper.helpers.Utility.WALK_WITH_U;

public class ElderlyProvideFeedBackActivity extends AppCompatActivity {

    private ArrayList<ArrayList<String>> volunteerProfilesLists, elderlyRequestsList, volunteerAccountList;
    private ArrayList<String> _tempList1, _tempList2;
    private ArrayList<String> list;

    private ArrayList<String> serviceTypes;
    private String[] serviceTypeIDs;
    private String selectedServiceTypeID;
    private Spinner spnServiceTypes;
    private ArrayAdapter<String> arrayAdapter;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elderly_provide_feed_back);

        this.loadServicesTypes();
        this.handleSpinnerSelectedItemChange();
    }

    /***
     * Loads all the service types from an array into the spinner control
     */
    private void loadServicesTypes(){

        firebaseFirestore = Utility.getFirebaseFireStoreInstance();
        firebaseAuth = Utility.getFirebaseAuthenticationInstance();

        spnServiceTypes = findViewById(R.id.availableServiceTypeSpinner);
        //serviceTypes = getResources().getStringArray(R.array.serviceTypes);
        serviceTypeIDs = getResources().getStringArray(R.array.serviceTypeIDs);

        serviceTypes = new ArrayList<>();
        final CollectionReference elderlyCollection = firebaseFirestore.collection("elderly_requests")
                .document(firebaseAuth.getCurrentUser().getUid()).collection("requests");
        elderlyCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
              @Override
              public void onComplete(@NonNull Task<QuerySnapshot> task) {
                  if (task.isSuccessful()) {
                      if (task.getResult() != null) {

                          QuerySnapshot snapshot = task.getResult();
                          for (QueryDocumentSnapshot qDoc : snapshot) {

                              // instantiate the temporal array list of volunteer services
                              list = new ArrayList<>();
                              list.add(qDoc.getString("requestServiceTypeId"));
                              switch (list.toString().replaceAll("(^\\[|\\]$)", "")) {
                                  case "100":
                                      serviceTypes.add(TEACH_USAGE_MOBILE_DEVICES);
                                      break;
                                  case "200":
                                      serviceTypes.add(TEACH_USAGE_WEB_APPS);
                                      break;
                                  case "300":
                                      serviceTypes.add(WALK_WITH_U);
                                      break;
                                  case "400":
                                      serviceTypes.add(PROVIDE_LIFT_TO_SOCIAL);
                                      break;
                                  case "500":
                                      serviceTypes.add(ASSIST_WITH_HOUSE_CLEANING);
                                      break;
                                  case "600":
                                      serviceTypes.add(ASSIST_WITH_HOUSE_MAINTENANCE);
                                      break;
                                  case "700":
                                      serviceTypes.add(ASSIST_WITH_GARDENING);
                                      break;
                                  case "800":
                                      serviceTypes.add(ASSIST_WITH_ERRANDS);
                                      break;
                                  case "900":
                                      serviceTypes.add(ASSIST_WITH_GROCERY_SHOPPING);
                                      break;
                                  case "1000":
                                      serviceTypes.add(PROVIDE_LIFT_TO_SHOP);
                                      break;
                                  default:
                                      serviceTypes.add(TAKE_CARE_OF_PETS);
                                      break;
                              }

                              //serviceTypes.add(String.valueOf());

                              arrayAdapter = new ArrayAdapter<String>(ElderlyProvideFeedBackActivity.this,
                                      support_simple_spinner_dropdown_item, serviceTypes);
                              arrayAdapter.setDropDownViewResource(support_simple_spinner_dropdown_item);
                              spnServiceTypes.setAdapter(arrayAdapter);
                          }
                      }
                  }
              }
        });
    }

    private void handleSpinnerSelectedItemChange(){

        spnServiceTypes = findViewById(R.id.availableServiceTypeSpinner);
        spnServiceTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (!(spnServiceTypes.getSelectedItem().toString().trim() == "")){

                    switch (spnServiceTypes.getSelectedItem().toString()){
                        case TEACH_USAGE_MOBILE_DEVICES:
                            selectedServiceTypeID = serviceTypeIDs[0];
                            break;
                        case TEACH_USAGE_WEB_APPS:
                            selectedServiceTypeID = serviceTypeIDs[1];
                            break;
                        case WALK_WITH_U:
                            selectedServiceTypeID = serviceTypeIDs[2];
                            break;
                        case PROVIDE_LIFT_TO_SOCIAL:
                            selectedServiceTypeID = serviceTypeIDs[3];
                            break;
                        case ASSIST_WITH_HOUSE_CLEANING:
                            selectedServiceTypeID = serviceTypeIDs[4];
                            break;
                        case ASSIST_WITH_HOUSE_MAINTENANCE:
                            selectedServiceTypeID = serviceTypeIDs[5];
                            break;
                        case ASSIST_WITH_GARDENING:
                            selectedServiceTypeID = serviceTypeIDs[6];
                            break;
                        case ASSIST_WITH_ERRANDS:
                            selectedServiceTypeID = serviceTypeIDs[7];
                            break;
                        case ASSIST_WITH_GROCERY_SHOPPING:
                            selectedServiceTypeID = serviceTypeIDs[8];
                            break;
                        case PROVIDE_LIFT_TO_SHOP :
                            selectedServiceTypeID = serviceTypeIDs[9];
                            break;
                        default:
                            selectedServiceTypeID = serviceTypeIDs[10];
                            break;
                    }
                }

                RecyclerView recyclerView = findViewById(R.id.rcvViewProvidedServices);
                recyclerView.removeAllViewsInLayout();

                // load the list by showing the recycler view
                loadVolunteerServicesRecyclerView(selectedServiceTypeID);
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

        firebaseFirestore = Utility.getFirebaseFireStoreInstance();
        firebaseAuth = Utility.getFirebaseAuthenticationInstance();

        // instantiate the array list of volunteers
        volunteerProfilesLists = new ArrayList<>();
        elderlyRequestsList = new ArrayList<>();

        // STEP 1. get the elderly requests;
        final CollectionReference elderlyCollection = firebaseFirestore.collection("elderly_requests")
                .document(firebaseAuth.getCurrentUser().getUid()).collection("requests");
        elderlyCollection.whereEqualTo("requestServiceTypeId", serviceTypeID)
                .whereEqualTo("elderlyId", firebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                            _tempList1.add(String.valueOf(qDoc.get("requestDaysForService")));
                            _tempList1.add(String.valueOf(qDoc.get("requestTimesForService")));
                            _tempList1.add(qDoc.getString("requestMessage"));

                            elderlyRequestsList.add(_tempList1);
                        }
                    }
                }

                RecyclerView viewVolunteerServices = findViewById(R.id.rcvViewProvidedServices);
                ElderlyProvideFeedbackAdapter provideFeedbackAdapter = new ElderlyProvideFeedbackAdapter(elderlyRequestsList,
                        ElderlyProvideFeedBackActivity.this );
                viewVolunteerServices.setAdapter(provideFeedbackAdapter);
                viewVolunteerServices.setLayoutManager(new LinearLayoutManager(ElderlyProvideFeedBackActivity.this));

            }
        });
    }


}