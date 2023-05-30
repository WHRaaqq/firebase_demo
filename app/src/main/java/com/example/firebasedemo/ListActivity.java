package com.example.firebasedemo;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


class Post {
    public String title;
    public String content;

    public Post() {}

    public Post(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

}

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
