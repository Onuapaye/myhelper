package com.martin.myhelper.views;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.martin.myhelper.MainActivity;
import com.martin.myhelper.R;
import com.martin.myhelper.helpers.ElderlyVolunteerCRUDHelper;
import com.martin.myhelper.helpers.Utility;
import com.martin.myhelper.helpers.VolunteerCRUDHelper;
import com.martin.myhelper.model.VolunteerModel;

import java.util.ArrayList;

import static com.martin.myhelper.helpers.Utility.*;


public class VolunteerProfileCreateActivity extends AppCompatActivity {

    private Button btnSelectDaysForService, btnSelectTimesForService, btnSelectTimesForCalls;
    private String[] availableDays, availableTimesForService, availableTimesForCalls;
    private TextView tvDaysForService, tvTimesForService, tvTimesForCalls, tvDescription;

    private boolean[] checkedDaysItemBoxes;
    private boolean[] checkedTimesItemBoxes;
    private boolean[] checkedCallsItemBoxes;

    private ArrayList<Integer> userSelectedDaysItems = new ArrayList<>();
    private ArrayList<Integer> userSelectedTimesItems = new ArrayList<>();
    private ArrayList<Integer> userSelectedCallsItems = new ArrayList<>();

    private ArrayList<String> actualDaysForServiceList = new ArrayList<>();
    private ArrayList<String> actualTimesForServiceList = new ArrayList<>();
    private ArrayList<String> actualTimesForCallsList = new ArrayList<>();

    private AlertDialog.Builder daysAlertBuilder, timesAlertBuilder, callsAlertBuilder;
    private AlertDialog daysAlertDialog, timesAlertDialog, callAlertDialog;

    private String[] serviceTypes;
    private Spinner spnServiceType;
    private Button btnConfirmCreate;

    private AppCompatActivity appCompatActivity = VolunteerProfileCreateActivity.this;
    private Intent intent;

    private VolunteerCRUDHelper volunteerCRUDHelper;
    private VolunteerModel volunteerModel;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;

