package com.bcit.ma_kim;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.text.InputFilter;
import android.text.InputType;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.internal.SafeIterableMap;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    private TextView timeText;
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
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        textView.setText(getString(R.string.reading_date) + " " + sdf.format(myCalendar.getTime()));
    }

    //Uses the firebase id of the reading to replace old one
    public void editReading(View view){

        EditText editText1 = findViewById(R.id.textViewSystolicReading);
        String systolicReading = editText1.getText().toString();

        EditText editText2 = findViewById(R.id.textViewDiastolicReading);
        String diastolicReading = editText2.getText().toString();

        if(editText1.getText().toString().trim().isEmpty() || editText2.getText().toString().trim().isEmpty() ||
                Integer.parseInt(editText1.getText().toString()) > 300 ||
                Integer.parseInt(editText2.getText().toString()) > 300 ||
                Integer.parseInt(editText1.getText().toString()) < 30 ||
                Integer.parseInt(editText2.getText().toString()) < 30)
        {
         Toast.makeText(EditEntryActivity.this, "INVALID INPUT", Toast.LENGTH_SHORT).show();
        } else {

            BloodPressureReading updatedBPReading = (BloodPressureReading) getIntent().getSerializableExtra("BloodPressure");
            modifyReading(updatedBPReading, systolicReading, diastolicReading, null); //TODO
            Task setValueTask = myRef.child(updatedBPReading.getId()).setValue(updatedBPReading);

            setValueTask.addOnSuccessListener(new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    Toast.makeText(EditEntryActivity.this,
                            getString(R.string.successAdd),
                            Toast.LENGTH_SHORT).show();
                }
            });

            setValueTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditEntryActivity.this,
                            getString(R.string.error) + e.toString(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void initStatic(){

        Date d = new Date();
        CharSequence defaultDate = DateFormat.format("MM/dd/yy", d.getTime());
        dateText = findViewById(R.id.readingDate);
        dateText.setText(getString(R.string.reading_date) + " " + defaultDate);

        CharSequence defaultTime = DateFormat.format("hh:mm", d.getTime());
        timeText = findViewById(R.id.readingTime);
        timeText.setText(getString(R.string.reading_time) + " " + defaultTime);

        dateText = findViewById(R.id.readingDate);

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

        // TimePicker
        //Text to fill time with
        timeText = findViewById(R.id.readingTime);
        timeText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Calendar mCurrentTime = Calendar.getInstance();
                int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mCurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(EditEntryActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                timeText.setText(selectedHour + ":" + selectedMinute);
                            }
                        }, hour, minute, true);
                mTimePicker.show();

            }
        });
    }

    private void modifyReading(BloodPressureReading reading, String newSys, String newDia, String newDate){
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        newSys = "9999";
        newDia = "-9999";
        newDate = sdf.format(myCalendar.getTime());

        reading.setDate(newDate);
        reading.setDiastolicReading(newDia);
        reading.setSystolicReading(newSys);
    }
}

