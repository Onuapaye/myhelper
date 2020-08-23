package com.martin.myhelper.helpers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.martin.myhelper.R;
import com.martin.myhelper.model.ElderlyModel;
import com.martin.myhelper.views.ElderlyHomeActivity;
import com.martin.myhelper.views.ElderlyProvideFeedBackActivity;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ElderlyProvideFeedbackAdapter extends RecyclerView.Adapter<ElderlyProvideFeedbackAdapter.ElderlyProvideFeedbackViewHolder> {

    ArrayList<ArrayList<String>> _volunteerProfileData, _volunteerAccountData, _elderlyRequestData;

    Context _context;
    FirebaseFirestore firebaseFirestore;
    ArrayList<String> _tempList2;
    int ratingValue;

    //AppCompatActivity compatActivity;

    public ElderlyProvideFeedbackAdapter(ArrayList<ArrayList<String>> elderlyRequestData, Context _context) {
        this._elderlyRequestData = elderlyRequestData;
        this._volunteerProfileData = new ArrayList<>();
        this._context = _context;
        this.ratingValue = 0;
        //this.compatActivity = ElderlyProvideFeedBackActivity.this;
    }

    @NonNull
    @Override
    public ElderlyProvideFeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a layout inflater to inflate the view
        LayoutInflater layoutInflater = LayoutInflater.from(_context);

        // set the layout inflater into a view
        View view = layoutInflater.inflate(R.layout.elderly_provide_feedback_row, parent, false);

        // return the view using the view holder
        return new ElderlyProvideFeedbackAdapter.ElderlyProvideFeedbackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ElderlyProvideFeedbackViewHolder holder, final int position) {

        _volunteerAccountData = new ArrayList<>();
        getVolunteerAccountDetails(_elderlyRequestData.get(position).get(2), holder);
        getVolunteerProfileDetails(_elderlyRequestData.get(position).get(2), _elderlyRequestData.get(position).get(3), holder);

        holder.btnSendConfirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            if (holder.etFeedbackMessage.getText().toString() == "" || holder.etFeedbackMessage.getText().toString().isEmpty() || holder.etFeedbackMessage.getText().toString().trim().length() <= 0){
                Utility.showInformationDialog(Utility.REQUIRED_FIELD_TITLE, "Please enter your message for this feedback", (AppCompatActivity) _context);
                return;
            }
            if (ratingValue <= 0){
                Utility.showInformationDialog("NOT RATING DEFINED", "Please RATE this feedback", (AppCompatActivity) _context);
                return;
            }

                ElderlyModel model = new ElderlyModel();
                model.setFeedBackElderlyId(_elderlyRequestData.get(position).get(1));
                model.setFeedBackVolunteerId(_elderlyRequestData.get(position).get(2));
                model.setFeedBackServiceTypeId(_elderlyRequestData.get(position).get(3));
                model.setFeedBackRequestId(_elderlyRequestData.get(position).get(0));
                model.setFeedBackRating(ratingValue);
                model.setFeedBackMessage(holder.etFeedbackMessage.getText().toString().trim());

                ElderlyCRUDHelper crudHelper = new ElderlyCRUDHelper();
                crudHelper.createElderlyServiceFeedback(view.getContext(), model, _context);

                Intent intent = new Intent(_context, ElderlyHomeActivity.class);
                intent.putExtra("feedBackProvided", "You have successfully RATED the Volunteer for his/her service.");
               _context.startActivity(intent);

            }
        });

        holder.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                int value = (int) v;
                //int value = (int) ratingBar.getRating();

                if (value == 1) {
                    ratingValue = (int) ratingBar.getRating();
                } else if (value == 2){
                    ratingValue = (int) ratingBar.getRating();
                } else if (value == 3){
                    ratingValue = (int) ratingBar.getRating();
                } else if (value == 4) {
                    ratingValue = (int) ratingBar.getRating();
                } else {
                    ratingValue = (int) ratingBar.getRating();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return _elderlyRequestData.size();// + _volunteerProfileData.size();
    }

    public class ElderlyProvideFeedbackViewHolder extends RecyclerView.ViewHolder {

        // create a variable for the items in the view
        TextView volunteerName, serviceDescription, volunteerMobile;
        CircularImageView volunteerImage;
        Button btnSendConfirmation;
        RatingBar ratingBar;
        EditText etFeedbackMessage;

        public ElderlyProvideFeedbackViewHolder(@NonNull View itemView) {
            super(itemView);

            volunteerName = itemView.findViewById(R.id.viewRequestVolunteerName);
            volunteerMobile = itemView.findViewById(R.id.viewVolunteerMobile);
            serviceDescription = itemView.findViewById(R.id.viewVolunteerServiceDescription);
            volunteerImage = itemView.findViewById(R.id.imgViewVolunteerImage);

            etFeedbackMessage = itemView.findViewById(R.id.etFeedbackMessage);

            btnSendConfirmation = itemView.findViewById(R.id.btnSendFeedback);
            ratingBar = itemView.findViewById(R.id.ratingBarFeedback);
        }
    }

    private void getVolunteerProfileDetails(String requestVolunteerId, String requestServiceTypeId, final ElderlyProvideFeedbackAdapter.ElderlyProvideFeedbackViewHolder holder){

        // STEP 2 : Get the collections and documents from the volunteer profiles
        CollectionReference volunteerCollection = firebaseFirestore.collection("volunteer_profiles")
                .document(requestVolunteerId).collection("profiles");

        volunteerCollection
                .whereEqualTo("volunteerId", requestVolunteerId)
                .whereEqualTo("serviceTypeId", requestServiceTypeId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            if (task.getResult() != null){

                                QuerySnapshot querySnapshot = task.getResult();

                                for(QueryDocumentSnapshot docSnapshot : querySnapshot){

                                    // instantiate the temporal array list of volunteer services
                                    _tempList2 = new ArrayList<>();

                                    holder.serviceDescription.setText(docSnapshot.getString("description"));

                                    _tempList2.add(docSnapshot.getString("id"));
                                    _tempList2.add(docSnapshot.getString("serviceTypeId"));
                                    _tempList2.add(docSnapshot.getString("volunteerId"));
                                    _tempList2.add(docSnapshot.getString("description"));
                                    _tempList2.add(String.valueOf(docSnapshot.get("daysForService")));
                                    _tempList2.add(String.valueOf(docSnapshot.get("timesForService")));
                                    _tempList2.add(String.valueOf(docSnapshot.get("timesForCalls")));

                                    _volunteerProfileData.add(_tempList2);
                                }
                            }
                        }
                    }
                });
    }

    private void getVolunteerAccountDetails(String volunteerId, final ElderlyProvideFeedbackAdapter.ElderlyProvideFeedbackViewHolder holder) {

        firebaseFirestore = Utility.getFirebaseFireStoreInstance();
        DocumentReference volunteerDocRef = firebaseFirestore.collection("volunteers").document(volunteerId);
        volunteerDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    _tempList2 = new ArrayList<>();

                    if (task.getResult() != null) {

                        DocumentSnapshot vSnapshot = task.getResult();

                        // Get the volunteer details to the array list
                        holder.volunteerName.setText(String.format("%s, %s", vSnapshot.getString("firstName"), vSnapshot.getString("lastName")));
                        holder.volunteerMobile.setText(vSnapshot.getString("mobileNumber"));

                        _tempList2.add(vSnapshot.getString("firstName"));
                        _tempList2.add(vSnapshot.getString("lastName"));
                        _tempList2.add(vSnapshot.getString("email"));
                        _tempList2.add(vSnapshot.getString("mobileNumber"));
                        _tempList2.add(vSnapshot.getString("imageType"));

                        _volunteerAccountData.add(_tempList2);

                        // load and show the profile image
                        loadProfilePhotoIntoImageView(vSnapshot.getString("id"), vSnapshot.getString("imageType"), holder);
                    }
                }
            }
        });
    }

    private void loadProfilePhotoIntoImageView(String imageName, String imageExtension, final ElderlyProvideFeedbackAdapter.ElderlyProvideFeedbackViewHolder holder) {

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference("images/").child(imageName + "." + imageExtension);

        storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()){

                    Uri uri = task.getResult();

                    //CircularImageView imageView =  itemView.findViewById(R.id.imgViewVolunteerImage);
                    Picasso.get().load(uri).into(holder.volunteerImage);
                }
            }
        });
    }
}
