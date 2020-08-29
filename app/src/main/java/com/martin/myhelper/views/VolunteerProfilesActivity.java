package com.martin.myhelper.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.martin.myhelper.R;
import com.martin.myhelper.helpers.Utility;
import com.martin.myhelper.helpers.VolunteerProfileAdapter;

import java.util.ArrayList;
import java.util.Arrays;

import static com.martin.myhelper.helpers.Utility.CREATE_RECORD_SUCCESS_TITLE;
import static com.martin.myhelper.helpers.Utility.CREATE_VOLUNTEER_PROFILE_SUCCESS_MSG;
import static com.martin.myhelper.helpers.Utility.UPDATE_RECORD_SUCCESS_MSG;
import static com.martin.myhelper.helpers.Utility.UPDATE_RECORD_SUCCESS_TITLE;

public class VolunteerProfilesActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    VolunteerProfileAdapter volunteerProfileAdapter;

    private ArrayList<ArrayList<String>> profileList; //= new ArrayList<String>();
    private RecyclerView rcvProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_profiles);

        this.showProfileRecyclerView();

        this.showCreateSuccessMessage();
        this.showUpdateSuccessMessage();
    }


    private void showProfileRecyclerView() {

        rcvProfile = findViewById(R.id.rcvVolunteerProfile);
        firebaseFirestore = Utility.getFirebaseFireStoreInstance();
        firebaseAuth = Utility.getFirebaseAuthenticationInstance();

        final CollectionReference profileReference = firebaseFirestore.collection("volunteer_profiles")
                .document(firebaseAuth.getCurrentUser().getUid()).collection("profiles");

        profileReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

            profileList = new ArrayList<>();

            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){

                ArrayList<String> _tempList = new ArrayList<>(Arrays.asList(
                    documentSnapshot.getString("id"),
                    documentSnapshot.getString("serviceTypeId"),
                    documentSnapshot.getString("description"),
                    String.valueOf(documentSnapshot.get("daysForService")),
                    String.valueOf(documentSnapshot.get("timesForService")),
                    String.valueOf(documentSnapshot.get("timesForCalls"))
                ));

                profileList.add(_tempList);
            }

            volunteerProfileAdapter = new VolunteerProfileAdapter(VolunteerProfilesActivity.this,
                    profileList, queryDocumentSnapshots.size());
            rcvProfile.setAdapter(volunteerProfileAdapter);
            rcvProfile.setLayoutManager(new LinearLayoutManager(VolunteerProfilesActivity.this));
            }
        });
    }

    private  void showCreateSuccessMessage(){
        Intent intent = getIntent();
        intent.getExtras();

        if (intent.hasExtra("recordCreated")){
            // show a message for successful record recreation
            Utility.showInformationDialog(CREATE_RECORD_SUCCESS_TITLE,
                    CREATE_VOLUNTEER_PROFILE_SUCCESS_MSG , VolunteerProfilesActivity.this);
        }
    }

    private void showUpdateSuccessMessage(){
        Intent intent = getIntent();
        intent.getExtras();

        if (intent.hasExtra("recordUpdate")){
         Utility.showInformationDialog(UPDATE_RECORD_SUCCESS_TITLE, UPDATE_RECORD_SUCCESS_MSG, VolunteerProfilesActivity.this);
        }
    }
}