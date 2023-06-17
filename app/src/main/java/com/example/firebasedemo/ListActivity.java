package com.example.firebasedemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;

public class ListActivity extends AppCompatActivity {
    private String uid;
    private FirebaseUser user;

    private RecyclerView recyclerView;
    private LinearLayoutManager llm;
    private RecyclerView.Adapter adapter;

    private DatabaseReference mDatabase;

    private ArrayList<Post> postlist;

    public ListActivity() { }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.list_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Button button = (Button)findViewById(R.id.btn_list_add);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Post emptyPost = new Post();
                Intent it = new Intent(view.getContext(), EditActivity.class);
                emptyPost.addToIntent(it);
                startActivity(it);
            }
        });
        button = (Button)findViewById(R.id.btn_list_setting);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(view.getContext(), SettingActivity.class);
                startActivity(it);
            }
        });

        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        postlist = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);

        adapter = new ListViewAdapter(postlist, uid,this);
        recyclerView.setAdapter(adapter);

//        ArrayList<Post> pl = new ArrayList<>();
//        Post post1 = new Post("title1", "content1", Calendar.getInstance().getTime());
//        pl.add(post1);
//        adapter = new ListViewAdapter(pl, this);

        loadList();
    }

    private void loadList() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("contents").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NotNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            postlist.clear();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Post post = document.toObject(Post.class);
                                post.id = document.getId();

                                postlist.add(post);

                                //postlist.append(post.title).append("\n").append(post.content).append("\n\n");
                                //postlist.append(document.getId()).append("\n");

                                //Log.d(TAG, document.getId() + " => " + document.getData());
                            }

                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}

/*
public class ListActivity extends AppCompatActivity {
    private FirebaseUser user;
    private DatabaseReference mDatabase;


    private TextView tv;
    private EditText ed_title;
    private EditText ed_content;

    public ListActivity() { }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        tv = findViewById(R.id.tv_list);
        ed_title = findViewById(R.id.editText_title);
        ed_content = findViewById(R.id.editText_content);

        loadList();

        findViewById(R.id.btn_post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addList();
            }
        });
    }

    private void loadList() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Toast.makeText(getApplicationContext(), "load list called", Toast.LENGTH_SHORT).show();


        db.collection("contents").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NotNull Task<QuerySnapshot> task) {
                        Toast.makeText(getApplicationContext(), "query complete", Toast.LENGTH_SHORT).show();

                        if (task.isSuccessful()) {
                            StringBuilder postlist = new StringBuilder();

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Post post = document.toObject(Post.class);
                                postlist.append(post.title).append("\n").append(post.content).append("\n\n");
                                //postlist.append(document.getId()).append("\n");

                                //Log.d(TAG, document.getId() + " => " + document.getData());
                            }

                            tv.setText(postlist);
                        } else {
                            Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                            tv.setText(task.getException().toString());
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void addList() {
        Toast.makeText(getApplicationContext(), "add list called", Toast.LENGTH_SHORT).show();


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String title = ed_title.getText().toString();
        String content = ed_content.getText().toString();

        Toast.makeText(getApplicationContext(), title + content, Toast.LENGTH_SHORT).show();


        Post post = new Post(title, content);
        db.collection("contents").add(post);

        loadList();
    }


}
*/