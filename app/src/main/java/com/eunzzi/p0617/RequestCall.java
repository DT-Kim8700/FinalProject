package com.eunzzi.p0617;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RequestCall extends StringRequest {

    private Map<String, String> map;

    public RequestCall(String brac_num, String url, Response.Listener<String> listener){
        super(Method.POST, url, listener, null);

        map = new HashMap<>();
        map.put("brac_num", brac_num);

    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }

}