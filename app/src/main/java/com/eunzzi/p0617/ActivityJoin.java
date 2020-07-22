package com.eunzzi.p0617;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;


import org.json.JSONException;
import org.json.JSONObject;

public class ActivityJoin extends AppCompatActivity {

    private EditText join_id_input, join_pw_input, join_pwCheck_input, join_name_input, join_snFront_input, join_snBack_input = null;
    private Button join_submit_btn = null;

    private String id, password, passwordCheck, name, socialNumber = null;

    private boolean joinCheck = false;

    private String url = null;
    private RequestQueue requestQueue = null;     // 서버에 요청을 보내는 역할

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        viewSetting();

        // 정보를 입력받는다. 버튼을 누르면 이벤트 발동
        join_submit_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                memberJoin();
            }
        });
    }

    public void onBackPressed() {           // 뒤로가기
        Intent intent = new Intent(ActivityJoin.this, ActivityLogin.class);
        finish();
        startActivity(intent);
    }

    private void viewSetting(){
        // 뷰 구성
        join_id_input = findViewById(R.id.join_id_input);       // 아이디
        join_pw_input = findViewById(R.id.join_pw_input);       // 비밀번호
        join_pwCheck_input = findViewById(R.id.join_pwCheck_input);       //비밀번호 확인
        join_name_input = findViewById(R.id.join_name_input);       // 이름
        join_snFront_input = findViewById(R.id.join_snFront_input);       // 주민번호 앞번호
        join_snBack_input = findViewById(R.id.join_snBack_input);       // 주민번호 뒷번호
        join_submit_btn = findViewById(R.id.join_submit_btn);       // submit
    }

    private void memberJoin(){
        url = ActivityLogin.url_ip + "Join";

        id = join_id_input.getText().toString();
        password = join_pw_input.getText().toString();
        passwordCheck = join_pwCheck_input.getText().toString();
        name = join_name_input.getText().toString();
        socialNumber = join_snFront_input.getText().toString() + join_snBack_input.getText().toString();

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(ActivityJoin.this);
        }

        if(password.equals(passwordCheck)){
            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        joinCheck = jsonObject.getBoolean("joinCheck");

                        if(joinCheck){      // 회원 가입 성공
                            Toast.makeText(getApplicationContext(), "가입되었습니다", Toast.LENGTH_SHORT).show();

                            // 회원가입 페이지에서 로그인 페이지로 이동
                            Intent intent = new Intent(ActivityJoin.this, ActivityLogin.class);
                            startActivity(intent);
                        }else{      // 회원가입 실패
                            Toast.makeText(getApplicationContext(), "입력하신 ID가 이미 존재합니다", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

            // 서버로 Volley를 이용해서 요청
            RequestJoin requestJoin = new RequestJoin(id, password, name, socialNumber, url, responseListener);
            requestQueue.add(requestJoin);
        }else{
            Toast.makeText(getApplicationContext(), "비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show();
        }
    }

}
