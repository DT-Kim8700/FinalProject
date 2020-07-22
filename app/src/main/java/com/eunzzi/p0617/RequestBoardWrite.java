package com.eunzzi.p0617;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RequestBoardWrite extends StringRequest {

    private Map<String, String> map;

    public RequestBoardWrite(String title, String content, String id, String url, Response.Listener<String> listener){
        super(Method.POST, url, listener, null);

        map = new HashMap<>();
        map.put("title", title);
        map.put("content", content);
        map.put("id", id);

    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }

}
