package com.words.chat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;

import sw.ui.SwListView;

/**
 * Created by donglinghao on 2016-01-28.
 */
public class HomeFragment extends Fragment {

    private View mRootView;
    MaterialRefreshLayout ref;
    SwListView lst;
    int idx=0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null){
            Log.e("666","HomeFragment");
            mRootView = inflater.inflate(R.layout.activity_frm_text,container,false);
        }
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null){
            parent.removeView(mRootView);
        }
        init();
        return mRootView;
    }

    void toast(String s)
    {
        Toast.makeText(getActivity(),s,Toast.LENGTH_SHORT).show();
    }

    void init()
    {
        ref=(MaterialRefreshLayout)mRootView.findViewById(R.id.refresh);
        lst=(SwListView)mRootView.findViewById(R.id.lst1);

        ref.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                toast("onRefresh");
                ref.finishRefresh();
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                super.onRefreshLoadMore(materialRefreshLayout);
                toast("loadmore");
                reload_list();
                ref.finishRefreshLoadMore();
            }
        });

        reload_list();

    }

    void reload_list()
    {
        idx+=10;
        for(int i=idx-10;i<=idx;i++)
        {
            lst.add("No."+i);
        }
        lst.reload();
    }
}
