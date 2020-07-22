package com.eunzzi.p0617;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RequestBoardRead extends StringRequest {

    private Map<String, String> map;

    public RequestBoardRead(String board_num, String url, Response.Listener<String> listener){
        super(Method.POST, url, listener, null);

        map = new HashMap<>();
        map.put("board_num", board_num);

    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }

}
