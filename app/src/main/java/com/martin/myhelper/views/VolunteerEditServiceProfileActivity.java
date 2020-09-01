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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.martin.myhelper.MainActivity;
import com.martin.myhelper.R;
import com.martin.myhelper.helpers.ElderlyVolunteerCRUDHelper;
import com.martin.myhelper.helpers.Utility;
import com.martin.myhelper.helpers.VolunteerCRUDHelper;
import com.martin.myhelper.model.VolunteerModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.martin.myhelper.helpers.Utility.CREATE_RECORD_SUCCESS_MSG;
import static com.martin.myhelper.helpers.Utility.CREATE_VOLUNTEER_PROFILE_SUCCESS_MSG;
import static com.martin.myhelper.helpers.Utility.REQUIRED_FIELD_TITLE;
import static com.martin.myhelper.helpers.Utility.SELECT_SERVICE_CALL_FOR;
import static com.martin.myhelper.helpers.Utility.SELECT_SERVICE_TIME_FOR;
import static com.martin.myhelper.helpers.Utility.SELECT_SERVICE_TYPE;
import static com.martin.myhelper.helpers.Utility.SELECT_SERVICE_TYPE_MSG;
import static com.martin.myhelper.helpers.Utility.UPDATE_RECORD_SUCCESS_MSG;
import static com.martin.myhelper.helpers.Utility.UPDATE_VOLUNTEER_PROFILE_SUCCESS_MSG;
import static com.martin.myhelper.helpers.Utility.getFirebaseAuthenticationInstance;
import static com.martin.myhelper.helpers.Utility.showInformationDialog;

public class VolunteerEditServiceProfileActivity extends AppCompatActivity {
    //private ArrayList<ArrayList<String>> listOfElderlyFeedBacks, _elderlyAccountList;
    //private ArrayList<String> _tempListOfElders;
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

    /* variables for monday */
    private Button _btnMondayTimes, _btnMondayCalls;
    private TextView _tvMondayTimes, _tvMondayCalls;
    private CheckBox _chkMonday;
    private AlertDialog _adMondayTimes, _adMondayCalls;
    private AlertDialog.Builder _adbMondayTimes, _adbMondayCalls;
    private boolean[] _mondayTimeCheckedItemBoxes, _mondayCallsCheckedItemBoxes;
    private ArrayList<String> _actualSelectedMondayTimes = new ArrayList<>();
    private ArrayList<String> _actualSelectedMondayCalls = new ArrayList<>();
    private ArrayList<Integer> _selectedMondayTimeItems = new ArrayList<>();
    private ArrayList<Integer> _selectedMondayCallItems = new ArrayList<>();

    /* variables for tuesday */
    private Button _btnTuesdayTimes, _btnTuesdayCalls;
    private TextView _tvTuesdayTimes, _tvTuesdayCalls;
    private CheckBox _chkTuesday;
    private AlertDialog _adTuesdayTimes, _adTuesdayCalls;
    private AlertDialog.Builder _adbTuesdayTimes, _adbTuesdayCalls;
    private boolean[] _tuesdayTimeCheckedItemBoxes, _tuesdayCallsCheckedItemBoxes;
    private ArrayList<String> _actualSelectedTuesdayTimes = new ArrayList<>();
    private ArrayList<String> _actualSelectedTuesdayCalls = new ArrayList<>();
    private ArrayList<Integer> _selectedTuesdayTimeItems = new ArrayList<>();
    private ArrayList<Integer> _selectedTuesdayCallItems = new ArrayList<>();

    /* variables for wednesday */
    private Button _btnWednesdayTimes, _btnWednesdayCalls;
    private TextView _tvWednesdayTimes, _tvWednesdayCalls;
    private CheckBox _chkWednesday;
    private AlertDialog _adWednesdayTimes, _adWednesdayCalls;
    private AlertDialog.Builder _adbWednesdayTimes, _adbWednesdayCalls;
    private boolean[] _wednesdayTimeCheckedItemBoxes, _wednesdayCallsCheckedItemBoxes;
    private ArrayList<String> _actualSelectedWednesdayTimes = new ArrayList<>();
    private ArrayList<String> _actualSelectedWednesdayCalls = new ArrayList<>();
    private ArrayList<Integer> _selectedWednesdayTimeItems = new ArrayList<>();
    private ArrayList<Integer> _selectedWednesdayCallItems = new ArrayList<>();

    /* variables for thursday */
    private Button _btnThursdayTimes, _btnThursdayCalls;
    private TextView _tvThursdayTimes, _tvThursdayCalls;
    private CheckBox _chkThursday;
    private AlertDialog _adThursdayTimes, _adThursdayCalls;
    private AlertDialog.Builder _adbThursdayTimes, _adbThursdayCalls;
    private boolean[] _thursdayTimeCheckedItemBoxes, _thursdayCallsCheckedItemBoxes;
    private ArrayList<String> _actualSelectedThursdayTimes = new ArrayList<>();
    private ArrayList<String> _actualSelectedThursdayCalls = new ArrayList<>();
    private ArrayList<Integer> _selectedThursdayTimeItems = new ArrayList<>();
    private ArrayList<Integer> _selectedThursdayCallItems = new ArrayList<>();

    /* variables for friday */
    private Button _btnFridayTimes, _btnFridayCalls;
    private TextView _tvFridayTimes, _tvFridayCalls;
    private CheckBox _chkFriday;
    private AlertDialog _adFridayTimes, _adFridayCalls;
    private AlertDialog.Builder _adbFridayTimes, _adbFridayCalls;
    private boolean[] _fridayTimeCheckedItemBoxes, _fridayCallsCheckedItemBoxes;
    private ArrayList<String> _actualSelectedFridayTimes = new ArrayList<>();
    private ArrayList<String> _actualSelectedFridayCalls = new ArrayList<>();
    private ArrayList<Integer> _selectedFridayTimeItems = new ArrayList<>();
    private ArrayList<Integer> _selectedFridayCallItems = new ArrayList<>();

