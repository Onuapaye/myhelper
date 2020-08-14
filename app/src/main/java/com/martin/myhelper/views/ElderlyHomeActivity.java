package com.martin.myhelper.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.martin.myhelper.R;
import com.martin.myhelper.helpers.OpenActivity;

public class ElderlyHomeActivity extends AppCompatActivity {
    CardView cardViewViewServices, cardViewProvideFeedback;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elderly_home);

        // handle onclick of card view for view volunteer services
        this.handleViewVolunteerServicesCardViewOnClick();
    }

    private void handleViewVolunteerServicesCardViewOnClick(){

        cardViewViewServices = findViewById(R.id.viewListOfVolunteerCardView);
        cardViewViewServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(ElderlyHomeActivity.this, ElderlyViewProvidedServiceActivity.class);
                startActivity(intent);
            }
        });
    }
}
