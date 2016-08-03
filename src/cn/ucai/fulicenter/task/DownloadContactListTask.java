package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.Map;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.bean.Result;
import cn.ucai.fulicenter.bean.UserAvatar;
import cn.ucai.fulicenter.utils.OkHttpUtils2;
import cn.ucai.fulicenter.utils.Utils;

/**
 * Created by Administrator on 2016/7/22 0022.
 */
public class DownloadContactListTask {
    public static final String TAG = DownloadContactListTask.class.getName();

    private String username;
    Context mContext;

    public DownloadContactListTask(Context mContext, String username) {
        this.mContext = mContext;
        this.username = username;
    }

    public void execute(){
        OkHttpUtils2<String> utils2 = new OkHttpUtils2<String>();
        utils2.setRequestUrl(I.REQUEST_DOWNLOAD_CONTACT_ALL_LIST)
                .addParam(I.Contact.USER_NAME,username)
                .targetClass(String.class)
                .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Log.e(TAG, "s====" + s);
                        Result result = Utils.getListResultFromJson(s, UserAvatar.class);
                        Log.e(TAG, "result====" + result);
                        if (result != null) {
                            ArrayList<UserAvatar> userList = (ArrayList<UserAvatar>) result.getRetData();
                            if (userList != null && userList.size() > 0) {
                                Log.e(TAG, "userList.size()===" + userList.size());
                                FuLiCenterApplication.getInstance().setUserList(userList);
                                mContext.sendStickyBroadcast(new Intent("update_contact_list"));

                                Map<String, UserAvatar> userAvatar = FuLiCenterApplication.getInstance().getContactMap();
                                for (UserAvatar user : userList) {
                                    userAvatar.put(user.getMUserName(), user);
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG,"error==="+error);
                    }
                });
    }
}
