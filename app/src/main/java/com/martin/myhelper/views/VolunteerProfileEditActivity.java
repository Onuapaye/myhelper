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

import static com.martin.myhelper.helpers.Utility.ASSIST_WITH_ERRANDS;
import static com.martin.myhelper.helpers.Utility.ASSIST_WITH_GARDENING;
import static com.martin.myhelper.helpers.Utility.ASSIST_WITH_GROCERY_SHOPPING;
import static com.martin.myhelper.helpers.Utility.ASSIST_WITH_HOUSE_CLEANING;
import static com.martin.myhelper.helpers.Utility.ASSIST_WITH_HOUSE_MAINTENANCE;
import static com.martin.myhelper.helpers.Utility.PROVIDE_LIFT_TO_SHOP;
import static com.martin.myhelper.helpers.Utility.PROVIDE_LIFT_TO_SOCIAL;
import static com.martin.myhelper.helpers.Utility.REQUIRED_FIELD_TITLE;
import static com.martin.myhelper.helpers.Utility.SELECT_DAYS_FOR_SERVICE_MSG;
import static com.martin.myhelper.helpers.Utility.SELECT_SERVICE_TYPE;
import static com.martin.myhelper.helpers.Utility.SELECT_SERVICE_TYPE_MSG;
import static com.martin.myhelper.helpers.Utility.SELECT_TIMES_FOR_CALLS_MSG;
import static com.martin.myhelper.helpers.Utility.SELECT_TIMES_FOR_SERVICE_MSG;
import static com.martin.myhelper.helpers.Utility.TEACH_USAGE_MOBILE_DEVICES;
import static com.martin.myhelper.helpers.Utility.TEACH_USAGE_WEB_APPS;
import static com.martin.myhelper.helpers.Utility.UPDATE_RECORD_SUCCESS_MSG;
import static com.martin.myhelper.helpers.Utility.WALK_WITH_U;
import static com.martin.myhelper.helpers.Utility.getFirebaseAuthenticationInstance;
import static com.martin.myhelper.helpers.Utility.showInformationDialog;

public class VolunteerProfileEditActivity extends AppCompatActivity {

    private ArrayList<String> _editProfileList = new ArrayList<>();;
    private String _description;

    private Button btnSelectDaysForService, btnSelectTimesOnDaysForService, btnSelectAvailableDaysForCalls;
    private String[] availableDays, availableTimesOnDay, availableTimesForCalls;
    private TextView tvDaysForService, tvTimesOfDays, tvTimesForCalls, tvDescription;

    private boolean[] checkedDaysItemBoxes;
    private boolean[] checkedTimesItemBoxes;
    private boolean[] checkedCallsItemBoxes;

    private ArrayList<Integer> userSelectedDaysItems = new ArrayList<>();
    private ArrayList<Integer> userSelectedTimesItems = new ArrayList<>();
    private ArrayList<Integer> userSelectedCallsItems = new ArrayList<>();

    private ArrayList<String> actualPickedItemsForDays = new ArrayList<>();
    private ArrayList<String> actualPickedItemsForTimesForService = new ArrayList<>();
    private ArrayList<String> actualPickedItemsForTimesForCalls = new ArrayList<>();

    private AlertDialog.Builder mBuilder;
    private AlertDialog alertDialog;

    private String[] serviceTypes;
    private Spinner spnServiceType;
    private Button btnConfirmUpdate;

    private AppCompatActivity appCompatActivity = VolunteerProfileEditActivity.this;
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
        setContentView(R.layout.activity_volunteer_edit_profile);

        this.showProvidedServiceTypes();

        this.selectDaysForServiceProvision();
        this.selectTimesOnDaysForServiceProvision();
        this.selectAvailableTimesOfDaysForCalls();

        this.getData();
        this.setData();

        this.handleUpdateButtonOnClick();
        this.handleServiceTypeSpinnerOnItemSelected();

