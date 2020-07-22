package com.eunzzi.p0617;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivityBoardWrite extends AppCompatActivity {

    private TextView write_title, write_content = null;
    private Button write_btn = null;

    private String id, title, content ,name = null;

    private boolean writeCheck, bracelet = false;

    private String url = null;
    private RequestQueue requestQueue = null;      // 서버에 요청을 보내는 역할

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        viewSetting();
        getIntentExtra();

        write_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                write();
            }
        });

    }

    private void viewSetting(){
        write_title = findViewById(R.id.write_title);
        write_content = findViewById(R.id.write_content);
        write_btn = findViewById(R.id.write_btn);
    }

    private void getIntentExtra(){
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        bracelet = intent.getBooleanExtra("bracelet", false);
        name = intent.getStringExtra("name");
    }

    private void write(){
        url = ActivityLogin.url_ip + "Write";

        title = write_title.getText().toString();
        content = write_content.getText().toString();

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(ActivityBoardWrite.this);
        }

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    writeCheck = jsonObject.getBoolean("writeCheck");

                    if(writeCheck){      // 글 등록 성공
                        Toast.makeText(getApplicationContext(), "글이 등록되었습니다.", Toast.LENGTH_SHORT).show();

                        // 액티비티를 종료시키고 전 액티비티로 돌아간다.
                        Intent intent = new Intent(ActivityBoardWrite.this, ActivityMenu.class);
                        intent.putExtra("writeCheck", writeCheck);
                        intent.putExtra("id", id);
                        intent.putExtra("bracelet", bracelet);
                        intent.putExtra("name", name);

                        finish();
                        startActivity(intent);

                    }else{      // 회원가입 실패
                        Toast.makeText(getApplicationContext(), "글 등록에 실패하였습니다. 잠시 후 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        // 서버로 Volley를 이용해서 요청
        RequestBoardWrite requestBoardWrite = new RequestBoardWrite(title, content, id, url, responseListener);
        requestQueue.add(requestBoardWrite);
    }

    public void onBackPressed() {           // 뒤로가기 클릭시 게시판 탭이 선택 된 상태로 돌아간다.
        Intent intent = new Intent(ActivityBoardWrite.this, ActivityMenu.class);
        intent.putExtra("writeCheck", true);
        intent.putExtra("id", id);
        intent.putExtra("bracelet", bracelet);
        intent.putExtra("name", name);
        finish();
        startActivity(intent);
    }


}
