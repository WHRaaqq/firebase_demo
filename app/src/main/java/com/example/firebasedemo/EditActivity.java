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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


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

import org.checkerframework.checker.nullness.qual.NonNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class EditActivity extends AppCompatActivity {
    private Post post;
    private DatabaseReference DB;

    private String uid;

    private TextView tv_date;
    private EditText ev_title;
    private EditText ev_content;

    private TextView tv_poem;


    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // get post data
        Intent intent = getIntent();
        post = new Post(intent);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        showContent();

        addSaveButtonListener();
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

            }
        });

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
                db.collection(uid)
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
        tv_poem = findViewById(R.id.edit_poem);

        String str_date = sdf.format(post.date);
        tv_date.setText(str_date);
        ev_title.setText(post.title);
        ev_content.setText(post.content);
        tv_poem.setText(post.poem);
    }

    private void AiGeneratePoem(String content){
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("model","text-davinci-003");
            jsonBody.put("prompt","다음의 일기를 시로 만들어 줘 내용은 다음과 같아.\n"+content);
            jsonBody.put("max_tokens",100);
            jsonBody.put("temperature",0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(jsonBody.toString(),JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/completions")
                .header("Authorization","Bearer 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")
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
                Toast.makeText(getApplicationContext(), "responed!", Toast.LENGTH_SHORT).show();

                if(response.isSuccessful()){
                    JSONObject  jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        String poem = jsonArray.getJSONObject(0).getString("text");
                        post.poem = poem;
                        tv_poem.setText(poem);
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
                    Log.d("What?",response.body().toString());
                    tv_poem.setText("생성에 실패했습니다. 다시 시도해주세요");

                }
            }
        });
    }
}
