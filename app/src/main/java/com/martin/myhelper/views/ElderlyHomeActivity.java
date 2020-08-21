package com.martin.myhelper.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.martin.myhelper.R;
import com.martin.myhelper.helpers.Utility;

import static com.martin.myhelper.helpers.Utility.CREATE_RECORD_SUCCESS_TITLE;
import static com.martin.myhelper.helpers.Utility.CREATE_VOLUNTEER_PROFILE_SUCCESS_MSG;

public class ElderlyHomeActivity extends AppCompatActivity {
    CardView cardViewViewServices, cardViewProvideFeedback;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elderly_home);

        // handle onclick of card view for view volunteer services
        this.handleViewVolunteerServicesCardViewOnClick();
        this.handleViewListOnClick();
        this.handleProvideFeedBackButtonClick();

        this.showFeedBackSuccessMessage();
    }

    private void handleViewVolunteerServicesCardViewOnClick(){

        cardViewViewServices = findViewById(R.id.viewListOfVolunteerCardView);
        cardViewViewServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(ElderlyHomeActivity.this, ElderlyViewVolunteerServicesActivity.class);
                startActivity(intent);
            }
        });
    }

    private void handleViewListOnClick(){
        TextView textView = findViewById(R.id.viewListOfVolunteerServiceTextView);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(ElderlyHomeActivity.this, ElderlyViewVolunteerServicesActivity.class);
                startActivity(intent);
            }
        });
    }

    private void handleProvideFeedBackButtonClick(){

        cardViewProvideFeedback = findViewById(R.id.provideFeedBackCardView);
        cardViewProvideFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(ElderlyHomeActivity.this, ElderlyProvideFeedBackActivity.class);
                startActivity(intent);
            }
        });
    }

    private  void showFeedBackSuccessMessage(){
        Intent intent = getIntent();
        intent.getExtras();

        if (intent.hasExtra("feedBackProvided")){
            // show a message for successful record recreation
            Utility.showInformationDialog("FEEDBACK SUCCESS",
                    intent.getStringExtra("feedBackProvided") , this);
        }
    }
}
