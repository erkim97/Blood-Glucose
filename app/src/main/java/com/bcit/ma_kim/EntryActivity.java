package com.bcit.ma_kim;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    private DatabaseReference dbRef;
    private String relative;

    ArrayList<BloodPressureReading> bpReadingsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        relative = getIntent().getStringExtra("relative");
        dbRef = database.getReference(relative).child("data");

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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
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

    //to be implemented
    private void addReading() {
        Spinner spText = (Spinner) findViewById(R.id.spinnerID);
        String spUser = spText.getSelectedItem().toString();

        EditText editText1 = findViewById(R.id.textViewSystolic);
        String systolicReading = editText1.getText().toString();

        EditText editText2 = findViewById(R.id.textViewDiastolic);
        String diastolicReading = editText2.getText().toString();

        if (editText1.getText().toString().trim().isEmpty() || editText2.getText().toString().trim().isEmpty() ||
                Integer.parseInt(editText1.getText().toString()) > 300 ||
                Integer.parseInt(editText2.getText().toString()) > 300 ||
                Integer.parseInt(editText1.getText().toString()) < 30 ||
                Integer.parseInt(editText2.getText().toString()) < 30) {
            Toast.makeText(EntryActivity.this, "INVALID INPUT", Toast.LENGTH_SHORT).show();
        } else {

            //creates new reading from input data
            BloodPressureReading bpReading = new BloodPressureReading(spUser,
                    systolicReading,
                    diastolicReading);

            if (bpReading.condition.equals("HYPERTENSIVE")) {
                AlertDialog.Builder alert = new AlertDialog.Builder(this,R.style.AlertDialogCustom);
                alert.setTitle("Hypertensive Reading Added!");
                alert.setMessage("Please go see a doctor immediately!");
                AlertDialog alertdialog = alert.create();
                alertdialog.show();
            }

            Task setValueTask = dbRef.child(bpReading.id).setValue(bpReading);
            setValueTask.addOnSuccessListener(new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    Toast.makeText(EntryActivity.this,
                            getString(R.string.success),
                            Toast.LENGTH_SHORT).show();
                }
            });

            setValueTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EntryActivity.this,
                            getString(R.string.error) + e.toString(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
