package com.example.firebasedemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class SettingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.list_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        TextView tv_title = findViewById(R.id.toolbar_title);
        tv_title.setText("Setting");

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
        button = (Button)findViewById(R.id.btn_setAPI);
        button.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                askAPIKey();
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

    @Override
    public void onBackPressed() {
        Intent it = new Intent(this, ListActivity.class);
        startActivity(it);
        finish();
    }

    public void askAPIKey() {
        final EditText txtEdit = new EditText( this);

        AlertDialog.Builder clsBuilder = new AlertDialog.Builder( this );
        clsBuilder.setTitle( "api key" );
        clsBuilder.setView( txtEdit );
        clsBuilder.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick( DialogInterface dialog, int which) {
                        String strText = txtEdit.getText().toString();
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        Map<String, Object> data = new HashMap<>();
                        data.put("key", strText);

                        db.collection(uid).document("APIKEY")
                                .set(data);
                    }
                });
        clsBuilder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        clsBuilder.show();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent it;
        switch (item.getItemId()) {
            case android.R.id.home:
                it = new Intent(this, ListActivity.class);
                startActivity(it);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
