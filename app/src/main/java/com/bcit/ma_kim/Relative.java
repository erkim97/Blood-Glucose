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
        dbRef = database.getReference(title);
        initFirebaseListener();
    }

    private void initFirebaseListener() {

        dbRef.child("data").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot snapshot) {
                final ArrayList<String> data = new ArrayList<>();
                snapshot.getChildren().forEach(new Consumer<DataSnapshot>() {
                    @Override
                    public void accept(DataSnapshot dataSnapshot) {
                        data.add(dataSnapshot.getValue().toString());
                    }
                });

                Relative.this.updateFragment(data);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateFragment(ArrayList<String> data) {

        int total = 0;
        int count = 0;
        for (String s : data) {
            int reading = Integer.parseInt(s);
            total += reading;
            ++count;
        }
        int average = total / count;

        TextView sys_readings = getView().findViewById(R.id.sys_reading);
        sys_readings.setText(Integer.toString(average));

        TextView dia_readings = getView().findViewById(R.id.dia_reading);
        dia_readings.setText(Integer.toString(average));

        TextView average_condition = getView().findViewById(R.id.average_condition);
        average_condition.setText(Integer.toString(average));


    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private void initAddButton() {

        View add_btn = getView().findViewById(R.id.floating_add_btn);
        add_btn.setOnClickListener(
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

        if (!clicked) {
            edit_btn.startAnimation(fromBottom);
            add_btn.startAnimation(rotateOpen);
        } else {
            edit_btn.startAnimation(toBottom);
            add_btn.startAnimation(rotateClose);

        }
    }
}