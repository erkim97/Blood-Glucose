package com.bcit.ma_kim;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MTDReportActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference().child("BPReadings");

    ArrayList<BloodPressureReadingActivity> bpReadingsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mtd_report);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    BloodPressureReadingActivity bpReading = snapshot.getValue(BloodPressureReadingActivity.class);
                    bpReadingsList.add(bpReading);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /*
        Placeholder "button" to generate report
         */
        Button genReportBtn = findViewById(R.id.btnGenReport);
        genReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateReport(view);
            }
        });
    }

    public void generateReport(View view){
        Spinner chosenSpinnerUser = (Spinner)findViewById(R.id.spUserReport);
        String spinnerUser = chosenSpinnerUser.getSelectedItem().toString();

        //used for calculating averages
        int systolicTotal = 0;
        int diastolicTotal = 0;
        int counter = 0;
        //calculated averages
        double systolicAverage;
        double diastolicAverage;
        String conditionAverage;

        for(BloodPressureReadingActivity bpReading: bpReadingsList){
            if(bpReading.spUser.equals(spinnerUser)){
                systolicTotal = systolicTotal + Integer.parseInt(bpReading.systolicReading);
                diastolicTotal = diastolicTotal + Integer.parseInt(bpReading.diastolicReading);
                counter++;
            }
        }

        //Assign the calculated average
        systolicAverage = systolicTotal/counter;
        diastolicAverage = diastolicTotal/counter;

        if(systolicAverage > 180 || diastolicAverage > 120){
            conditionAverage = ConditionTypes.HYPERTENSIVE.toString();
        } else if(systolicAverage >= 140 || diastolicAverage >= 90){
            conditionAverage = ConditionTypes.STAGE2.toString();
        } else if(systolicAverage >= 130 || diastolicAverage >= 80){
            conditionAverage = ConditionTypes.STAGE1.toString();
        } else if(systolicAverage >= 120){
            conditionAverage = ConditionTypes.ELEVATED.toString();
        } else {
            conditionAverage = ConditionTypes.NORMAL.toString();
        }

        /*
        Placeholder textviews to output these results
         */
        TextView tvSystolicAvg = findViewById(R.id.textViewSystolicAve);
        TextView tvDiastolicAvg = findViewById(R.id.textViewDiastolicAve);
        TextView tvConditionAvg = findViewById(R.id.textViewConditionAve);

        tvSystolicAvg.setText(Double.toString(systolicAverage));
        tvDiastolicAvg.setText(Double.toString(diastolicAverage));
        tvConditionAvg.setText(conditionAverage);

    }
}
