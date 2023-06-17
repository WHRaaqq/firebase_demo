package com.example.firebasedemo;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;

public class EditActivity extends AppCompatActivity {
    private Post post;
    private DatabaseReference DB;

    private TextView tv_date;
    private EditText ev_title;
    private EditText ev_content;

    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // get post data
        Intent intent = getIntent();
        post = new Post(intent);

        addSaveButtonListener();

        showContent();
    }

    private void addSaveButtonListener() {
        Button button = (Button)findViewById(R.id.btn_edit_save);
        button.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                DB = FirebaseDatabase.getInstance().getReference();

                if(post.id == null)
                    post.id = DB.push().getKey();

                post.content = ev_content.getText().toString();
                post.title = ev_title.getText().toString();

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("contents")
                        .document(post.id).set(post);

//                Intent it = new Intent(view.getContext(), PostActivity.class);
//                post.addToIntent(it);
//                startActivity(it);
                finish();
            }
        });
    }

    private void showContent() {
        tv_date = findViewById(R.id.edit_date);
        ev_title = findViewById(R.id.edit_title);
        ev_content = findViewById(R.id.edit_content);

        String str_date = sdf.format(post.date);
        tv_date.setText(str_date);
        ev_title.setText(post.title);
        ev_content.setText(post.content);
    }
}
