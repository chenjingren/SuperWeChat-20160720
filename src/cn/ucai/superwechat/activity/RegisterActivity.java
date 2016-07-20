/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.ucai.superwechat.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EaseMobException;

import java.io.File;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatApplication;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.listener.OnSetAvatarListener;
import cn.ucai.superwechat.utils.OkHttpUtils2;

/**
 * 注册页
 * 
 */
public class RegisterActivity extends BaseActivity {

	public static final String TAG = RegisterActivity.class.getName();

	private EditText userNameEditText;
	private EditText userNickEditText;
	private EditText passwordEditText;
	private EditText confirmPwdEditText;

	ImageView mivAvatar;

	RelativeLayout mLayoutAvatar;
	LinearLayout mLayoutParent;

	String username;
	String usernick;
	String pwd;

	ProgressDialog pd;

	OnSetAvatarListener mAvatarListener;
	String avatarName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		initView();

		setListener();

	}

	private void setListener() {
		findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		findViewById(R.id.btn_register).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				register();
			}
		});

		mLayoutAvatar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mAvatarListener = new OnSetAvatarListener(RegisterActivity.this,
						R.id.layoutParent, getAvatarName(), I.AVATAR_TYPE_USER_PATH);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode!=RESULT_OK){
			return;
		}
		mAvatarListener.setAvatar(requestCode,data,mivAvatar);
	}

	private void initView() {
		userNameEditText = (EditText) findViewById(R.id.username);
		userNickEditText = (EditText) findViewById(R.id.usernick);
		passwordEditText = (EditText) findViewById(R.id.password);
		confirmPwdEditText = (EditText) findViewById(R.id.confirm_password);

		mivAvatar = (ImageView) findViewById(R.id.iv_avatar);
		mLayoutAvatar = (RelativeLayout) findViewById(R.id.layoutAvatar);
		mLayoutParent = (LinearLayout) findViewById(R.id.layoutParent);
	}


	public void register() {
		username = userNameEditText.getText().toString().trim();
		usernick = userNickEditText.getText().toString().trim();
		pwd = passwordEditText.getText().toString().trim();
		String confirm_pwd = confirmPwdEditText.getText().toString().trim();
		if (TextUtils.isEmpty(username)) {
			Toast.makeText(this, getResources().getString(R.string.User_name_cannot_be_empty), Toast.LENGTH_SHORT).show();
			userNameEditText.requestFocus();
			return;
		} else if (TextUtils.isEmpty(username)) {
			Toast.makeText(this, getResources().getString(R.string.illegal_user_name), Toast.LENGTH_SHORT).show();
			userNameEditText.requestFocus();
			return;
		}else if (TextUtils.isEmpty(usernick)) {
			Toast.makeText(this, getResources().getString(R.string.toast_nick_not_isnull), Toast.LENGTH_SHORT).show();
			userNickEditText.requestFocus();
			return;
		}else if (TextUtils.isEmpty(pwd)) {
			Toast.makeText(this, getResources().getString(R.string.Password_cannot_be_empty), Toast.LENGTH_SHORT).show();
			passwordEditText.requestFocus();
			return;
		} else if (TextUtils.isEmpty(confirm_pwd)) {
			Toast.makeText(this, getResources().getString(R.string.Confirm_password_cannot_be_empty), Toast.LENGTH_SHORT).show();
			confirmPwdEditText.requestFocus();
			return;
		} else if (!pwd.equals(confirm_pwd)) {
			Toast.makeText(this, getResources().getString(R.string.Two_input_password), Toast.LENGTH_SHORT).show();
			return;
		}

		if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(pwd)) {
			pd = new ProgressDialog(this);
			pd.setMessage(getResources().getString(R.string.Is_the_registered));
			pd.show();

			registerAppServer();
		}
	}

	private void registerAppServer() {
		File file = new File(OnSetAvatarListener.getAvatarPath(RegisterActivity.this,I.AVATAR_TYPE_USER_PATH)
								,avatarName+I.AVATAR_SUFFIX_JPG);
		OkHttpUtils2<Result> utils2 = new OkHttpUtils2<Result>();
		utils2.setRequestUrl(I.SERVER_ROOT)
				.addParam(I.User.USER_NAME,username)
				.addParam(I.User.NICK,usernick)
				.addParam(I.User.PASSWORD,pwd)
				.addFile(file)
				.targetClass(Result.class)
				.execute(new OkHttpUtils2.OnCompleteListener<Result>() {
					@Override
					public void onSuccess(Result result) {
						Log.e(TAG,"result=="+result);
						if (result.isRetMsg()){
							Log.e(TAG,"result=="+result);
							registerEMServer();
							Toast.makeText(RegisterActivity.this, "result success" + result.getRetCode(), Toast.LENGTH_SHORT).show();
						}else {
							pd.dismiss();
							Toast.makeText(RegisterActivity.this, "result fail"+result.getRetCode(), Toast.LENGTH_SHORT).show();
						}
					}

					@Override
					public void onError(String error) {
						pd.dismiss();
						Toast.makeText(RegisterActivity.this, "error"+error, Toast.LENGTH_SHORT).show();
					}
				});
	}

	public void registerEMServer(){
		new Thread(new Runnable() {
			public void run() {
				try {
					// 调用sdk注册方法
					EMChatManager.getInstance().createAccountOnServer(username, pwd);
					runOnUiThread(new Runnable() {
						public void run() {
							if (!RegisterActivity.this.isFinishing())
								pd.dismiss();
							// 保存用户名
							SuperWeChatApplication.getInstance().setUserName(username);
							Toast.makeText(getApplicationContext(), getResources().getString(R.string.Registered_successfully), Toast.LENGTH_SHORT).show();
							finish();
						}
					});
				} catch (final EaseMobException e) {
					runOnUiThread(new Runnable() {
						public void run() {
							if (!RegisterActivity.this.isFinishing())
								pd.dismiss();
							int errorCode=e.getErrorCode();
							if(errorCode==EMError.NONETWORK_ERROR){
								Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_anomalies), Toast.LENGTH_SHORT).show();
							}else if(errorCode == EMError.USER_ALREADY_EXISTS){
								Toast.makeText(getApplicationContext(), getResources().getString(R.string.User_already_exists), Toast.LENGTH_SHORT).show();
							}else if(errorCode == EMError.UNAUTHORIZED){
								Toast.makeText(getApplicationContext(), getResources().getString(R.string.registration_failed_without_permission), Toast.LENGTH_SHORT).show();
							}else if(errorCode == EMError.ILLEGAL_USER_NAME){
								Toast.makeText(getApplicationContext(), getResources().getString(R.string.illegal_user_name),Toast.LENGTH_SHORT).show();
							}else{
								Toast.makeText(getApplicationContext(), getResources().getString(R.string.Registration_failed) + e.getMessage(), Toast.LENGTH_SHORT).show();
							}
						}
					});
				}
			}
		}).start();
	}


	public void back(View view) {
		finish();
	}

	public String getAvatarName() {
		avatarName = String.valueOf(System.currentTimeMillis());
		return avatarName;
	}
}