    private String serviceTypeID = "";
    private String[] serviceTypeIDs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_create_profile);

        this.showProvidedServiceTypes();

        this.selectDaysForServiceProvision();
        this.selectTimesForServiceProvision();
        this.selectTimesForCalls();

        this.handleConfirmButtonOnClick();
        this.handleServiceTypeSpinnerOnItemSelected();

        this.handleSignOutOnClick();
    }

    private void handleSignOutOnClick(){
        TextView signOut = findViewById(R.id.createProfileLogOutButton);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ElderlyVolunteerCRUDHelper.signOutUser(VolunteerProfileCreateActivity.this, MainActivity.class);
            }
        });
    }

    private void handleConfirmButtonOnClick(){
        btnConfirmCreate = findViewById(R.id.btnCreateProfileConfirm);
        btnConfirmCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createProfile();
            }
        });
    }

    private void createProfile(){

        // do validation first
        if(!validateFields()){
            return;
        }

        // create instance of objects
        volunteerCRUDHelper = new VolunteerCRUDHelper();
        volunteerModel = new VolunteerModel();
        firebaseAuth = getFirebaseAuthenticationInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        tvDescription = findViewById(R.id.tvServiceDescriptionMultiLine);
        spnServiceType = findViewById(R.id.spnServiceType);

        volunteerModel.setId(firebaseUser.getUid());
        volunteerModel.setServiceTypeId(serviceTypeID);
        volunteerModel.setDescriptionOfService(tvDescription.getText().toString().trim());
        volunteerModel.setDaysForService(actualDaysForServiceList);
        volunteerModel.setTimesForService(actualTimesForServiceList);
        volunteerModel.setTimesForCalls(actualTimesForCallsList);

        volunteerCRUDHelper.createVolunteerServiceProfile(VolunteerProfileCreateActivity.this, volunteerModel);

        // redirect to login activity
        //intent = new Intent(VolunteerProfileCreateActivity.this, VolunteerProfilesActivity.class);
        intent = new Intent(VolunteerProfileCreateActivity.this, VolunteerHomeActivity.class);
        intent.putExtra("recordCreated", CREATE_RECORD_SUCCESS_MSG + "\n" + CREATE_VOLUNTEER_PROFILE_SUCCESS_MSG);
        startActivity(intent);
    }


    private void handleServiceTypeSpinnerOnItemSelected(){

        serviceTypeIDs = getResources().getStringArray(R.array.serviceTypeIDs);

        spnServiceType = findViewById(R.id.spnServiceType);
        spnServiceType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                //String bis = spnServiceType.getSelectedItem().toString();

                if (!(spnServiceType.getSelectedItem().toString().trim() == "")){

                    switch (spnServiceType.getSelectedItem().toString()){
                        case TEACH_USAGE_MOBILE_DEVICES:
                            serviceTypeID = serviceTypeIDs[0];
                            break;
                        case TEACH_USAGE_WEB_APPS:
                            serviceTypeID = serviceTypeIDs[1];
                            break;
                        case WALK_WITH_U:
                            serviceTypeID = serviceTypeIDs[2];
                            break;
                        case PROVIDE_LIFT_TO_SOCIAL:
                            serviceTypeID = serviceTypeIDs[3];
                            break;
                        case ASSIST_WITH_HOUSE_CLEANING:
                            serviceTypeID = serviceTypeIDs[4];
                            break;
                        case ASSIST_WITH_HOUSE_MAINTENANCE:
                            serviceTypeID = serviceTypeIDs[5];
                            break;
                        case ASSIST_WITH_GARDENING:
                            serviceTypeID = serviceTypeIDs[6];
                            break;
                        case ASSIST_WITH_ERRANDS:
                            serviceTypeID = serviceTypeIDs[7];
                            break;
                        case ASSIST_WITH_GROCERY_SHOPPING:
                            serviceTypeID = serviceTypeIDs[8];
                            break;
                        case PROVIDE_LIFT_TO_SHOP :
                            serviceTypeID = serviceTypeIDs[9];
                            break;
                        default:
                            serviceTypeID = serviceTypeIDs[10];
                            break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private boolean validateFields(){

        spnServiceType = findViewById(R.id.spnServiceType);
        tvDescription = findViewById(R.id.tvServiceDescriptionMultiLine);

        if (serviceTypeID == "" || serviceTypeID.isEmpty()){
            Utility.showInformationDialog(REQUIRED_FIELD_TITLE, SELECT_SERVICE_TYPE_MSG, appCompatActivity);
            return false;
        }

        if (spnServiceType.getSelectedItem().toString().trim() == ""
                || spnServiceType.getSelectedItem().toString().isEmpty()
                || spnServiceType.getSelectedItem().toString().toLowerCase() == SELECT_SERVICE_TYPE.toLowerCase()){

            showInformationDialog(REQUIRED_FIELD_TITLE, SELECT_SERVICE_TYPE_MSG, appCompatActivity);
            return false;
        }

        if (tvDescription.getText().toString().trim() == "" || tvDescription.getText().toString().isEmpty()){
            showInformationDialog(REQUIRED_FIELD_TITLE, "Please enter the description for this profile", appCompatActivity);
            return false;
        }
        if (actualDaysForServiceList.size() <= 0){
            showInformationDialog(REQUIRED_FIELD_TITLE, SELECT_DAYS_FOR_SERVICE_MSG, appCompatActivity);
            return false;
        }

        if (actualTimesForServiceList.size() <= 0){
            showInformationDialog(REQUIRED_FIELD_TITLE, SELECT_TIMES_FOR_SERVICE_MSG, appCompatActivity);
            return false;
        }

        if (actualTimesForCallsList.size() <= 0){
            showInformationDialog(REQUIRED_FIELD_TITLE, SELECT_TIMES_FOR_CALLS_MSG , appCompatActivity);
            return false;
        }

        return true;
    }

    private void selectDaysForServiceProvision(){

        btnSelectDaysForService = (Button) findViewById(R.id.btnDaysForService);
        tvDaysForService = (TextView) findViewById(R.id.tvDaysForService);

        availableDays = getResources().getStringArray(R.array.availableDays);
        checkedDaysItemBoxes = new boolean[availableDays.length];

        btnSelectDaysForService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                daysAlertBuilder = new AlertDialog.Builder(VolunteerProfileCreateActivity.this);
                daysAlertBuilder.setTitle("Days for Service");
                daysAlertBuilder.setMultiChoiceItems(availableDays, checkedDaysItemBoxes, new DialogInterface.OnMultiChoiceClickListener() {
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
                        actualDaysForServiceList.clear();

                        for (int j = 0; j < userSelectedDaysItems.size(); j++){
                            item += availableDays[userSelectedDaysItems.get(j)];

                            // check if the item selected by the user is not the last
                            // item and append a comma
                            if (j != userSelectedDaysItems.size() - 1){
                                item += ", ";
                            }

                            // set the items checked to the textView
                            tvDaysForService.setText(item);
                            actualDaysForServiceList.add(availableDays[userSelectedDaysItems.get(j)]);
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

                            tvDaysForService.setText("");
                            actualDaysForServiceList.clear();
                        }
                    }
                });

                daysAlertDialog = daysAlertBuilder.create();
                daysAlertDialog.show();
            }

        });
    }

    private void selectTimesForServiceProvision(){

        btnSelectTimesForService = (Button) findViewById(R.id.btnTimesForeService);
        tvTimesForService = (TextView) findViewById(R.id.tvTimesForService);

        availableTimesForService = getResources().getStringArray(R.array.availableTimes);
        checkedTimesItemBoxes = new boolean[availableTimesForService.length];

        btnSelectTimesForService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                timesAlertBuilder = new AlertDialog.Builder(VolunteerProfileCreateActivity.this);
                timesAlertBuilder.setTitle("Times for Service");
                timesAlertBuilder.setMultiChoiceItems(availableTimesForService, checkedTimesItemBoxes, new DialogInterface.OnMultiChoiceClickListener() {
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
                        actualTimesForServiceList.clear();

                        for (int j = 0; j < userSelectedTimesItems.size(); j++){
                            item += availableTimesForService[userSelectedTimesItems.get(j)];

                            // check if the item selected by the user is not the last
                            // item and append a comma
                            if (j != userSelectedTimesItems.size() - 1){
                                item += ", ";
                            }

                            // set the items checked to the textView
                            tvTimesForService.setText(item);
                            actualTimesForServiceList.add(availableTimesForService[userSelectedTimesItems.get(j)]);
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

                            tvTimesForService.setText("");
                            actualTimesForServiceList.clear();
                        }
                    }
                });

                timesAlertDialog = timesAlertBuilder.create();
                timesAlertDialog.show();
            }

        });
    }

    private void selectTimesForCalls(){

        btnSelectTimesForCalls = (Button) findViewById(R.id.btnTimesForeCalls);
        tvTimesForCalls = (TextView) findViewById(R.id.tvTimesForCalls);

        availableTimesForCalls = getResources().getStringArray(R.array.availableTimes);
        checkedCallsItemBoxes = new boolean[availableTimesForCalls.length];

        btnSelectTimesForCalls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                callsAlertBuilder = new AlertDialog.Builder(VolunteerProfileCreateActivity.this);
                callsAlertBuilder.setTitle("Available Times For Calls");
                callsAlertBuilder.setMultiChoiceItems(availableTimesForCalls, checkedCallsItemBoxes, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isItemChecked) {

                        if (isItemChecked) {
                            userSelectedCallsItems.add(position);
                        } else {
                            userSelectedCallsItems.remove(Integer.valueOf(position));
                        }
                    }
                });

                callsAlertBuilder.setCancelable(false);
                callsAlertBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String item = "";
                        actualTimesForCallsList.clear();

                        for (int j = 0; j < userSelectedCallsItems.size(); j++){
                            item += availableTimesForCalls[userSelectedCallsItems.get(j)];

                            // check if the item selected by the user is not the last
                            // item and append a comma
                            if (j != userSelectedCallsItems.size() - 1){
                                item += ", ";
                            }

                            // set the items checked to the textView
                            tvTimesForCalls.setText(item);
                            actualTimesForCallsList.add(availableTimesForCalls[userSelectedCallsItems.get(j)]);
                        }
                    }
                });

                callsAlertBuilder.setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                callsAlertBuilder.setNeutralButton("Clear", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        for (int k = 0; k < checkedCallsItemBoxes.length; k++){
                            checkedCallsItemBoxes[k] = false;
                            userSelectedCallsItems.clear();

                            tvTimesForCalls.setText("");
                            actualTimesForCallsList.clear();
                        }
                    }
                });

                callAlertDialog = callsAlertBuilder.create();
                callAlertDialog.show();
            }

        });
    }

    private void showProvidedServiceTypes(){

        spnServiceType = findViewById(R.id.spnServiceType);
        serviceTypes = getResources().getStringArray(R.array.serviceTypes);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item,  serviceTypes);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spnServiceType.setAdapter(arrayAdapter);

    }
}