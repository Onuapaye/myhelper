package com.martin.myhelper.views;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.ivbaranov.mli.MaterialLetterIcon;
import com.martin.myhelper.MainActivity;
import com.martin.myhelper.R;
import com.martin.myhelper.helpers.ElderlyVolunteerCRUDHelper;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

public class VolunteerNotifyElderlyForServiceActivity extends AppCompatActivity {
    private Button btnConfirmCreateProfile;
    private TextView tvSelectedItems;
    private String[] availableDays;
    private boolean[] checkedItemBoxes;
    private ArrayList<Integer> userSelectedItems = new ArrayList<>();

    private AlertDialog.Builder mBuilder;
    private AlertDialog alertDialog;

    private MaterialLetterIcon materialLetterIcon;
    private int[] mMaterialColors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_notify_elderly_for_service);

        selectDaysForServiceProvision();
        this.setMaterialLetterIcon();

        this.handleSignOutOnClick();
    }

    private void handleSignOutOnClick(){
        TextView signOut = findViewById(R.id.btnLogOut);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ElderlyVolunteerCRUDHelper.signOutUser(VolunteerNotifyElderlyForServiceActivity.this, MainActivity.class);
            }
        });
    }
    private void selectDaysForServiceProvision(){

        btnConfirmCreateProfile = (Button) findViewById(R.id.btnCreateProfileConfirm);
        //tvSelectedItems = (TextView) findViewById(R.id.tvTestBismark);

        availableDays = getResources().getStringArray(R.array.availableDays);
        checkedItemBoxes = new boolean[availableDays.length];

        btnConfirmCreateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            mBuilder = new AlertDialog.Builder(VolunteerNotifyElderlyForServiceActivity.this);
            mBuilder.setTitle("Available days");
            mBuilder.setMultiChoiceItems(availableDays, checkedItemBoxes, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int position, boolean isItemChecked) {

                    if (isItemChecked) {
                        userSelectedItems.add(position);
                    } else {
                        userSelectedItems.remove(Integer.valueOf(position));
                    }
                }
            });

            mBuilder.setCancelable(false);
            mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String item = "";
                    for (int j = 0; j < userSelectedItems.size(); j++){
                        item += availableDays[userSelectedItems.get(j)];

                        // check if the item selected by the user is not the last
                        // item and append a comma
                        if (j != userSelectedItems.size() - 1){
                            item += ", ";
                        }

                        // set the items checked to the textView
                        tvSelectedItems.setText(item);
                    }
                }
            });

            mBuilder.setNegativeButton("CLOSE", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });

            mBuilder.setNeutralButton("Clear", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                for (int k = 0; k < checkedItemBoxes.length; k++){
                    checkedItemBoxes[k] = false;
                    userSelectedItems.clear();

                    tvSelectedItems.setText("");
                }
                }
            });

            alertDialog = mBuilder.create();
            alertDialog.show();
            }

        });
    }

    private void setMaterialLetterIcon(){
        TextView textView = findViewById(R.id.availableServicesTitle);
        mMaterialColors = getResources().getIntArray(R.array.materialColors);

        //materialLetterIcon = (MaterialLetterIcon) findViewById(R.id.volunteerElderlyDetailsLetterIcon);
        materialLetterIcon.setInitials(true);
        materialLetterIcon.setInitialsNumber(2);
        materialLetterIcon.setLetterSize(25);
        materialLetterIcon.setShapeColor(mMaterialColors[new Random().nextInt(mMaterialColors.length)]);
        materialLetterIcon.setLetter(textView.getText().toString());
    }


}