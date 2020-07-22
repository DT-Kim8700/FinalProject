package com.eunzzi.p0617;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivityLogin extends AppCompatActivity {

    public final static String url_ip = "http://192.168.43.151:8043/HelpServer/";
    public static SharedPreferences guardianPoint, wardPoint = null;

    private EditText id_tx, pw_tx = null;
    private Button login_bt, join_bt = null;

    private String id, name, password = null;
    boolean bracelet = false;

    private String url = null;
    private RequestQueue requestQueue = null;      // 서버에 요청을 보내는 역할

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_login);

        viewSetting();

        // 로그인
        login_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        // 회원가입
        join_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextActivity();
            }
        });

    }

    private void viewSetting(){
        id_tx = findViewById(R.id.id_tx);
        pw_tx = findViewById(R.id.pw_tx);
        login_bt = findViewById(R.id.login_bt);
        join_bt = findViewById(R.id.join_bt);

        guardianPoint = getSharedPreferences("guardianPoint", 0);
        wardPoint = getSharedPreferences("wardPoint", 0);
    }

    private void login(){
        url = url_ip + "Login";

        id = id_tx.getText().toString();
        password = pw_tx.getText().toString();

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(ActivityLogin.this);
        }

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    id = jsonObject.getString("id");
                    name = jsonObject.getString("name");
                    bracelet = jsonObject.getString("bracelet").equals("1") ? true : false;

                    // 로그인 성공
                    Toast.makeText(getApplicationContext(), "로그인 되었습니다", Toast.LENGTH_SHORT).show();

                    // 로그인 페이지에서 메인 페이지로 이동
                    Intent intent = new Intent(ActivityLogin.this, ActivityMenu.class);
                    intent.putExtra("id", id);
                    intent.putExtra("name", name);
                    intent.putExtra("bracelet", bracelet);

                    startActivity(intent);
                    finish();

                } catch (JSONException e) {     // 입력한 정보가 맞지 않으면 예외처리가 된다.
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "ID 또는 비밀번호가 맞지 않습니다", Toast.LENGTH_SHORT).show();
                }
            }
        };

        // 서버로 Volley를 이용해서 요청
        RequestLogin requestLogin = new RequestLogin(id, password, url, responseListener);
        requestQueue.add(requestLogin);
    }

    private void nextActivity(){
        Intent intent = new Intent(ActivityLogin.this, ActivityJoin.class);
        startActivity(intent);
        finish();
    }

}
