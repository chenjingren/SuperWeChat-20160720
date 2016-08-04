package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.NewGoodsBean;
import cn.ucai.fulicenter.utils.ImageUtils;

/**
 * Created by Administrator on 2016/8/3 0003.
 */
public class GoodAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    Context mContext;
    ArrayList<NewGoodsBean> newGoodsList;

    GoodViewHolder mGoodViewHolder;

    boolean isMore;

    public GoodAdapter(Context mContext, ArrayList<NewGoodsBean> newGoodsList) {
        this.mContext = mContext;
        this.newGoodsList = new ArrayList<NewGoodsBean>();
        newGoodsList.addAll(newGoodsList);
    }

    public boolean isMore() {
        return isMore;
    }

    public void setMore(boolean more) {
        isMore = more;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder =null;
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_new_good, null, false);
        holder = new GoodViewHolder(inflate);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof GoodViewHolder){
            mGoodViewHolder = (GoodViewHolder) holder;
            NewGoodsBean goods = newGoodsList.get(position);
            mGoodViewHolder.tvGoodsName.setText(goods.getGoodsName());
            mGoodViewHolder.tvGoodsPrice.setText(goods.getCurrencyPrice());

            ImageUtils.setNewGoodThumb(mContext,mGoodViewHolder.ivThumb,goods.getGoodsThumb());
        }
    }

    @Override
    public int getItemCount() {
        return newGoodsList.size();
    }

    public void initData(ArrayList<NewGoodsBean> goods) {
        if (newGoodsList!=null){
            newGoodsList.clear();
        }
        newGoodsList.addAll(goods);
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
}
