package cn.ucai.fulicenter.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import cn.ucai.fulicenter.R;

public class FuLiCenterMainActivity extends BaseActivity {
    public static final String TAG = FuLiCenterMainActivity.class.getName();

    RadioButton rbNewGoods;
    RadioButton rbBoutique;
    RadioButton rbCategory;
    RadioButton rbCart;
    RadioButton rbPersonalCenter;

    RadioButton[] mrbTabs;

    TextView tvCartHint;

    int index;
    int currentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fu_li_center_main);
        initView();
    }

    private void initView() {
        rbNewGoods = (RadioButton) findViewById(R.id.rb_new_good);
        rbBoutique = (RadioButton) findViewById(R.id.rb_boutique);
        rbCategory = (RadioButton) findViewById(R.id.rb_category);
        rbCart = (RadioButton) findViewById(R.id.rb_cart);
        rbPersonalCenter = (RadioButton) findViewById(R.id.rb_personal_center);

        tvCartHint = (TextView) findViewById(R.id.tvCartHint);

        mrbTabs = new RadioButton[5];
        mrbTabs[0] = rbNewGoods;
        mrbTabs[1] = rbBoutique;
        mrbTabs[2] = rbCategory;
        mrbTabs[3] = rbCart;
        mrbTabs[4] = rbPersonalCenter;
    }

    public void onCheckedChange(View view) {
        switch (view.getId()) {
            case R.id.rb_new_good:
                index = 0;
                break;
            case R.id.rb_boutique:
                index = 1;
                break;
            case R.id.rb_category:
                index = 2;
                break;
            case R.id.rb_cart:
                index = 3;
                break;
            case R.id.rb_personal_center:
                index = 4;
                break;
        }
        Log.e(TAG,"index==="+index+"currentIndex===="+currentIndex);
        if (index != currentIndex) {
            setRadioButtonStatus(index);
            currentIndex=index;
        }
    }

    public void setRadioButtonStatus(int index) {
        for (int i=0;i<mrbTabs.length;i++){
            if (index==i){
                mrbTabs[i].setChecked(true);

                Log.e(TAG,"currentIndex===="+currentIndex);
            }else {
                mrbTabs[i].setChecked(false);
            }
        }
    }
}
