package com.eunzzi.p0617;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecyclerBoardAll extends Fragment {

    private RecyclerView recycler_board_all = null;
    private AdapterBoard adapterBoard = null;

    private String url = null;
    private RequestQueue requestQueue = null;      // 서버에 요청을 보내는 역할

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_board_all, container, false);

        viewSetting(view);
        adapterSetting();

        return view;
    }

    private void viewSetting(View view){
        recycler_board_all = view.findViewById(R.id.recycler_board_all);

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        recycler_board_all.setLayoutManager(layoutManager);
    }

    private void adapterSetting(){
        url = ActivityLogin.url_ip + "BoardAll";

        adapterBoard = new AdapterBoard();

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getActivity());
        }

        // DB에서 데이터를 가져와 어뎁터에 부착
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);

                    for(int i = 0; i<jsonArray.length(); i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        int boardNumber = jsonObject.getInt("board_num");
                        String name = jsonObject.getString("name");
                        String boardTitle = jsonObject.getString("title");

                        DTOBoard dtoBoard= new DTOBoard(boardNumber, name, boardTitle);
                        // 전체적인 리스트를 불러올 때에는 타이틀만 보여주고 글 본문은 필요X
                        // 리스트 중 하나를 선택하면 그 글의 전체적인 데이터를 가져온다. 그 때 그 페이지에서 글의 본문을 서버에 요청하여 가져온다.

                        adapterBoard.addItem(dtoBoard);

                    }

                    adapterBoard.notifyDataSetChanged();

                    // 셋팅된 어뎁터를 리싸이클뷰에 셋팅
                    recycler_board_all.setAdapter(adapterBoard);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        RequestBoardAll requestBoardAll = new RequestBoardAll(url, responseListener);
        requestQueue.add(requestBoardAll);
    }

}
