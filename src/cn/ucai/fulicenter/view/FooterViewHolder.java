package cn.ucai.fulicenter.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import cn.ucai.fulicenter.R;

/**
 * Created by Administrator on 2016/8/5 0005.
 */
public class FooterViewHolder extends RecyclerView.ViewHolder{
    TextView tvFooter;

    public FooterViewHolder(View itemView) {
        super(itemView);
        tvFooter = (TextView) itemView.findViewById(R.id.tvFooter);

    }
}
