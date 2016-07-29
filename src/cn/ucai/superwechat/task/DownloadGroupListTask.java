package cn.ucai.superwechat.task;

import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.util.Log;

import java.util.ArrayList;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.SuperWeChatApplication;
import cn.ucai.superwechat.bean.GroupAvatar;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.utils.OkHttpUtils2;
import cn.ucai.superwechat.utils.Utils;

/**
 * Created by Administrator on 2016/7/26 0026.
 */
public class DownloadGroupListTask {

    public static final String TAG = DownloadGroupListTask.class.getName();

    Context mContext;

    String username;

    public DownloadGroupListTask(Context mContext, String username) {
        this.mContext = mContext;
        this.username = username;
    }

    public void execute(){
        OkHttpUtils2<String> utils2 = new OkHttpUtils2<>();
        utils2.setRequestUrl(I.REQUEST_FIND_GROUP_BY_USER_NAME)
                .addParam(I.User.USER_NAME,username)
                .targetClass(String.class)
                .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Log.e(TAG,"S========="+s);
                        Result result = Utils.getListResultFromJson(s, GroupAvatar.class);
                        Log.e(TAG,"result==========="+result);
                        if (result!=null&&result.isRetMsg()){
                            ArrayList<GroupAvatar> groupAvatars = (ArrayList<GroupAvatar>) result.getRetData();
                            if (groupAvatars!=null&&groupAvatars.size()>0){
                                Log.e(TAG,"groupAvatars.size===="+groupAvatars.size());
                                SuperWeChatApplication.getInstance().setGroupList(groupAvatars);
                                for (GroupAvatar g:groupAvatars){
                                    SuperWeChatApplication.getInstance().getGroupMap().put(g.getMGroupHxid(),g);
                                }
                                mContext.sendStickyBroadcast(new Intent("update_group_list"));
                            }
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG,"error========"+error);
                    }
                });
    }
}
