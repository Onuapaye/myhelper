package com.martin.myhelper.helpers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.martin.myhelper.views.ElderlyCreateRequestActivity;
import com.martin.myhelper.views.ElderlyEditRequestActivity;
import com.martin.myhelper.views.ElderlyHomeActivity;
import com.martin.myhelper.views.ElderlyMakeRequestActivity;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ElderlyViewVolunteerServicesToMakeRequestAdapter extends RecyclerView.Adapter<ElderlyViewVolunteerServicesToMakeRequestAdapter.ElderlyMakeRequestViewHolder> {

    ArrayList<ArrayList<String>> _volunteersProfiles, _volunteersAccounts;
    Context _context;
    FirebaseFirestore firebaseFirestore;
    ArrayList<String> _tempList2;

    public ElderlyViewVolunteerServicesToMakeRequestAdapter(Context context, ArrayList<ArrayList<String>> volunteersProfiles) {
        this._context = context;
        this._volunteersProfiles = volunteersProfiles;
        this._volunteersAccounts = null;
    }

    @NonNull
    @Override
    public ElderlyMakeRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // create a layout inflater to inflate the view
        LayoutInflater layoutInflater = LayoutInflater.from(_context);

        // set the layout inflater into a view
        View view = layoutInflater.inflate(R.layout.elderly_view_provided_services_row, parent, false);

        // return the view using the view holder
        return new ElderlyViewVolunteerServicesToMakeRequestAdapter.ElderlyMakeRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ElderlyMakeRequestViewHolder holder, final int position) {

        _volunteersAccounts = new ArrayList<>();
        // set the text view values from the data or arrays
        getVolunteerAccountDetails(_volunteersProfiles.get(position).get(2), holder);
        holder.serviceDescription.setText(_volunteersProfiles.get(position).get(3));

        holder.btnContactVolunteer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(_context, ElderlyMakeRequestActivity.class);
                intent.putExtra("profileRecordForRequest", _volunteersProfiles.get(position));
                intent.putExtra("accountRecordForRequest", _volunteersAccounts.get(position));
                _context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return _volunteersProfiles.size();
    }

    public class ElderlyMakeRequestViewHolder extends RecyclerView.ViewHolder {

        // create a variable for the items in the view
        TextView volunteerName, serviceDescription, volunteerMobile;
        CircularImageView volunteerImage;
        Button btnContactVolunteer;

        public ElderlyMakeRequestViewHolder(@NonNull View itemView) {
            super(itemView);

            volunteerName = itemView.findViewById(R.id.viewRequestVolunteerName);
            volunteerMobile = itemView.findViewById(R.id.viewVolunteerMobile);
            serviceDescription = itemView.findViewById(R.id.viewVolunteerServiceDescription);
            volunteerImage = itemView.findViewById(R.id.imgViewVolunteerImage);
            btnContactVolunteer = itemView.findViewById(R.id.btnViewMakeRequest);
        }
    }

    private void getVolunteerAccountDetails(String volunteerId, final ElderlyViewVolunteerServicesToMakeRequestAdapter.ElderlyMakeRequestViewHolder holder) {

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

                        _volunteersAccounts.add(_tempList2);

                        // load and show the profile image
                        loadProfilePhotoIntoImageView(vSnapshot.getString("id"), vSnapshot.getString("imageType"), holder);
                    }
                }
            }
        });
    }

    private void loadProfilePhotoIntoImageView(String imageName, String imageExtension, final ElderlyViewVolunteerServicesToMakeRequestAdapter.ElderlyMakeRequestViewHolder holder) {

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
