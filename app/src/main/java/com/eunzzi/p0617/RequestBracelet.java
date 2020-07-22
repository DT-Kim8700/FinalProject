package com.eunzzi.p0617;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RequestBracelet extends StringRequest {

    private Map<String, String> map;

    public RequestBracelet(String id, String brac_num, String brac_name, String brac_sn, String brac_gender, String url, Response.Listener<String> listener){
        super(Method.POST, url, listener, null);

        map = new HashMap<>();
        map.put("id", id);
        map.put("brac_num", brac_num);
        map.put("brac_name", brac_name);
        map.put("brac_sn", brac_sn);
        map.put("brac_gender", brac_gender);
    }

    public RequestBracelet(String id, String url, Response.Listener<String> listener){
        super(Method.POST, url, listener, null);

        map = new HashMap<>();
        map.put("id", id);

    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }

}