package cn.ucai.fulicenter.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
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
import cn.ucai.fulicenter.adapter.GoodAdapter;
import cn.ucai.fulicenter.bean.NewGoodsBean;
import cn.ucai.fulicenter.utils.OkHttpUtils2;
import cn.ucai.fulicenter.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewGoodFragment extends Fragment {

    public static final String TAG = NewGoodFragment.class.getName();

    FuLiCenterMainActivity mContext;

    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;

    GridLayoutManager mGridLayoutManager;

    GoodAdapter mAdapter;

    ArrayList<NewGoodsBean> goodList;

    int pageId =1;

    TextView tvRefreshHint;

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
        initData();
        setListener();
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

        tvRefreshHint = (TextView) layout.findViewById(R.id.tv_refresh);
    }

    private void initData() {
        findNewGoodBean(new OkHttpUtils2.OnCompleteListener<NewGoodsBean[]>() {
                            @Override
                            public void onSuccess(NewGoodsBean[] result) {
                                tvRefreshHint.setVisibility(View.GONE);
                                mSwipeRefreshLayout.setRefreshing(false);
                                Log.e(TAG,"result===="+result);
                                if (result!=null){
                                    Log.e(TAG,"result.length====="+result.length);
                                    ArrayList<NewGoodsBean> goods = Utils.array2List(result);
                                    mAdapter.initData(goods);
                                }
                            }

                            @Override
                            public void onError(String error) {
                                Log.e(TAG,"error===="+error);
                                tvRefreshHint.setVisibility(View.GONE);
                                mSwipeRefreshLayout.setRefreshing(false);
                            }
                        }
        );
    }

    public void findNewGoodBean(OkHttpUtils2.OnCompleteListener<NewGoodsBean[]> listener){
        OkHttpUtils2<NewGoodsBean[]> utils2 = new OkHttpUtils2<NewGoodsBean[]>();
        utils2.setRequestUrl(I.REQUEST_FIND_NEW_BOUTIQUE_GOODS)
                .addParam(I.NewAndBoutiqueGood.CAT_ID,I.CAT_ID+"")
                .addParam(I.PAGE_ID,pageId+"")
                .addParam(I.PAGE_SIZE,I.PAGE_SIZE_DEFAULT+"")
                .targetClass(NewGoodsBean[].class)
                .execute(listener);
    }

    private void setListener() {
        setPullDownRefreshListener();
    }

    private void setPullDownRefreshListener() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                tvRefreshHint.setVisibility(View.VISIBLE);
                pageId =1;
                initData();
            }
        });
    }
}
