package com.eunzzi.p0617;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivityMenu extends AppCompatActivity {

    private BottomNavigationView bottomNavigation = null;
    private FragmentGps fragment_gps = null;
    private FragmentEnroll fragment_enroll = null;
    private FragmentBoard fragment_board = null;
    private FragmentMypage fragment_mypage = null;

    private String id, name, brac_num = null;

    private boolean bracelet, writeCheck = false;

    private String url = null;
    private RequestQueue requestQueue = null;      // 서버에 요청을 보내는 역할

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        viewSetting();
        getIntentExtra();
        returnMenuPage();

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.tap_gps:
                        gpsPageSet(bracelet);

                        return true;
                    case R.id.tap_news:
                        boardPageSet();

                        return true;
                    case R.id.tap_myPage:
                        myPageSet();

                        return true;
                }

                return false;
            }
        });

    }

    private void viewSetting(){
        bottomNavigation = findViewById(R.id.main_bottom_nav);
    }

    private void getIntentExtra(){
        Intent intent = getIntent();

        id = intent.getStringExtra("id");
        name = intent.getStringExtra("name");
        bracelet = intent.getBooleanExtra("bracelet", false);
        writeCheck = intent.getBooleanExtra("writeCheck", false);
    }

    private void returnMenuPage(){
        if (writeCheck) {
            bottomNavigation.setSelectedItemId(R.id.tap_news);      // 탭의 선택 상태를 바꾼다.
            fragment_board = new FragmentBoard(id, bracelet, name);
            getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, fragment_board).commit();

        } else {
            gpsPageSet(bracelet);
        }
    }

    public void gpsPageSet(boolean bracelet) {
        if (bracelet) {   // 팔찌 등록 ON일 때
            url = ActivityLogin.url_ip + "BraceletGet";

            if (requestQueue == null) {
                requestQueue = Volley.newRequestQueue(getApplicationContext());
            }

            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        brac_num = jsonObject.getString("brac_num");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Intent intent = new Intent(ActivityMenu.this, ServiceAlert.class);
                    intent.putExtra("id", id);
                    intent.putExtra("name", name);
                    intent.putExtra("bracelet", true);
                    intent.putExtra("brac_num", brac_num);
                    startService(intent);

                    fragment_gps = new FragmentGps(brac_num);
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, fragment_gps).commit();

                }
            };

            // 서버로 Volley를 이용해서 요청
            RequestBracelet requestBracelet = new RequestBracelet(id, url, responseListener);
            requestQueue.add(requestBracelet);

        } else {   // 팔찌 등록 OFF 일 때
            fragment_enroll = new FragmentEnroll(id);
            getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, fragment_enroll).commit();
        }
    }

    private void boardPageSet(){
        fragment_board = new FragmentBoard(id, bracelet, name);
        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, fragment_board).commit();
    }

    private void myPageSet(){
        if (bracelet) {
            url = ActivityLogin.url_ip + "BraceletGet";

            if (requestQueue == null) {
                requestQueue = Volley.newRequestQueue(getApplicationContext());
            }

            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        fragment_mypage = new FragmentMypage(jsonObject);
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, fragment_mypage).commit();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            };

            // 서버로 Volley를 이용해서 요청
            RequestBracelet requestBracelet = new RequestBracelet(id, url, responseListener);
            requestQueue.add(requestBracelet);
        } else {
            fragment_mypage = new FragmentMypage();
            getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, fragment_mypage).commit();
        }
    }

    public void onBackPressed() {           // 뒤로가기
        Intent intent = new Intent(ActivityMenu.this, ActivityLogin.class);
        finish();
        startActivity(intent);
    }

}
