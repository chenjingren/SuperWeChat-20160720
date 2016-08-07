package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.GoodDetailsActivity;
import cn.ucai.fulicenter.bean.NewGoodsBean;
import cn.ucai.fulicenter.utils.ImageUtils;
import cn.ucai.fulicenter.view.FooterViewHolder;

/**
 * Created by Administrator on 2016/8/3 0003.
 */
public class GoodAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    Context mContext;
    ArrayList<NewGoodsBean> newGoodsList;

    GoodViewHolder mGoodViewHolder;
    FooterViewHolder mFooterViewHolder;

    boolean isMore;

    String tvFooter;

    int sortBy;

    public GoodAdapter(Context mContext, ArrayList<NewGoodsBean> newGoodsList) {
        this.mContext = mContext;
        this.newGoodsList = new ArrayList<NewGoodsBean>();
        newGoodsList.addAll(newGoodsList);
        sortBy = I.SORT_BY_PRICE_DESC;
        sortBy();
    }

    public void setSortBy(int sortBy) {
        this.sortBy = sortBy;
        sortBy();
        notifyDataSetChanged();
    }

    public boolean isMore() {
        return isMore;
    }

    public void setMore(boolean more) {
        isMore = more;
    }

    public void setTvFooter(String tvFooter) {
        this.tvFooter = tvFooter;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder =null;
        switch (viewType){
            case I.TYPE_FOOTER:
                holder = new FooterViewHolder(LayoutInflater.from(mContext).
                        inflate(R.layout.item_footer,null,false));
                break;

            case I.TYPE_ITEM:
                holder = new GoodViewHolder(LayoutInflater.from(mContext).
                        inflate(R.layout.item_new_good, null, false));
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof GoodViewHolder){
            mGoodViewHolder = (GoodViewHolder) holder;
            final NewGoodsBean goods = newGoodsList.get(position);
            mGoodViewHolder.tvGoodsName.setText(goods.getGoodsName());
            mGoodViewHolder.tvGoodsPrice.setText(goods.getCurrencyPrice());

            ImageUtils.setNewGoodThumb(mContext,mGoodViewHolder.ivThumb,goods.getGoodsThumb());

            mGoodViewHolder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(new Intent(mContext, GoodDetailsActivity.class)
                        .putExtra(D.GoodDetails.KEY_GOODS_ID,goods.getGoodsId()));
                }
            });
        }
        if (holder instanceof FooterViewHolder){
            mFooterViewHolder = (FooterViewHolder) holder;
            mFooterViewHolder.tvFooter.setText(tvFooter);
        }
    }

    @Override
    public int getItemCount() {
        return newGoodsList!=null?newGoodsList.size()+1:1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position==getItemCount()-1){
            return I.TYPE_FOOTER;
        }else {
            return I.TYPE_ITEM;
        }
    }

    public void initData(ArrayList<NewGoodsBean> goods) {
        if (newGoodsList!=null){
            newGoodsList.clear();
        }
        newGoodsList.addAll(goods);

        sortBy();
        notifyDataSetChanged();
    }

    public void addData(ArrayList<NewGoodsBean> goods) {
        newGoodsList.addAll(goods);
        sortBy();
        notifyDataSetChanged();
    }

    class GoodViewHolder extends RecyclerView.ViewHolder{
        LinearLayout layout;
        ImageView ivThumb;
        TextView tvGoodsName;
        TextView tvGoodsPrice;

        public GoodViewHolder(View itemView) {
            super(itemView);
            layout = (LinearLayout) itemView.findViewById(R.id.layout_new_good);
            ivThumb = (ImageView) itemView.findViewById(R.id.iv_goods_thumb);
            tvGoodsName = (TextView) itemView.findViewById(R.id.tv_goods_name);
            tvGoodsPrice = (TextView) itemView.findViewById(R.id.tv_goods_price);
        }
    }

    public void sortBy(){
        Collections.sort(newGoodsList, new Comparator<NewGoodsBean>() {
            int result =0 ;
            @Override
            public int compare(NewGoodsBean lhs, NewGoodsBean rhs) {
                switch (sortBy){
                    case I.SORT_BY_ADDTIME_ASC:
                        result = (int)(Long.valueOf(rhs.getAddTime())-Long.valueOf(lhs.getAddTime()));
                        break;
                    case I.SORT_BY_ADDTIME_DESC:
                        result = (int)(Long.valueOf(lhs.getAddTime())-Long.valueOf(rhs.getAddTime()));
                        break;

                    case I.SORT_BY_PRICE_ASC:
                        result = convertPrice(rhs.getCurrencyPrice())-convertPrice(lhs.getCurrencyPrice());
                        break;

                    case I.SORT_BY_PRICE_DESC:
                        result = convertPrice(lhs.getCurrencyPrice())-convertPrice(rhs.getCurrencyPrice());
                        break;
                }
                return result;
            }
        });
    }

    //￥88￥88$￥¥ $￥¥  ￥140
    public int convertPrice(String price){
        price = price.substring(price.indexOf("￥")+1);
        return Integer.valueOf(price);
    }
}
