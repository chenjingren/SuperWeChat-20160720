package cn.ucai.fulicenter.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.fragment.BoutiqueFragment;
import cn.ucai.fulicenter.fragment.CategoryFragment;
import cn.ucai.fulicenter.fragment.NewGoodFragment;

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

    Fragment[] fragments;

    NewGoodFragment newGoodFragment;

    BoutiqueFragment boutiqueFragment;

    CategoryFragment categoryFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fu_li_center_main);
        initView();
        initFragment();
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

    private void initFragment() {
        fragments = new Fragment[5];
        newGoodFragment = new NewGoodFragment();
        boutiqueFragment = new BoutiqueFragment();
        categoryFragment = new CategoryFragment();
        fragments[0] = newGoodFragment;
        fragments[1] = boutiqueFragment;
        fragments[2] =categoryFragment;

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container,newGoodFragment)
                .add(R.id.fragment_container,boutiqueFragment).hide(boutiqueFragment)
                .add(R.id.fragment_container,categoryFragment).hide(categoryFragment)
                .show(newGoodFragment)
                .commit();
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

            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(fragments[currentIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.fragment_container, fragments[index]);
            }
            trx.show(fragments[index]).commit();

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
