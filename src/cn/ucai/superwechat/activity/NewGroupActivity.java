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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatApplication;
import cn.ucai.superwechat.bean.GroupAvatar;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.listener.OnSetAvatarListener;
import cn.ucai.superwechat.utils.OkHttpUtils2;
import cn.ucai.superwechat.utils.Utils;

import com.easemob.exceptions.EaseMobException;

import java.io.File;

public class NewGroupActivity extends BaseActivity {

    public static final String TAG = NewGroupActivity.class.getName();

	private EditText groupNameEditText;
	private ProgressDialog progressDialog;
	private EditText introductionEditText;
	private CheckBox checkBox;
	private CheckBox memberCheckbox;
	private LinearLayout openInviteContainer;


    ImageView iv_avatar;

	OnSetAvatarListener mOnSerAvatarListener;

    public static final int CREATE_GROUP = 100;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_group);
		groupNameEditText = (EditText) findViewById(R.id.edit_group_name);
		introductionEditText = (EditText) findViewById(R.id.edit_group_introduction);
		checkBox = (CheckBox) findViewById(R.id.cb_public);
		memberCheckbox = (CheckBox) findViewById(R.id.cb_member_inviter);
		openInviteContainer = (LinearLayout) findViewById(R.id.ll_open_invite);

        iv_avatar = (ImageView) findViewById(R.id.iv_avatar);



		initListener();
	}

	public void initListener(){

        checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    openInviteContainer.setVisibility(View.INVISIBLE);
                }else{
                    openInviteContainer.setVisibility(View.VISIBLE);
                }
            }
        });

		findViewById(R.id.layoutAvatar).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mOnSerAvatarListener = new OnSetAvatarListener(NewGroupActivity.this,R.id.layout_parent,
						getAvatarName(), I.AVATAR_TYPE_GROUP_PATH);
			}
		});
	}

	String avatarName;
	private String getAvatarName() {
		avatarName = String.valueOf(System.currentTimeMillis());
		return avatarName;
	}

	/**
	 * @param v
	 */
	public void save(View v) {
		String str6 = getResources().getString(R.string.Group_name_cannot_be_empty);
		String name = groupNameEditText.getText().toString();
		if (TextUtils.isEmpty(name)) {
			Intent intent = new Intent(this, AlertDialog.class);
			intent.putExtra("msg", str6);
			startActivity(intent);
		} else {
			// 进通讯录选人
			startActivityForResult(new Intent(this, GroupPickContactsActivity.class).putExtra("groupName", name), CREATE_GROUP);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode!=RESULT_OK){
			return;
		}

		mOnSerAvatarListener.setAvatar(requestCode,data,iv_avatar);

		if (requestCode == CREATE_GROUP) {
            createEMGroup(data);
		}
	}


    //String st2 = getResources().getString(R.string.Failed_to_create_groups);
    private void createEMGroup(final Intent data) {

        //新建群组
        setProgressDialog();

        new Thread(new Runnable() {
            @Override
            public void run() {

                EMGroup group;
                // 调用sdk创建群组方法
                String groupName = groupNameEditText.getText().toString().trim();
                String desc = introductionEditText.getText().toString();
                String[] members = data.getStringArrayExtra("newmembers");
                try {
                    if(checkBox.isChecked()){
                        //创建公开群，此种方式创建的群，可以自由加入
                        //创建公开群，此种方式创建的群，用户需要申请，等群主同意后才能加入此群
                        group = EMGroupManager.getInstance().createPublicGroup(groupName, desc, members, true, 200);
                    }else{
                        //创建不公开群
                        group = EMGroupManager.getInstance().createPrivateGroup(groupName, desc, members, memberCheckbox.isChecked(), 200);
                    }

                    Log.e(TAG,"HXID=="+group.getGroupId());

                    createAppGroup(group.getGroupId(),groupName,desc,members);

                } catch (final EaseMobException e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(NewGroupActivity.this, R.string.Failed_to_create_groups + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }
        }).start();
    }

    private void createAppGroup(final String groupId, final String groupName, String desc, final String[] members) {
        File file = new File(OnSetAvatarListener.getAvatarPath(NewGroupActivity.this,I.AVATAR_TYPE_GROUP_PATH)
                                ,avatarName+I.AVATAR_SUFFIX_JPG);
        boolean isPublic = checkBox.isChecked();
        boolean invites = !isPublic;
        String own = SuperWeChatApplication.getInstance().getUserName();

        OkHttpUtils2<String> utils2 = new OkHttpUtils2<>();
        utils2.setRequestUrl(I.REQUEST_CREATE_GROUP)
                .addParam(I.Group.HX_ID,groupId)
                .addParam(I.Group.OWNER,own)
                .addParam(I.Group.NAME,groupName)
                .addParam(I.Group.ALLOW_INVITES,String.valueOf(invites))
                .addParam(I.Group.DESCRIPTION,desc)
                .addParam(I.Group.IS_PUBLIC,String.valueOf(isPublic))
                .addFile(file)
                .targetClass(String.class)
                .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Log.e(TAG,"s===="+s);
                        Result result = Utils.getResultFromJson(s, GroupAvatar.class);
                        Log.e(TAG,"result==="+result);
                        if (result!=null&&result.isRetMsg()){
                            GroupAvatar groupAvatar = (GroupAvatar) result.getRetData();
                            if (members!=null&&members.length>0){
                                addGroupMembers(groupId,members,groupAvatar);
                            }else {
                                createAppGroupSuccess(groupAvatar);
                            }

                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG,"error===="+error);
                        progressDialog.dismiss();
                        Toast.makeText(NewGroupActivity.this,R.string.Failed_to_create_groups+error, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public void createAppGroupSuccess( GroupAvatar groupAvatar){
        //新建群组成功之后，将群组信息填加到全局变量中
        SuperWeChatApplication.getInstance().getGroupMap().put(groupAvatar.getMGroupHxid(),groupAvatar);
        SuperWeChatApplication.getInstance().getGroupList().add(groupAvatar);
        runOnUiThread(new Runnable() {
            public void run() {
                progressDialog.dismiss();
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    private void addGroupMembers(String hxid, final String[] members, final GroupAvatar groupAvatar) {
        Log.e(TAG,"members======="+members.toString());
        Log.e(TAG,"members=========="+members);
        String memberArr ="";
        for (String s:members){
            memberArr+=s +",";
        }
        memberArr = memberArr.substring(0,memberArr.length()-1);
        Log.e(TAG,"memberArr======="+memberArr);

        OkHttpUtils2<String> utils2 = new OkHttpUtils2<>();
        utils2.setRequestUrl(I.REQUEST_ADD_GROUP_MEMBERS)
                .addParam(I.Member.GROUP_HX_ID,hxid)
                .addParam(I.Member.USER_NAME,memberArr)
                .targetClass(String.class)
                .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Log.e(TAG,"s============="+s);
                        Result result = Utils.getResultFromJson(s, GroupAvatar.class);
                        Log.e(TAG,"result============="+result);
                        if (result!=null&&result.isRetMsg()){
                            createAppGroupSuccess(groupAvatar);
                        }else {
                            progressDialog.dismiss();
                            Toast.makeText(NewGroupActivity.this,R.string.Failed_to_create_groups, Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG,"error===="+error);
                        progressDialog.dismiss();
                        Toast.makeText(NewGroupActivity.this,R.string.Failed_to_create_groups+error, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void setProgressDialog() {
        String st1 = getResources().getString(R.string.Is_to_create_a_group_chat);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(st1);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    public void back(View view) {
		finish();
	}
}
