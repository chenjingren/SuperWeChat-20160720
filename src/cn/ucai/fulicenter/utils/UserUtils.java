package cn.ucai.fulicenter.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.applib.controller.HXSDKHelper;
import cn.ucai.fulicenter.DemoHXSDKHelper;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.MemberUserAvatar;
import cn.ucai.fulicenter.bean.UserAvatar;
import cn.ucai.fulicenter.domain.User;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class UserUtils {

	public static final String TAG = UserUtils.class.getName();
    /**
     * 根据username获取相应user，由于demo没有真实的用户数据，这里给的模拟的数据；
     * @param username
     * @return
     */
    public static User getUserInfo(String username){

        User user = ((DemoHXSDKHelper)HXSDKHelper.getInstance()).getContactList().get(username);
        if(user == null){
            user = new User(username);
        }
            
        if(user != null){
            //demo没有这些数据，临时填充
        	if(TextUtils.isEmpty(user.getNick()))
        		user.setNick(username);
        }
        return user;
    }
    
    /**
     * 设置用户头像
     * @param username
     */
    public static void setUserAvatar(Context context, String username, ImageView imageView){
    	User user = getUserInfo(username);
        if(user != null && user.getAvatar() != null){
            Picasso.with(context).load(user.getAvatar()).placeholder(R.drawable.default_avatar).into(imageView);
        }else{
            Picasso.with(context).load(R.drawable.default_avatar).into(imageView);
        }
    }
    
    /**
     * 设置当前用户头像
     */
	public static void setCurrentUserAvatar(Context context, ImageView imageView) {
		User user = ((DemoHXSDKHelper)HXSDKHelper.getInstance()).getUserProfileManager().getCurrentUserInfo();
		if (user != null && user.getAvatar() != null) {
			Picasso.with(context).load(user.getAvatar()).placeholder(R.drawable.default_avatar).into(imageView);
		} else {
			Picasso.with(context).load(R.drawable.default_avatar).into(imageView);
		}
	}
    
    /**
     * 设置用户昵称
     */
    public static void setUserNick(String username,TextView textView){
    	User user = getUserInfo(username);
    	if(user != null){
    		textView.setText(user.getNick());
    	}else{
    		textView.setText(username);
    	}
    }





    
    /**
	 * 设置当前用户昵称
	 */
	public static void setCurrentUserNick(TextView textView){
		User user = ((DemoHXSDKHelper)HXSDKHelper.getInstance()).getUserProfileManager().getCurrentUserInfo();
		if(textView != null){
			textView.setText(user.getNick());
		}
	}

	/**
	 * 设置当前登录用户昵称
	 */
	public static void setAppCurrentUserNick(TextView textView){
		//User user = ((DemoHXSDKHelper)HXSDKHelper.getInstance()).getUserProfileManager().getCurrentUserInfo();
		UserAvatar user = FuLiCenterApplication.getInstance().getUser();
		Log.e(TAG,"setAppCurrentUserNick.user==="+user);
		if(textView != null &&user!=null){
			if (user.getMUserNick()!=null) {
				textView.setText(user.getMUserNick());
			}else {
				textView.setText(user.getMUserName());
			}

		}
	}
    
    /**
     * 保存或更新某个用户
     * @param newUser
     */
	public static void saveUserInfo(User newUser) {
		if (newUser == null || newUser.getUsername() == null) {
			return;
		}
		((DemoHXSDKHelper) HXSDKHelper.getInstance()).saveContact(newUser);
	}



	/**
	 * 设置用户头像
	 * @param username
	 */
	public static void setAppUserAvatar(Context context, String username, ImageView imageView){
		String path = "";
		path = getAppAvatarPath(username);
		if(path != null && username != null){
			Log.e(TAG,"path ==="+path);
			Picasso.with(context).load(path).placeholder(R.drawable.default_avatar).into(imageView);
		}else{
			Picasso.with(context).load(R.drawable.default_avatar).into(imageView);
		}
	}

	public static String getAppAvatarPath(String username){
		StringBuilder path = new StringBuilder();
		path.append(I.SERVER_ROOT).append(I.QUESTION).
				append(I.KEY_REQUEST).append(I.EQU).append(I.REQUEST_DOWNLOAD_AVATAR)
				.append(I.AND)
				.append(I.NAME_OR_HXID).append(I.EQU).append(username)
				.append(I.AND)
				.append(I.AVATAR_TYPE).append(I.EQU).append(I.AVATAR_TYPE_USER_PATH);
		return path.toString();
	}


	/**
	 * 根据username获取相应useravatar
	 * @param username
	 * @return
	 */
	public static UserAvatar getAppUserInfo(String username){

		UserAvatar userAvatar = FuLiCenterApplication.getInstance().getContactMap().get(username);
		if(userAvatar == null){
			userAvatar = new UserAvatar(username);
		}

		return userAvatar;
	}

	/**
	 * 设置当前登录用户的好友昵称
	 */
	public static void setAppUserNick(String username,TextView textView){
		UserAvatar user = getAppUserInfo(username);
		if(user != null){
			if (user.getMUserNick()!=null){
				textView.setText(user.getMUserNick());
			}else {
				textView.setText(username);
			}
		}else {
			textView.setText(username);
		}
	}


	public static MemberUserAvatar getAppGroupUserInfo(String hxid, String username){

		//UserAvatar userAvatar = FuLiCenterApplication.getInstance().getContactMap().get(username);

        MemberUserAvatar member =null;
        HashMap<String, MemberUserAvatar> members =
                FuLiCenterApplication.getInstance().getGroupMemebers().get(hxid);
        if (members==null||members.size()<0){
            return null;
        }else {
            member = members.get(username);
        }
		return member;
	}


	public static void setAppGroupUserNick(String hxid,String username,TextView textView){
		MemberUserAvatar member = getAppGroupUserInfo(hxid,username);

		if(member != null){
			if (member.getMUserNick()!=null){
				textView.setText(member.getMUserNick());
			}else {
				textView.setText(username);
			}
		}else {
			textView.setText(username);
		}
	}

    public static MemberUserAvatar getAppMemberInfo(String username,String hxid){
        MemberUserAvatar member = null;
        HashMap<String, MemberUserAvatar> members =
                FuLiCenterApplication.getInstance().getGroupMemebers().get(hxid);
        if (members==null||members.size()<0){
            return null;
        }else {
            member = members.get(username);
        }
        return member;
    }

    public static void setAppMemberNick(String username,String hxid,TextView textView){
        MemberUserAvatar member = getAppMemberInfo(username, hxid);
        if (member!=null&&member.getMUserNick()!=null){
            textView.setText(member.getMUserNick());
        }else {
            textView.setText(username);
        }
    }


    /**
     * 设置用户的昵称
     */
    public static void setAppUserNickByUser(UserAvatar user,TextView textView){
        if (user != null) {
            if (user.getMUserNick() != null) {
                textView.setText(user.getMUserNick());
            } else {
                textView.setText(user.getMUserName());
            }
        }
    }


	/*public static void setAppCurrentUserAvatar(Context context, ImageView imageView) {
		String path = "";
		String username = FuLiCenterApplication.getInstance().getUserName();
		path = getAppAvatarPath(username);
		if(path != null && username != null){
			Log.e(TAG,"path ==="+path);
			Picasso.with(context).load(path).placeholder(R.drawable.default_avatar).into(imageView);
		}else{
			Picasso.with(context).load(R.drawable.default_avatar).into(imageView);
		}
	}*/

    /**获取群组头像地址*/
	public static String getAppGroupAvatarPath(String hxid){
		StringBuilder path = new StringBuilder();
		path.append(I.SERVER_ROOT).append(I.QUESTION).
				append(I.KEY_REQUEST).append(I.EQU).append(I.REQUEST_DOWNLOAD_AVATAR)
				.append(I.AND)
				.append(I.NAME_OR_HXID).append(I.EQU).append(hxid)
				.append(I.AND)
				.append(I.AVATAR_TYPE).append(I.EQU).append(I.AVATAR_TYPE_GROUP_PATH);
		return path.toString();
	}


    /**设置群组头像*/
	public static void setAppGroupAvatar(Context context, String hxid, ImageView imageView){
		String path = "";
		path = getAppGroupAvatarPath(hxid);
		if(path != null && hxid != null){
			Log.e(TAG,"path ==="+path);
			Picasso.with(context).load(path).placeholder(R.drawable.group_icon).into(imageView);
		}else{
			Picasso.with(context).load(R.drawable.group_icon).into(imageView);
		}
	}
}
