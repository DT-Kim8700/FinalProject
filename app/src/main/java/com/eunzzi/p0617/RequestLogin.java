package com.eunzzi.p0617;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RequestLogin extends StringRequest {

    private Map<String, String> map;

    public RequestLogin(String id, String password, String url, Response.Listener<String> listener){
        super(Method.POST, url, listener, null);

        // 키값 : 값
        map = new HashMap<>();
        map.put("id", id);
        map.put("password", password);

    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
