package com.bcit.ma_kim;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.function.Consumer;

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
    private String selectedMonth = "11";
    private String selectedYear = "2020";

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
        if (savedInstanceState != null) {
            this.title = savedInstanceState.getString("relative");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_relative, container, false);
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        loadAnimations();
        initButtons();
        initStaticContent();
        dbRef = database.getReference(title);
        initRecyclerView();
        initMonthSpinner();
        initYearSpinner();
        initRecyclerListener();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("relative", this.title);
        super.onSaveInstanceState(savedInstanceState);
    }

    private void initMonthSpinner() {
        Spinner month_spinner = getView().findViewById(R.id.month_spinner);
        Spinner year_spinner = getView().findViewById(R.id.year_spinner);
        month_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                switch (position) {
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
                updateTable();
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
                updateTable();
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

    private void initRecyclerListener() {

        dbRef.child("data").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot snapshot) {
                final ArrayList<BloodPressureReading> data = new ArrayList<>();
                snapshot.getChildren().forEach(new Consumer<DataSnapshot>() {
                    @Override
                    public void accept(DataSnapshot dataSnapshot) {
                        data.add(dataSnapshot.getValue(BloodPressureReading.class));
                    }
                });

                if (adapter != null) {
                    adapter.setData(data.toArray(new BloodPressureReading[data.size()]));
                    adapter.notifyDataSetChanged();
                }

                updateTable();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private String buildLastDayMonth() {
        StringBuilder lastDayMonthString = new StringBuilder();
        lastDayMonthString.append(selectedYear);
        lastDayMonthString.append("/");
        lastDayMonthString.append(selectedMonth);
        lastDayMonthString.append("/");
        lastDayMonthString.append("31");
        return lastDayMonthString.toString();
    }

    private String buildFirstDayMonth() {
        StringBuilder firstDayMonthString = new StringBuilder();
        firstDayMonthString.append(selectedYear);
        firstDayMonthString.append("/");
        firstDayMonthString.append(selectedMonth);
        firstDayMonthString.append("/");
        firstDayMonthString.append("01");
        return firstDayMonthString.toString();
    }

    private void updateTable() {

        String firstDayMonth = buildFirstDayMonth();
        String lastDayMonth = buildLastDayMonth();
        dbRef.child("data").orderByChild("date").startAt(firstDayMonth).endAt(lastDayMonth).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ArrayList<BloodPressureReading> results = new ArrayList<>();
                if (snapshot.getValue() != null) {
                    snapshot.getChildren().forEach((data) -> {
                        BloodPressureReading bp = data.getValue(BloodPressureReading.class);
                        results.add(bp);
                    });
                }
                updateTableUI(results);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });


    }

    private void updateTableUI(ArrayList<BloodPressureReading> results) {

        if(getView() == null) return;

        int totalS = 0;
        int totalD = 0;
        int count = 0;
        String conditionAverage;

        if (results.size() > 0) {
            for (BloodPressureReading s : results) {
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
        } else {
            TextView sys_readings = getView().findViewById(R.id.sys_reading);
            sys_readings.setText(getResources().getString(R.string.no_data_available));

            TextView dia_readings = getView().findViewById(R.id.dia_reading);
            dia_readings.setText(getResources().getString(R.string.no_data_available));

            TextView average_condition = getView().findViewById(R.id.average_condition);
            average_condition.setText(getResources().getString(R.string.no_data_available));
        }

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
                        setAnimation(clicked);
                        clicked = !clicked;
                    }
                }


        );
        View add_btn = getView().findViewById(R.id.floating_add_btn);

        add_btn.setOnClickListener(
                v -> {
                    Intent i = new Intent(getContext(), EntryActivity.class);
                    i.putExtra("relative", Relative.this.getTitle());
                    getContext().startActivity(i);
                }


        );
    }

    private void loadAnimations() {
        rotateOpen = AnimationUtils.loadAnimation(this.getContext(), R.anim.rotate_open_anim);
        rotateClose = AnimationUtils.loadAnimation(this.getContext(), R.anim.rotate_close_anim);
        fromBottom = AnimationUtils.loadAnimation(this.getContext(), R.anim.from_bottom_anim);
        toBottom = AnimationUtils.loadAnimation(this.getContext(), R.anim.to_bottom_anim);
    }

    private void setAnimation(boolean clicked) {
        View add_btn = getView().findViewById(R.id.floating_add_btn);
        View pop_btn = getView().findViewById(R.id.floating_pop_btn);
        View add_text = getView().findViewById(R.id.floating_add_text);

        if (!clicked) {
            add_btn.startAnimation(fromBottom);
            add_text.startAnimation(fromBottom);
            pop_btn.startAnimation(rotateOpen);
        } else {
            add_btn.startAnimation(toBottom);
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
    }

}