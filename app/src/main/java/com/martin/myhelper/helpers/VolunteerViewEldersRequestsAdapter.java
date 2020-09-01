package com.martin.myhelper.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class VolunteerViewEldersRequestsAdapter extends RecyclerView.Adapter<VolunteerViewEldersRequestsAdapter.VolunteerViewEldersRequestViewHolder> {

    Context _context;
    ArrayList<ArrayList<String>> _listOfElders, _elderlyRequestsForVolunteerList, _volunteerAccountList;
    ArrayList<String> _tempList, _tempList2;
    int[] _materialColors;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    public VolunteerViewEldersRequestsAdapter(Context context, ArrayList<ArrayList<String>> listOfElders) {
        this._context = context;
        this._listOfElders = listOfElders;
        this.firebaseAuth = Utility.getFirebaseAuthenticationInstance();
        this.firebaseFirestore = Utility.getFirebaseFireStoreInstance();
        this._materialColors = this._context.getResources().getIntArray(R.array.materialColors);
    }

    @NonNull
    @Override
    public VolunteerViewEldersRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a layout inflater to inflate the view
        LayoutInflater layoutInflater = LayoutInflater.from(_context);

        // set the layout inflater into a view
        View view = layoutInflater.inflate(R.layout.volunteer_view_elderly_requests_row, parent, false);

        // return the view using the view holder
        return new VolunteerViewEldersRequestsAdapter.VolunteerViewEldersRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VolunteerViewEldersRequestViewHolder holder, int position) {

        // STEP 1. get list of all elders by passing `listOfElders` as a parameter argument to the constructor;
        //holder.tvDays.setText(_listOfElders.get(position).get(4));
        //holder.tvTimes.setText(_listOfElders.get(position).get(5));
        getDayTimesAndCalls(holder, position);
        holder.requestedServiceDescription.setText(_listOfElders.get(position).get(18));
        setRequestServiceTypeName(holder, position);

        // STEP 2. use the list of all elders to get all requests from the elderly based on the current volunteer id
        getElderlyAccountDetails(_listOfElders.get(position).get(1), holder );

    }

    @Override
    public int getItemCount() {
        return _listOfElders.size();
    }

    public class VolunteerViewEldersRequestViewHolder extends RecyclerView.ViewHolder {

        TextView elderlyName, elderlyMobileNumber, requestedServiceDescription, serviceTypeName, tvDays, tvTimes;
        MaterialLetterIcon materialLetterIcon;
        Button btnGoToNotificationPage;

        public VolunteerViewEldersRequestViewHolder(@NonNull View itemView) {
            super(itemView);

            elderlyName = itemView.findViewById(R.id.tvRequestedElderlyName);
            elderlyMobileNumber = itemView.findViewById(R.id.tvRequestedElderlyMobile);
            requestedServiceDescription = itemView.findViewById(R.id.tvRequestedServiceDescription);
            serviceTypeName = itemView.findViewById(R.id.tvServiceTypeName);
            tvDays = itemView.findViewById(R.id.tvDays);
            tvTimes = itemView.findViewById(R.id.tvTimes);
            materialLetterIcon = itemView.findViewById(R.id.elderlyMaterialLetterIcon);

            btnGoToNotificationPage = itemView.findViewById(R.id.btnGoToNotificationPage);
        }
    }

    private void getElderlyAccountDetails(String elderlyId, final VolunteerViewEldersRequestsAdapter.VolunteerViewEldersRequestViewHolder holder) {

        final DocumentReference reference = firebaseFirestore.collection("elders").document(elderlyId);
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

    private void setRequestServiceTypeName(final VolunteerViewEldersRequestsAdapter.VolunteerViewEldersRequestViewHolder holder, int position){

        DocumentReference reference = firebaseFirestore.collection("service_types").document(_listOfElders.get(position).get(3));
        reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                if (snapshot.exists()){
                    holder.serviceTypeName.setText(snapshot.getString("service_name"));
                }
            }
        });
    }

    private void getDayTimesAndCalls(final VolunteerViewEldersRequestsAdapter.VolunteerViewEldersRequestViewHolder holder, int position){

        String _allTimes = "", _allCalls = "";

        // set times
        if(!_listOfElders.get(position).get(4).replaceAll("(^\\[|\\]$)", "").equals("")){
            _allCalls += "Monday - Call Time\n" + _listOfElders.get(position).get(4).replaceAll("(^\\[|\\]$)", "") + " \n\n";
            _allTimes += "Monday - Service\n" + _listOfElders.get(position).get(5).replaceAll("(^\\[|\\]$)", "") + " \n\n";
        }

        if(!_listOfElders.get(position).get(6).replaceAll("(^\\[|\\]$)", "").equals("") ){
            _allCalls += "Tuesday - Call Time\n" + _listOfElders.get(position).get(6).replaceAll("(^\\[|\\]$)", "") + " \n\n";
            _allTimes += "Tuesday - Service Time\n" + _listOfElders.get(position).get(7).replaceAll("(^\\[|\\]$)", "") + " \n\n";
        }

        if(!_listOfElders.get(position).get(8).replaceAll("(^\\[|\\]$)", "").equals("")){
            _allCalls += "Wednesday - Call Time\n" + _listOfElders.get(position).get(8).replaceAll("(^\\[|\\]$)", "") + " \n\n";
            _allTimes += "Wednesday - Service Time\n" + _listOfElders.get(position).get(9).replaceAll("(^\\[|\\]$)", "") + " \n\n";
        }

        if(!_listOfElders.get(position).get(10).replaceAll("(^\\[|\\]$)", "").equals("")){
            _allCalls += "Thursday - Call Time\n" + _listOfElders.get(position).get(10).replaceAll("(^\\[|\\]$)", "") + " \n\n";
            _allTimes += "Thursday - Service Time\n" + _listOfElders.get(position).get(11).replaceAll("(^\\[|\\]$)", "") + " \n\n";
        }

        if(!_listOfElders.get(position).get(12).replaceAll("(^\\[|\\]$)", "").equals("")){
            _allCalls += "Friday - Call Time\n" + _listOfElders.get(position).get(12).replaceAll("(^\\[|\\]$)", "") + " \n\n";
            _allTimes += "Friday - Service Time\n" + _listOfElders.get(position).get(13).replaceAll("(^\\[|\\]$)", "") + " \n\n";
        }

        if(!_listOfElders.get(position).get(14).replaceAll("(^\\[|\\]$)", "").equals("")){
            _allCalls += "Saturday - Call Time\n" + _listOfElders.get(position).get(14).replaceAll("(^\\[|\\]$)", "") + " \n\n";
            _allTimes += "Saturday - Service Time\n" + _listOfElders.get(position).get(15).replaceAll("(^\\[|\\]$)", "") + " \n\n";
        }

        if(!_listOfElders.get(position).get(16).replaceAll("(^\\[|\\]$)", "").equals("")){
            _allCalls += "Sunday - Call Time\n" + _listOfElders.get(position).get(16).replaceAll("(^\\[|\\]$)", "");
            _allTimes += "Sunday - Service Time\n" + _listOfElders.get(position).get(17).replaceAll("(^\\[|\\]$)", "");
        }

        holder.tvDays.setText(_allCalls);
        holder.tvTimes.setText(_allTimes);
    }
}