    /* variables for saturday */
    private Button _btnSaturdayTimes, _btnSaturdayCalls;
    private TextView _tvSaturdayTimes, _tvSaturdayCalls;
    private CheckBox _chkSaturday;
    private AlertDialog _adSaturdayTimes, _adSaturdayCalls;
    private AlertDialog.Builder _adbSaturdayTimes, _adbSaturdayCalls;
    private boolean[] _saturdayTimeCheckedItemBoxes, _saturdayCallsCheckedItemBoxes;
    private ArrayList<String> _actualSelectedSaturdayTimes = new ArrayList<>();
    private ArrayList<String> _actualSelectedSaturdayCalls = new ArrayList<>();
    private ArrayList<Integer> _selectedSaturdayTimeItems = new ArrayList<>();
    private ArrayList<Integer> _selectedSaturdayCallItems = new ArrayList<>();

    /* variables for sunday */
    private Button _btnSundayTimes, _btnSundayCalls;
    private TextView _tvSundayTimes, _tvSundayCalls;
    private CheckBox _chkSunday;
    private AlertDialog _adSundayTimes, _adSundayCalls;
    private AlertDialog.Builder _adbSundayTimes, _adbSundayCalls;
    private boolean[] _sundayTimeCheckedItemBoxes, _sundayCallsCheckedItemBoxes;
    private ArrayList<String> _actualSelectedSundayTimes = new ArrayList<>();
    private ArrayList<String> _actualSelectedSundayCalls = new ArrayList<>();
    private ArrayList<Integer> _selectedSundayTimeItems = new ArrayList<>();
    private ArrayList<Integer> _selectedSundayCallItems = new ArrayList<>();

    /* general */
    private String[] timesCallsArray;
    private EditText _etServiceDetails;

    private VolunteerCRUDHelper volunteerCRUDHelper;
    private VolunteerModel volunteerModel;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private Intent intent;
    private AppCompatActivity appCompatActivity = VolunteerEditServiceProfileActivity.this;

