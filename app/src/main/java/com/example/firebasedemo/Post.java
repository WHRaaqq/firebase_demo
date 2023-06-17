package com.example.firebasedemo;

import android.content.Intent;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Calendar;
import java.util.Date;

public class Post {
    public String id;
    public String title;
    public String content;
    public Date date;

    public Post() {
        this.date = Calendar.getInstance().getTime();
    }

    public Post(String title, String content, Date date) {
        this.title = title;
        this.content = content;
        this.date = date;
    }

    public Post(Intent intent) {
        this.id = intent.getStringExtra("post_id");
        this.title = intent.getStringExtra("post_title");
        this.content = intent.getStringExtra("post_content");
        this.date = new Date();
        this.date.setTime(intent.getLongExtra("post_date", -1));
    }

    public void addToIntent(Intent intent) {
        intent.putExtra("post_id", this.id);
        intent.putExtra("post_content", this.content);
        intent.putExtra("post_title", this.title);
        intent.putExtra("post_date", date.getTime());
    }

    //public String getId() {return id;}

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Date getDate() {return date;}


}