package com.example.firebasedemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

//import com.google.android.gms.common.api.Response;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;



public class PostActivity extends AppCompatActivity {
    private Post post;

    private TextView tv_date;
    private TextView tv_title;
    private TextView tv_content;
    private TextView tv_poem;
    private ImageView iv_image;

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

        TextView tv_title = findViewById(R.id.toolbar_title);
        tv_title.setText(post.title);

        showContent();

//        Button button = (Button)findViewById(R.id.bnt_post_edit);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
//        button = (Button)findViewById(R.id.btn_post_delete);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
    }

    private void showContent() {
        tv_date = findViewById(R.id.post_date);
        tv_content = findViewById(R.id.post_content);
        tv_poem = findViewById(R.id.post_poem);
        iv_image = findViewById(R.id.post_image);

        String str_date = sdf.format(post.date);
        tv_date.setText(str_date);
        tv_content.setText(post.content);
        tv_poem.setText(post.poem);
        Picasso.get().load(post.image).into(iv_image);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent it;
        switch (item.getItemId()) {
            case android.R.id.home: //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;

            case R.id.btn_toolbar_edit:
                it = new Intent(this, EditActivity.class);
                post.addToIntent(it);
                startActivity(it);
                finish();
                return true;

            case R.id.btn_toolbar_delete:
                askDelete();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void askDelete(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("");
        builder.setMessage("정말로 삭제하시겠습니까?");
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Snackbar.make(textView,"예 버튼이 눌렸습니다.",Snackbar.LENGTH_LONG).show();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                db.collection(uid).document(post.id)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                finish();
                            }

                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "일기 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Snackbar.make(textView,"아니오 버튼이 눌렸습니다.",Snackbar.LENGTH_LONG).show();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
