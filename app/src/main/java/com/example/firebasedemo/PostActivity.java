package com.example.firebasedemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.annotations.Nullable;

import java.text.SimpleDateFormat;

public class PostActivity extends AppCompatActivity {
    private Post post;

    private TextView tv_date;
    private TextView tv_title;
    private TextView tv_content;

    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.list_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        post = new Post(intent);

        showContent();

        Button button = (Button)findViewById(R.id.btn_post_edit);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(view.getContext(), EditActivity.class);
                post.addToIntent(it);
                startActivity(it);
                //finish();
            }
        });


    }

    private void showContent() {
        tv_date = findViewById(R.id.post_date);
        tv_title = findViewById(R.id.post_title);
        tv_content = findViewById(R.id.post_content);

        String str_date = sdf.format(post.date);
        tv_date.setText(str_date);
        tv_title.setText(post.title);
        tv_content.setText(post.content);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}