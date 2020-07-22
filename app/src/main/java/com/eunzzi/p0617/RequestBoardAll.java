package com.eunzzi.p0617;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

public class RequestBoardAll extends StringRequest {

    public RequestBoardAll(String url, Response.Listener<String> listener){
        super(Method.POST, url, listener, null);
    }
}