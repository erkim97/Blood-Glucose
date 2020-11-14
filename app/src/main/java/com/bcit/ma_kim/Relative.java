package com.bcit.ma_kim;

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
import android.widget.TextView;

import com.google.firebase.components.Lazy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
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
        loadAnimations();
        initAddButton();
        initStaticContent();
        dbRef = database.getReference(title);
        initFirebaseListener();
        initRecyclerView();
    }

    private void initStaticContent() {
        TextView emailTextView = getView().findViewById(R.id.family_email);
        emailTextView.setText(title + "@home.com");
    }

    private void initFirebaseListener() {

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

                Relative.this.updateFragment(data);
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

    private void updateFragment(ArrayList<BloodPressureReading> data) {

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

    private void initAddButton() {

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