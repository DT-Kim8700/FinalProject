package com.eunzzi.p0617;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RequestJoin extends StringRequest {

    private Map<String, String> map;

    public RequestJoin(String id, String password, String name, String socialNumber, String url, Response.Listener<String> listener){
        super(Method.POST, url, listener, null);

        // 키값 : 값
        map = new HashMap<>();
        map.put("id", id);
        map.put("password", password);
        map.put("name", name);
        map.put("socialNumber", socialNumber);

    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
