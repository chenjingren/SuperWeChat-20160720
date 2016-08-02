package cn.ucai.superwechat.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import cn.ucai.superwechat.DemoHXSDKHelper;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatApplication;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.bean.UserAvatar;
import cn.ucai.superwechat.db.UserDao;
import cn.ucai.superwechat.task.DownloadContactListTask;
import cn.ucai.superwechat.task.DownloadGroupListTask;
import cn.ucai.superwechat.utils.OkHttpUtils2;
import cn.ucai.superwechat.utils.Utils;

/**
 * 开屏页
 *
 */
public class SplashActivity extends BaseActivity {
	public static final String TAG = SplashActivity.class.getName();

	private RelativeLayout rootLayout;
	private TextView versionText;
	
	private static final int sleepTime = 2000;

	@Override
	protected void onCreate(Bundle arg0) {
		setContentView(R.layout.activity_splash);
		super.onCreate(arg0);

		rootLayout = (RelativeLayout) findViewById(R.id.splash_root);
		versionText = (TextView) findViewById(R.id.tv_version);

		versionText.setText(getVersion());
		AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
		animation.setDuration(1500);
		rootLayout.startAnimation(animation);
	}

	@Override
	protected void onStart() {
		super.onStart();

		new Thread(new Runnable() {
			public void run() {
				if (DemoHXSDKHelper.getInstance().isLogined()) {
					// ** 免登陆情况 加载所有本地群和会话
					//不是必须的，不加sdk也会自动异步去加载(不会重复加载)；
					//加上的话保证进了主页面会话和群组都已经load完毕
					long start = System.currentTimeMillis();
					EMGroupManager.getInstance().loadAllGroups();
					EMChatManager.getInstance().loadAllConversations();
					//从全局变量中获取当前登录用户的账号
					String userName = SuperWeChatApplication.getInstance().getUserName();
					//UserAvatar user = SuperWeChatApplication.getInstance().getUser();
					Log.e(TAG,"userName========="+userName);
					//Log.e(TAG,"user=============="+user);
					//根据用户账号获取用户的所有信息
					UserDao dao = new UserDao(SplashActivity.this);
					UserAvatar userAvatar = dao.getUserAvatar(userName);
					Log.e(TAG,"user=========="+userAvatar);

					if(userAvatar==null) {
						OkHttpUtils2<String> utils2 = new OkHttpUtils2<String>();
						utils2.setRequestUrl(I.REQUEST_FIND_USER)
								.addParam(I.User.USER_NAME, userName)
								.targetClass(String.class)
								.execute(new OkHttpUtils2.OnCompleteListener<String>() {
									@Override
									public void onSuccess(String s) {
										Log.e(TAG,"s============"+s);
										Result result = Utils.getResultFromJson(s, UserAvatar.class);
										Log.e(TAG,"result========"+result);
										if (result!=null&&result.isRetMsg()){
											UserAvatar userAvatar1 = (UserAvatar) result.getRetData();
											if (userAvatar1!=null){
												Log.e(TAG,"userAvatar1============"+userAvatar1);
												//保存用户信息到全局变量中
												SuperWeChatApplication.getInstance().setUser(userAvatar1);
												SuperWeChatApplication.currentUserNick = userAvatar1.getMUserNick();
											}
										}
									}

									@Override
									public void onError(String error) {
                                        Log.e(TAG,"error========"+error);
									}
								});
					}else {
                        Log.e(TAG,"USERAVATAR!=NULL");
						//保存用户信息到全局变量中
						SuperWeChatApplication.getInstance().setUser(userAvatar);
						SuperWeChatApplication.currentUserNick = userAvatar.getMUserNick();
					}

					//下载用户的好友列表
					new DownloadContactListTask(SplashActivity.this,userName).execute();

					new DownloadGroupListTask(SplashActivity.this,userName).execute();

					long costTime = System.currentTimeMillis() - start;
					//等待sleeptime时长
					if (sleepTime - costTime > 0) {
						try {
							Thread.sleep(sleepTime - costTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					//进入主页面
					startActivity(new Intent(SplashActivity.this, MainActivity.class));
					finish();
				}else {
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
					}
					startActivity(new Intent(SplashActivity.this, LoginActivity.class));
					finish();
				}
			}
		}).start();

	}
	
	/**
	 * 获取当前应用程序的版本号
	 */
	private String getVersion() {
		String st = getResources().getString(R.string.Version_number_is_wrong);
		PackageManager pm = getPackageManager();
		try {
			PackageInfo packinfo = pm.getPackageInfo(getPackageName(), 0);
			String version = packinfo.versionName;
			return version;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return st;
		}
	}
}