        this.handleSignOutOnClick();
    }

    private void handleUpdateButtonOnClick(){
        btnConfirmUpdate = findViewById(R.id.btnCreateProfileConfirm);
        btnConfirmUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
            }
        });
    }

    private void handleSignOutOnClick(){
        TextView signOut = findViewById(R.id.createProfileLogOutButton);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ElderlyVolunteerCRUDHelper.signOutUser(VolunteerProfileEditActivity.this, MainActivity.class);
            }
        });
    }

    private void updateProfile(){

        // do validation first
        if(!this.validateFields()){
            return;
        }

        // create instance of objects
        volunteerCRUDHelper = new VolunteerCRUDHelper();
        volunteerModel = new VolunteerModel();
        firebaseAuth = getFirebaseAuthenticationInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        tvDescription = findViewById(R.id.tvServiceDescriptionMultiLine);
        spnServiceType = findViewById(R.id.spnServiceType);

        volunteerModel.setProfileId(_editProfileList.get(0));
        volunteerModel.setServiceTypeId(serviceTypeID);
        volunteerModel.setDescriptionOfService(tvDescription.getText().toString().trim());
        volunteerModel.setDaysForService(actualPickedItemsForDays);
        volunteerModel.setTimesForService(actualPickedItemsForTimesForService);
        volunteerModel.setTimesForCalls(actualPickedItemsForTimesForCalls);

        volunteerCRUDHelper.updateVolunteerServiceProfile(VolunteerProfileEditActivity.this,
                volunteerModel);

        // redirect to login activity
        //intent = new Intent(VolunteerProfileEditActivity.this, VolunteerProfilesActivity.class);
        intent = new Intent(VolunteerProfileEditActivity.this, VolunteerHomeActivity.class);
        intent.putExtra("recordUpdated", UPDATE_RECORD_SUCCESS_MSG );
        startActivity(intent);
    }

    private void getData(){
        Intent intent;
        intent = getIntent();
        intent.getExtras();

        if (intent.hasExtra("record")){
            _editProfileList = intent.getStringArrayListExtra("record");
        }
    }

    private  void setData(){

        tvDescription = findViewById(R.id.tvServiceDescriptionMultiLine);
        spnServiceType = findViewById(R.id.spnServiceType);

        tvTimesOfDays = findViewById(R.id.tvTimeOnDays);
        tvDaysForService = findViewById(R.id.tvDaysForService);
        tvTimesForCalls = findViewById(R.id.tvTimesForCalls);

        tvDescription.setText(_editProfileList.get(2));
        tvDaysForService.setText(_editProfileList.get(3).replaceAll("(^\\[|\\]$)", ""));
        tvTimesOfDays.setText(_editProfileList.get(4).replaceAll("(^\\[|\\]$)", ""));
        tvTimesForCalls.setText(_editProfileList.get(5).replaceAll("(^\\[|\\]$)", ""));

        actualPickedItemsForDays.add(_editProfileList.get(3).replaceAll("(^\\[|\\]$)", ""));
        actualPickedItemsForTimesForService.add(_editProfileList.get(4).replaceAll("(^\\[|\\]$)", ""));
        actualPickedItemsForTimesForCalls.add(_editProfileList.get(5).replaceAll("(^\\[|\\]$)", ""));

        serviceTypeIDs = getResources().getStringArray(R.array.serviceTypeIDs);
        switch (_editProfileList.get(1)) {
            case "100":
                spnServiceType.setSelection(0);
                serviceTypeID = serviceTypeIDs[0];
                break;
            case "200":
                spnServiceType.setSelection(1);
                serviceTypeID = serviceTypeIDs[1];
                break;
            case "300":
                spnServiceType.setSelection(2);
                serviceTypeID = serviceTypeIDs[2];
                break;
            case "400":
                spnServiceType.setSelection(3);
                serviceTypeID = serviceTypeIDs[3];
                break;
            case "500":
                spnServiceType.setSelection(4);
                serviceTypeID = serviceTypeIDs[4];
                break;
            case "600":
                spnServiceType.setSelection(5);
                serviceTypeID = serviceTypeIDs[5];
                break;
            case "700":
                spnServiceType.setSelection(6);
                serviceTypeID = serviceTypeIDs[6];
                break;
            case "800":
                spnServiceType.setSelection(7);
                serviceTypeID = serviceTypeIDs[7];
                break;
            case "900":
                spnServiceType.setSelection(8);
                serviceTypeID = serviceTypeIDs[8];
                break;
            case "1000":
                spnServiceType.setSelection(9);
                serviceTypeID = serviceTypeIDs[9];
                break;
            default:
                spnServiceType.setSelection(10);
                serviceTypeID = serviceTypeIDs[10];
                break;
        }
    }

    private void handleServiceTypeSpinnerOnItemSelected(){

        serviceTypeIDs = getResources().getStringArray(R.array.serviceTypeIDs);

        spnServiceType = findViewById(R.id.spnServiceType);
        spnServiceType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

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
        if (actualPickedItemsForDays.size() <= 0){
            showInformationDialog(REQUIRED_FIELD_TITLE, SELECT_DAYS_FOR_SERVICE_MSG, appCompatActivity);
            return false;
        }

        if (actualPickedItemsForTimesForService.size() <= 0){
            showInformationDialog(REQUIRED_FIELD_TITLE, SELECT_TIMES_FOR_SERVICE_MSG, appCompatActivity);
            return false;
        }

        if (actualPickedItemsForTimesForCalls.size() <= 0){
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

                mBuilder = new AlertDialog.Builder(VolunteerProfileEditActivity.this);
                mBuilder.setTitle("Available Days For Service");
                mBuilder.setMultiChoiceItems(availableDays, checkedDaysItemBoxes, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isItemChecked) {

                        if (isItemChecked) {
                            userSelectedDaysItems.add(position);
                        } else {
                            userSelectedDaysItems.remove(Integer.valueOf(position));
                        }
                    }
                });

                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String item = "";
                        for (int j = 0; j < userSelectedDaysItems.size(); j++){
                            item += availableDays[userSelectedDaysItems.get(j)];

                            // check if the item selected by the user is not the last
                            // item and append a comma
                            if (j != userSelectedDaysItems.size() - 1){
                                item += ", ";
                            }

                            // set the items checked to the textView
                            tvDaysForService.setText(item);
                            actualPickedItemsForDays.add(availableDays[userSelectedDaysItems.get(j)]);
                        }
                        Log.i("DAYS", actualPickedItemsForDays.toString());
                    }
                });

                mBuilder.setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                mBuilder.setNeutralButton("Clear", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        for (int k = 0; k < checkedDaysItemBoxes.length; k++){
                            checkedDaysItemBoxes[k] = false;
                            userSelectedDaysItems.clear();

                            tvDaysForService.setText("");
                            actualPickedItemsForDays.clear();
                        }
                    }
                });

                alertDialog = mBuilder.create();
                alertDialog.show();
            }

        });
    }

    private void selectTimesOnDaysForServiceProvision(){

        btnSelectTimesOnDaysForService = (Button) findViewById(R.id.btnTimesForeService);
        tvTimesOfDays = (TextView) findViewById(R.id.tvTimeOnDays);

        availableTimesOnDay = getResources().getStringArray(R.array.availableTimes);
        checkedTimesItemBoxes = new boolean[availableTimesOnDay.length];

        btnSelectTimesOnDaysForService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mBuilder = new AlertDialog.Builder(VolunteerProfileEditActivity.this);
                mBuilder.setTitle("Time On Days For Service");
                mBuilder.setMultiChoiceItems(availableTimesOnDay, checkedTimesItemBoxes, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isItemChecked) {

                        if (isItemChecked) {
                            userSelectedTimesItems.add(position);
                        } else {
                            userSelectedTimesItems.remove(Integer.valueOf(position));
                        }
                    }
                });

                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String item = "";
                        for (int j = 0; j < userSelectedTimesItems.size(); j++){
                            item += availableTimesOnDay[userSelectedTimesItems.get(j)];

                            // check if the item selected by the user is not the last
                            // item and append a comma
                            if (j != userSelectedTimesItems.size() - 1){
                                item += ", ";
                            }

                            // set the items checked to the textView
                            tvTimesOfDays.setText(item);
                            actualPickedItemsForTimesForService.add(availableTimesOnDay[userSelectedTimesItems.get(j)]);
                        }
                    }
                });

                mBuilder.setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                mBuilder.setNeutralButton("Clear", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        for (int k = 0; k < checkedTimesItemBoxes.length; k++){
                            checkedTimesItemBoxes[k] = false;
                            userSelectedTimesItems.clear();

                            tvTimesOfDays.setText("");
                            actualPickedItemsForTimesForService.clear();
                        }
                    }
                });

                alertDialog = mBuilder.create();
                alertDialog.show();
            }

        });
    }

    private void selectAvailableTimesOfDaysForCalls(){

        btnSelectAvailableDaysForCalls = (Button) findViewById(R.id.btnTimesForeCalls);
        tvTimesForCalls = (TextView) findViewById(R.id.tvTimesForCalls);

        availableTimesForCalls = getResources().getStringArray(R.array.availableTimes);
        checkedCallsItemBoxes = new boolean[availableTimesForCalls.length];

        btnSelectAvailableDaysForCalls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mBuilder = new AlertDialog.Builder(VolunteerProfileEditActivity.this);
                mBuilder.setTitle("Available Times For Calls");
                mBuilder.setMultiChoiceItems(availableTimesForCalls, checkedCallsItemBoxes, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isItemChecked) {

                        if (isItemChecked) {
                            userSelectedCallsItems.add(position);
                        } else {
                            userSelectedCallsItems.remove(Integer.valueOf(position));
                        }
                    }
                });

                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String item = "";
                        for (int j = 0; j < userSelectedCallsItems.size(); j++){
                            item += availableTimesForCalls[userSelectedCallsItems.get(j)];

                            // check if the item selected by the user is not the last
                            // item and append a comma
                            if (j != userSelectedCallsItems.size() - 1){
                                item += ", ";
                            }

                            // set the items checked to the textView
                            tvTimesForCalls.setText(item);
                            actualPickedItemsForTimesForCalls.add(availableTimesForCalls[userSelectedCallsItems.get(j)]);
                        }
                    }
                });

                mBuilder.setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                mBuilder.setNeutralButton("Clear", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        for (int k = 0; k < checkedCallsItemBoxes.length; k++){
                            checkedCallsItemBoxes[k] = false;
                            userSelectedCallsItems.clear();

                            tvTimesForCalls.setText("");
                            actualPickedItemsForTimesForCalls.clear();
                        }
                    }
                });

                alertDialog = mBuilder.create();
                alertDialog.show();
            }
        });
    }

    private void setCheckedItemsWithoutDialogOpening(String[] _arrayForDialog){

        availableTimesForCalls = _arrayForDialog;
        checkedCallsItemBoxes = new boolean[availableTimesForCalls.length];

        mBuilder = new AlertDialog.Builder(VolunteerProfileEditActivity.this);
        mBuilder.setMultiChoiceItems(availableTimesForCalls, checkedCallsItemBoxes, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int position, boolean isItemChecked) {

                if (isItemChecked) {
                    userSelectedCallsItems.add(position);
                } else {
                    userSelectedCallsItems.remove(Integer.valueOf(position));
                }
            }
        });

        mBuilder.setCancelable(false);
        mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String item = "";
                for (int j = 0; j < userSelectedCallsItems.size(); j++){
                    item += availableTimesForCalls[userSelectedCallsItems.get(j)];

                    // check if the item selected by the user is not the last
                    // item and append a comma
                    if (j != userSelectedCallsItems.size() - 1){
                        item += ", ";
                    }

                    // set the items checked to the textView
                    tvTimesForCalls.setText(item);
                    actualPickedItemsForTimesForCalls.add(availableTimesForCalls[userSelectedCallsItems.get(j)]);
                }
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