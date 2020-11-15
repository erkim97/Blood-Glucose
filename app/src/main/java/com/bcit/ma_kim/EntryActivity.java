package com.bcit.ma_kim;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class EntryActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef;
    private String relative;

    ArrayList<BloodPressureReading> bpReadingsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        relative = getIntent().getStringExtra("relative");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        initStatic();

    }

    private void addReading() {


        Spinner spText = (Spinner) findViewById(R.id.spinnerID);
        String spUser = getName(spText.getSelectedItem().toString());
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


            final BloodPressureReading bpReading = new BloodPressureReading(spUser,
                    systolicReading,
                    diastolicReading);

            dbRef = database.getReference(bpReading.getSpUser()).child("data");

            if (bpReading.condition.equals("HYPERTENSIVE")) {
                AlertDialog.Builder builder = new MaterialAlertDialogBuilder(this);
                builder.setTitle("Hypertensive Reading Added!");
                builder.setMessage("The reading you entered is in the Hypertensive Crisis Range. Are you sure?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Task setValueTask = dbRef.child(bpReading.id).setValue(bpReading);
                        setValueTask.addOnSuccessListener(new OnSuccessListener() {
                                                              @Override
                                                              public void onSuccess(Object o) {
                                                                  Toast.makeText(EntryActivity.this,
                                                                          getString(R.string.success),
                                                                          Toast.LENGTH_SHORT).show();
                                                              }

                                                          }
                        );
                        setValueTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EntryActivity.this,
                                        getString(R.string.error) + e.toString(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                        dialog.dismiss();

                    }
                }).setNegativeButton("Cancel", null).setCancelable(false);
                AlertDialog alertdialog = builder.create();
                alertdialog.show();
            }
            else{
                Task setValueTask = dbRef.child(bpReading.id).setValue(bpReading);
                setValueTask.addOnSuccessListener(new OnSuccessListener() {
                                                      @Override
                                                      public void onSuccess(Object o) {
                                                          Toast.makeText(EntryActivity.this,
                                                                  getString(R.string.success),
                                                                  Toast.LENGTH_SHORT).show();
                                                      }

                                                  }
                );
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

    private void initStatic(){
        try {
            this.getSupportActionBar().hide();
        } catch (NullPointerException e) {
        }


        /*
        Placeholder button to add
         */
        Button addReportBtn = findViewById(R.id.btnAddReport);
        ;
        addReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addReading();
            }
        });

        Spinner spText = (Spinner) findViewById(R.id.spinnerID);
        switch(relative) {
            case "father":{
                spText.setSelection(0);
                break;
            }
            case "mother":{
                spText.setSelection(1);
                break;
            }
            case "grandma":{
                spText.setSelection(2);
                break;
            }
            case "grandpa":{
                spText.setSelection(3);
                break;
            } default:{
                spText.setSelection(0);
            }
        }
    }

    private String getName(String relative){
        int indexOfFirstDot = relative.indexOf('@'); // Find the first occurrence of @
        relative = relative.substring(0,indexOfFirstDot); // Create a new string so you just get the name of the person ABCD@home.com -> ABCD
        return relative;
    }
}
