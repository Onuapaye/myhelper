package com.martin.myhelper.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.martin.myhelper.R;
import com.mikhaellopez.circularimageview.CircularImageView;

public class ElderlyViewProvidedServicesAdapter extends RecyclerView.Adapter<ElderlyViewProvidedServicesAdapter.ElderlyViewProvidedServicesViewHolder> {

    String[] data1, data2, data3;
    int _images[];
    Context _context;

    public ElderlyViewProvidedServicesAdapter(Context context, String[] s1, String[] s2, String[] s3, int[] images){
        _context = context;
        data1 = s1;
        data2 = s2;
        data3 = s3;
        _images = images;
    }

    @NonNull
    @Override
    public ElderlyViewProvidedServicesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // create a layout inflater to inflate the view
        LayoutInflater layoutInflater = LayoutInflater.from(_context);

        // set the layout inflater into a view
        View view = layoutInflater.inflate(R.layout.elderly_view_provided_services_row, parent, false);

        // return the view using the view holder
        return new ElderlyViewProvidedServicesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ElderlyViewProvidedServicesViewHolder holder, int position) {

        // set the text view values from the data or arrays
        holder.volunteerImage.setImageResource(_images[position]);
        holder.volunteerName.setText(data1[position]);
        holder.volunteerMobile.setText(data3[position]);
        holder.serviceDescription.setText(data2[position]);
    }

    @Override
    public int getItemCount() {
        return data1.length;
    }

    public class ElderlyViewProvidedServicesViewHolder extends RecyclerView.ViewHolder {

        // create a variable for the items in the view
        TextView volunteerName, serviceDescription, volunteerMobile;
        CircularImageView volunteerImage;

        public ElderlyViewProvidedServicesViewHolder(@NonNull View itemView) {
            super(itemView);

            volunteerName = itemView.findViewById(R.id.volunteerName);
            volunteerMobile = itemView.findViewById(R.id.volunteerMobile);
            serviceDescription = itemView.findViewById(R.id.serviceDescription);
            volunteerImage = (CircularImageView) itemView.findViewById(R.id.volunteerImage);
        }
    }
}
