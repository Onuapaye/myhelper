package com.martin.myhelper.helpers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.martin.myhelper.R;
import com.martin.myhelper.views.VolunteerProfileActivity;
import com.martin.myhelper.views.VolunteerProfileEditActivity;

import java.util.ArrayList;

import static com.martin.myhelper.helpers.Utility.*;
import static java.lang.String.*;

public class VolunteerProfileAdapter extends RecyclerView.Adapter<VolunteerProfileAdapter.VolunteerProfileViewHolder> {

    ArrayList<ArrayList<String>> _profileList;
    Context _context;
    int _collectionCount;

    public VolunteerProfileAdapter(Context context, ArrayList<ArrayList<String>> profileList, int collectionCount ) {
        Log.d("INSIDE_ADAPTER", valueOf(profileList.size()));
        Log.d("INSIDE_ADAPTER2", String.valueOf(profileList.toArray().length));

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
        View view = layoutInflater.inflate(R.layout.volunteer_profile_row, parent, false);

        // return the view using the view holder
        return new VolunteerProfileAdapter.VolunteerProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final VolunteerProfileViewHolder holder, final int position) {

        switch (_profileList.get(position).get(1)) {
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
        }

        holder.tvServiceDescription.setText(_profileList.get(position).get(2));
        holder.tvServiceDays.setText(_profileList.get(position).get(3).replaceAll("(^\\[|\\]$)", ""));
        holder.tvServicesTimes.setText(_profileList.get(position).get(4).replaceAll("(^\\[|\\]$)", ""));
        holder.tvServiceCalls.setText(_profileList.get(position).get(5).replaceAll("(^\\[|\\]$)", ""));

        holder.btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(_context, VolunteerProfileEditActivity.class);
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

}
