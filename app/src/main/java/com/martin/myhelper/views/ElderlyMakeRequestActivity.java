package com.martin.myhelper.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.solver.widgets.ConstraintHorizontalLayout;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.martin.myhelper.MainActivity;
import com.martin.myhelper.R;
import com.martin.myhelper.helpers.ElderlyCRUDHelper;
import com.martin.myhelper.helpers.ElderlyVolunteerCRUDHelper;
import com.martin.myhelper.helpers.Utility;
import com.martin.myhelper.model.ElderlyModel;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.martin.myhelper.helpers.Utility.CREATE_RECORD_SUCCESS_MSG;
import static com.martin.myhelper.helpers.Utility.CREATE_VOLUNTEER_PROFILE_SUCCESS_MSG;
import static com.martin.myhelper.helpers.Utility.REQUIRED_FIELD_TITLE;
import static com.martin.myhelper.helpers.Utility.SELECT_DAYS_FOR_SERVICE_MSG;
import static com.martin.myhelper.helpers.Utility.SELECT_TIMES_FOR_SERVICE_MSG;
import static com.martin.myhelper.helpers.Utility.getFirebaseAuthenticationInstance;
import static com.martin.myhelper.helpers.Utility.showInformationDialog;

public class ElderlyMakeRequestActivity extends AppCompatActivity {

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

    private TextView _tvServiceDescription, _tvServiceDays, _tvServiceTimes, _tvServiceCalls,
            _tvServiceVolunteerName, _tvServiceVolunteerMobile, _tvSelectedServiceName;
    private TextView _tvElderlyDays, _tvElderlyTimes;
    private EditText _etElderlyRequestMessage;
    private Button _btnRequestService, _btnElderlyRequestDays, _btnElderlyRequestTimes;
    private CircularImageView _imgSelectedVolunteer;

    private AppCompatActivity appCompatActivity = ElderlyMakeRequestActivity.this;
    private Intent intent;

    private ElderlyCRUDHelper elderlyCRUDHelper;
    private ElderlyModel elderlyModel;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;

    private LinearLayout lLayoutMondayCalls, lLayoutMondayTimes, lLayoutTuesdayCalls, lLayoutTuesdayTimes,
            lLayoutWednesdayCalls, lLayoutWednesdayTimes, lLayoutThursdayCalls, lLayoutThursdayTimes,
            lLayoutFridayCalls, lLayoutFridayTimes, lLayoutSaturdayCalls, lLayoutSaturdayTimes,
            lLayoutSundayCalls, lLayoutSundayTimes;

    private CardView cvMondayMains, cvTuesdayMains, cvWednesdayMains, cvThursdayMains,
            cvFridayMains, cvSaturdayMains, cvSundayMains;

    private HashMap<Integer, String> hashMapMondayCalls = new HashMap<>(), hashMapMondayTimes = new HashMap<>();
    private HashMap<Integer, String> hashMapTuesdayCalls = new HashMap<>(), hashMapTuesdayTimes = new HashMap<>();
    private HashMap<Integer, String> hashMapWednesdayCalls = new HashMap<>(), hashMapWednesdayTimes = new HashMap<>();
    private HashMap<Integer, String> hashMapThursdayCalls = new HashMap<>(), hashMapThursdayTimes = new HashMap<>();
    private HashMap<Integer, String> hashMapFridayCalls = new HashMap<>(), hashMapFridayTimes = new HashMap<>();
    private HashMap<Integer, String> hashMapSaturdayCalls = new HashMap<>(), hashMapSaturdayTimes = new HashMap<>();
    private HashMap<Integer, String> hashMapSundayCalls = new HashMap<>(), hashMapSundayTimes = new HashMap<>();

