package cn.ucai.fulicenter.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.GoodDetailsBean;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.utils.OkHttpUtils2;
import cn.ucai.fulicenter.view.FlowIndicator;
import cn.ucai.fulicenter.view.SlideAutoLoopView;

public class GoodDetailsActivity extends Activity {

    public static final String TAG = GoodDetailsActivity.class.getName();

    ImageView ivShare,ivCollect,ivCart;
    TextView tvCartCount;

    TextView tvGoodEnglishName,tvGoodName,tvCurrentPrice,tvShopPrice;

    SlideAutoLoopView salv;

    FlowIndicator indicator;

    WebView wvGoodBrief;

    int goodId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_good_details);
        initView();
        initData();
    }


    private void initView() {
        ivShare = (ImageView) findViewById(R.id.iv_share);
        ivCollect = (ImageView) findViewById(R.id.iv_collect);
        ivCart = (ImageView) findViewById(R.id.iv_cart);

        tvCartCount = (TextView) findViewById(R.id.tvCartCount);

        tvGoodEnglishName = (TextView) findViewById(R.id.tv_goods_english_name);
        tvGoodName = (TextView) findViewById(R.id.tv_goods_name);
        tvCurrentPrice = (TextView) findViewById(R.id.tv_goods_price_current);
        tvShopPrice = (TextView) findViewById(R.id.tv_goods_price_shop);

        salv = (SlideAutoLoopView) findViewById(R.id.salv);
        indicator = (FlowIndicator) findViewById(R.id.indicator);
        wvGoodBrief = (WebView) findViewById(R.id.wv_good_brief);
        WebSettings settings =
                wvGoodBrief.getSettings();
        settings.setBuiltInZoomControls(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
    }


    private void initData() {
        goodId = getIntent().getIntExtra(D.GoodDetails.KEY_GOODS_ID, 0);
        Log.e(TAG,"goodId===="+goodId);
        findGoodDetailsRequest(goodId, new OkHttpUtils2.OnCompleteListener<GoodDetailsBean>() {
            @Override
            public void onSuccess(GoodDetailsBean goodDetails) {
                Log.e(TAG,"goodDetails===="+goodDetails);
                if (goodDetails!=null){
                    showGoodDetails(goodDetails);

                    //wvGoodBrief.setTag(goodDetails.getGoodsBrief());

                    /*downloadGoodDetailsImg(goodDetails, new OkHttpUtils2.OnCompleteListener<MessageBean>() {
                        @Override
                        public void onSuccess(MessageBean result) {
                            Log.e(TAG,"result===="+result);
                            if (result!=null){
                                Toast.makeText(GoodDetailsActivity.this, "下载相册成功", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(String error) {
                            Log.e(TAG,"error===="+error);
                            Toast.makeText(GoodDetailsActivity.this, "下载相册失败", Toast.LENGTH_SHORT).show();
                        }
                    });*/
                }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG,"error===="+error);
            }
        });

    }

    private void showGoodDetails(GoodDetailsBean goodDetails) {
        tvGoodEnglishName.setText(goodDetails.getGoodsEnglishName());
        tvGoodName.setText(goodDetails.getGoodsName());
        tvShopPrice.setText(goodDetails.getShopPrice());
        tvCurrentPrice.setText(goodDetails.getCurrencyPrice());
    }

    private void findGoodDetailsRequest(int goodId,OkHttpUtils2.OnCompleteListener<GoodDetailsBean> listener) {
        OkHttpUtils2<GoodDetailsBean> utils2 = new OkHttpUtils2<GoodDetailsBean>();
        utils2.setRequestUrl(I.REQUEST_FIND_GOOD_DETAILS)
                .addParam(D.GoodDetails.KEY_GOODS_ID,String.valueOf(goodId))
                .targetClass(GoodDetailsBean.class)
                .execute(listener);
    }

    /*private void downloadGoodDetailsImg(GoodDetailsBean goodDetails,OkHttpUtils2.OnCompleteListener<MessageBean> listener) {
        OkHttpUtils2<MessageBean> utils = new OkHttpUtils2<>();
        utils.setRequestUrl(I.DOWNLOAD_ALBUM_IMG_URL+goodDetails.getGoodsImg())
                .targetClass(MessageBean.class)
                .execute(listener);
    }*/
}
