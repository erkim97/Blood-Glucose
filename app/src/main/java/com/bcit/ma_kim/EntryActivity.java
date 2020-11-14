package com.bcit.ma_kim;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class EntryActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dbRef = database.getReference().child("BPReadings");

    ArrayList<BloodPressureReading> bpReadingsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e) {
        }

        setContentView(R.layout.activity_entry);

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bpReadingsList.clear();

                for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                    BloodPressureReading bpReading = studentSnapshot.getValue(BloodPressureReading.class);
                    bpReadingsList.add(bpReading);
                }

                //Function to be implemented to display the readings
                displayReadings(bpReadingsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        /*
        Placeholder button to link to month-to-date report activity for now
         */
        Button btnGoToMTDReport = findViewById(R.id.btnGoToMTDActivity);
        btnGoToMTDReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MTDReportActivity.class);
                startActivity(intent);
            }
        });

        /*
        Placeholder button to add
         */
        Button addReportBtn = findViewById(R.id.btnAddReport);;
        addReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addReading();
            }
        });

    }

    private void displayReadings(ArrayList<BloodPressureReading> bpReadingsList) {
        for (int i = 0; i < bpReadingsList.size(); i++) {
        }
    }

    //to be implemented
    private void addReading(){
        Spinner spText = (Spinner)findViewById(R.id.spinnerID);
        String spUser = spText.getSelectedItem().toString();

        EditText editText;
        editText = findViewById(R.id.textViewSystolic);
        String systolicReading = editText.getText().toString();
        editText.setText("");

        editText = findViewById(R.id.textViewDiastolic);
        String diastolicReading = editText.getText().toString();
        editText.setText("");


        //creates new reading from input data
        BloodPressureReading bpReading = new BloodPressureReading(spUser,
                systolicReading,
                diastolicReading);

        //*TO BE IMPLEMENTED -> MAKE IT STAY ON SCREEN
        if (bpReading.condition.equals("HYPERTENSIVE")) {
            Toast.makeText(EntryActivity.this,
                    "WARNIRNINGINIIG",
                    Toast.LENGTH_SHORT).show();
        }
        Task setValueTask = dbRef.child(bpReading.id).setValue(bpReading);

        setValueTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EntryActivity.this,
                        getString(R.string.error) + e.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*
     final String id = bpReadingsList.get(i).id;

            // Sets up edit textview button, send the reading id to the other activity
            TextView editReading = getString(R.string.edit));
            editReading.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(view.getContext(), EditEntryActivity.class);
                    intent.putExtra("id", id);
                    startActivity(intent);
                }
            });
     */

}
