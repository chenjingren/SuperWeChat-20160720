package cn.ucai.superwechat.task;

import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.SuperWeChatApplication;
import cn.ucai.superwechat.bean.MemberUserAvatar;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.utils.OkHttpUtils2;
import cn.ucai.superwechat.utils.Utils;

/**
 * Created by Administrator on 2016/7/27 0027.
 */
public class DownloadGroupMembersTask {

    public static final String TAG = DownloadGroupMembersTask.class.getName();

    Context mContext;
    String hxid;

    public DownloadGroupMembersTask(Context mContext, String hxid) {
        this.mContext = mContext;
        this.hxid = hxid;
    }

    public void execute(){
        OkHttpUtils2<String> utils2 = new OkHttpUtils2<>();


        utils2.setRequestUrl(I.REQUEST_DOWNLOAD_GROUP_MEMBERS_BY_HXID)
                .addParam(I.Member.GROUP_HX_ID,hxid)
                .targetClass(String.class)
                .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Log.e(TAG,"s==========="+s);
                        Result result = Utils.getListResultFromJson(s, MemberUserAvatar.class);
                        Log.e(TAG,"result========"+result);
                        List<MemberUserAvatar> list = (List<MemberUserAvatar>) result.getRetData();
                        Log.e(TAG,"list.size===="+list.size());
                        if (list!=null&&list.size()>0){
                            Map<String, HashMap<String, MemberUserAvatar>> groupMembers =
                                    SuperWeChatApplication.getInstance().getGroupMemebers();
                            if (!groupMembers.containsKey(hxid)){
                                groupMembers.put(hxid,new HashMap<String, MemberUserAvatar>());
                            }
                            HashMap<String, MemberUserAvatar> memberMap = groupMembers.get(hxid);
                            for (MemberUserAvatar m:list){
                                memberMap.put(m.getMUserName(),m);
                            }

                            mContext.sendStickyBroadcast(new Intent("update_members_list"));
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG,"error========"+error);
                    }
                });

    }
}
