package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.BoutiqueBean;
import cn.ucai.fulicenter.utils.ImageUtils;
import cn.ucai.fulicenter.view.FooterViewHolder;

/**
 * Created by Administrator on 2016/8/6 0006.
 */
public class BoutiqueAdapter extends RecyclerView.Adapter {

    Context mContext;
    ArrayList<BoutiqueBean> boutiqueList;

    BoutiqueViewHolder boutiqueViewHolder;
    FooterViewHolder footerViewHolder;

    boolean isMore;

    String footerText;

    public boolean isMore() {
        return isMore;
    }

    public void setMore(boolean more) {
        isMore = more;
    }

    public void setFooterText(String footerText) {
        this.footerText = footerText;
        notifyDataSetChanged();
    }

    public BoutiqueAdapter(Context mContext, ArrayList<BoutiqueBean> boutiqueList) {
        this.mContext = mContext;
        this.boutiqueList = boutiqueList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder =null;

        switch (viewType){
            case I.TYPE_FOOTER:
                holder = new FooterViewHolder(View.inflate(mContext,R.layout.item_footer,null));
                break;

            case I.TYPE_ITEM:
                holder = new BoutiqueViewHolder(View.inflate(mContext,R.layout.item_boutique,null));
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FooterViewHolder){
            footerViewHolder = (FooterViewHolder) holder;
            footerViewHolder.tvFooter.setText(footerText);
    }

        if (holder instanceof BoutiqueViewHolder){
            boutiqueViewHolder = (BoutiqueViewHolder) holder;
            BoutiqueBean boutique = boutiqueList.get(position);
            boutiqueViewHolder.tvName.setText(boutique.getName());
            boutiqueViewHolder.tvTitle.setText(boutique.getTitle());
            boutiqueViewHolder.tvDesc.setText(boutique.getDescription());

            ImageUtils.setNewGoodThumb(mContext,boutiqueViewHolder.ivThumb,boutique.getImageurl());
        }
    }

    @Override
    public int getItemCount() {
        return boutiqueList!=null?boutiqueList.size()+1:1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position==getItemCount()-1){
            return I.TYPE_FOOTER;
        }else {
            return I.TYPE_ITEM;
        }
    }

    public void initData(ArrayList<BoutiqueBean> boutiques) {
        if (boutiqueList!=null){
            boutiqueList.clear();
        }
        boutiqueList.addAll(boutiques);
        notifyDataSetChanged();
    }

    public void addData(ArrayList<BoutiqueBean> boutiques) {
        boutiqueList.addAll(boutiques);
        notifyDataSetChanged();
    }


    class BoutiqueViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout mLayout;
        ImageView ivThumb;
        TextView tvName,tvTitle,tvDesc;

        public BoutiqueViewHolder(View itemView) {
            super(itemView);

            mLayout = (RelativeLayout) itemView.findViewById(R.id.layout_boutique);
            ivThumb = (ImageView) itemView.findViewById(R.id.iv_boutique_thumb);
            tvName = (TextView) itemView.findViewById(R.id.tv_boutique_name);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_boutique_title);
            tvDesc = (TextView) itemView.findViewById(R.id.tv_boutique_desc);
        }
    }
}
