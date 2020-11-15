package com.bcit.ma_kim;


import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/*
class to be implemented, when wanting to edit a reading
 */
public class EditEntryActivity extends AppCompatActivity {

    // Connect to firebase
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    private DatabaseReference myRef = null;
    final Calendar myCalendar = Calendar.getInstance();
    private TextView dateText;
    private BloodPressureReading reading = null;
    private String relative = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        reading = (BloodPressureReading) getIntent().getSerializableExtra("BloodPressure");
        relative = getIntent().getStringExtra("relative");
        myRef = database.getReference(relative).child("data");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_entry);

        initStatic();

    }

    // Updates textview with calendar picker selection
    private void updateDate(TextView textView) {
        String myFormat = "MM/dd/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        textView.setText(sdf.format(myCalendar.getTime()));
    }

    //Uses the firebase id of the reading to replace old one
    public void editReading(View view) {

        EditText editText1 = findViewById(R.id.textViewSystolicReading);
        String systolicReading = editText1.getText().toString();

        EditText editText2 = findViewById(R.id.textViewDiastolicReading);
        String diastolicReading = editText2.getText().toString();

        EditText dateText = findViewById(R.id.readingDate);
        String dateString = dateText.getText().toString();

        if (editText1.getText().toString().trim().isEmpty() || editText2.getText().toString().trim().isEmpty() ||
                Integer.parseInt(editText1.getText().toString()) > 300 ||
                Integer.parseInt(editText2.getText().toString()) > 300 ||
                Integer.parseInt(editText1.getText().toString()) < 30 ||
                Integer.parseInt(editText2.getText().toString()) < 30) {
            Toast.makeText(EditEntryActivity.this, "INVALID INPUT", Toast.LENGTH_SHORT).show();

        } else {

            BloodPressureReading updatedBPReading = (BloodPressureReading) getIntent().getSerializableExtra("BloodPressure");
            modifyReading(updatedBPReading, systolicReading, diastolicReading, dateString);

            if (updatedBPReading.condition.equals("HYPERTENSIVE")) {
                AlertDialog.Builder builder = new MaterialAlertDialogBuilder(this);
                builder.setTitle("Hypertensive Reading Added!");
                builder.setMessage("The reading you entered is in the Hypertensive Crisis Range. Are you sure?");
                builder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Task setValueTask = myRef.child(reading.getId()).setValue(reading);
                                setValueTask.addOnSuccessListener(
                                        new OnSuccessListener() {
                                            @Override
                                            public void onSuccess(Object o) {
                                                Toast.makeText(EditEntryActivity.this,
                                                        getString(R.string.success),
                                                        Toast.LENGTH_SHORT).show();
                                                EditEntryActivity.this.onBackPressed();
                                            }

                                        }
                                );
                                setValueTask.addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(EditEntryActivity.this,
                                                        getString(R.string.error) + e.toString(),
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                dialog.dismiss();

                            }
                        }).setNegativeButton("Cancel", null).setCancelable(false);
                AlertDialog alertdialog = builder.create();
                alertdialog.show();
            } else {
                Task setValueTask = myRef.child(reading.getId()).setValue(reading);
                setValueTask.addOnSuccessListener(
                        new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                                Toast.makeText(EditEntryActivity.this,
                                        getString(R.string.success),
                                        Toast.LENGTH_SHORT).show();
                                EditEntryActivity.this.onBackPressed();
                            }

                        }
                );
                setValueTask.addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EditEntryActivity.this,
                                        getString(R.string.error) + e.toString(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }

        }

    }

    private void initStatic() {

        dateText = findViewById(R.id.readingDate);
        EditText sysText = findViewById(R.id.textViewSystolicReading);
        EditText diaText = findViewById(R.id.textViewDiastolicReading);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDate(dateText);
            }

        };
        dateText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(EditEntryActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        if (reading != null) {
            dateText.setText(reading.getDate());
            sysText.setText(reading.getSystolicReading());
            diaText.setText(reading.getDiastolicReading());

        }
    }

    private void modifyReading(final BloodPressureReading reading, String newSys, String newDia, String newDate) {
        reading.setDate(newDate);
        reading.setDiastolicReading(newDia);
        reading.setSystolicReading(newSys);
        reading.setCondition();
    }

    public void deleteReading(View view) {

        if (reading != null && myRef != null) {

            AlertDialog.Builder builder = new MaterialAlertDialogBuilder(this);
            builder.setTitle("You are deleting a reading!");
            builder.setMessage("Are you sure?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Task setValueTask = myRef.child(reading.getId()).removeValue();
                    setValueTask.addOnSuccessListener(
                            new OnSuccessListener() {
                                @Override
                                public void onSuccess(Object o) {
                                    Toast.makeText(EditEntryActivity.this,
                                            getString(R.string.success),
                                            Toast.LENGTH_LONG).show();
                                    EditEntryActivity.this.onBackPressed();
                                }

                            }
                    );
                    setValueTask.addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(EditEntryActivity.this,
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
    }
}

