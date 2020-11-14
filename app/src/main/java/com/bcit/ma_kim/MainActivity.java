package com.bcit.ma_kim;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private final List<Fragment> mFragmentList = new ArrayList<>();



    public class SectionsPageAdapter extends FragmentPagerAdapter {
        public SectionsPageAdapter(FragmentManager fm) { super(fm); }

        @Override
        public int getCount() { return mFragmentList.size(); }


        public void addFragment(Relative fragment) {
            mFragmentList.add(fragment);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            Relative relative = (Relative) mFragmentList.get(position);
            return relative.getTitle();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabLayout tabLayout = findViewById(R.id.tableLayout);
        ViewPager pager = findViewById(R.id.pager);
        tabLayout.setupWithViewPager(pager);

        SectionsPageAdapter pagerAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        String[] names = getResources().getStringArray(R.array.familyMember);
        for(String name: names){
            int indexOfFirstDot = name.indexOf('@'); // Find the first occurrence of @
            name = name.substring(0,indexOfFirstDot); // Create a new string so you just get the name of the person ABCD@home.com -> ABCD
            pagerAdapter.addFragment(new Relative(name)); // Create the Relative Fragment
        }
        pager.setAdapter(pagerAdapter);
    }

    public void addReading(View view) {
        Intent intent = new Intent(view.getContext(), EntryActivity.class);
        startActivity(intent);
    }
}