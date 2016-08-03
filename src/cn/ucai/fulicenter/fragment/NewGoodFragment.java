package cn.ucai.fulicenter.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.FuLiCenterMainActivity;
import cn.ucai.fulicenter.adapter.GoodAdapter;
import cn.ucai.fulicenter.bean.NewGoodsBean;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewGoodFragment extends Fragment {

    FuLiCenterMainActivity mContext;

    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;

    GridLayoutManager mGridLayoutManager;

    GoodAdapter mAdapter;

    ArrayList<NewGoodsBean> goodList;

    public NewGoodFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mContext = (FuLiCenterMainActivity) getContext();
        View layout = inflater.inflate(R.layout.fragment_new_good, container, false);
        initView(layout);
        return layout;
    }

    private void initView(View layout) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.srl_new_good);
        mSwipeRefreshLayout.setColorSchemeColors(
                R.color.google_yellow,
                R.color.google_red,
                R.color.google_green,
                R.color.google_blue
        );
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.rv_new_good);

        mGridLayoutManager = new GridLayoutManager(mContext, I.COLUM_NUM);
        mGridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(mGridLayoutManager);

        goodList = new ArrayList<NewGoodsBean>();
        mAdapter = new GoodAdapter(mContext,goodList);

        mRecyclerView.setAdapter(mAdapter);
    }
}
