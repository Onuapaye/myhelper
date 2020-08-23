package com.martin.myhelper.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ivbaranov.mli.MaterialLetterIcon;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.martin.myhelper.R;

import java.util.ArrayList;
import java.util.Random;

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

public class VolunteerViewElderlyFeedbackAdapter extends RecyclerView.Adapter<VolunteerViewElderlyFeedbackAdapter.VolunteerViewElderlyFeedbackViewHolder> {
    Context _context;
    ArrayList<ArrayList<String>> _listOfElderlyFeedBacks;
    int[] _materialColors;

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;

    public VolunteerViewElderlyFeedbackAdapter(Context context, ArrayList<ArrayList<String>> listOfElderlyFeedBacks) {
        this._context = context;
        this._listOfElderlyFeedBacks = listOfElderlyFeedBacks;
        //this._test = test;
        this._materialColors = this._context.getResources().getIntArray(R.array.materialColors);

        this.firebaseAuth = Utility.getFirebaseAuthenticationInstance();
        this.firebaseFirestore = Utility.getFirebaseFireStoreInstance();
    }

    @NonNull
    @Override
    public VolunteerViewElderlyFeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a layout inflater to inflate the view
        LayoutInflater layoutInflater = LayoutInflater.from(_context);

        // set the layout inflater into a view
        View view = layoutInflater.inflate(R.layout.volunteer_view_elderly_feedback_row, parent, false);

        // return the view using the view holder
        return new VolunteerViewElderlyFeedbackAdapter.VolunteerViewElderlyFeedbackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final VolunteerViewElderlyFeedbackViewHolder holder, int position) {

        // STEP 1. get list of all elders by passing `listOfElders` as a parameter argument to the constructor;
        holder.requestedServiceDescription.setText(_listOfElderlyFeedBacks.get(position).get(4));
        setFeedbackServiceTypeName(holder, position);

        // STEP 2. use the list of all elders to get all requests from the elderly based on the current volunteer id
        getElderlyAccountDetails(holder, position );

        // set the rating bar values
        float _ratingValue = Float.parseFloat(_listOfElderlyFeedBacks.get(position).get(5));
        holder.ratingBar.setRating(_ratingValue);
    }

    @Override
    public int getItemCount() {
        return _listOfElderlyFeedBacks.size();
    }

    public class VolunteerViewElderlyFeedbackViewHolder extends RecyclerView.ViewHolder {

        TextView elderlyName, elderlyMobileNumber, requestedServiceDescription, serviceTypeName;
        MaterialLetterIcon materialLetterIcon;
        RatingBar ratingBar;

        public VolunteerViewElderlyFeedbackViewHolder(@NonNull View itemView) {
            super(itemView);

            elderlyName = itemView.findViewById(R.id.tvFeedbackElderlyName);
            elderlyMobileNumber = itemView.findViewById(R.id.tvFeedbackElderlyMobile);
            requestedServiceDescription = itemView.findViewById(R.id.tvFeedbackMessage);
            serviceTypeName = itemView.findViewById(R.id.tvFeedbackServiceTypeName);
            materialLetterIcon = itemView.findViewById(R.id.feedbackMaterialLetterIcon);
            ratingBar = itemView.findViewById(R.id.feedbackRatingBarFeedback);
        }
    }

    private void getElderlyAccountDetails(final VolunteerViewElderlyFeedbackAdapter.VolunteerViewElderlyFeedbackViewHolder holder, int position) {

        /*final CollectionReference reference = firebaseFirestore.collection("elders");

            reference.whereEqualTo("id", _listOfElderlyFeedBacks.get(position).get(1))
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {

                            // set material letter icon props
                            holder.materialLetterIcon.setInitials(true);
                            holder.materialLetterIcon.setInitialsNumber(2);
                            holder.materialLetterIcon.setLetterSize(25);
                            holder.materialLetterIcon.setShapeColor(_materialColors[new Random().nextInt(_materialColors.length)]);

                            String _fullName = String.format("%s, %s", snapshot.get("firstName"), snapshot.get("lastName").toString().toUpperCase());

                            // Get the elderly details to the array list
                            holder.materialLetterIcon.setLetter(_fullName);
                            holder.elderlyName.setText(_fullName);
                            holder.elderlyMobileNumber.setText(snapshot.get("mobileNumber").toString());
                        }
                    }
                });*/

        final DocumentReference reference = firebaseFirestore.collection("elders").document(_listOfElderlyFeedBacks.get(position).get(1));
        reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {

                // set material letter icon props
                holder.materialLetterIcon.setInitials(true);
                holder.materialLetterIcon.setInitialsNumber(2);
                holder.materialLetterIcon.setLetterSize(25);
                holder.materialLetterIcon.setShapeColor(_materialColors[new Random().nextInt(_materialColors.length)]);

                String _fullName = String.format("%s, %s", snapshot.get("firstName"), snapshot.get("lastName").toString().toUpperCase());

                // Get the elderly details to the array list
                holder.materialLetterIcon.setLetter(_fullName);
                holder.elderlyName.setText(_fullName);
                holder.elderlyMobileNumber.setText(snapshot.get("mobileNumber").toString());
            }
        });
    }

    private void setFeedbackServiceTypeName(VolunteerViewElderlyFeedbackAdapter.VolunteerViewElderlyFeedbackViewHolder holder, int position){
        switch (_listOfElderlyFeedBacks.get(position).get(3)) {
            case "100":
                holder.serviceTypeName.setText(TEACH_USAGE_MOBILE_DEVICES);
                break;
            case "200":
                holder.serviceTypeName.setText(TEACH_USAGE_WEB_APPS);
                break;
            case "300":
                holder.serviceTypeName.setText(WALK_WITH_U);
                break;
            case "400":
                holder.serviceTypeName.setText(PROVIDE_LIFT_TO_SOCIAL);
                break;
            case "500":
                holder.serviceTypeName.setText(ASSIST_WITH_HOUSE_CLEANING);
                break;
            case "600":
                holder.serviceTypeName.setText(ASSIST_WITH_HOUSE_MAINTENANCE);
                break;
            case "700":
                holder.serviceTypeName.setText(ASSIST_WITH_GARDENING);
                break;
            case "800":
                holder.serviceTypeName.setText(ASSIST_WITH_ERRANDS);
                break;
            case "900":
                holder.serviceTypeName.setText(ASSIST_WITH_GROCERY_SHOPPING);
                break;
            case "1000":
                holder.serviceTypeName.setText(PROVIDE_LIFT_TO_SHOP);
                break;
            default:
                holder.serviceTypeName.setText(TAKE_CARE_OF_PETS);
                break;
        }
    }
}