    private ArrayList<String> _editProfileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_edit_service_profile);

        // load all service types into the spinner
        this.getServiceTypesRecordsForSpinner();

        // load data for editing
        this.getData();
        this.setData();

        // handle the spinner item change
        this.handleSelectServiceTypeSpinnerOnItemChange();

        // checkboxes
        this.handleCheckBoxesCheckedEvents();

        // handle buttons event handlers
        this.handleMondayTimesButtonClick();
        this.handleMondayCallsButtonClick();
        this.handleTuesdayCallsButtonClick();
        this.handleTuesdayTimesButtonClick();
        this.handleWednesdayCallsButtonClick();
        this.handleWednesdayTimesButtonClick();
        this.handleThursdayCallsButtonClick();
        this.handleThursdayTimesButtonClick();
        this.handleFridayCallsButtonClick();
        this.handleFridayTimesButtonClick();
        this.handleSaturdayCallsButtonClick();
        this.handleSaturdayTimesButtonClick();
        this.handleSundayCallsButtonClick();
        this.handleSundayTimesButtonClick();

        this.handleConfirmButtonOnClick();

        this.handleSignOutOnClick();
    }

    private void initializeWidgets() {

        _etServiceDetails = findViewById(R.id.etServiceDetails);

        _chkMonday = findViewById(R.id.chkMonday);
        _chkTuesday = findViewById(R.id.chkTuesday);
        _chkWednesday = findViewById(R.id.chkWednesday);
        _chkThursday = findViewById(R.id.chkThursday);
        _chkFriday = findViewById(R.id.chkFriday);
        _chkSaturday = findViewById(R.id.chkSaturday);
        _chkSunday = findViewById(R.id.chkSunday);

        _tvMondayTimes = findViewById(R.id.tvMondayTimes);
        _tvMondayCalls = findViewById(R.id.tvMondayCalls);

        _tvTuesdayCalls = findViewById(R.id.tvTuesdayCalls);
        _tvTuesdayTimes = findViewById(R.id.tvTuesdayTimes);

        _tvWednesdayCalls = findViewById(R.id.tvWednesdayCalls);
        _tvWednesdayTimes = findViewById(R.id.tvWednesdayTimes);

        _tvThursdayCalls = findViewById(R.id.tvThursdayCalls);
        _tvThursdayTimes = findViewById(R.id.tvThursdayTimes);

        _tvFridayCalls = findViewById(R.id.tvFridayCalls);
        _tvFridayTimes = findViewById(R.id.tvFridayTimes);

        _tvSaturdayCalls = findViewById(R.id.tvSaturdayCalls);
        _tvSaturdayTimes = findViewById(R.id.tvSaturdayTimes);

        _tvSundayCalls = findViewById(R.id.tvSundayCalls);
        _tvSundayTimes = findViewById(R.id.tvSundayTimes);

        _btnMondayTimes = findViewById(R.id.btnMondayTimes);
        _btnMondayCalls = findViewById(R.id.btnMondayCalls);

        _btnTuesdayCalls = findViewById(R.id.btnTuesdayCalls);
        _btnTuesdayTimes = findViewById(R.id.btnTuesdayTimes);

        _btnWednesdayCalls = findViewById(R.id.btnWednesdayCalls);
        _btnWednesdayTimes = findViewById(R.id.btnWednesdayTimes);

        _btnThursdayCalls = findViewById(R.id.btnThursdayCalls);
        _btnThursdayTimes = findViewById(R.id.btnThursdayTimes);

        _btnFridayCalls = findViewById(R.id.btnFridayCalls);
        _btnFridayTimes = findViewById(R.id.btnFridayTimes);

        _btnSaturdayCalls = findViewById(R.id.btnSaturdayCalls);
        _btnSaturdayTimes = findViewById(R.id.btnSaturdayTimes);

        _btnSundayCalls = findViewById(R.id.btnSundayCalls);
        _btnSundayTimes = findViewById(R.id.btnSundayTimes);
    }

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

                serviceTypesArrayAdapter = new ArrayAdapter<>(VolunteerEditServiceProfileActivity.this,
                        R.layout.support_simple_spinner_dropdown_item, serviceArrayForSpinner);

                serviceTypesArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

                spnServiceType.setAdapter(serviceTypesArrayAdapter);
            }
        });

    }

    private void handleConfirmButtonOnClick(){
        Button confirmButton = findViewById(R.id.btnCreateProfileConfirm);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
            }
        });
    }

    private void updateProfile(){

        // do validation first
        if(!validateFields()){
            return;
        }

        // create instance of objects
        volunteerCRUDHelper = new VolunteerCRUDHelper();
        volunteerModel = new VolunteerModel();
        firebaseAuth = getFirebaseAuthenticationInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        this.initializeWidgets();

        volunteerModel.setId(firebaseUser.getUid());
        volunteerModel.setProfileId(_editProfileList.get(0));
        volunteerModel.setServiceTypeId(selectedServiceTypeID);
        volunteerModel.setDescriptionOfService(_etServiceDetails.getText().toString().trim());

        volunteerModel.setMondayTimes(_actualSelectedMondayTimes);
        volunteerModel.setMondayCalls(_actualSelectedMondayCalls);

        volunteerModel.setTuesdayCalls(_actualSelectedTuesdayCalls);
        volunteerModel.setTuesdayTimes(_actualSelectedTuesdayTimes);

        volunteerModel.setWednesdayCalls(_actualSelectedWednesdayCalls);
        volunteerModel.setWednesdayTimes(_actualSelectedWednesdayTimes);

        volunteerModel.setThursdayCalls(_actualSelectedThursdayCalls);
        volunteerModel.setThursdayTimes(_actualSelectedThursdayTimes);

        volunteerModel.setFridayCalls(_actualSelectedFridayCalls);
        volunteerModel.setFridayTimes(_actualSelectedFridayTimes);

        volunteerModel.setSaturdayCalls(_actualSelectedSaturdayCalls);
        volunteerModel.setSaturdayTimes(_actualSelectedSaturdayTimes);

        volunteerModel.setSundayCalls(_actualSelectedSundayCalls);
        volunteerModel.setSundayTimes(_actualSelectedSundayTimes);

        volunteerCRUDHelper.volunteerUpdateService(VolunteerEditServiceProfileActivity.this, volunteerModel,
                _chkMonday, _chkTuesday, _chkWednesday, _chkThursday, _chkFriday, _chkSaturday, _chkSunday);

        // redirect to login activity
        //intent = new Intent(VolunteerProfileCreateActivity.this, VolunteerProfilesActivity.class);
        intent = new Intent(VolunteerEditServiceProfileActivity.this, VolunteerHomeActivity.class);
        intent.putExtra("recordUpdated", UPDATE_RECORD_SUCCESS_MSG + "\n" + UPDATE_VOLUNTEER_PROFILE_SUCCESS_MSG);
        startActivity(intent);
    }

    private boolean validateFields(){

        this.initializeWidgets();

        if (selectedServiceTypeID == "" || selectedServiceTypeID.isEmpty()){
            Utility.showInformationDialog(REQUIRED_FIELD_TITLE, SELECT_SERVICE_TYPE_MSG, appCompatActivity);
            return false;
        }

        if (spnServiceType.getSelectedItem().toString().trim() == ""
                || spnServiceType.getSelectedItem().toString().isEmpty()
                || spnServiceType.getSelectedItem().toString().toLowerCase() == SELECT_SERVICE_TYPE.toLowerCase()){

            showInformationDialog(REQUIRED_FIELD_TITLE, SELECT_SERVICE_TYPE_MSG, appCompatActivity);
            return false;
        }

        if (_etServiceDetails.getText().toString().trim() == "" || _etServiceDetails.getText().toString().isEmpty()){
            showInformationDialog(REQUIRED_FIELD_TITLE, "Please enter the description for this profile", appCompatActivity);
            return false;
        }
        if (_chkMonday.isChecked()){
            if(_actualSelectedMondayCalls.size() <= 0){
                showInformationDialog(REQUIRED_FIELD_TITLE, SELECT_SERVICE_CALL_FOR + "Monday", appCompatActivity);
                return false;
            }
            if(_actualSelectedMondayTimes.size() <= 0){
                showInformationDialog(REQUIRED_FIELD_TITLE, SELECT_SERVICE_TIME_FOR + "Monday", appCompatActivity);
                return false;
            }
        }

        if (_chkTuesday.isChecked()){
            if(_actualSelectedTuesdayCalls.size() <= 0){
                showInformationDialog(REQUIRED_FIELD_TITLE, SELECT_SERVICE_CALL_FOR + "Tuesday", appCompatActivity);
                return false;
            }
            if(_actualSelectedTuesdayTimes.size() <= 0){
                showInformationDialog(REQUIRED_FIELD_TITLE, SELECT_SERVICE_TIME_FOR + "Tuesday", appCompatActivity);
                return false;
            }
        }

        if (_chkWednesday.isChecked()){
            if(_actualSelectedWednesdayCalls.size() <= 0){
                showInformationDialog(REQUIRED_FIELD_TITLE, SELECT_SERVICE_CALL_FOR + "Wednesday", appCompatActivity);
                return false;
            }
            if(_actualSelectedWednesdayTimes.size() <= 0){
                showInformationDialog(REQUIRED_FIELD_TITLE, SELECT_SERVICE_TIME_FOR + "Wednesday", appCompatActivity);
                return false;
            }
        }

        if (_chkThursday.isChecked()){
            if(_actualSelectedThursdayCalls.size() <= 0){
                showInformationDialog(REQUIRED_FIELD_TITLE, SELECT_SERVICE_CALL_FOR + "Thursday", appCompatActivity);
                return false;
            }
            if(_actualSelectedThursdayTimes.size() <= 0){
                showInformationDialog(REQUIRED_FIELD_TITLE, SELECT_SERVICE_TIME_FOR + "Thursday", appCompatActivity);
                return false;
            }
        }

        if (_chkFriday.isChecked()){
            if(_actualSelectedFridayCalls.size() <= 0){
                showInformationDialog(REQUIRED_FIELD_TITLE, SELECT_SERVICE_CALL_FOR + "Friday", appCompatActivity);
                return false;
            }
            if(_actualSelectedFridayTimes.size() <= 0){
                showInformationDialog(REQUIRED_FIELD_TITLE, SELECT_SERVICE_TIME_FOR + "Friday", appCompatActivity);
                return false;
            }
        }

        if (_chkSaturday.isChecked()){
            if(_actualSelectedSaturdayCalls.size() <= 0){
                showInformationDialog(REQUIRED_FIELD_TITLE, SELECT_SERVICE_CALL_FOR + "Saturday", appCompatActivity);
                return false;
            }
            if(_actualSelectedSaturdayTimes.size() <= 0){
                showInformationDialog(REQUIRED_FIELD_TITLE, SELECT_SERVICE_TIME_FOR + "Saturday", appCompatActivity);
                return false;
            }
        }

        if (_chkSunday.isChecked()){
            if(_actualSelectedSundayCalls.size() <= 0){
                showInformationDialog(REQUIRED_FIELD_TITLE, SELECT_SERVICE_CALL_FOR + "Sunday", appCompatActivity);
                return false;
            }
            if(_actualSelectedSundayTimes.size() <= 0){
                showInformationDialog(REQUIRED_FIELD_TITLE, SELECT_SERVICE_TIME_FOR + "Sunday", appCompatActivity);
                return false;
            }
        }

        return true;
    }

    // a callback method to get the results of the hash map
    private void setServiceTypesHashCallBack(HashMap<Integer, String> _hash){
        this.finalServiceTypesHash = _hash;
    }

    private void handleSelectServiceTypeSpinnerOnItemChange(){

        spnServiceType = findViewById(R.id.spnServiceType);
        spnServiceType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String _spinnerKey;
                _spinnerKey = finalServiceTypesHash.get(spnServiceType.getSelectedItemPosition());

                // set the selected service time key for saving later
                selectedServiceTypeID = _spinnerKey;

                // load the system times for dialog builder selection
                loadSystemTimesForDialogBuilder();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void loadSystemTimesForDialogBuilder(){

        firebaseFirestore = Utility.getFirebaseFireStoreInstance();
        CollectionReference collectionReference = firebaseFirestore.collection("system_times");

        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (QueryDocumentSnapshot qds : queryDocumentSnapshots){
                    tempSystemTimeList = new ArrayList<>();
                    tempSystemTimeList.add(qds.getString("time"));

                    systemTimeList.add(tempSystemTimeList.toString().replaceAll("(^\\[|\\]$)", ""));
                }

                setSystemTimesCallBack(systemTimeList);
            }
        });
    }

    private void setSystemTimesCallBack(List<String> _list){
        lstFinalSystemTimes = _list;
    }

    private void handleButtonClickForDialogBuilder(final String dialogTitle, final TextView _textViewToShowSelectedItems,
                                                   AlertDialog _alertDialog, @NotNull AlertDialog.Builder _alertBuilder,
                                                   final ArrayList<String> actualTimesList, final boolean[] checkedItemBoxes,
                                                   final ArrayList<Integer> _userSelectedItems){

        // convert the list into a normal array
         /*   timesCallsArray = new String[lstFinalSystemTimes.size()];
            lstFinalSystemTimes.toArray(timesCallsArray);

            Log.e("ONE", timesCallsArray.toString());
            checkedItemBoxes = new boolean[timesCallsArray.length];*/

        _alertBuilder
                .setTitle(dialogTitle)
                .setMultiChoiceItems(timesCallsArray, checkedItemBoxes, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isItemChecked) {

                        if (isItemChecked) {
                            _userSelectedItems.add(position);
                        } else {
                            _userSelectedItems.remove(Integer.valueOf(position));
                        }
                    }
                })
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String item = "";
                        actualTimesList.clear();

                        for (int j = 0; j < _userSelectedItems.size(); j++){

                            item += timesCallsArray[_userSelectedItems.get(j)];

                            // check if the item selected by the user is not the last
                            // item and append a comma
                            if (j != _userSelectedItems.size() - 1){
                                item += ", ";
                            }

                            // set the items checked to the textView
                            _textViewToShowSelectedItems.setText(item);
                            actualTimesList.add(timesCallsArray[_userSelectedItems.get(j)]);
                        }
                    }
                })
                .setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setNeutralButton("Clear", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        for (int k = 0; k < checkedItemBoxes.length; k++){
                            checkedItemBoxes[k] = false;
                            _userSelectedItems.clear();

                            _textViewToShowSelectedItems.setText("");
                            actualTimesList.clear();
                        }
                    }
                });

        _alertDialog = _alertBuilder.create();
        _alertDialog.show();

    }

    private void handleCheckBoxesCheckedEvents(){

        this.initializeWidgets();

        _chkMonday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                _btnMondayTimes = findViewById(R.id.btnMondayTimes);
                _btnMondayCalls = findViewById(R.id.btnMondayCalls);

                if (_chkMonday.isChecked()){
                    _btnMondayTimes.setEnabled(true);
                    _btnMondayCalls.setEnabled(true);
                } else {
                    _btnMondayTimes.setEnabled(false);
                    _btnMondayCalls.setEnabled(false);

                    _tvMondayCalls.setText("");
                    _tvMondayTimes.setText("");

                    _actualSelectedMondayCalls.clear();
                    _actualSelectedMondayTimes.clear();

                    _selectedMondayCallItems.clear();
                    _selectedMondayTimeItems.clear();

                    for (int i = 0; i < _mondayCallsCheckedItemBoxes.length; i++) {
                        _mondayCallsCheckedItemBoxes[i] = false;
                    }
                    for (int i = 0; i < _mondayTimeCheckedItemBoxes.length; i++) {
                        _mondayTimeCheckedItemBoxes[i] = false;
                    }
                }
            }
        });

        _chkTuesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                _btnTuesdayTimes = findViewById(R.id.btnTuesdayTimes);
                _btnTuesdayCalls = findViewById(R.id.btnTuesdayCalls);

                if (_chkTuesday.isChecked()){
                    _btnTuesdayTimes.setEnabled(true);
                    _btnTuesdayCalls.setEnabled(true);
                } else {
                    _btnTuesdayTimes.setEnabled(false);
                    _btnTuesdayCalls.setEnabled(false);

                    _tvTuesdayCalls.setText("");
                    _tvTuesdayTimes.setText("");

                    _actualSelectedTuesdayCalls.clear();
                    _actualSelectedTuesdayTimes.clear();

                    _selectedTuesdayCallItems.clear();
                    _selectedTuesdayTimeItems.clear();

                    for (int i = 0; i < _tuesdayCallsCheckedItemBoxes.length; i++) {
                        _tuesdayCallsCheckedItemBoxes[i] = false;
                    }
                    for (int i = 0; i < _tuesdayTimeCheckedItemBoxes.length; i++) {
                        _tuesdayTimeCheckedItemBoxes[i] = false;
                    }
                }
            }
        });

        _chkWednesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                _btnWednesdayTimes = findViewById(R.id.btnWednesdayTimes);
                _btnWednesdayCalls = findViewById(R.id.btnWednesdayCalls);

                if (_chkWednesday.isChecked()){
                    _btnWednesdayTimes.setEnabled(true);
                    _btnWednesdayCalls.setEnabled(true);
                } else {
                    _btnWednesdayTimes.setEnabled(false);
                    _btnWednesdayCalls.setEnabled(false);

                    _tvWednesdayCalls.setText("");
                    _tvWednesdayTimes.setText("");

                    _actualSelectedWednesdayCalls.clear();
                    _actualSelectedWednesdayTimes.clear();

                    _selectedWednesdayCallItems.clear();
                    _selectedWednesdayTimeItems.clear();

                    for (int i = 0; i < _wednesdayCallsCheckedItemBoxes.length; i++) {
                        _wednesdayCallsCheckedItemBoxes[i] = false;
                    }
                    for (int i = 0; i < _wednesdayTimeCheckedItemBoxes.length; i++) {
                        _wednesdayTimeCheckedItemBoxes[i] = false;
                    }
                }
            }
        });

        _chkThursday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                _btnThursdayTimes = findViewById(R.id.btnThursdayTimes);
                _btnThursdayCalls = findViewById(R.id.btnThursdayCalls);

                if (_chkThursday.isChecked()){
                    _btnThursdayTimes.setEnabled(true);
                    _btnThursdayCalls.setEnabled(true);
                } else {
                    _btnThursdayTimes.setEnabled(false);
                    _btnThursdayCalls.setEnabled(false);

                    _tvThursdayCalls.setText("");
                    _tvThursdayTimes.setText("");

                    _actualSelectedThursdayCalls.clear();
                    _actualSelectedThursdayTimes.clear();

                    _selectedThursdayCallItems.clear();
                    _selectedThursdayTimeItems.clear();

                    for (int i = 0; i < _thursdayCallsCheckedItemBoxes.length; i++) {
                        _thursdayCallsCheckedItemBoxes[i] = false;
                    }
                    for (int i = 0; i < _thursdayTimeCheckedItemBoxes.length; i++) {
                        _thursdayTimeCheckedItemBoxes[i] = false;
                    }
                }
            }
        });

        _chkFriday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                _btnFridayTimes = findViewById(R.id.btnFridayTimes);
                _btnFridayCalls = findViewById(R.id.btnFridayCalls);

                if (_chkFriday.isChecked()){
                    _btnFridayTimes.setEnabled(true);
                    _btnFridayCalls.setEnabled(true);
                } else {
                    _btnFridayTimes.setEnabled(false);
                    _btnFridayCalls.setEnabled(false);

                    _tvFridayCalls.setText("");
                    _tvFridayTimes.setText("");

                    _actualSelectedFridayCalls.clear();
                    _actualSelectedFridayTimes.clear();

                    _selectedFridayCallItems.clear();
                    _selectedFridayTimeItems.clear();

                    for (int i = 0; i < _fridayCallsCheckedItemBoxes.length; i++) {
                        _fridayCallsCheckedItemBoxes[i] = false;
                    }
                    for (int i = 0; i < _fridayTimeCheckedItemBoxes.length; i++) {
                        _fridayTimeCheckedItemBoxes[i] = false;
                    }
                }
            }
        });

        _chkSaturday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                _btnSaturdayTimes = findViewById(R.id.btnSaturdayTimes);
                _btnSaturdayCalls = findViewById(R.id.btnSaturdayCalls);

                if (_chkSaturday.isChecked()){
                    _btnSaturdayTimes.setEnabled(true);
                    _btnSaturdayCalls.setEnabled(true);
                } else {
                    _btnSaturdayTimes.setEnabled(false);
                    _btnSaturdayCalls.setEnabled(false);

                    _tvSaturdayCalls.setText("");
                    _tvSaturdayTimes.setText("");

                    _actualSelectedSaturdayCalls.clear();
                    _actualSelectedSaturdayTimes.clear();

                    _selectedSaturdayCallItems.clear();
                    _selectedSaturdayTimeItems.clear();

                    for (int i = 0; i < _saturdayCallsCheckedItemBoxes.length; i++) {
                        _saturdayCallsCheckedItemBoxes[i] = false;
                    }
                    for (int i = 0; i < _saturdayTimeCheckedItemBoxes.length; i++) {
                        _saturdayTimeCheckedItemBoxes[i] = false;
                    }
                }
            }
        });

        _chkSunday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                _btnSundayTimes = findViewById(R.id.btnSundayTimes);
                _btnSundayCalls = findViewById(R.id.btnSundayCalls);

                if (_chkSunday.isChecked()){
                    _btnSundayTimes.setEnabled(true);
                    _btnSundayCalls.setEnabled(true);
                } else {
                    _btnSundayTimes.setEnabled(false);
                    _btnSundayCalls.setEnabled(false);

                    _tvSundayCalls.setText("");
                    _tvSundayTimes.setText("");

                    _actualSelectedSundayCalls.clear();
                    _actualSelectedSundayTimes.clear();

                    _selectedSundayCallItems.clear();
                    _selectedSundayTimeItems.clear();

                    for (int i = 0; i < _sundayCallsCheckedItemBoxes.length; i++) {
                        _sundayCallsCheckedItemBoxes[i] = false;
                    }
                    for (int i = 0; i < _sundayTimeCheckedItemBoxes.length; i++) {
                        _sundayTimeCheckedItemBoxes[i] = false;
                    }
                }
            }
        });
    }

    /*MONDAY*/
    private void handleMondayTimesButtonClick(){

        timesCallsArray = getResources().getStringArray(R.array.systemLocalTime);
        _mondayTimeCheckedItemBoxes = new boolean[timesCallsArray.length];

        //1. set monday button and text view
        this.initializeWidgets();

        _btnMondayTimes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                _adbMondayTimes = new AlertDialog.Builder(VolunteerEditServiceProfileActivity.this);

                handleButtonClickForDialogBuilder("Times for Service",
                        _tvMondayTimes, _adMondayTimes, _adbMondayTimes,
                        _actualSelectedMondayTimes, _mondayTimeCheckedItemBoxes, _selectedMondayTimeItems);
            }
        });

    }

    private void handleMondayCallsButtonClick(){

        timesCallsArray = getResources().getStringArray(R.array.systemLocalTime);
        _mondayCallsCheckedItemBoxes = new boolean[timesCallsArray.length];

        //1. set monday button and text view
        this.initializeWidgets();

        _btnMondayCalls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                _adbMondayCalls = new AlertDialog.Builder(VolunteerEditServiceProfileActivity.this);

                handleButtonClickForDialogBuilder("Times for Calls",
                        _tvMondayCalls, _adMondayCalls, _adbMondayCalls,
                        _actualSelectedMondayCalls, _mondayCallsCheckedItemBoxes,
                        _selectedMondayCallItems);
            }
        });
    }

    /*TUESDAY*/
    private void handleTuesdayTimesButtonClick(){

        timesCallsArray = getResources().getStringArray(R.array.systemLocalTime);
        _tuesdayTimeCheckedItemBoxes = new boolean[timesCallsArray.length];

        //1. set tuesday button and text view
        this.initializeWidgets();

        _btnTuesdayTimes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                _adbTuesdayTimes = new AlertDialog.Builder(VolunteerEditServiceProfileActivity.this);

                handleButtonClickForDialogBuilder("Times for Service",
                        _tvTuesdayTimes, _adTuesdayTimes, _adbTuesdayTimes,
                        _actualSelectedTuesdayTimes, _tuesdayTimeCheckedItemBoxes, _selectedTuesdayTimeItems);
            }
        });
    }

    private void handleTuesdayCallsButtonClick(){

        timesCallsArray = getResources().getStringArray(R.array.systemLocalTime);
        _tuesdayCallsCheckedItemBoxes = new boolean[timesCallsArray.length];

        //1. set tuesday button and text view
        this.initializeWidgets();

        _btnTuesdayCalls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                _adbTuesdayCalls = new AlertDialog.Builder(VolunteerEditServiceProfileActivity.this);

                handleButtonClickForDialogBuilder("Times for Calls",
                        _tvTuesdayCalls, _adTuesdayCalls, _adbTuesdayCalls,
                        _actualSelectedTuesdayCalls, _tuesdayCallsCheckedItemBoxes,
                        _selectedTuesdayCallItems);
            }
        });

    }

    /*WEDNESDAY*/
    private void handleWednesdayTimesButtonClick(){

        timesCallsArray = getResources().getStringArray(R.array.systemLocalTime);
        _wednesdayTimeCheckedItemBoxes = new boolean[timesCallsArray.length];

        //1. set Wednesday button and text view
        this.initializeWidgets();

        _btnWednesdayTimes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                _adbWednesdayTimes = new AlertDialog.Builder(VolunteerEditServiceProfileActivity.this);

                handleButtonClickForDialogBuilder("Times for Service",
                        _tvWednesdayTimes, _adWednesdayTimes, _adbWednesdayTimes,
                        _actualSelectedWednesdayTimes, _wednesdayTimeCheckedItemBoxes, _selectedWednesdayTimeItems);
            }
        });

    }

    private void handleWednesdayCallsButtonClick(){

        timesCallsArray = getResources().getStringArray(R.array.systemLocalTime);
        _wednesdayCallsCheckedItemBoxes = new boolean[timesCallsArray.length];

        //1. set Wednesday button and text view
        this.initializeWidgets();

        _btnWednesdayCalls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                _adbWednesdayCalls = new AlertDialog.Builder(VolunteerEditServiceProfileActivity.this);

                handleButtonClickForDialogBuilder("Times for Calls",
                        _tvWednesdayCalls, _adWednesdayCalls, _adbWednesdayCalls,
                        _actualSelectedWednesdayCalls, _wednesdayCallsCheckedItemBoxes,
                        _selectedWednesdayCallItems);
            }
        });

    }

    /*THURSDAY*/
    private void handleThursdayTimesButtonClick(){

        timesCallsArray = getResources().getStringArray(R.array.systemLocalTime);
        _thursdayTimeCheckedItemBoxes = new boolean[timesCallsArray.length];

        //1. set Thursday button and text view
        this.initializeWidgets();

        _btnThursdayTimes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                _adbThursdayTimes = new AlertDialog.Builder(VolunteerEditServiceProfileActivity.this);

                handleButtonClickForDialogBuilder("Times for Service",
                        _tvThursdayTimes, _adThursdayTimes, _adbThursdayTimes,
                        _actualSelectedThursdayTimes, _thursdayTimeCheckedItemBoxes, _selectedThursdayTimeItems);
            }
        });

    }

    private void handleThursdayCallsButtonClick(){

        timesCallsArray = getResources().getStringArray(R.array.systemLocalTime);
        _thursdayCallsCheckedItemBoxes = new boolean[timesCallsArray.length];

        //1. set Thursday button and text view
        this.initializeWidgets();

        _btnThursdayCalls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                _adbThursdayCalls = new AlertDialog.Builder(VolunteerEditServiceProfileActivity.this);

                handleButtonClickForDialogBuilder("Times for Calls",
                        _tvThursdayCalls, _adThursdayCalls, _adbThursdayCalls,
                        _actualSelectedThursdayCalls, _thursdayCallsCheckedItemBoxes,
                        _selectedThursdayCallItems);
            }
        });
    }

    /*FRIDAY*/
    private void handleFridayTimesButtonClick(){

        timesCallsArray = getResources().getStringArray(R.array.systemLocalTime);
        _fridayTimeCheckedItemBoxes = new boolean[timesCallsArray.length];

        //1. set Friday button and text view
        this.initializeWidgets();

        _btnFridayTimes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                _adbFridayTimes = new AlertDialog.Builder(VolunteerEditServiceProfileActivity.this);

                handleButtonClickForDialogBuilder("Times for Service",
                        _tvFridayTimes, _adFridayTimes, _adbFridayTimes,
                        _actualSelectedFridayTimes, _fridayTimeCheckedItemBoxes, _selectedFridayTimeItems);
            }
        });
    }

    private void handleFridayCallsButtonClick(){

        timesCallsArray = getResources().getStringArray(R.array.systemLocalTime);
        _fridayCallsCheckedItemBoxes = new boolean[timesCallsArray.length];

        //1. set Friday button and text view
        this.initializeWidgets();

        _btnFridayCalls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                _adbFridayCalls = new AlertDialog.Builder(VolunteerEditServiceProfileActivity.this);

                handleButtonClickForDialogBuilder("Times for Calls",
                        _tvFridayCalls, _adFridayCalls, _adbFridayCalls,
                        _actualSelectedFridayCalls, _fridayCallsCheckedItemBoxes,
                        _selectedFridayCallItems);
            }
        });
    }

    /*SATURDAY*/
    private void handleSaturdayTimesButtonClick(){

        timesCallsArray = getResources().getStringArray(R.array.systemLocalTime);
        _saturdayTimeCheckedItemBoxes = new boolean[timesCallsArray.length];

        //1. set Saturday button and text view
        this.initializeWidgets();

        _btnSaturdayTimes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                _adbSaturdayTimes = new AlertDialog.Builder(VolunteerEditServiceProfileActivity.this);

                handleButtonClickForDialogBuilder("Times for Service",
                        _tvSaturdayTimes, _adSaturdayTimes, _adbSaturdayTimes,
                        _actualSelectedSaturdayTimes, _saturdayTimeCheckedItemBoxes, _selectedSaturdayTimeItems);
            }
        });
    }

    private void handleSaturdayCallsButtonClick(){

        timesCallsArray = getResources().getStringArray(R.array.systemLocalTime);
        _saturdayCallsCheckedItemBoxes = new boolean[timesCallsArray.length];

        //1. set Saturday button and text view
        this.initializeWidgets();

        _btnSaturdayCalls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                _adbSaturdayCalls = new AlertDialog.Builder(VolunteerEditServiceProfileActivity.this);

                handleButtonClickForDialogBuilder("Times for Calls",
                        _tvSaturdayCalls, _adSaturdayCalls, _adbSaturdayCalls,
                        _actualSelectedSaturdayCalls, _saturdayCallsCheckedItemBoxes,
                        _selectedSaturdayCallItems);
            }
        });
    }

    /*SUNDAY*/
    private void handleSundayTimesButtonClick(){

        timesCallsArray = getResources().getStringArray(R.array.systemLocalTime);
        _sundayTimeCheckedItemBoxes = new boolean[timesCallsArray.length];

        //1. set Sunday button and text view
        this.initializeWidgets();

        _btnSundayTimes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                _adbSundayTimes = new AlertDialog.Builder(VolunteerEditServiceProfileActivity.this);

                handleButtonClickForDialogBuilder("Times for Service",
                        _tvSundayTimes, _adSundayTimes, _adbSundayTimes,
                        _actualSelectedSundayTimes, _sundayTimeCheckedItemBoxes, _selectedSundayTimeItems);
            }
        });
    }

    private void handleSundayCallsButtonClick(){

        timesCallsArray = getResources().getStringArray(R.array.systemLocalTime);
        _sundayCallsCheckedItemBoxes = new boolean[timesCallsArray.length];

        //1. set Sunday button and text view
        this.initializeWidgets();

        _btnSundayCalls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                _adbSundayCalls = new AlertDialog.Builder(VolunteerEditServiceProfileActivity.this);

                handleButtonClickForDialogBuilder("Times for Calls",
                        _tvSundayCalls, _adSundayCalls, _adbSundayCalls,
                        _actualSelectedSundayCalls, _sundayCallsCheckedItemBoxes,
                        _selectedSundayCallItems);
            }
        });
    }

    private void getData(){
        Intent intent;
        intent = getIntent();
        intent.getExtras();

        _editProfileList = new ArrayList<>();

        if (intent.hasExtra("record")){
            _editProfileList = intent.getStringArrayListExtra("record");
        }
    }

    private  void setData(){

        this.initializeWidgets();

        _etServiceDetails.setText(_editProfileList.get(2));

        // set times
        if(!_editProfileList.get(3).replaceAll("(^\\[|\\]$)", "").equals("")){

            _chkMonday.setChecked(true);
            _btnMondayCalls.setEnabled(true);
            _btnMondayTimes.setEnabled(true);

            _tvMondayCalls.setText(_editProfileList.get(3).replaceAll("(^\\[|\\]$)", ""));
            _tvMondayTimes.setText(_editProfileList.get(4).replaceAll("(^\\[|\\]$)", ""));

            _actualSelectedMondayCalls.add(_editProfileList.get(3).replaceAll("(^\\[|\\]$)", ""));
            _actualSelectedMondayTimes.add(_editProfileList.get(4).replaceAll("(^\\[|\\]$)", ""));
        }

        if(!_editProfileList.get(5).replaceAll("(^\\[|\\]$)", "").equals("") ){

            _chkTuesday.setChecked(true);
            _btnTuesdayCalls.setEnabled(true);
            _btnTuesdayTimes.setEnabled(true);

            _tvTuesdayCalls.setText(_editProfileList.get(5).replaceAll("(^\\[|\\]$)", ""));
            _tvTuesdayTimes.setText(_editProfileList.get(6).replaceAll("(^\\[|\\]$)", ""));

            _actualSelectedTuesdayCalls.add(_editProfileList.get(5).replaceAll("(^\\[|\\]$)", ""));
            _actualSelectedTuesdayTimes.add(_editProfileList.get(6).replaceAll("(^\\[|\\]$)", ""));
        }

        if(!_editProfileList.get(7).replaceAll("(^\\[|\\]$)", "").equals("")){

            _chkWednesday.setChecked(true);
            _btnWednesdayCalls.setEnabled(true);
            _btnWednesdayTimes.setEnabled(true);

            _tvWednesdayCalls.setText(_editProfileList.get(7).replaceAll("(^\\[|\\]$)", ""));
            _tvWednesdayTimes.setText(_editProfileList.get(8).replaceAll("(^\\[|\\]$)", ""));

            _actualSelectedWednesdayCalls.add(_editProfileList.get(7).replaceAll("(^\\[|\\]$)", ""));
            _actualSelectedWednesdayTimes.add(_editProfileList.get(8).replaceAll("(^\\[|\\]$)", ""));
        }

        if(!_editProfileList.get(9).replaceAll("(^\\[|\\]$)", "").equals("")){

            _chkThursday.setChecked(true);
            _btnThursdayCalls.setEnabled(true);
            _btnThursdayTimes.setEnabled(true);

            _tvThursdayCalls.setText(_editProfileList.get(9).replaceAll("(^\\[|\\]$)", ""));
            _tvThursdayTimes.setText(_editProfileList.get(10).replaceAll("(^\\[|\\]$)", ""));

            _actualSelectedThursdayCalls.add(_editProfileList.get(9).replaceAll("(^\\[|\\]$)", ""));
            _actualSelectedThursdayTimes.add(_editProfileList.get(10).replaceAll("(^\\[|\\]$)", ""));
        }

        if(!_editProfileList.get(11).replaceAll("(^\\[|\\]$)", "").equals("")){

            _chkFriday.setChecked(true);
            _btnFridayCalls.setEnabled(true);
            _btnFridayTimes.setEnabled(true);

            _tvFridayCalls.setText(_editProfileList.get(11).replaceAll("(^\\[|\\]$)", ""));
            _tvFridayTimes.setText(_editProfileList.get(12).replaceAll("(^\\[|\\]$)", ""));

            _actualSelectedFridayCalls.add(_editProfileList.get(11).replaceAll("(^\\[|\\]$)", ""));
            _actualSelectedFridayTimes.add(_editProfileList.get(12).replaceAll("(^\\[|\\]$)", ""));
        }

        if(!_editProfileList.get(13).replaceAll("(^\\[|\\]$)", "").equals("")){

            _chkSaturday.setChecked(true);
            _btnSaturdayCalls.setEnabled(true);
            _btnSaturdayTimes.setEnabled(true);

            _tvSaturdayCalls.setText(_editProfileList.get(13).replaceAll("(^\\[|\\]$)", ""));
            _tvSaturdayTimes.setText(_editProfileList.get(14).replaceAll("(^\\[|\\]$)", ""));

            _actualSelectedSaturdayCalls.add(_editProfileList.get(13).replaceAll("(^\\[|\\]$)", ""));
            _actualSelectedSaturdayTimes.add(_editProfileList.get(14).replaceAll("(^\\[|\\]$)", ""));
        }

        if(!_editProfileList.get(15).replaceAll("(^\\[|\\]$)", "").equals("")){

            _chkSunday.setChecked(true);
            _btnSundayCalls.setEnabled(true);
            _btnSundayTimes.setEnabled(true);

            _tvSundayCalls.setText(_editProfileList.get(15).replaceAll("(^\\[|\\]$)", ""));
            _tvSundayTimes.setText(_editProfileList.get(16).replaceAll("(^\\[|\\]$)", ""));

            _actualSelectedSundayCalls.add(_editProfileList.get(15).replaceAll("(^\\[|\\]$)", ""));
            _actualSelectedSundayTimes.add(_editProfileList.get(16).replaceAll("(^\\[|\\]$)", ""));
        }

        selectedServiceTypeID = _editProfileList.get(1);
        firebaseFirestore = Utility.getFirebaseFireStoreInstance();
        Log.e("ID PASSED", _editProfileList.get(1));
        DocumentReference reference = firebaseFirestore.collection("service_types")
                .document(_editProfileList.get(1));

        reference.get()
        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                if(snapshot.exists()){
                    //spnServiceType.setSelection(_editProfileList.indexOf(snapshot.getString("service_name")));
                    for (int i = 0; i < spnServiceType.getCount(); i++) {
                        if (spnServiceType.getItemAtPosition(i).toString().equalsIgnoreCase(snapshot.getString("service_name"))){
                            spnServiceType.setSelection(i);
                            return;
                        }
                    }
                }
            }
        });
    }

    private void handleSignOutOnClick(){
        TextView signOut = findViewById(R.id.createProfileLogOutButton);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ElderlyVolunteerCRUDHelper.signOutUser(VolunteerEditServiceProfileActivity.this, MainActivity.class);
            }
        });
    }

}