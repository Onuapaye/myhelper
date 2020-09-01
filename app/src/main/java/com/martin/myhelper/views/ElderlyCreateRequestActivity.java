package com.martin.myhelper.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.martin.myhelper.MainActivity;
import com.martin.myhelper.R;
import com.martin.myhelper.helpers.ElderlyCRUDHelper;
import com.martin.myhelper.helpers.ElderlyRequestsAdapter;
import com.martin.myhelper.helpers.ElderlyVolunteerCRUDHelper;
import com.martin.myhelper.helpers.Utility;
import com.martin.myhelper.model.ElderlyModel;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.martin.myhelper.helpers.Utility.ASSIST_WITH_ERRANDS;
import static com.martin.myhelper.helpers.Utility.ASSIST_WITH_GARDENING;
import static com.martin.myhelper.helpers.Utility.ASSIST_WITH_GROCERY_SHOPPING;
import static com.martin.myhelper.helpers.Utility.ASSIST_WITH_HOUSE_CLEANING;
import static com.martin.myhelper.helpers.Utility.ASSIST_WITH_HOUSE_MAINTENANCE;
import static com.martin.myhelper.helpers.Utility.CREATE_RECORD_SUCCESS_MSG;
import static com.martin.myhelper.helpers.Utility.CREATE_VOLUNTEER_PROFILE_SUCCESS_MSG;
import static com.martin.myhelper.helpers.Utility.PROVIDE_LIFT_TO_SHOP;
import static com.martin.myhelper.helpers.Utility.PROVIDE_LIFT_TO_SOCIAL;
import static com.martin.myhelper.helpers.Utility.REQUIRED_FIELD_TITLE;
import static com.martin.myhelper.helpers.Utility.SELECT_DAYS_FOR_SERVICE_MSG;
import static com.martin.myhelper.helpers.Utility.SELECT_TIMES_FOR_SERVICE_MSG;
import static com.martin.myhelper.helpers.Utility.TAKE_CARE_OF_PETS;
import static com.martin.myhelper.helpers.Utility.TEACH_USAGE_MOBILE_DEVICES;
import static com.martin.myhelper.helpers.Utility.TEACH_USAGE_WEB_APPS;
import static com.martin.myhelper.helpers.Utility.WALK_WITH_U;
import static com.martin.myhelper.helpers.Utility.getFirebaseAuthenticationInstance;
import static com.martin.myhelper.helpers.Utility.showInformationDialog;

public class ElderlyCreateRequestActivity extends AppCompatActivity {

    private ArrayList<String> _requestedVolunteerProfileData, _requestedVolunteerAccountData;
    private String[] serviceTypeIDs;
    private String serviceType, serviceTypeId, volunteerId, volunteerProfileId, elderlyId;
    private String[] elderlyAvailableDays, elderlyAvailableTimesOnDay;

    private boolean[] checkedDaysItemBoxes;
    private boolean[] checkedTimesItemBoxes;
    //private boolean[] checkedCallsItemBoxes;

    private ArrayList<Integer> userSelectedDaysItems = new ArrayList<>();
    private ArrayList<Integer> userSelectedTimesItems = new ArrayList<>();
    //private ArrayList<Integer> userSelectedCallsItems = new ArrayList<>();

    private ArrayList<String> actualPickedItemsForDays = new ArrayList<>();
    private ArrayList<String> actualPickedItemsForTimesForService = new ArrayList<>();
    //private ArrayList<String> actualPickedItemsForTimesForCalls = new ArrayList<>();

    private AlertDialog.Builder daysAlertBuilder, timesAlertBuilder, callsAlertBuilder;
    private AlertDialog daysAlertDialog, timesAlertDialog, callAlertDialog;

