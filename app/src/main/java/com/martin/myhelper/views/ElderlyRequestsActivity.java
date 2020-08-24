package com.martin.myhelper.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
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

import static com.martin.myhelper.helpers.Utility.CREATE_ELDERLY_REQUEST_SUCCESS_MSG;
import static com.martin.myhelper.helpers.Utility.CREATE_RECORD_SUCCESS_TITLE;
import static com.martin.myhelper.helpers.Utility.CREATE_VOLUNTEER_PROFILE_SUCCESS_MSG;
import static com.martin.myhelper.helpers.Utility.UPDATE_RECORD_SUCCESS_MSG;
import static com.martin.myhelper.helpers.Utility.UPDATE_RECORD_SUCCESS_TITLE;

public class ElderlyRequestsActivity extends AppCompatActivity {

    private ArrayList<ArrayList<String>> elderlyRequestList;
    private ArrayList<String> _tempElderlyRequestList, _tempVolunteerProfileList, _tempVolunteerList, _tempList;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elderly_requests);

        this.getElderlyRequestDetails();

        //this.showUpdateSuccessMessage();
    }

    private void getElderlyRequestDetails(){

        firebaseFirestore = Utility.getFirebaseFireStoreInstance();
        firebaseAuth = Utility.getFirebaseAuthenticationInstance();

        final CollectionReference eldersColsRef = firebaseFirestore.collection("elderly_requests")
                                                                   .document(firebaseAuth.getCurrentUser().getUid())
                                                                   .collection("requests");

        // instantiate the array list of volunteers
        elderlyRequestList = new ArrayList<>();
        _tempList = new ArrayList<>();

        eldersColsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){

                    if (task.getResult() != null) {

                        // get the query snapshot from the task
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                            _tempElderlyRequestList = new ArrayList<>();

                            _tempElderlyRequestList.add(documentSnapshot.getString("id"));
                            _tempElderlyRequestList.add(documentSnapshot.getString("elderlyId"));
                            _tempElderlyRequestList.add(documentSnapshot.getString("requestVolunteerId"));
                            _tempElderlyRequestList.add(documentSnapshot.getString("requestServiceTypeId"));
                            _tempElderlyRequestList.add(documentSnapshot.getString("requestDaysForService"));
                            _tempElderlyRequestList.add(documentSnapshot.getString("requestTimesForService"));
                            _tempElderlyRequestList.add(documentSnapshot.getString("requestMessage"));

                            // merge into the temp array list variable
                            _tempList.addAll(_tempElderlyRequestList);

                            // we get the records of the volunteer
                            final CollectionReference volunteerProfileReference = firebaseFirestore.collection("volunteer_profiles")
                                    .document(documentSnapshot.getString("requestVolunteerId"))
                                    .collection("profiles");

                            volunteerProfileReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                    if (task.isSuccessful()) {
                                        if (task.getResult() != null) {

                                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {

                                                _tempVolunteerProfileList = new ArrayList<>();

                                                _tempVolunteerProfileList.add(queryDocumentSnapshot.getString("serviceTypeId"));
                                                _tempVolunteerProfileList.add(queryDocumentSnapshot.getString("description"));
                                                _tempVolunteerProfileList.add(queryDocumentSnapshot.getString("daysForService"));
                                                _tempVolunteerProfileList.add(queryDocumentSnapshot.getString("timesForService"));
                                                _tempVolunteerProfileList.add(queryDocumentSnapshot.getString("timesForCalls"));

                                                // merge into the temp array list variable
                                                _tempList.addAll(_tempVolunteerProfileList);

                                                final DocumentReference volunteerDocRef = firebaseFirestore.collection("volunteers")
                                                        .document(queryDocumentSnapshot.getString("volunteerId"));

                                                volunteerDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()){

                                                        if (task.getResult() != null){

                                                            DocumentSnapshot snapshot = task.getResult();

                                                            _tempVolunteerList = new ArrayList<>();

                                                            _tempVolunteerList.add(snapshot.getString("firstName"));
                                                            _tempVolunteerList.add(snapshot.getString("lastName"));
                                                            _tempVolunteerList.add(snapshot.getString("email"));
                                                            _tempVolunteerList.add(snapshot.getString("mobileNumber"));
                                                            _tempVolunteerList.add(snapshot.getString("imageType"));

                                                            // get the image
                                                            loadProfilePhotoIntoImageView(snapshot.getString("id"), snapshot.getString("imageType"));
                                                        }
                                                    }

                                                    _tempList.addAll(_tempVolunteerList);
                                                    }
                                                });
                                            }
                                        }
                                    }

                                    elderlyRequestList.add(_tempList);
                                    RecyclerView rcvElderlyRequest = findViewById(R.id.rcvElderlyRequests);
                                    ElderlyRequestsAdapter elderlyRequestsAdapter;

                                    elderlyRequestsAdapter = new ElderlyRequestsAdapter(elderlyRequestList, ElderlyRequestsActivity.this);
                                    rcvElderlyRequest.setAdapter(elderlyRequestsAdapter);
                                    rcvElderlyRequest.setLayoutManager(new LinearLayoutManager(ElderlyRequestsActivity.this));
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    private void loadProfilePhotoIntoImageView(String imageName, String imageExtension) {

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference("images/").child(imageName + "." + imageExtension);

        storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()){

                    Uri uri = task.getResult();

                    CircularImageView imageView = findViewById(R.id.imgRequestVolunteerPhoto);
                    Picasso.get().load(uri).into(imageView);
                }
            }
        });
    }

    private void showUpdateSuccessMessage(){
        Intent intent = getIntent();
        intent.getExtras();

        if (intent.hasExtra("recordUpdate")){
            Utility.showInformationDialog(UPDATE_RECORD_SUCCESS_TITLE, UPDATE_RECORD_SUCCESS_MSG, ElderlyRequestsActivity.this);
        }
    }
}