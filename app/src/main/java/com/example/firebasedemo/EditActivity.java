package com.example.firebasedemo;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import com.squareup.picasso.Picasso;


public class EditActivity extends AppCompatActivity {
    private Post post;
    private DatabaseReference DB;

    private String uid;
    String APIkey;

    private TextView tv_date;
    private EditText ev_title;
    private EditText ev_content;

    private TextView tv_poem;
    private ImageView iv_image;


    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.edit_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        TextView tv_title = findViewById(R.id.toolbar_title);
        tv_title.setText("Edit Diary");

        // get post data
        Intent intent = getIntent();
        post = new Post(intent);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(uid).document("APIKEY").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        APIkey = (String) document.getData().get("key");
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        showContent();

        //addSaveButtonListener();
        Button button = (Button)findViewById(R.id.btn_ai1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AiGeneratePoem(post.content);
            }

        });
        button = (Button)findViewById(R.id.btn_ai2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AiGenerateImage(post.content);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent it;
        switch (item.getItemId()) {
            case android.R.id.home: //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;

            case R.id.btn_toolbar_save:
                DB = FirebaseDatabase.getInstance().getReference();

                if(post.id == null)
                    post.id = DB.push().getKey();

                post.content = ev_content.getText().toString();
                post.title = ev_title.getText().toString();

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection(uid)
                        .document(post.id).set(post);

                it = new Intent(this, PostActivity.class);
                post.addToIntent(it);
                startActivity(it);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


//    private void addSaveButtonListener() {
//        Button button = (Button)findViewById(R.id.btn_edit_save);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override public void onClick(View view) {
//
//            }
//        });
//    }

    private void showContent() {
        ev_title = findViewById(R.id.edit_title);
        ev_content = findViewById(R.id.edit_content);
        tv_poem = findViewById(R.id.edit_poem);
        iv_image = findViewById(R.id.edit_image);

        ev_title.setText(post.title);
        ev_content.setText(post.content);
        tv_poem.setText(post.poem);
    }

    private void AiGeneratePoem(String content){
        post.content = ev_content.getText().toString();

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model","text-davinci-003");
            jsonBody.put("prompt","다음의 일기를 시로 만들어 줘. 내용은 다음과 같아.\n"+content);
            jsonBody.put("max_tokens",2000);
            jsonBody.put("temperature",0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(jsonBody.toString(),JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/completions")
                .header("Authorization","Bearer "+APIkey)
                .post(body)
                .build();

        tv_poem.setText("생성 중...");

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                tv_poem.setText("생성에 실패했습니다. 다시 시도해주세요");
                Log.d("failed",e.getMessage());
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                //  Toast.makeText(getApplicationContext(), "responed!", Toast.LENGTH_SHORT).show();

                if(response.isSuccessful()){
                    JSONObject  jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        String poem = jsonArray.getJSONObject(0).getString("text");
                        addPoem(poem.trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    JSONObject  jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.body().string());
                        //JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        String result = jsonObject.toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //addResponse("Failed to load response due to "+response.body().toString());
                    Log.d("case1?",response.body().toString());
                    tv_poem.setText("생성에 실패했습니다. 다시 시도해주세요");

                }
            }
        });
    }

    private void addPoem(String poem) {

        //poem = poem
        post.poem = poem.replace(". ", ".\n");

        //tv_poem.setText(poem);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_poem.setText(post.poem);
            }
        });
    }


    private void AiGenerateImage(String content){
        post.content = ev_content.getText().toString();

        JSONObject jsonBody = new JSONObject();
        try{
            jsonBody.put("prompt",content);
            jsonBody.put("size","512x512");
        }catch (Exception e){
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(jsonBody.toString(),JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/images/generations")
                .header("Authorization","Bearer "+APIkey)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Toast.makeText(getApplicationContext(),"Failed to generate image",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                try{
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String imageUrl = jsonObject.getJSONArray("data").getJSONObject(0).getString("url");
                    post.image = imageUrl;
                    loadImage(imageUrl);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    void loadImage(String url){
        //load image

        runOnUiThread(()->{
            Picasso.get().load(url).into(iv_image);
        });
    }
}