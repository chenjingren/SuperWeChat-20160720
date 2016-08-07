package cn.ucai.fulicenter.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.List;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.FuLiCenterMainActivity;
import cn.ucai.fulicenter.adapter.CategoryAdapter;
import cn.ucai.fulicenter.bean.CategoryChildBean;
import cn.ucai.fulicenter.bean.CategoryGroupBean;
import cn.ucai.fulicenter.utils.OkHttpUtils2;
import cn.ucai.fulicenter.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFragment extends Fragment {

    public static final String TAG = CategoryFragment.class.getName();

    FuLiCenterMainActivity mContext;

    ExpandableListView mExpandableListView;

    List<CategoryGroupBean> groupList;
    List<ArrayList<CategoryChildBean>> childList;

    CategoryAdapter mAdapter;

    public CategoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mContext = (FuLiCenterMainActivity) getActivity();

        View layout = inflater.inflate(R.layout.fragment_category, container, false);
        initView(layout);
        initData();
        return layout;
    }

    private void initView(View layout) {
        mExpandableListView = (ExpandableListView) layout.findViewById(R.id.elv_category);

        mExpandableListView.setGroupIndicator(null);

        groupList = new ArrayList<CategoryGroupBean>();
        childList = new ArrayList<ArrayList<CategoryChildBean>>();
        mAdapter = new CategoryAdapter(mContext,groupList,childList);

        mExpandableListView.setAdapter(mAdapter);
    }

    private void initData() {
        findCategoryGroupList(new OkHttpUtils2.OnCompleteListener<CategoryGroupBean[]>() {
            @Override
            public void onSuccess(CategoryGroupBean[] result) {
                Log.e(TAG,"group,,,,result===="+result);
                if (result!=null){
                    final ArrayList<CategoryGroupBean> groupLlist = Utils.array2List(result);
                    if (groupLlist!=null){
                        groupList = groupLlist;
                        Log.e(TAG,"groupLlist.size==="+groupLlist.size());

                        for (CategoryGroupBean g:groupLlist){
                            findCategoryChildList(new OkHttpUtils2.OnCompleteListener<CategoryChildBean[]>() {
                                @Override
                                public void onSuccess(CategoryChildBean[] result) {
                                    Log.e(TAG,"child,,,,result===="+result);
                                    if (result!=null){
                                        ArrayList<CategoryChildBean> childLlist = Utils.array2List(result);
                                        Log.e(TAG,"childLlist.size====="+childLlist.size());

                                        childList.add(childLlist);
                                        //mAdapter.notifyDataSetChanged();
                                        mAdapter.addData(groupList,childList);

                                    }
                                }

                                @Override
                                public void onError(String error) {
                                    Log.e(TAG,"child,,,error==="+error);
                                }
                            },g.getId());
                        }
                    }

                }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG,"group,,,error==="+error);
            }
        });
    }

    public void findCategoryGroupList(OkHttpUtils2.OnCompleteListener<CategoryGroupBean[]> listener){
        OkHttpUtils2<CategoryGroupBean[]> utils2 = new OkHttpUtils2<>();
        utils2.setRequestUrl(I.REQUEST_FIND_CATEGORY_GROUP)
                .targetClass(CategoryGroupBean[].class)
                .execute(listener);
    }

    public void findCategoryChildList(OkHttpUtils2.OnCompleteListener<CategoryChildBean[]> listener,int parentId){
        OkHttpUtils2<CategoryChildBean[]> utils2 = new OkHttpUtils2<>();
        utils2.setRequestUrl(I.REQUEST_FIND_CATEGORY_CHILDREN)
                .addParam(I.CategoryChild.PARENT_ID,String.valueOf(parentId))
                .addParam(I.PAGE_ID,String.valueOf(I.PAGE_ID_DEFAULT))
                .addParam(I.PAGE_SIZE,String.valueOf(I.PAGE_SIZE_DEFAULT))
                .targetClass(CategoryChildBean[].class)
                .execute(listener);
    }
}
