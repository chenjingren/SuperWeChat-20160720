package cn.ucai.fulicenter.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.AlbumsBean;
import cn.ucai.fulicenter.bean.GoodDetailsBean;
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

    GoodDetailsBean mGoodDetails = new GoodDetailsBean();

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

                    mGoodDetails = goodDetails;

                    showGoodDetails();

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

    private void showGoodDetails() {
        tvGoodEnglishName.setText(mGoodDetails.getGoodsEnglishName());
        tvGoodName.setText(mGoodDetails.getGoodsName());
        tvShopPrice.setText(mGoodDetails.getShopPrice());
        tvCurrentPrice.setText(mGoodDetails.getCurrencyPrice());

        salv.startPlayLoop(indicator,getAlbumImgUrl(),getAlbumImgLength());

        wvGoodBrief.loadDataWithBaseURL(null,mGoodDetails.getGoodsBrief(),D.TEXT_HTML,D.UTF_8,null);
    }

    private int getAlbumImgLength() {
        int albumLength = 0;
        /*if (mGoodDetails.getPropertiesBean()!=null && mGoodDetails.getPropertiesBean().length>0){
            for (int i=0;i<mGoodDetails.getPropertiesBean().length;i++){
                 albumLength =mGoodDetails.getPropertiesBean()[i].getAlbumsBean().length;
                Log.e(TAG,"albumLength======"+albumLength);
            }
        }*/

        if (mGoodDetails.getPropertyBean()!=null && mGoodDetails.getPropertyBean().length>0){

                albumLength =mGoodDetails.getPropertyBean()[0].getAlbumsBean().length;
                Log.e(TAG,"albumLength======"+albumLength);

        }
        return albumLength;
    }

    private String[] getAlbumImgUrl() {
        String[] imgUrls = new String[]{};

        /*if (mGoodDetails.getPropertiesBean()!=null && mGoodDetails.getPropertiesBean().length>0){
            for (int i=0;i<mGoodDetails.getPropertiesBean().length;i++){
                AlbumsBean[] albumsBean = mGoodDetails.getPropertiesBean()[i].getAlbumsBean();
                int albumLength = albumsBean.length;
                imgUrls = new String[albumLength];
               for(int j=0;j<albumLength;j++){
                   imgUrls[j] = albumsBean[j].getImgUrl();
                   Log.e(TAG,"imgUrl[j]=========="+imgUrls[j]);
               }
            }
        }*/

        if (mGoodDetails.getPropertyBean()!=null && mGoodDetails.getPropertyBean().length>0){
                AlbumsBean[] albumsBean = mGoodDetails.getPropertyBean()[0].getAlbumsBean();
                int albumLength = albumsBean.length;
                imgUrls = new String[albumLength];
                for(int j=0;j<albumLength;j++){
                    imgUrls[j] = albumsBean[j].getImgUrl();
                    Log.e(TAG,"imgUrl[j]=========="+imgUrls[j]);
                }
        }
        return imgUrls;
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
