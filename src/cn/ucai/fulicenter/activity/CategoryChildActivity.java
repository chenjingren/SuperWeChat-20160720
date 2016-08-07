package cn.ucai.fulicenter.activity;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.adapter.GoodAdapter;
import cn.ucai.fulicenter.bean.CategoryChildBean;
import cn.ucai.fulicenter.bean.NewGoodsBean;
import cn.ucai.fulicenter.utils.DisplayUtils;
import cn.ucai.fulicenter.utils.OkHttpUtils2;
import cn.ucai.fulicenter.utils.Utils;
import cn.ucai.fulicenter.view.CatChildFilterButton;

public class CategoryChildActivity extends Activity {

    public static final String TAG = CategoryChildActivity.class.getName();

    Activity mContext;

    SwipeRefreshLayout mSwipeRefreshLayout;

    RecyclerView mRecyclerView;

    TextView mtvRefreshHint;

    GridLayoutManager mGridLayoutManager;

    GoodAdapter mAdapter;

    ArrayList<NewGoodsBean> boutiqueChildList;


    int action = I.ACTION_DOWNLOAD;
    int pageId=0;

    int cat_id;

    Button mbtnPrice,mbtnAddTime;

    boolean mSortPriceAsc;
    boolean mSortAddTimeAsc;

    int sortBy;

    CatChildFilterButton mCatChidFilterButton;
    String name;