    private TextView _tvServiceDescription, _tvServiceDays, _tvServiceTimes, _tvServiceCalls, _tvServiceVolunteerName, _tvServiceVolunteerMobile, _tvSelectedServiceName;
    private TextView _tvElderlyDays, _tvElderlyTimes;
    private EditText _etElderlyRequestMessage;
    private Button _btnRequestService, _btnElderlyRequestDays, _btnElderlyRequestTimes;
    private CircularImageView _imgSelectedVolunteer;

    private AppCompatActivity appCompatActivity = ElderlyCreateRequestActivity.this;
    private Intent intent;

    private ElderlyCRUDHelper elderlyCRUDHelper;
    private ElderlyModel elderlyModel;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;

    private String[] _tempElderlyDAYS, _tempElderlyTIMES, _tempElderlyCALLS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elderly_create_request);

        this.getData();
        this.setData();


        this.handleSendRequestButtonOnClick();
        this.handleSignOutOnClick();
    }

    private void handleSignOutOnClick(){
        TextView signOut = findViewById(R.id.createProfileLogOutButton);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ElderlyVolunteerCRUDHelper.signOutUser(ElderlyCreateRequestActivity.this, MainActivity.class);
            }
        });
    }

    private void handleSendRequestButtonOnClick(){
        _btnRequestService = findViewById(R.id.btnSendServiceRequest);
        _btnRequestService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createRequest();
            }
        });
    }

    private void createRequest(){

        // create instance of objects
        elderlyCRUDHelper = new ElderlyCRUDHelper();
        elderlyModel = new ElderlyModel();
        
        firebaseAuth = getFirebaseAuthenticationInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        _etElderlyRequestMessage = findViewById(R.id.etElderlyRequestMessage);

        elderlyId = firebaseUser.getUid();

        // do validation first
        if(!this.validateFields()){
            return;
        }

        elderlyModel.setId(elderlyId);
        elderlyModel.setServiceRequestServiceTypeId(serviceTypeId);
        elderlyModel.setServiceRequestVolunteerId(volunteerId);
        elderlyModel.setServiceRequestVolunteerProfileId(volunteerProfileId);
        elderlyModel.setServiceRequestMessage(_etElderlyRequestMessage.getText().toString().trim());
        elderlyModel.setDaysForService(actualPickedItemsForDays);
        elderlyModel.setTimesForService(actualPickedItemsForTimesForService);
        //elderlyModel.setTimesForCalls(actualPickedItemsForTimesForCalls);

        elderlyCRUDHelper.createElderlyServiceRequest(ElderlyCreateRequestActivity.this, elderlyModel);

        // redirect to login activity
        intent = new Intent(ElderlyCreateRequestActivity.this, ElderlyHomeActivity.class);
        intent.putExtra("recordCreated", CREATE_RECORD_SUCCESS_MSG + "\n" + CREATE_VOLUNTEER_PROFILE_SUCCESS_MSG);
        startActivity(intent);
    }

    private void getData(){
        Intent intent;
        intent = getIntent();
        intent.getExtras();

        _requestedVolunteerAccountData = new ArrayList<>();
        _requestedVolunteerProfileData = new ArrayList<>();

        if (intent.hasExtra("profileRecordForRequest") && intent.hasExtra("accountRecordForRequest")){
            _requestedVolunteerProfileData = intent.getStringArrayListExtra("profileRecordForRequest");
            _requestedVolunteerAccountData = intent.getStringArrayListExtra("accountRecordForRequest");
        }
    }


    private  void setData(){

       _tvServiceDescription = findViewById(R.id.tvSelectedServiceDescription);
       _tvServiceDays = findViewById(R.id.tvSelectedVolunteerDays);
       _tvServiceTimes = findViewById(R.id.tvSelectedVolunteerTimes);
       _tvServiceCalls = findViewById(R.id.tvSelectedVolunteerCalls);
       _tvServiceVolunteerName = findViewById(R.id.tvSelectedVolunteer);
       _tvServiceVolunteerMobile = findViewById(R.id.tvSelectedVolunteerMobile);
       _tvSelectedServiceName = findViewById(R.id.tvSelectedServiceName);

       // profile details
        volunteerProfileId = _requestedVolunteerProfileData.get(0);
        serviceTypeId = _requestedVolunteerProfileData.get(1);
        volunteerId = _requestedVolunteerProfileData.get(2);
        _tvServiceDescription.setText(_requestedVolunteerProfileData.get(3));
        _tvServiceDays.setText(_requestedVolunteerProfileData.get(4).replaceAll("(^\\[|\\]$)", ""));
        _tvServiceTimes.setText(_requestedVolunteerProfileData.get(5).replaceAll("(^\\[|\\]$)", ""));
        _tvServiceCalls.setText(_requestedVolunteerProfileData.get(6).replaceAll("(^\\[|\\]$)", ""));

        // account details
        _tvServiceVolunteerName.setText(_requestedVolunteerAccountData.get(0) + ", " + _requestedVolunteerAccountData.get(1).toUpperCase());
        _tvServiceVolunteerMobile.setText(_requestedVolunteerAccountData.get(3));
        this.loadProfilePhotoIntoImageView(_requestedVolunteerProfileData.get(2), _requestedVolunteerAccountData.get(4));

        serviceTypeIDs = getResources().getStringArray(R.array.serviceTypeIDs);

        //String serviceTypeCode = _requestedVolunteerProfileData.get(1);
        firebaseFirestore = Utility.getFirebaseFireStoreInstance();
        DocumentReference reference = firebaseFirestore.collection("service_types").document(_requestedVolunteerProfileData.get(1));
        reference
            //.whereEqualTo("id", _requestedVolunteerProfileData.get(1))
            .get()
            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot snapshot) {
                    if(snapshot.exists()){
                        _tvSelectedServiceName.setText(snapshot.getString("service_name"));
                    }
                }
            });

        /*if (serviceTypeCode == serviceTypeIDs[0]){
            serviceType = TEACH_USAGE_MOBILE_DEVICES;
        } else if (serviceTypeCode == serviceTypeIDs[1]){
            serviceType = TEACH_USAGE_WEB_APPS;
        } else if (serviceTypeCode == serviceTypeIDs[2]){
            serviceType = WALK_WITH_U;
        } else if (serviceTypeCode == serviceTypeIDs[3]){
            serviceType = PROVIDE_LIFT_TO_SOCIAL;
        } else if (serviceTypeCode == serviceTypeIDs[4]){
            serviceType = ASSIST_WITH_HOUSE_CLEANING;
        } else if (serviceTypeCode == serviceTypeIDs[5]) {
            serviceType = ASSIST_WITH_HOUSE_MAINTENANCE;
        } else if(serviceTypeCode == serviceTypeIDs[6]) {
            serviceType = ASSIST_WITH_GARDENING;
        } else if (serviceTypeCode == serviceTypeIDs[7]) {
            serviceType = ASSIST_WITH_ERRANDS;
        } else if ( serviceTypeCode == serviceTypeIDs[8]) {
            serviceType = ASSIST_WITH_GROCERY_SHOPPING;
        } else if(serviceTypeCode == serviceTypeIDs[9]) {
            serviceType = PROVIDE_LIFT_TO_SHOP;
        } else {
            serviceType = TAKE_CARE_OF_PETS;
        }

        _tvSelectedServiceName.setText(serviceType);*/

        // START
        // 1. GET THE DAYS SET BY THE VOLUNTEER IN HIS/PROFILES FOR THE SPINNER
        List<String> volDays = new ArrayList<>();
        List<String> callsList;
        String[] d = _requestedVolunteerProfileData.get(4).split("\\s*,\\s*");
        for(int i = 0; i < d.length; i++){
            callsList = new ArrayList<>();
            callsList.add(d[i].replaceAll("(^\\[|\\]$)", ""));
            volDays.add(callsList.toString().replaceAll("(^\\[|\\]$)", ""));
        }

        // convert the list into a normal array
        _tempElderlyDAYS = new String[volDays.size()];
        volDays.toArray(_tempElderlyDAYS);

        this.selectDaysForServiceRequest(_tempElderlyDAYS);
        // END

        // START
        // 2. GET THE TIMES FOR SERVICE SET BY THE VOLUNTEER IN HIS/PROFILES FOR THE SPINNER
        List<String> volTimes = new ArrayList<>();
        List<String> timesList;
        String[] t = _requestedVolunteerProfileData.get(5).split("\\s*,\\s*");
        for(int i = 0; i < t.length; i++){
            timesList = new ArrayList<>();
            timesList.add(t[i].replaceAll("(^\\[|\\]$)", ""));
            volTimes.add(timesList.toString().replaceAll("(^\\[|\\]$)", ""));
        }

        // convert the list into a normal array
        _tempElderlyTIMES = new String[volTimes.size()];
        volTimes.toArray(_tempElderlyTIMES);

        this.selectTimesOnDaysForServiceRequest(_tempElderlyTIMES);
        // END
    }

    private void selectDaysForServiceRequest(String[] days){

        _btnElderlyRequestDays = findViewById(R.id.btnElderlyDays);
        _tvElderlyDays = findViewById(R.id.tvElderlyDays);

        elderlyAvailableDays = days;// getResources().getStringArray(R.array.availableDays);
        checkedDaysItemBoxes = new boolean[elderlyAvailableDays.length];

        _btnElderlyRequestDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                daysAlertBuilder = new AlertDialog.Builder(ElderlyCreateRequestActivity.this);
                daysAlertBuilder.setTitle("Days for Service");
                daysAlertBuilder.setMultiChoiceItems(elderlyAvailableDays, checkedDaysItemBoxes, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isItemChecked) {

                        if (isItemChecked) {
                            userSelectedDaysItems.add(position);
                        } else {
                            userSelectedDaysItems.remove(Integer.valueOf(position));
                        }
                    }
                });

                daysAlertBuilder.setCancelable(false);
                daysAlertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String item = "";
                        actualPickedItemsForDays.clear();

                        for (int j = 0; j < userSelectedDaysItems.size(); j++){
                            item += elderlyAvailableDays[userSelectedDaysItems.get(j)];

                            // check if the item selected by the user is not the last
                            // item and append a comma
                            if (j != userSelectedDaysItems.size() - 1){
                                item += ", ";
                            }

                            // set the items checked to the textView
                            _tvElderlyDays.setText(item);
                            actualPickedItemsForDays.add(elderlyAvailableDays[userSelectedDaysItems.get(j)]);
                        }
                    }
                });

                daysAlertBuilder.setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                daysAlertBuilder.setNeutralButton("Clear", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        for (int k = 0; k < checkedDaysItemBoxes.length; k++){
                            checkedDaysItemBoxes[k] = false;
                            userSelectedDaysItems.clear();

                            _tvElderlyDays.setText("");
                            actualPickedItemsForDays.clear();
                        }
                    }
                });

                daysAlertDialog = daysAlertBuilder.create();
                daysAlertDialog.show();
            }

        });
    }

    private void selectTimesOnDaysForServiceRequest(String[] times){

        _btnElderlyRequestTimes = findViewById(R.id.btnElderlyTimes);
        _tvElderlyTimes = findViewById(R.id.tvElderlyTimes);

        elderlyAvailableTimesOnDay = times; // getResources().getStringArray(R.array.availableTimes);
        checkedTimesItemBoxes = new boolean[elderlyAvailableTimesOnDay.length];

        _btnElderlyRequestTimes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                timesAlertBuilder = new AlertDialog.Builder(ElderlyCreateRequestActivity.this);
                timesAlertBuilder.setTitle("Time for Service");
                timesAlertBuilder.setMultiChoiceItems(elderlyAvailableTimesOnDay, checkedTimesItemBoxes, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isItemChecked) {

                        if (isItemChecked) {
                            userSelectedTimesItems.add(position);
                        } else {
                            userSelectedTimesItems.remove(Integer.valueOf(position));
                        }
                    }
                });

                timesAlertBuilder.setCancelable(false);
                timesAlertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String item = "";
                        actualPickedItemsForTimesForService.clear();
                        for (int j = 0; j < userSelectedTimesItems.size(); j++){
                            item += elderlyAvailableTimesOnDay[userSelectedTimesItems.get(j)];

                            // check if the item selected by the user is not the last
                            // item and append a comma
                            if (j != userSelectedTimesItems.size() - 1){
                                item += ", ";
                            }

                            // set the items checked to the textView
                            _tvElderlyTimes.setText(item);
                            actualPickedItemsForTimesForService.add(elderlyAvailableTimesOnDay[userSelectedTimesItems.get(j)]);
                        }
                    }
                });

                timesAlertBuilder.setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                timesAlertBuilder.setNeutralButton("Clear", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        for (int k = 0; k < checkedTimesItemBoxes.length; k++){
                            checkedTimesItemBoxes[k] = false;
                            userSelectedTimesItems.clear();

                            _tvElderlyTimes.setText("");
                            actualPickedItemsForTimesForService.clear();
                        }
                    }
                });

                timesAlertDialog = timesAlertBuilder.create();
                timesAlertDialog.show();
            }

        });
    }

    private boolean validateFields(){

        _etElderlyRequestMessage = findViewById(R.id.etElderlyRequestMessage);

        if (elderlyId == "" || elderlyId.isEmpty()){
            showInformationDialog(REQUIRED_FIELD_TITLE, "No Elderly ID is defined for this request.", appCompatActivity);
            return false;
        }

        if (volunteerId == "" || volunteerId.isEmpty()){
            showInformationDialog(REQUIRED_FIELD_TITLE, "No Volunteer is selected for this request.", appCompatActivity);
            return false;
        }

        if (volunteerProfileId == "" || volunteerProfileId.isEmpty()){
            showInformationDialog(REQUIRED_FIELD_TITLE, "No Volunteer Service is selected for this request.", appCompatActivity);
            return false;
        }

        if (serviceTypeId == "" || serviceTypeId.isEmpty()){
            showInformationDialog(REQUIRED_FIELD_TITLE, "No Service Type is selected for this request.", appCompatActivity);
            return false;
        }

        if (_etElderlyRequestMessage.getText().toString().trim() == "" || _etElderlyRequestMessage.getText().toString().isEmpty()){
            showInformationDialog(REQUIRED_FIELD_TITLE, "Please enter the description for this profile", appCompatActivity);
            return false;
        }

        if (actualPickedItemsForDays.size() <= 0){
            showInformationDialog(REQUIRED_FIELD_TITLE, SELECT_DAYS_FOR_SERVICE_MSG, appCompatActivity);
            return false;
        }

        if (actualPickedItemsForTimesForService.size() <= 0){
            showInformationDialog(REQUIRED_FIELD_TITLE, SELECT_TIMES_FOR_SERVICE_MSG, appCompatActivity);
            return false;
        }

        /*if (actualPickedItemsForTimesForCalls.size() <= 0){
            showInformationDialog(REQUIRED_FIELD_TITLE, SELECT_TIMES_FOR_CALLS_MSG , appCompatActivity);
            return false;
        }*/

        return true;
    }

    private void loadProfilePhotoIntoImageView(String imageName, String imageExtension) {

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference("images/").child(imageName + "." + imageExtension);

        storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()){

                    Uri uri = task.getResult();

                    CircularImageView imageView = findViewById(R.id.civSelectedVolunteer);
                    Picasso.get().load(uri).into(imageView);
                }
            }
        });
    }
}