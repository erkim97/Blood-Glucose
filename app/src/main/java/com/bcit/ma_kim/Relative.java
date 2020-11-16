package com.bcit.ma_kim;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class Relative extends Fragment {

    private String title;
    public static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef;
    private boolean clicked = false;
    private Animation rotateOpen;
    private Animation rotateClose;
    private Animation fromBottom;
    private Animation toBottom;
    private RelativeAdapter adapter;
    private BloodPressureReading reading;
    private String selectedMonth="11";
    private String selectedYear="2020";

    public Relative() {
        // Required empty public constructor
    }

    public Relative(String title) {
        super();
        this.title = title;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_relative, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState != null){
            this.title = savedInstanceState.getString("relative");
        }

        loadAnimations();
        initButtons();
        initStaticContent();
        dbRef = database.getReference(title);
        initFirebaseListener();
        initRecyclerView();
        initMonthSpinner();
        initYearSpinner();
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("relative", this.title);
        super.onSaveInstanceState(savedInstanceState);
    }

    private void updateTable(String month, String year) {

    }
    private void initMonthSpinner() {
        Spinner month_spinner = getView().findViewById(R.id.month_spinner);
        Spinner year_spinner = getView().findViewById(R.id.year_spinner);

        month_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                switch(position){
                    case 0:
                        selectedMonth = "01";
                        break;
                    case 1:
                        selectedMonth = "02";
                        break;
                    case 2:
                        selectedMonth = "03";
                        break;
                    case 3:
                        selectedMonth = "04";
                        break;
                    case 4:
                        selectedMonth = "05";
                        break;
                    case 5:
                        selectedMonth = "06";
                        break;
                    case 6:
                        selectedMonth = "07";
                        break;
                    case 7:
                        selectedMonth = "08";
                        break;
                    case 8:
                        selectedMonth = "09";
                        break;
                    case 9:
                        selectedMonth = "10";
                        break;
                    case 10:
                        selectedMonth = "11";
                        break;
                    case 11:
                        selectedMonth = "12";
                        break;
                    default:
                        selectedMonth = "11";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

    private void initYearSpinner() {
        final Spinner year_spinner = getView().findViewById(R.id.year_spinner);

        year_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedYear = year_spinner.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initStaticContent() {
        TextView emailTextView = getView().findViewById(R.id.family_email);
        emailTextView.setText(title + "@home.com");

        ArrayList<String> years = new ArrayList<String>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = thisYear; i <= 2040; i++) {
            years.add(Integer.toString(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, years);

        Spinner month_spinner = getView().findViewById(R.id.month_spinner);
        Spinner year_spinner = getView().findViewById(R.id.year_spinner);

        int indexOfMonth = Calendar.getInstance().get(Calendar.MONTH);
        month_spinner.setSelection(indexOfMonth);
        year_spinner.setAdapter(adapter);
    }


    private void initFirebaseListener() {

        dbRef.child("data").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull final DataSnapshot snapshot) {
                StringBuilder firstDayMonthString = new StringBuilder();
                firstDayMonthString.append(selectedMonth);
                firstDayMonthString.append("/");
                firstDayMonthString.append("01");
                firstDayMonthString.append("/");
                firstDayMonthString.append(selectedYear);

                StringBuilder lastDayMonthString = new StringBuilder();
                lastDayMonthString.append(selectedMonth);
                lastDayMonthString.append("/");
                lastDayMonthString.append("31");
                lastDayMonthString.append("/");
                lastDayMonthString.append(selectedYear);

                Query filteredValues = dbRef.child("data").child("1605406642192").orderByChild("date").startAt(firstDayMonthString.toString()).endAt(lastDayMonthString.toString());

                final ArrayList<BloodPressureReading> data = new ArrayList<>();

                filteredValues.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot snapshot) {

                        for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                            data.add(postSnapshot.getValue(BloodPressureReading.class));
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("onQueryTextChange: " ,databaseError.getMessage());
                    }
                });

                Relative.this.updateTable(data);
                if (adapter != null) {
                    adapter.setData(data.toArray(new BloodPressureReading[data.size()]));
                    Log.e("NOTIFYING", "DATA CHANGED");
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateTable(ArrayList<BloodPressureReading> data) {

        int totalS = 0;
        int totalD = 0;
        int count = 0;
        String conditionAverage;
        for (BloodPressureReading s : data) {
            int readingD = Integer.parseInt(s.getDiastolicReading());
            int readingS = Integer.parseInt(s.getSystolicReading());

            totalS += readingS;
            totalD += readingD;

            ++count;
        }
        int systolicAverage = totalS / count;
        int diastolicAverage = totalD / count;

        if (systolicAverage > 180 || diastolicAverage > 120) {
            conditionAverage = ConditionTypes.HYPERTENSIVE.toString();
        } else if (systolicAverage >= 140 || diastolicAverage >= 90) {
            conditionAverage = ConditionTypes.STAGE2.toString();
        } else if (systolicAverage >= 130 || diastolicAverage >= 80) {
            conditionAverage = ConditionTypes.STAGE1.toString();
        } else if (systolicAverage >= 120) {
            conditionAverage = ConditionTypes.ELEVATED.toString();
        } else {
            conditionAverage = ConditionTypes.NORMAL.toString();
        }

        TextView sys_readings = getView().findViewById(R.id.sys_reading);
        sys_readings.setText(Integer.toString(systolicAverage));

        TextView dia_readings = getView().findViewById(R.id.dia_reading);
        dia_readings.setText(Integer.toString(diastolicAverage));

        TextView average_condition = getView().findViewById(R.id.average_condition);
        average_condition.setText((conditionAverage));


    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private void initButtons() {

        View pop_btn = getView().findViewById(R.id.floating_pop_btn);
        pop_btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setVisibilityBtns(clicked);
                        setAnimation(clicked);
                        clicked = !clicked;
                    }
                }


        );
        View add_btn = getView().findViewById(R.id.floating_add_btn);

        add_btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getContext(), EntryActivity.class);
                        i.putExtra("relative", Relative.this.getTitle());
                        getContext().startActivity(i);
                    }
                }


        );
    }

    private void loadAnimations() {
        rotateOpen = AnimationUtils.loadAnimation(this.getContext(), R.anim.rotate_open_anim);
        rotateClose = AnimationUtils.loadAnimation(this.getContext(), R.anim.rotate_close_anim);
        fromBottom = AnimationUtils.loadAnimation(this.getContext(), R.anim.from_bottom_anim);
        toBottom = AnimationUtils.loadAnimation(this.getContext(), R.anim.to_bottom_anim);
    }

    private void setVisibilityBtns(boolean clicked) {
        View edit_btn = getView().findViewById(R.id.floating_edit_btn);
        if (!clicked) {
            edit_btn.setVisibility(View.VISIBLE);
        } else {
            edit_btn.setVisibility(View.INVISIBLE);
        }
    }

    private void setAnimation(boolean clicked) {
        View edit_btn = getView().findViewById(R.id.floating_edit_btn);
        View add_btn = getView().findViewById(R.id.floating_add_btn);
        View pop_btn = getView().findViewById(R.id.floating_pop_btn);
        View edit_text = getView().findViewById(R.id.floating_edit_text);
        View add_text = getView().findViewById(R.id.floating_add_text);

        if (!clicked) {
            edit_btn.startAnimation(fromBottom);
            add_btn.startAnimation(fromBottom);
            edit_text.startAnimation(fromBottom);
            add_text.startAnimation(fromBottom);
            pop_btn.startAnimation(rotateOpen);
        } else {
            add_btn.startAnimation(toBottom);
            edit_btn.startAnimation(toBottom);
            edit_text.startAnimation(toBottom);
            add_text.startAnimation(toBottom);
            pop_btn.startAnimation(rotateClose);
        }
    }

    private void initRecyclerView() {
        RecyclerView relativeRecycler = getView().findViewById(R.id.recyclerView);
        RelativeAdapter adapter = new RelativeAdapter(this);
        this.adapter = adapter;
        relativeRecycler.setAdapter(adapter);
        GridLayoutManager lm = new GridLayoutManager(getView().getContext(), 1);
        relativeRecycler.setLayoutManager(lm);
        Log.e("RELATIVE", "COMPLETE INIT RECYCLER");
    }

}