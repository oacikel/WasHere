package com.example.washere.Views.Activities.User_Entrance_Activity;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.washere.R;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

public class UserEntranceActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {
    private static String LOG_TAG = "OCUL - UserEntranceActivity";
    private UserEntranceActivityViewModel userEntranceActivityViewModel;
    private PagerAdapter pagerAdapter;
    private TabLayout tabLayoutLoginOrRegister;
    private TabItem tabItemSignIn, tabItemRegister;
    private ViewPager viewPagerLoginOrRegister;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_entrance);
        initViews();
        initOtherObjects();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    private void initViews() {
        tabLayoutLoginOrRegister = findViewById(R.id.tabLayoutLoginOrRegister);
        tabItemSignIn = findViewById(R.id.tabItemSignIn);
        tabItemRegister = findViewById(R.id.tabItemRegister);
        viewPagerLoginOrRegister = findViewById(R.id.viewPagerLoginOrRegister);
    }

    private void initOtherObjects() {
        userEntranceActivityViewModel = ViewModelProviders.of(this).get(UserEntranceActivityViewModel.class);
        pagerAdapter=new com.example.washere.adapters.PagerAdapter(getSupportFragmentManager(),tabLayoutLoginOrRegister.getTabCount());
        viewPagerLoginOrRegister.setAdapter(pagerAdapter);
        viewPagerLoginOrRegister.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayoutLoginOrRegister));
        tabLayoutLoginOrRegister.setupWithViewPager(viewPagerLoginOrRegister);

    }

    private void setListeners(){
        tabLayoutLoginOrRegister.addOnTabSelectedListener(this);
    }
}