    ArrayList<CategoryChildBean> childList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_child);

        mContext = this;

        initView();
        initData();
        setListener();
    }

    private void initView() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.salv_category_child);
        mSwipeRefreshLayout.setColorSchemeColors(
                R.color.google_yellow,
                R.color.google_red,
                R.color.google_green,
                R.color.google_blue
        );

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_category_child);

        mGridLayoutManager = new GridLayoutManager(mContext, I.COLUM_NUM);
        mGridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(mGridLayoutManager);

        boutiqueChildList = new ArrayList<NewGoodsBean>();
        mAdapter = new GoodAdapter(mContext,boutiqueChildList);

        mRecyclerView.setAdapter(mAdapter);

        mtvRefreshHint = (TextView) findViewById(R.id.tv_refresh);

        /*String name = getIntent().getStringExtra(D.Boutique.KEY_NAME);
        DisplayUtils.initBackWithTitle(mContext,name);*/

        DisplayUtils.initBack(mContext);

        mbtnPrice = (Button) findViewById(R.id.btn_price);
        mbtnAddTime = (Button) findViewById(R.id.btn_time);

        sortBy = I.SORT_BY_ADDTIME_DESC;

        mCatChidFilterButton = (CatChildFilterButton) findViewById(R.id.btnCatChildFilter);
        name=getIntent().getStringExtra(I.CategoryGroup.NAME);
        mCatChidFilterButton.setText(name);

        childList = (ArrayList<CategoryChildBean>) getIntent().getSerializableExtra("childList");
    }

    private void initData() {
        findNewGoodBean(new OkHttpUtils2.OnCompleteListener<NewGoodsBean[]>() {
                            @Override
                            public void onSuccess(NewGoodsBean[] result) {
                                mtvRefreshHint.setVisibility(View.GONE);
                                mSwipeRefreshLayout.setRefreshing(false);
                                mAdapter.setMore(true);
                                mAdapter.setTvFooter(getResources().getString(R.string.load_more));
                                Log.e(TAG,"result===="+result);
                                if (result!=null){
                                    Log.e(TAG,"result.length====="+result.length);
                                    ArrayList<NewGoodsBean> goods = Utils.array2List(result);
                                    if (action==I.ACTION_DOWNLOAD || action==I.ACTION_PULL_DOWN){
                                        mAdapter.initData(goods);
                                    }else {
                                        mAdapter.addData(goods);
                                    }
                                    if (goods.size()<I.PAGE_SIZE_DEFAULT){
                                        mAdapter.setMore(false);
                                        mAdapter.setTvFooter(getResources().getString(R.string.no_more));
                                    }
                                }else {
                                    mAdapter.setMore(false);
                                    mAdapter.setTvFooter(getResources().getString(R.string.no_more));
                                }

                            }

                            @Override
                            public void onError(String error) {
                                Log.e(TAG,"error===="+error);
                                mtvRefreshHint.setVisibility(View.GONE);
                                mSwipeRefreshLayout.setRefreshing(false);
                            }
                        }
        );
    }

    public void findNewGoodBean(OkHttpUtils2.OnCompleteListener<NewGoodsBean[]> listener){
        cat_id = getIntent().getIntExtra(I.CategoryChild.CAT_ID,0);
        Log.e(TAG,"catId========"+cat_id);
        if (cat_id<0) finish();
        OkHttpUtils2<NewGoodsBean[]> utils2 = new OkHttpUtils2<NewGoodsBean[]>();
        utils2.setRequestUrl(I.REQUEST_FIND_NEW_BOUTIQUE_GOODS)
                .addParam(I.NewAndBoutiqueGood.CAT_ID,cat_id+"")
                .addParam(I.PAGE_ID,pageId+"")
                .addParam(I.PAGE_SIZE,I.PAGE_SIZE_DEFAULT+"")
                .targetClass(NewGoodsBean[].class)
                .execute(listener);
    }

    private void setListener() {
        setPullDownRefreshListener();
        setPullUpRefreshListener();

        SortStatusChangedListener listener = new SortStatusChangedListener();
        mbtnPrice.setOnClickListener(listener);
        mbtnAddTime.setOnClickListener(listener);

        mCatChidFilterButton.setOnCatFilterClickListener(name,childList);
    }

    private void setPullUpRefreshListener() {
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            int lastItemPosition;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int a = RecyclerView.SCROLL_STATE_DRAGGING; //1
                int b = RecyclerView.SCROLL_STATE_IDLE; //0
                int c = RecyclerView.SCROLL_STATE_SETTLING; //2
                Log.e(TAG,"STATE===="+newState);

                if (newState==RecyclerView.SCROLL_STATE_IDLE
                        && lastItemPosition==mAdapter.getItemCount()-1){
                    if (mAdapter.isMore()){
                        action=I.ACTION_PULL_UP;
                        pageId +=I.PAGE_SIZE_DEFAULT;
                        initData();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int f = mGridLayoutManager.findFirstVisibleItemPosition();
                int l = mGridLayoutManager.findLastVisibleItemPosition();
                Log.e(TAG,"f==="+f+",l===="+l);
                lastItemPosition = mGridLayoutManager.findLastVisibleItemPosition();
                mSwipeRefreshLayout.setEnabled(mGridLayoutManager.findFirstVisibleItemPosition()==0);
                if (f==-1||l==-1){
                    lastItemPosition = mAdapter.getItemCount()-1;
                }
            }
        });
    }

    private void setPullDownRefreshListener() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                action = I.ACTION_PULL_DOWN;
                mtvRefreshHint.setVisibility(View.VISIBLE);
                pageId =0;
                initData();
            }
        });
    }

    class SortStatusChangedListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Drawable right;

            switch (v.getId()){
                case R.id.btn_price:
                    if (mSortPriceAsc){
                        sortBy = I.SORT_BY_PRICE_ASC;
                        right = getResources().getDrawable(R.drawable.arrow_order_up);
                    }else {
                        sortBy = I.SORT_BY_PRICE_DESC;
                        right = getResources().getDrawable(R.drawable.arrow_order_down);
                    }
                    right.setBounds(0,0,right.getIntrinsicWidth(),right.getIntrinsicHeight());
                    mbtnPrice.setCompoundDrawablesWithIntrinsicBounds(null,null,right,null);
                    mSortPriceAsc = !mSortPriceAsc;
                    break;
                case R.id.btn_time:
                    if (mSortAddTimeAsc){
                        sortBy = I.SORT_BY_ADDTIME_ASC;
                        right = getResources().getDrawable(R.drawable.arrow_order_up);
                    }else {
                        sortBy = I.SORT_BY_ADDTIME_DESC;
                        right = getResources().getDrawable(R.drawable.arrow_order_down);
                    }
                    right.setBounds(0,0,right.getIntrinsicWidth(),right.getIntrinsicHeight());
                    mbtnAddTime.setCompoundDrawablesWithIntrinsicBounds(null,null,right,null);
                    mSortAddTimeAsc = !mSortAddTimeAsc;
                    break;
            }

            mAdapter.setSortBy(sortBy);
        }
    }
}
