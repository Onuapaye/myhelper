package com.martin.myhelper.helpers;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.martin.myhelper.R;
import com.martin.myhelper.views.VolunteerEditServiceProfileActivity;
import com.martin.myhelper.views.VolunteerProfileEditActivity;

import java.util.ArrayList;

import static com.martin.myhelper.helpers.Utility.*;

public class VolunteerProfileAdapter extends RecyclerView.Adapter<VolunteerProfileAdapter.VolunteerProfileViewHolder> {

    ArrayList<ArrayList<String>> _profileList;
    Context _context;
    int _collectionCount;

    public VolunteerProfileAdapter(Context context, ArrayList<ArrayList<String>> profileList, int collectionCount ) {
        _context = context;
        _profileList = profileList;
        _collectionCount = collectionCount;
    }

    @NonNull
    @Override
    public VolunteerProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // create a layout inflater to inflate the view
        LayoutInflater layoutInflater = LayoutInflater.from(_context);

        // set the layout inflater into a view
        View view = layoutInflater.inflate(R.layout.volunteer_profiles_row, parent, false);

        // return the view using the view holder
        return new VolunteerProfileAdapter.VolunteerProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final VolunteerProfileViewHolder holder, final int position) {

        /*switch (_profileList.get(position).get(1)) {
            case "100":
                holder.tvServiceType.setText(TEACH_USAGE_MOBILE_DEVICES);
                break;
            case "200":
                holder.tvServiceType.setText(TEACH_USAGE_WEB_APPS);
                break;
            case "300":
                holder.tvServiceType.setText(WALK_WITH_U);
                break;
            case "400":
                holder.tvServiceType.setText(PROVIDE_LIFT_TO_SOCIAL);
                break;
            case "500":
                holder.tvServiceType.setText(ASSIST_WITH_HOUSE_CLEANING);
                break;
            case "600":
                holder.tvServiceType.setText(ASSIST_WITH_HOUSE_MAINTENANCE);
                break;
            case "700":
                holder.tvServiceType.setText(ASSIST_WITH_GARDENING);
                break;
            case "800":
                holder.tvServiceType.setText(ASSIST_WITH_ERRANDS);
                break;
            case "900":
                holder.tvServiceType.setText(ASSIST_WITH_GROCERY_SHOPPING);
                break;
            case "1000":
                holder.tvServiceType.setText(PROVIDE_LIFT_TO_SHOP);
                break;
            default:
                holder.tvServiceType.setText(TAKE_CARE_OF_PETS);
                break;
        }*/
        setServiceTypeName(holder, position);
        holder.tvServiceDescription.setText(_profileList.get(position).get(2));
        getDayTimesAndCalls(holder, position);
        //holder.tvServiceDays.setText(_profileList.get(position).get(3).replaceAll("(^\\[|\\]$)", ""));
        //holder.tvServicesTimes.setText(_profileList.get(position).get(4).replaceAll("(^\\[|\\]$)", ""));
        //holder.tvServiceCalls.setText(_profileList.get(position).get(5).replaceAll("(^\\[|\\]$)", ""));

        holder.btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(_context, VolunteerEditServiceProfileActivity.class);
                intent.putExtra("record", _profileList.get(position));
                _context.startActivity(intent);
            }
        });

        holder.btnDeleteProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VolunteerCRUDHelper crudHelper = new VolunteerCRUDHelper();
                crudHelper.deleteVolunteerServiceProfile((AppCompatActivity)_context, _profileList.get(position).get(0));
                //this.notifyAll();
            }
        });
    }

    @Override
    public int getItemCount() {
        return _collectionCount;
    }

    public class VolunteerProfileViewHolder extends RecyclerView.ViewHolder {

        TextView tvServiceDays, tvServicesTimes, tvServiceCalls, tvServiceType, tvServiceDescription;
        Button btnEditProfile, btnDeleteProfile;

        public VolunteerProfileViewHolder(@NonNull View itemView) {
            super(itemView);

            tvServiceType = itemView.findViewById(R.id.tvProfileServiceType);
            tvServiceDescription = itemView.findViewById(R.id.tvProfileServiceDescription);
            tvServiceDays = itemView.findViewById(R.id.tvProfileServiceDays);
            tvServicesTimes = itemView.findViewById(R.id.tvProfileServiceTimes);
            tvServiceCalls = itemView.findViewById(R.id.tvProfileServiceCalls);

            btnEditProfile = itemView.findViewById(R.id.btnEditProfile);
            btnDeleteProfile = itemView.findViewById(R.id.btnDeleteProfile);
        }
    }

    private void setServiceTypeName(final VolunteerProfileAdapter.VolunteerProfileViewHolder holder, int position){

        FirebaseFirestore firebaseFirestore = Utility.getFirebaseFireStoreInstance();

        DocumentReference reference = firebaseFirestore.collection("service_types").document(_profileList.get(position).get(1));
        reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                if (snapshot.exists()){
                    holder.tvServiceType.setText(snapshot.getString("service_name"));
                }
            }
        });
    }

    private void getDayTimesAndCalls(final VolunteerProfileAdapter.VolunteerProfileViewHolder holder, int position){

        String _allTimes = "", _allCalls = "";

        // set times
        if(!_profileList.get(position).get(3).replaceAll("(^\\[|\\]$)", "").equals("")){
            _allCalls += "Monday - Call Time\n" + _profileList.get(position).get(3).replaceAll("(^\\[|\\]$)", "") + " \n\n";
            _allTimes += "Monday - Service\n" + _profileList.get(position).get(4).replaceAll("(^\\[|\\]$)", "") + " \n\n";
        }

        if(!_profileList.get(position).get(5).replaceAll("(^\\[|\\]$)", "").equals("") ){
            _allCalls += "Tuesday - Call Time\n" + _profileList.get(position).get(5).replaceAll("(^\\[|\\]$)", "") + " \n\n";
            _allTimes += "Tuesday - Service Time\n" + _profileList.get(position).get(6).replaceAll("(^\\[|\\]$)", "") + " \n\n";
        }

        if(!_profileList.get(position).get(7).replaceAll("(^\\[|\\]$)", "").equals("")){
            _allCalls += "Wednesday - Call Time\n" + _profileList.get(position).get(7).replaceAll("(^\\[|\\]$)", "") + " \n\n";
            _allTimes += "Wednesday - Service Time\n" + _profileList.get(position).get(8).replaceAll("(^\\[|\\]$)", "") + " \n\n";
        }

        if(!_profileList.get(position).get(9).replaceAll("(^\\[|\\]$)", "").equals("")){
            _allCalls += "Thursday - Call Time\n" + _profileList.get(position).get(9).replaceAll("(^\\[|\\]$)", "") + " \n\n";
            _allTimes += "Thursday - Service Time\n" + _profileList.get(position).get(10).replaceAll("(^\\[|\\]$)", "") + " \n\n";
        }

        if(!_profileList.get(position).get(11).replaceAll("(^\\[|\\]$)", "").equals("")){
            _allCalls += "Friday - Call Time\n" + _profileList.get(position).get(11).replaceAll("(^\\[|\\]$)", "") + " \n\n";
            _allTimes += "Friday - Service Time\n" + _profileList.get(position).get(12).replaceAll("(^\\[|\\]$)", "") + " \n\n";
        }

        if(!_profileList.get(position).get(13).replaceAll("(^\\[|\\]$)", "").equals("")){
            _allCalls += "Saturday - Call Time\n" + _profileList.get(position).get(13).replaceAll("(^\\[|\\]$)", "") + " \n\n";
            _allTimes += "Saturday - Service Time\n" + _profileList.get(position).get(14).replaceAll("(^\\[|\\]$)", "") + " \n\n";
        }

        if(!_profileList.get(position).get(15).replaceAll("(^\\[|\\]$)", "").equals("")){
            _allCalls += "Sunday - Call Time\n" + _profileList.get(position).get(15).replaceAll("(^\\[|\\]$)", "");
            _allTimes += "Sunday - Service Time\n" + _profileList.get(position).get(16).replaceAll("(^\\[|\\]$)", "") ;
        }

        holder.tvServiceDays.setText(_allCalls);
        holder.tvServicesTimes.setText(_allTimes);
    }
}
