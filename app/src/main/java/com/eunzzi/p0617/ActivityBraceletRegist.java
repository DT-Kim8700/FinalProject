package com.eunzzi.p0617;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

// 팔찌 등록 양식 액티비티
public class ActivityBraceletRegist extends AppCompatActivity {

    private EditText regist_num, ward_name, ward_sn = null;
    private Button regist_btn = null;
    private RadioGroup check_gender = null;

    private String id, brac_num, brac_name, brac_sn, brac_gender = null;
    private boolean bracelet = false;

    private String url = null;
    private RequestQueue requestQueue = null;    // 서버에 요청을 보내는 역할

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bracelet_regist);

        viewSetting();
        getIntentExtra();

        check_gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                setGender(checkedId);
            }
        });

        // 팔찌 등록 번호를 적는다.
        // 등록 버튼을 부른다.
        regist_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                braceletInsert();

            }
        });

    }

    private void viewSetting(){
        regist_num = findViewById(R.id.regist_num);
        ward_name = findViewById(R.id.ward_name);
        ward_sn = findViewById(R.id.ward_sn);
        check_gender = findViewById(R.id.check_gender);
        regist_btn = findViewById(R.id.regist_btn);
    }

    private void getIntentExtra(){
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
    }

    private void setGender(int checkedId){
        switch (checkedId){
            case R.id.check_male:
                brac_gender = "남";
                break;
            case R.id.check_female:
                brac_gender = "여";
                break;
        }
    }

    private void braceletInsert(){
        url = ActivityLogin.url_ip + "Regist";

        brac_num = regist_num.getText().toString();
        brac_name = ward_name.getText().toString();
        brac_sn = ward_sn.getText().toString();

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(ActivityBraceletRegist.this);
        }

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    bracelet = jsonObject.getBoolean("registCheck");

                    if (bracelet) {         // 팔찌 등록 성공
                        Toast.makeText(getApplicationContext(), "등록되었습니다", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(ActivityBraceletRegist.this, ActivityMenu.class);
                        intent.putExtra("id", id);
                        intent.putExtra("bracelet", bracelet);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "팔찌 등록에 실패하였습니다. 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        };

        // 서버로 Volley를 이용해서 요청
        RequestBracelet requestBracelet = new RequestBracelet(id, brac_num, brac_name, brac_sn, brac_gender, url, responseListener);
        requestQueue.add(requestBracelet);
    }
}