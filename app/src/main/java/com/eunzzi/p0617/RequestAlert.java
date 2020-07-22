package com.eunzzi.p0617;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

// 피보호자의 신호가 끊기거나 거리가 멀어졌을 때 보호자에게 알리기 위함.
// 서비스에서 요청 작업이 일정간격으로 계속 이루어지고 거리 계산을 통해 상단 알림으로 보호자에게 알려줌.
// 피보호자의 위치값을 서버에서 받아와야 함
public class RequestAlert extends StringRequest {

    private Map<String, String> map;

    // location_record 테이블의 기록중에서 마지막 기록을 가져온다.
    public RequestAlert(String brac_num, String url, Response.Listener<String> listener){
        super(Method.POST, url, listener, null);

        map = new HashMap<>();
        map.put("brac_num" , brac_num);

    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }

}