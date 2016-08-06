package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import java.util.ArrayList;

import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.GoodDetailsBean;
import cn.ucai.fulicenter.view.SlideAutoLoopView;

/**
 * Created by Administrator on 2016/8/6 0006.
 */
public class GoodDetailsAdapter extends RecyclerView.Adapter {

    Context mContext;
    ArrayList<GoodDetailsBean> goodDetailsList;

    GoodDetailsViewHolder mGoodDetailsViewholder;

    public GoodDetailsAdapter(Context mContext, ArrayList<GoodDetailsBean> goodDetailsList) {
        this.mContext = mContext;
        this.goodDetailsList = goodDetailsList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return goodDetailsList==null?0:goodDetailsList.size();
    }

    class GoodDetailsViewHolder extends RecyclerView.ViewHolder{

        TextView tvGoodEnglishName,tvGoodName,tvCurrentPrice,tvShopPrice;

        SlideAutoLoopView mSlideAutoLoopView;

        WebView mwvGoodsBrief;

        public GoodDetailsViewHolder(View itemView) {
            super(itemView);

            tvGoodEnglishName = (TextView) itemView.findViewById(R.id.tv_goods_english_name);
            tvGoodName = (TextView) itemView.findViewById(R.id.tv_goods_name);
            tvCurrentPrice = (TextView) itemView.findViewById(R.id.tv_goods_price_current);
            tvShopPrice = (TextView) itemView.findViewById(R.id.tv_goods_price_shop);

            mSlideAutoLoopView = (SlideAutoLoopView) itemView.findViewById(R.id.salv);
            mwvGoodsBrief = (WebView) itemView.findViewById(R.id.wv_good_brief);
        }
    }
}
