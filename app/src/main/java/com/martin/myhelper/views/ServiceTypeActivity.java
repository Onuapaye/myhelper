package com.martin.myhelper.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.martin.myhelper.R;
import com.martin.myhelper.helpers.ServiceTypeCRUDHelper;
import com.martin.myhelper.helpers.Utility;
import com.martin.myhelper.model.GenericModel;
import com.martin.myhelper.model.ServiceTypeModel;

import static com.martin.myhelper.helpers.Utility.CREATE_RECORD_EMAIL_SUCCESS_MSG;
import static com.martin.myhelper.helpers.Utility.CREATE_RECORD_SUCCESS_MSG;

public class ServiceTypeActivity extends AppCompatActivity {

    private Button createServiceTypeButton;
    private EditText serviceTypeName, serviceTypeDescription;
    private ServiceTypeCRUDHelper serviceTypeCRUDHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_type);

        // monitor OnClick event of the button
        this.triggerCreateServiceTypeButtonOnClick();
    }

    private void triggerCreateServiceTypeButtonOnClick() {

        createServiceTypeButton = findViewById(R.id.createServiceTypeButton);
        serviceTypeName = findViewById(R.id.serviceTypeName);
        serviceTypeDescription = findViewById(R.id.serviceTypeDescription);

        boolean isValidationSuccessful = Utility.validateInputsOnCreateServiceType(ServiceTypeActivity.this, serviceTypeName, serviceTypeDescription);
        if (!isValidationSuccessful){
            return;
        } else {

            ServiceTypeModel serviceTypeModel = new ServiceTypeModel(serviceTypeName.getText().toString(), serviceTypeDescription.getText().toString());

            serviceTypeCRUDHelper = new ServiceTypeCRUDHelper();
            serviceTypeCRUDHelper.createServiceTypeRecord(ServiceTypeActivity.this, serviceTypeModel );

            // redirect to login activity
            Intent intent = new Intent(ServiceTypeActivity.this, ServiceTypeHomeActivity.class);
            intent.putExtra("recordCreated", CREATE_RECORD_SUCCESS_MSG );
            startActivity(intent);
        }
    }

}