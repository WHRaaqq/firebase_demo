package com.example.firebasedemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;

public class SettingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Button button = (Button)findViewById(R.id.btn_logout);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();

                Intent it = new Intent(view.getContext(), LoginActivity.class);
                startActivity(it);
                finish();
            }
        });

        button = (Button)findViewById(R.id.btn_ldmode);
        button.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            }
        });
        button = (Button)findViewById(R.id.btn_lightmode);
        button.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });
        button = (Button)findViewById(R.id.btn_darkmode);
        button.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        });


//        if(isNightModeOn){
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//            switch_btn.text = "Enable Dark Mode"
//        } else {
//
//            switch_btn.text = "Disable Dark Mode"
//        }
    }

}
