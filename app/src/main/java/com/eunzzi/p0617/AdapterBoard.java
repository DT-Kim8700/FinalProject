package com.eunzzi.p0617;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AdapterBoard extends RecyclerView.Adapter<AdapterBoard.ViewHolder> {
    ArrayList<DTOBoard> items = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.item_board, viewGroup, false);

        return new ViewHolder(itemView);
    }

    @Override       // 공통
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.setItem(position);
    }

    @Override      // 공통
    public int getItemCount() {
        return items.size();
    }

    public void addItem(DTOBoard item) {
        items.add(item);
    }      // 공통

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ConstraintLayout board_item_layout;
        private TextView board_item_num, board_item_title, board_item_name;

        private String url = null;
        private RequestQueue requestQueue;      // 서버에 요청을 보내는 역할

        private String board_num, name, title, content = null;

        public ViewHolder(View itemView) {
            super(itemView);
            viewSetting(itemView);
        }

        public void setItem(int position) {
            viewSetText(position);

            board_item_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boardSelect(v);
                }
            });
        }

        private void viewSetting(View itemView){
            board_item_layout = itemView.findViewById(R.id.board_item_layout);
            board_item_num = itemView.findViewById(R.id.board_item_num);
            board_item_title = itemView.findViewById(R.id.board_item_title);
            board_item_name = itemView.findViewById(R.id.board_item_name);
        }

        private void viewSetText(int position){
            board_item_num.setText(items.get(position).getBoardNumber() + "");
            board_item_title.setText(items.get(position).getBoardTitle());
            board_item_name.setText(items.get(position).getName());

            board_num = board_item_num.getText().toString();
        }

        private void boardSelect(View v){
            url = ActivityLogin.url_ip + "Read";

            // 해당 글의 번호에 맞는 content를 요청해야 한다.
            if (requestQueue == null) {
                requestQueue = Volley.newRequestQueue(v.getContext());
            }

            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {

                        JSONObject jsonObject = new JSONObject(response);

                        name = jsonObject.getString("name");
                        title = jsonObject.getString("title");
                        content = jsonObject.getString("content");

                        Intent intent = new Intent(itemView.getContext(), ActivityBoardRead.class);
                        intent.putExtra("name", name);
                        intent.putExtra("title", title);
                        intent.putExtra("content", content);

                        itemView.getContext().startActivity(intent);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

            RequestBoardRead requestBoardRead = new RequestBoardRead(board_num, url, responseListener);

            requestQueue.add(requestBoardRead);
        }

    }

}
