package com.martin.myhelper.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.martin.myhelper.R;
import com.martin.myhelper.helpers.ElderlyViewProvidedServicesAdapter;

public class ElderlyViewProvidedServiceActivity extends AppCompatActivity {
    String[] s1, s2, s3;

    int images[] = {R.drawable.man, R.drawable.bis_smile, R.drawable.short_shave_1,
            R.drawable.birthday_balloons2, R.drawable.short_shave_2,
            R.drawable.short_shave_3, R.drawable.woman};

    RecyclerView providedServicesRecyclerView;

    String[] serviceTypes;
    Spinner serviceTypeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elderly_view_provided_service);

        showProvidedServicesRecyclerView();
    }

    private void showProvidedServicesRecyclerView(){

        serviceTypeSpinner = findViewById(R.id.availableServiceTypeSpinner);
        serviceTypes = getResources().getStringArray(R.array.serviceTypes);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item,  serviceTypes);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        serviceTypeSpinner.setAdapter(arrayAdapter);

        s1 = getResources().getStringArray(R.array.volunteers);
        s2 = getResources().getStringArray(R.array.descriptions);
        s3 = getResources().getStringArray(R.array.mobileNumbers);

        providedServicesRecyclerView = findViewById(R.id.elderlyViewProvidedServicesRecyclierView);

        ElderlyViewProvidedServicesAdapter viewProvidedServicesAdapter = new ElderlyViewProvidedServicesAdapter(this, s1, s2, s3, images);
        providedServicesRecyclerView.setAdapter(viewProvidedServicesAdapter);
        providedServicesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


}