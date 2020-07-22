package com.eunzzi.p0617;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ActivityBoardRead extends AppCompatActivity {

    private TextView read_title, read_name, read_content = null;

    private String title, name, content = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        viewSetting();
        getIntentExtra();
        viewSetText();
    }

    private void viewSetting(){
        read_title = findViewById(R.id.read_title);
        read_name = findViewById(R.id.read_name);
        read_content = findViewById(R.id.read_content);
    }

    private void getIntentExtra(){
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        name = intent.getStringExtra("name");
        content = intent.getStringExtra("content");
    }

    private void viewSetText(){
        read_title.setText(title);
        read_name.setText(name);
        read_content.setText(content);
    }
}
