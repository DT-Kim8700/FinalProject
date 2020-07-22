package com.eunzzi.p0617;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class ViewpagerBoard extends FragmentStatePagerAdapter {

    private int tabCount;
    private String id;
    private RecyclerBoardAll recyclerBoardAll = null;
    private RecyclerBoardOne recyclerBoardOne = null;

    public ViewpagerBoard(@NonNull FragmentManager fm, int behavior, String id) {
        super(fm, behavior);
        tabCount = behavior;
        this.id = id;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        if(position == 0) {
            recyclerBoardAll = new RecyclerBoardAll();
            return recyclerBoardAll;
        }else if(position == 1) {
            recyclerBoardOne = new RecyclerBoardOne(id);
            return recyclerBoardOne;
        }else{
            return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