    private ArrayList<String> _actualSelectedMondayTimes = new ArrayList<>(), _actualSelectedMondayCalls = new ArrayList<>();
    private ArrayList<String> _actualSelectedTuesdayTimes = new ArrayList<>(), _actualSelectedTuesdayCalls = new ArrayList<>();
    private ArrayList<String> _actualSelectedWednesdayTimes = new ArrayList<>(), _actualSelectedWednesdayCalls = new ArrayList<>();
    private ArrayList<String> _actualSelectedThursdayTimes = new ArrayList<>(), _actualSelectedThursdayCalls = new ArrayList<>();
    private ArrayList<String> _actualSelectedFridayTimes = new ArrayList<>(), _actualSelectedFridayCalls = new ArrayList<>();
    private ArrayList<String> _actualSelectedSaturdayTimes = new ArrayList<>(), _actualSelectedSaturdayCalls = new ArrayList<>();
    private ArrayList<String> _actualSelectedSundayTimes = new ArrayList<>(), _actualSelectedSundayCalls = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elderly_make_request);

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
                ElderlyVolunteerCRUDHelper.signOutUser(ElderlyMakeRequestActivity.this, MainActivity.class);
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

        this.setDayTimesAndCalls();

        elderlyModel.setId(elderlyId);
        elderlyModel.setServiceRequestServiceTypeId(serviceTypeId);
        elderlyModel.setServiceRequestVolunteerId(volunteerId);
        elderlyModel.setServiceRequestVolunteerProfileId(volunteerProfileId);
        elderlyModel.setServiceRequestMessage(_etElderlyRequestMessage.getText().toString().trim());

        elderlyModel.setMondayTimes(_actualSelectedMondayTimes);
        elderlyModel.setMondayCalls(_actualSelectedMondayCalls);

        elderlyModel.setTuesdayCalls(_actualSelectedTuesdayCalls);
        elderlyModel.setTuesdayTimes(_actualSelectedTuesdayTimes);

        elderlyModel.setWednesdayCalls(_actualSelectedWednesdayCalls);
        elderlyModel.setWednesdayTimes(_actualSelectedWednesdayTimes);

        elderlyModel.setThursdayCalls(_actualSelectedThursdayCalls);
        elderlyModel.setThursdayTimes(_actualSelectedThursdayTimes);

        elderlyModel.setFridayCalls(_actualSelectedFridayCalls);
        elderlyModel.setFridayTimes(_actualSelectedFridayTimes);

        elderlyModel.setSaturdayCalls(_actualSelectedSaturdayCalls);
        elderlyModel.setSaturdayTimes(_actualSelectedSaturdayTimes);

        elderlyModel.setSundayCalls(_actualSelectedSundayCalls);
        elderlyModel.setSundayTimes(_actualSelectedSundayTimes);

        elderlyCRUDHelper.makeElderlyServiceRequest(ElderlyMakeRequestActivity.this, elderlyModel);

        // redirect to login activity
        intent = new Intent(ElderlyMakeRequestActivity.this, ElderlyHomeActivity.class);
        intent.putExtra("recordCreated", CREATE_RECORD_SUCCESS_MSG + "\n" + CREATE_VOLUNTEER_PROFILE_SUCCESS_MSG);
        startActivity(intent);
    }

    private ArrayList<String> convertHashMapToArrayList(HashMap<Integer, String> hashMap){
        ArrayList<String> arrayList = new ArrayList<>(hashMap.values());
        return  arrayList;
    }

    private void setDayTimesAndCalls(){

        _actualSelectedMondayCalls = this.convertHashMapToArrayList(hashMapMondayCalls);
        _actualSelectedMondayTimes = this.convertHashMapToArrayList(hashMapMondayTimes);

        _actualSelectedTuesdayCalls = this.convertHashMapToArrayList(hashMapTuesdayCalls);
        _actualSelectedTuesdayTimes = this.convertHashMapToArrayList(hashMapTuesdayTimes);

        _actualSelectedWednesdayCalls = this.convertHashMapToArrayList(hashMapWednesdayCalls);
        _actualSelectedWednesdayTimes = this.convertHashMapToArrayList(hashMapWednesdayTimes);

        _actualSelectedThursdayCalls = this.convertHashMapToArrayList(hashMapThursdayCalls);
        _actualSelectedThursdayTimes = this.convertHashMapToArrayList(hashMapThursdayTimes);

        _actualSelectedFridayCalls = this.convertHashMapToArrayList(hashMapFridayCalls);
        _actualSelectedFridayTimes = this.convertHashMapToArrayList(hashMapFridayTimes);

        _actualSelectedSaturdayCalls = this.convertHashMapToArrayList(hashMapSaturdayCalls);
        _actualSelectedSaturdayTimes = this.convertHashMapToArrayList(hashMapSaturdayTimes);

        _actualSelectedSundayCalls = this.convertHashMapToArrayList(hashMapSundayCalls);
        _actualSelectedSundayTimes = this.convertHashMapToArrayList(hashMapSundayTimes);

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

        String _allTimes = "", _allCalls = "";

        this.initializeWidgets();

        // profile details
        volunteerProfileId = _requestedVolunteerProfileData.get(0);
        serviceTypeId = _requestedVolunteerProfileData.get(1);
        volunteerId = _requestedVolunteerProfileData.get(2);
        _tvServiceDescription.setText(_requestedVolunteerProfileData.get(3));

        // set times
        if(!_requestedVolunteerProfileData.get(4).replaceAll("(^\\[|\\]$)", "").equals("")){
            _allCalls += "Monday\n" + _requestedVolunteerProfileData.get(4).replaceAll("(^\\[|\\]$)", "") + " \n\n";
            _allTimes += "Monday\n" + _requestedVolunteerProfileData.get(5).replaceAll("(^\\[|\\]$)", "") + " \n\n";

            createAndShowDayCheckBoxes(cvMondayMains, 4, 5, lLayoutMondayCalls,
                    lLayoutMondayTimes, 1000, 2000);

        } else {
            hideCardView(cvMondayMains);
        }

        if(!_requestedVolunteerProfileData.get(6).replaceAll("(^\\[|\\]$)", "").equals("") ){
            _allCalls += "Tuesday\n" + _requestedVolunteerProfileData.get(6).replaceAll("(^\\[|\\]$)", "") + " \n\n";
            _allTimes += "Tuesday\n" + _requestedVolunteerProfileData.get(7).replaceAll("(^\\[|\\]$)", "") + " \n\n";

            createAndShowDayCheckBoxes(cvTuesdayMains, 6, 7, lLayoutTuesdayCalls,
                    lLayoutTuesdayTimes, 3000, 4000);

        } else {hideCardView(cvTuesdayMains);}

        if(!_requestedVolunteerProfileData.get(8).replaceAll("(^\\[|\\]$)", "").equals("")){
            _allCalls += "Wednesday\n" + _requestedVolunteerProfileData.get(8).replaceAll("(^\\[|\\]$)", "") + " \n\n";
            _allTimes += "Wednesday\n" + _requestedVolunteerProfileData.get(9).replaceAll("(^\\[|\\]$)", "") + " \n\n";

            createAndShowDayCheckBoxes(cvWednesdayMains, 8, 9, lLayoutWednesdayCalls,
                    lLayoutWednesdayTimes, 5000, 6000);

        } else { this.hideCardView(cvWednesdayMains);}

        if(!_requestedVolunteerProfileData.get(10).replaceAll("(^\\[|\\]$)", "").equals("")){
            _allCalls += "Thursday\n" + _requestedVolunteerProfileData.get(10).replaceAll("(^\\[|\\]$)", "") + " \n\n";
            _allTimes += "Thursday\n" + _requestedVolunteerProfileData.get(11).replaceAll("(^\\[|\\]$)", "") + " \n\n";

            createAndShowDayCheckBoxes(cvThursdayMains, 10, 11, lLayoutThursdayCalls,
                    lLayoutThursdayTimes, 7000, 8000);

        } else { hideCardView(cvThursdayMains);}

        if(!_requestedVolunteerProfileData.get(12).replaceAll("(^\\[|\\]$)", "").equals("")){
            _allCalls += "Friday\n" + _requestedVolunteerProfileData.get(12).replaceAll("(^\\[|\\]$)", "") + " \n\n";
            _allTimes += "Friday\n" + _requestedVolunteerProfileData.get(13).replaceAll("(^\\[|\\]$)", "") + " \n\n";

            createAndShowDayCheckBoxes(cvFridayMains, 12, 13, lLayoutFridayCalls,
                    lLayoutFridayTimes, 9000, 10000);

        } else { this.hideCardView(cvFridayMains); }

        if(!_requestedVolunteerProfileData.get(14).replaceAll("(^\\[|\\]$)", "").equals("")){
            _allCalls += "Saturday\n" + _requestedVolunteerProfileData.get(14).replaceAll("(^\\[|\\]$)", "") + " \n\n";
            _allTimes += "Saturday\n" + _requestedVolunteerProfileData.get(15).replaceAll("(^\\[|\\]$)", "") + " \n\n";

            createAndShowDayCheckBoxes(cvSaturdayMains, 14, 15, lLayoutSaturdayCalls,
                    lLayoutSaturdayTimes, 10100, 10200);

        } else {
            this.hideCardView(cvSaturdayMains);
        }

        if(!_requestedVolunteerProfileData.get(16).replaceAll("(^\\[|\\]$)", "").equals("")){
            _allCalls += "Sunday\n" + _requestedVolunteerProfileData.get(16).replaceAll("(^\\[|\\]$)", "");
            _allTimes += "Sunday\n" + _requestedVolunteerProfileData.get(17).replaceAll("(^\\[|\\]$)", "") + " \n\n";

            createAndShowDayCheckBoxes(cvSundayMains, 16, 17, lLayoutSundayCalls,
                    lLayoutSundayTimes, 10300, 10400);

        } else {
            this.hideCardView(cvSundayMains);
        }

        _tvServiceTimes.setText(_allTimes);
        _tvServiceCalls.setText(_allCalls);

        // account details
        _tvServiceVolunteerName.setText(String.format("%s, %s", _requestedVolunteerAccountData.get(0),
                _requestedVolunteerAccountData.get(1).toUpperCase()));

        _tvServiceVolunteerMobile.setText(_requestedVolunteerAccountData.get(3));

        this.loadProfilePhotoIntoImageView(_requestedVolunteerProfileData.get(2),
                _requestedVolunteerAccountData.get(4));

        serviceTypeIDs = getResources().getStringArray(R.array.serviceTypeIDs);
        firebaseFirestore = Utility.getFirebaseFireStoreInstance();

        DocumentReference reference = firebaseFirestore.collection("service_types")
                .document(_requestedVolunteerProfileData.get(1));

        reference.get()
        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                if(snapshot.exists()){
                    _tvSelectedServiceName.setText(snapshot.getString("service_name"));
                }
            }
        });

    }

    private void hideCardView(CardView cardView) {
        cardView.setVisibility(View.GONE);
    }

    private void createAndShowDayCheckBoxes(CardView cardView, int callPosition, int timePosition,
                                            LinearLayout callLayout, LinearLayout timeLayout,
                                            int callIdRange, int timeIdRange) {

        cardView.setVisibility(View.VISIBLE);

        String[] _dayCallsArray = _requestedVolunteerProfileData.get(callPosition).replaceAll("(^\\[|\\]$)", "").split(",");
        String[] _dayTimesArray = _requestedVolunteerProfileData.get(timePosition).replaceAll("(^\\[|\\]$)", "").split(",");

        for (int i = 0; i < _dayCallsArray.length; i++) {

            CheckBox _thisCheckBox = new CheckBox(this);
            _thisCheckBox.setId(callIdRange + i);
            _thisCheckBox.setText(_dayCallsArray[i].trim());
            _thisCheckBox.setOnClickListener(getCallCheckedItem(_thisCheckBox));
            callLayout.addView(_thisCheckBox);
        }

        for (int i = 0; i < _dayTimesArray.length; i++) {

            CheckBox _thisCheckBox = new CheckBox(this);
            _thisCheckBox.setId(timeIdRange + i);
            _thisCheckBox.setText(_dayTimesArray[i].trim());
            _thisCheckBox.setOnClickListener(getTimeCheckedItem(_thisCheckBox));
            timeLayout.addView(_thisCheckBox);
        }
    }

    View.OnClickListener getCallCheckedItem(final CheckBox checkBox){
        return  new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int id, checkBoxId;

                if (checkBox.isChecked()) {

                    id = ((View)checkBox.getParent()).getId();
                    checkBoxId = checkBox.getId();
                    String checkBoxText = checkBox.getText().toString();

                    if (id == lLayoutMondayCalls.getId()){
                        putItemIntoHashMap(checkBoxId, checkBoxText, hashMapMondayCalls);
                    } else if (id == lLayoutTuesdayCalls.getId()){
                        putItemIntoHashMap(checkBoxId, checkBoxText, hashMapTuesdayCalls);
                    } else if (id == lLayoutWednesdayCalls.getId()){
                        putItemIntoHashMap(checkBoxId, checkBoxText, hashMapWednesdayCalls);
                    } else if (id == lLayoutThursdayCalls.getId()){
                        putItemIntoHashMap(checkBoxId, checkBoxText, hashMapThursdayCalls);
                    } else if (id == lLayoutFridayCalls.getId()){
                        putItemIntoHashMap(checkBoxId, checkBoxText, hashMapFridayCalls);
                    } else if (id == lLayoutSaturdayCalls.getId()){
                        putItemIntoHashMap(checkBoxId, checkBoxText, hashMapSaturdayCalls);
                    } else {
                        putItemIntoHashMap(checkBoxId, checkBoxText, hashMapSundayCalls);
                    }

                } else {

                    id = ((View)checkBox.getParent()).getId();
                    checkBoxId = checkBox.getId();

                    if (id == lLayoutMondayCalls.getId()){
                        removeItemFromHashMap(checkBoxId, hashMapMondayCalls);
                    } else if (id == lLayoutTuesdayCalls.getId()){
                        removeItemFromHashMap(checkBoxId, hashMapTuesdayCalls);
                    } else if (id == lLayoutWednesdayCalls.getId()){
                        removeItemFromHashMap(checkBoxId, hashMapWednesdayCalls);
                    } else if (id == lLayoutThursdayCalls.getId()){
                        removeItemFromHashMap(checkBoxId, hashMapThursdayCalls);
                    } else if (id == lLayoutFridayCalls.getId()){
                        removeItemFromHashMap(checkBoxId, hashMapFridayCalls);
                    } else if (id == lLayoutSaturdayCalls.getId()){
                        removeItemFromHashMap(checkBoxId, hashMapSaturdayCalls);
                    } else {
                        removeItemFromHashMap(checkBoxId, hashMapSundayCalls);
                    }

                }
            }
        };
    }

    View.OnClickListener getTimeCheckedItem(final CheckBox checkBox){
        return  new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id, checkBoxId;

                if (checkBox.isChecked()) {

                    id = ((View)checkBox.getParent()).getId();
                    checkBoxId = checkBox.getId();
                    String checkBoxText = checkBox.getText().toString();

                    if (id == lLayoutMondayTimes.getId()){
                        putItemIntoHashMap(checkBoxId, checkBoxText, hashMapMondayTimes);
                    } else if (id == lLayoutTuesdayTimes.getId()){
                        putItemIntoHashMap(checkBoxId, checkBoxText, hashMapTuesdayTimes);
                    } else if (id == lLayoutWednesdayTimes.getId()){
                        putItemIntoHashMap(checkBoxId, checkBoxText, hashMapWednesdayTimes);
                    } else if (id == lLayoutThursdayTimes.getId()){
                        putItemIntoHashMap(checkBoxId, checkBoxText, hashMapThursdayTimes);
                    } else if (id == lLayoutFridayTimes.getId()){
                        putItemIntoHashMap(checkBoxId, checkBoxText, hashMapFridayTimes);
                    } else if (id == lLayoutSaturdayTimes.getId()){
                        putItemIntoHashMap(checkBoxId, checkBoxText, hashMapSaturdayTimes);
                    } else {
                        putItemIntoHashMap(checkBoxId, checkBoxText, hashMapSundayTimes);
                    }

                } else {

                    id = ((View)checkBox.getParent()).getId();
                    checkBoxId = checkBox.getId();

                    if (id == lLayoutMondayTimes.getId()){
                        removeItemFromHashMap(checkBoxId, hashMapMondayTimes);
                    } else if (id == lLayoutTuesdayTimes.getId()){
                        removeItemFromHashMap(checkBoxId, hashMapTuesdayTimes);
                    } else if (id == lLayoutWednesdayTimes.getId()){
                        removeItemFromHashMap(checkBoxId, hashMapWednesdayTimes);
                    } else if (id == lLayoutThursdayTimes.getId()){
                        removeItemFromHashMap(checkBoxId, hashMapThursdayTimes);
                    } else if (id == lLayoutFridayTimes.getId()){
                        removeItemFromHashMap(checkBoxId, hashMapFridayTimes);
                    } else if (id == lLayoutSaturdayTimes.getId()){
                        removeItemFromHashMap(checkBoxId, hashMapSaturdayTimes);
                    } else {
                        removeItemFromHashMap(checkBoxId, hashMapSundayTimes);
                    }

                }
            }
        };
    }

    private void removeItemFromHashMap(int checkBoxId, @NotNull HashMap<Integer, String> hashMap) {
        if (hashMap.containsKey(checkBoxId)) {
            hashMap.remove(checkBoxId);
        }
    }

    private void putItemIntoHashMap(int checkBoxId, String checkBoxText, @NotNull HashMap<Integer, String> hashMap) {
        if (!hashMap.containsKey(checkBoxId)) {
            hashMap.put(checkBoxId, checkBoxText);
        } else {
            Utility.showInformationDialog("DUPLICATE ITEM FOUND", " You have already added the item " + checkBoxText, this);
        }
    }

    private void initializeWidgets(){

        _tvServiceDescription = findViewById(R.id.tvSelectedServiceDescription);
        _tvServiceTimes = findViewById(R.id.tvSelectedVolunteerTimes);
        _tvServiceCalls = findViewById(R.id.tvSelectedVolunteerCalls);
        _tvServiceVolunteerName = findViewById(R.id.tvSelectedVolunteer);
        _tvServiceVolunteerMobile = findViewById(R.id.tvSelectedVolunteerMobile);
        _tvSelectedServiceName = findViewById(R.id.tvSelectedServiceName);

        lLayoutMondayCalls = findViewById(R.id.layMondayCalls);
        lLayoutMondayTimes = findViewById(R.id.layMondayTimes);
        lLayoutTuesdayCalls = findViewById(R.id.layTuesdayCalls);
        lLayoutTuesdayTimes = findViewById(R.id.layTuesdayTimes);
        lLayoutWednesdayCalls = findViewById(R.id.layWednesdayCalls);
        lLayoutWednesdayTimes = findViewById(R.id.layWednesdayTimes);
        lLayoutThursdayCalls = findViewById(R.id.layThursdayCalls);
        lLayoutThursdayTimes = findViewById(R.id.layThursdayTimes);
        lLayoutFridayCalls = findViewById(R.id.layFridayCalls);
        lLayoutFridayTimes = findViewById(R.id.layFridayTimes);
        lLayoutSaturdayCalls = findViewById(R.id.laySaturdayCalls);
        lLayoutSaturdayTimes = findViewById(R.id.laySaturdayTimes);
        lLayoutSundayCalls = findViewById(R.id.laySundayCalls);
        lLayoutSundayTimes = findViewById(R.id.laySundayTimes);

        cvMondayMains = findViewById(R.id.cvMondayMains);
        cvTuesdayMains = findViewById(R.id.cvTuesdayMains);
        cvWednesdayMains = findViewById(R.id.cvWednesdayMains);
        cvThursdayMains = findViewById(R.id.cvThursdayMains);
        cvFridayMains = findViewById(R.id.cvFridayMains);
        cvSaturdayMains = findViewById(R.id.cvSaturdayMains);
        cvSundayMains = findViewById(R.id.cvSundayMains);
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

        if ( hashMapMondayCalls.size() <= 0 && hashMapMondayTimes.size() <= 0
            && hashMapTuesdayCalls.size() <= 0 && hashMapTuesdayTimes.size() <= 0
            && hashMapWednesdayCalls.size() <= 0 && hashMapWednesdayTimes.size() <= 0
            && hashMapThursdayCalls.size() <= 0 && hashMapWednesdayTimes.size() <= 0
            && hashMapFridayCalls.size() <= 0 && hashMapFridayTimes.size() <= 0
            && hashMapSaturdayCalls.size() <= 0 && hashMapSaturdayTimes.size() <= 0
            && hashMapSundayCalls.size() <= 0 && hashMapSundayTimes.size() <= 0 ){

            showInformationDialog("INVALID DEFINITION", "No Times for Service or for Telephone Calls are defined. You must at least check a day for a service and call.", this);
            return false;
        }

        /*if ( (hashMapMondayCalls.size() > 0 && hashMapMondayTimes.size() <= 0) || (hashMapMondayCalls.size() <= 0 && hashMapMondayTimes.size() > 0)){
            showInformationDialog("INVALID DEFINITION", getInvalidDefinitionMessage("Monday"), this);
            return false;
        }

        if ( (hashMapTuesdayCalls.size() > 0 && hashMapTuesdayTimes.size() <= 0) || (hashMapTuesdayCalls.size() <= 0 && hashMapTuesdayTimes.size() > 0)){
            showInformationDialog("INVALID DEFINITION", getInvalidDefinitionMessage("Tuesday"), this);
            return false;
        }

        if ( (hashMapWednesdayCalls.size() > 0 && hashMapWednesdayTimes.size() <= 0) || (hashMapWednesdayCalls.size() <= 0 && hashMapWednesdayTimes.size() > 0)){
            showInformationDialog("INVALID DEFINITION", getInvalidDefinitionMessage("Wednesday"), this);
            return false;
        }

        if ( (hashMapThursdayCalls.size() > 0 && hashMapThursdayTimes.size() <= 0) || (hashMapThursdayCalls.size() <= 0 && hashMapThursdayTimes.size() > 0)){
            showInformationDialog("INVALID DEFINITION", getInvalidDefinitionMessage("Thursday"), this);
            return false;
        }

        if ( (hashMapFridayCalls.size() > 0 && hashMapFridayTimes.size() <= 0) || (hashMapFridayCalls.size() <= 0 && hashMapFridayTimes.size() > 0)){
            showInformationDialog("INVALID DEFINITION", getInvalidDefinitionMessage("Friday"), this);
            return false;
        }

        if ( (hashMapSaturdayCalls.size() > 0 && hashMapSaturdayTimes.size() <= 0) || (hashMapSaturdayCalls.size() <= 0 && hashMapSaturdayTimes.size() > 0)){
            showInformationDialog("INVALID DEFINITION", getInvalidDefinitionMessage("Saturday"), this);
            return false;
        }

        if ( (hashMapSundayCalls.size() > 0 && hashMapSundayTimes.size() <= 0) || (hashMapSundayCalls.size() <= 0 && hashMapSundayTimes.size() > 0)){
            showInformationDialog("INVALID DEFINITION", getInvalidDefinitionMessage("Sunday"), this);
            return false;
        }*/

        return true;
    }


    private String getInvalidDefinitionMessage(String day) {
        return "Both times for service and phone calls on " + day.toUpperCase() + " should be defined.";
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