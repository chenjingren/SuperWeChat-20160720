package cn.ucai.fulicenter.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.FuLiCenterMainActivity;
import cn.ucai.fulicenter.adapter.BoutiqueAdapter;
import cn.ucai.fulicenter.bean.BoutiqueBean;
import cn.ucai.fulicenter.utils.OkHttpUtils2;
import cn.ucai.fulicenter.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class BoutiqueFragment extends Fragment {

    public static final String TAG = BoutiqueFragment.class.getName();

    FuLiCenterMainActivity mContext;

    BoutiqueAdapter mAdapter;
    ArrayList<BoutiqueBean> boutiques;

    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;

    TextView mtvRefreshHint;

    LinearLayoutManager mLayoutManager;


    public BoutiqueFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_boutique, container, false);
        initView(layout);
        initData();
        return layout;
    }

    private void initView(View layout) {

        mContext = (FuLiCenterMainActivity) getActivity();

        mSwipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.srl_boutique);
        mSwipeRefreshLayout.setColorSchemeColors(
                R.color.google_yellow,
                R.color.google_red,
                R.color.google_green,
                R.color.google_blue
        );
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.rv_boutique);
        mtvRefreshHint = (TextView) layout.findViewById(R.id.tv_refresh);

        mLayoutManager = new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false);

        boutiques = new ArrayList<BoutiqueBean>();
        mAdapter = new BoutiqueAdapter(mContext,boutiques);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

    }

    private void initData() {
        findBoutique(new OkHttpUtils2.OnCompleteListener<BoutiqueBean[]>() {
            @Override
            public void onSuccess(BoutiqueBean[] boutiqueArray) {
                Log.e(TAG,"boutiqueArray======="+boutiqueArray);
                if (boutiqueArray!=null&&boutiqueArray.length>0){
                    ArrayList<BoutiqueBean> boutiqueBeans = Utils.array2List(boutiqueArray);
                    boutiques = boutiqueBeans;
                    mAdapter.initData(boutiques);
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    private void findBoutique(OkHttpUtils2.OnCompleteListener<BoutiqueBean[]> listener) {
        OkHttpUtils2<BoutiqueBean[]> utils2 = new OkHttpUtils2<BoutiqueBean[]>();
        utils2.setRequestUrl(I.REQUEST_FIND_BOUTIQUES)
                .targetClass(BoutiqueBean[].class)
                .execute(listener);
    }

}
