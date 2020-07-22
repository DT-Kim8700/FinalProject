package com.eunzzi.p0617;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

public class FragmentMypage extends Fragment {

    private String brac_name, brac_num, brac_end, brac_sn, brac_gender = null;

    private TextView ward_name, bracelet_number, bracelet_end, ward_sn, ward_gender = null;

    public FragmentMypage(JSONObject jsonObject) throws JSONException {         // 팔찌가 있을 경우
        this.brac_name = jsonObject.getString("brac_name");
        this.brac_num = jsonObject.getString("brac_num");
        this.brac_end = jsonObject.getString("brac_end");
        this.brac_sn = jsonObject.getString("brac_sn");
        this.brac_gender = jsonObject.getString("brac_gender");
    }

    public FragmentMypage() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mypage, container, false);

        if(brac_num != null){
            viewSetting(view);
            viewSetText();
        }

        return view;
    }

    private void viewSetting(View view){
        ward_name = view.findViewById(R.id.ward_name);
        bracelet_number = view.findViewById(R.id.bracelet_number);
        bracelet_end = view.findViewById(R.id.bracelet_end);
        ward_sn = view.findViewById(R.id.ward_sn);
        ward_gender = view.findViewById(R.id.ward_gender);
    }

    private void viewSetText(){
        ward_name.setText(brac_name);
        bracelet_number.setText(brac_num);
        bracelet_end.setText(brac_end.substring(0,4) + " / " + brac_end.substring(4,6) + " / " + brac_end.substring(6));
        ward_sn.setText(brac_sn.substring(0,4) + " / " + brac_sn.substring(4,6) + " / " + brac_sn.substring(6));
        ward_gender.setText(brac_gender);
    }

}
