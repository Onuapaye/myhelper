package com.martin.myhelper.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.martin.myhelper.R;
import com.martin.myhelper.helpers.ElderlyRequestsAdapter;
import com.martin.myhelper.helpers.ElderlyViewVolunteerServicesAdapter;
import com.martin.myhelper.helpers.Utility;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

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
    /*String[] s1, s2, s3;

    int images[] = {R.drawable.man, R.drawable.bis_smile, R.drawable.short_shave_1,
            R.drawable.birthday_balloons2, R.drawable.short_shave_2,
            R.drawable.short_shave_3, R.drawable.woman};*/

    private ArrayList<ArrayList<String>> volunteerProfilesLists, volunteerAccountList;
    private ArrayList<String> _tempList1;

    private String[] serviceTypes;
    private String[] serviceTypeIDs;
    private String selectedServiceTypeID;
    private Spinner spnServiceTypes;
    private ArrayAdapter<String> arrayAdapter;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    ///private CircularImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elderly_view_provided_service);

        this.loadServicesTypes();

        this.handleSpinnerSelectedItemChange();
    }

    /***
     * Loads all the service types from an array into the spinner control
     */
    private void loadServicesTypes(){

        spnServiceTypes = findViewById(R.id.availableServiceTypeSpinner);
        serviceTypes = getResources().getStringArray(R.array.serviceTypes);
        serviceTypeIDs = getResources().getStringArray(R.array.serviceTypeIDs);

        arrayAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item,  serviceTypes);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spnServiceTypes.setAdapter(arrayAdapter);
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
        //firebaseAuth = Utility.getFirebaseAuthenticationInstance();

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
                                    _tempList1.add(String.valueOf(ds1.get("daysForService")));
                                    _tempList1.add(String.valueOf(ds1.get("timesForService")));
                                    _tempList1.add(String.valueOf(ds1.get("timesForCalls")));

                                    volunteerProfilesLists.add(_tempList1);
                                }
                            }
                        }
                        RecyclerView viewVolunteerServices = findViewById(R.id.rcvViewProvidedServices);

                        ElderlyViewVolunteerServicesAdapter
                                viewVolunteerServicesAdapter = new ElderlyViewVolunteerServicesAdapter(
                                        ElderlyViewVolunteerServicesActivity.this, volunteerProfilesLists);
                        viewVolunteerServices.
                                setAdapter(viewVolunteerServicesAdapter);
                        viewVolunteerServices.
                                setLayoutManager(new LinearLayoutManager(ElderlyViewVolunteerServicesActivity.this));
                        }
                    });
                }
            }
            }
        });
    }


    /*private void loadProfilePhotoIntoImageView(String imageName, String imageExtension) {

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference("images/").child(imageName + "." + imageExtension);

        storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()){

                    Uri uri = task.getResult();

                    CircularImageView imageView = findViewById(R.id.imgViewVolunteerImage);
                    Picasso.get().load(uri).into(imageView);
                }
            }
        });
    }*/

}