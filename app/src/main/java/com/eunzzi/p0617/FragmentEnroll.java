package com.eunzzi.p0617;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentEnroll extends Fragment {

    private Button btn_fg_enroll = null;

    private String id;

    public FragmentEnroll(String id) {
        this.id = id;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enroll, container, false);

        viewSetting(view);

        // 팔찌 등록
        btn_fg_enroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextActivity();
            }
        });

        return view;
    }

    private void viewSetting(View view){
        btn_fg_enroll = view.findViewById(R.id.btn_fg_enroll);
    }

    private void nextActivity(){
        Intent intent = new Intent(getContext(), ActivityBraceletRegist.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

}
