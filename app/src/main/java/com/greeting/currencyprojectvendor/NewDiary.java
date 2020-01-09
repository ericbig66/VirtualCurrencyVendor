package com.greeting.currencyprojectvendor;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.greeting.currencyprojectvendor.ui.main.SectionsPagerAdapter;

public class NewDiary extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_new_diary);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        Toast.makeText(NewDiary.this,"請稍後...",Toast.LENGTH_SHORT).show();
    }
    public void onBackPressed(){
        Intent intent = new Intent(NewDiary.this, MainMenu.class);
        startActivity(intent);
        finish();
    }
}