package com.eunzzi.p0617;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class FragmentBoard extends Fragment {

    private ViewPager view_pager_board = null;
    private TabLayout view_pager_tabLayout = null;
    private Button view_pager_btn = null;

    private ViewpagerBoard viewpagerBoard = null;

    private String id, name;
    private boolean bracelet;

    public FragmentBoard(String id , boolean bracelet, String name) {
        this.id = id;
        this.bracelet = bracelet;
        this.name = name;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_board, container, false);

        viewSetting(view);

        // 상단 탭으로 화면 전환
        view_pager_tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //탭이 선택 되었을 때
                view_pager_board.setCurrentItem(tab.getPosition());
                view_pager_board.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        // 글쓰기 버튼
        view_pager_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextActivity();
            }
        });

        return view;
    }

    private void viewSetting(View view){
        view_pager_board = view.findViewById(R.id.view_pager_board);
        view_pager_tabLayout = view.findViewById(R.id.view_pager_tabLayout);
        view_pager_btn = view.findViewById(R.id.view_pager_btn);

        viewpagerBoard = new ViewpagerBoard(getChildFragmentManager(), view_pager_tabLayout.getTabCount(), id );

        view_pager_board.setAdapter(viewpagerBoard);
        view_pager_board.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(view_pager_tabLayout));
    }

    private void nextActivity(){
        Intent intent = new Intent(getActivity(), ActivityBoardWrite.class);
        intent.putExtra("id", id);
        intent.putExtra("bracelet", bracelet);
        intent.putExtra("name", name);

        getActivity().finish();
        startActivity(intent);
    }

}
