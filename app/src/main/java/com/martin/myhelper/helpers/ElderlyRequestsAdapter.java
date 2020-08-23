package com.martin.myhelper.helpers;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.martin.myhelper.R;
import com.martin.myhelper.views.ElderlyCreateRequestActivity;
import com.martin.myhelper.views.ElderlyEditRequestActivity;
import com.martin.myhelper.views.ElderlyHomeActivity;
import com.martin.myhelper.views.VolunteerProfileEditActivity;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

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

public class ElderlyRequestsAdapter extends RecyclerView.Adapter<ElderlyRequestsAdapter.ElderlyViewHolder> {

    ArrayList<ArrayList<String>> _requestsList;
    Context _context;

    public ElderlyRequestsAdapter(ArrayList<ArrayList<String>> _requestsList, Context _context) {
        this._requestsList = _requestsList;
        this._context = _context;
    }

    @NonNull
    @Override
    public ElderlyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a layout inflater to inflate the view
        LayoutInflater layoutInflater = LayoutInflater.from(_context);

        // set the layout inflater into a view
        View view = layoutInflater.inflate(R.layout.elderly_requests_row, parent, false);

        // return the view using the view holder
        return new ElderlyRequestsAdapter.ElderlyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ElderlyViewHolder holder, final int position) {

        switch (_requestsList.get(position).get(1)) {
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

        holder.tvRequestDays.setText(_requestsList.get(position).get(4).replaceAll("(^\\[|\\]$)", ""));
        holder.tvRequestTimes.setText(_requestsList.get(position).get(5).replaceAll("(^\\[|\\]$)", ""));
        holder.etRequestMessage.setText(_requestsList.get(position).get(6));
        holder.tvServiceDescription.setText(_requestsList.get(position).get(8));
        holder.tvServiceDays.setText(_requestsList.get(position).get(9).replaceAll("(^\\[|\\]$)", ""));
        holder.tvServiceTimes.setText(_requestsList.get(position).get(10).replaceAll("(^\\[|\\]$)", ""));
        holder.tvServiceCalls.setText(_requestsList.get(position).get(11).replaceAll("(^\\[|\\]$)", ""));
        holder.tvVolunteerMobile.setText(_requestsList.get(position).get(15));
        holder.tvVolunteerName.setText(String.format("%s, %s", _requestsList.get(position).get(2), _requestsList.get(position).get(16)));

        holder.btnEditRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(_context, ElderlyEditRequestActivity.class);
                intent.putExtra("editRequest", _requestsList.get(position));
                _context.startActivity(intent);
            }
        });

        holder.btnDeleteRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ElderlyCRUDHelper crudHelper = new ElderlyCRUDHelper();
                crudHelper.deleteElderlyServiceRequest((AppCompatActivity)_context, _requestsList.get(position).get(0));
            }
        });

        holder.btnSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(_context, ElderlyHomeActivity.class);
                intent.putExtra("createRequest", _requestsList.get(position));
                _context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return _requestsList.size();
    }

    public class ElderlyViewHolder extends RecyclerView.ViewHolder {

        TextView tvVolunteerName, tvVolunteerMobile, tvServiceType, tvServiceDescription, tvServiceDays, tvServiceTimes, tvServiceCalls, tvRequestDays, tvRequestTimes;
        EditText etRequestMessage;
        CircularImageView volunteerImage;
        Button btnDeleteRequest, btnSendRequest, btnEditRequest;

        public ElderlyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvVolunteerName = itemView.findViewById(R.id.tvRequestVolunteerName);
            tvVolunteerMobile = itemView.findViewById(R.id.tvRequestVolunteerMobile);
            tvServiceType = itemView.findViewById(R.id.tvRequestServiceType);
            tvServiceDescription = itemView.findViewById(R.id.tvRequestServiceDescription);
            tvServiceDays = itemView.findViewById(R.id.tvRequestServiceDays);
            tvServiceTimes = itemView.findViewById(R.id.tvRequestServiceTimes);
            tvServiceCalls = itemView.findViewById(R.id.tvRequestServiceCalls);
            tvRequestDays = itemView.findViewById(R.id.tvRequestDays);
            tvRequestTimes = itemView.findViewById(R.id.tvRequestTimes);

            etRequestMessage = itemView.findViewById(R.id.tvRequestMessage);

            volunteerImage = (CircularImageView) itemView.findViewById(R.id.imgRequestVolunteerPhoto);

            btnDeleteRequest = itemView.findViewById(R.id.btnDeleteRequest);
            btnSendRequest = itemView.findViewById(R.id.btnSendRequest);
            btnEditRequest = itemView.findViewById(R.id.btnEditRequest);
        }
    }
}
