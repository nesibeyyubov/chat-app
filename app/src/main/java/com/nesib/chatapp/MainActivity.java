package com.nesib.chatapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private ArrayList<Fragment> fragmentList;
    private ArrayList<String> fragmentTitleList;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        fragmentList = new ArrayList<>();
        fragmentTitleList = new ArrayList<>();
        fragmentList.add(new ChatsFragment(this));
        fragmentList.add(new UsersFragment(this));
        fragmentList.add(new ProfileFragment(this));

        fragmentTitleList.add("CHATS");fragmentTitleList.add("USERS");fragmentTitleList.add("PROFILE");

        pagerAdapter = new PagerAdapter(getSupportFragmentManager(),fragmentList,fragmentTitleList);
        viewPager.setAdapter(pagerAdapter);

        tabLayout.setupWithViewPager(viewPager);

    }


}