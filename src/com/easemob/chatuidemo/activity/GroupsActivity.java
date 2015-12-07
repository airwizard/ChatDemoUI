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
package com.easemob.chatuidemo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.chat.EMCursorResult;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupInfo;
import com.easemob.chat.EMGroupManager;
import com.easemob.chatuidemo.R;
import com.easemob.chatuidemo.adapter.GroupAdapter;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import moodle.util.Constant;
import moodle.util.L;
import moodle.util.PreferenceUtils;
import moodle.util.StringUtil;

public class GroupsActivity extends BaseActivity {
	public static final String TAG = "GroupsActivity";
	private ListView groupListView;
	protected List<EMGroup> grouplist;
	private GroupAdapter groupAdapter;
	private InputMethodManager inputMethodManager;
	public static GroupsActivity instance;
	private SyncListener syncListener;
	private View progressBar;
	private SwipeRefreshLayout swipeRefreshLayout;
	Handler handler = new Handler();

	private String moodlePageContent;
	//private ProgressDialog progressDialog;
	private String cursor;
	private final int pagesize = 20;

	class SyncListener implements HXSDKHelper.HXSyncListener {
		@Override
		public void onSyncSucess(final boolean success) {
			EMLog.d(TAG, "onSyncGroupsFinish success:" + success);
			runOnUiThread(new Runnable() {
				public void run() {
					swipeRefreshLayout.setRefreshing(false);
					if (success) {
						handler.postDelayed(new Runnable() {
							@Override
							public void run() {
								refresh();
								progressBar.setVisibility(View.GONE);
							}
						}, 1000);
					} else {
						if (!GroupsActivity.this.isFinishing()) {
							String s1 = getResources()
									.getString(
											R.string.Failed_to_get_group_chat_information);
							Toast.makeText(GroupsActivity.this, s1, Toast.LENGTH_LONG).show();
							progressBar.setVisibility(View.GONE);
						}
					}
				}
			});
		}
	}
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_groups);

		instance = this;
		inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		grouplist = EMGroupManager.getInstance().getAllGroups();

		moodlePageContent = PreferenceUtils.getPrefString(GroupsActivity.this, Constant.MOODLE_INFO, null);
		if (moodlePageContent != null) {
			parse_HTML_Source_and_Switch_Activity(moodlePageContent );
		}

		groupListView = (ListView) findViewById(R.id.list);
		swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
		swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
		                android.R.color.holo_orange_light, android.R.color.holo_red_light);
		swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
			    MainActivity.asyncFetchGroupsFromServer();
			}
		});
		
		groupAdapter = new GroupAdapter(this, 1, grouplist);
		groupListView.setAdapter(groupAdapter);
		groupListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				/*if (position == 1) {
					// 新建群聊
					startActivityForResult(new Intent(GroupsActivity.this, NewGroupActivity.class), 0);
				}*/ /*else if (position == 2) {
					// 添加公开群
					startActivityForResult(new Intent(GroupsActivity.this, PublicGroupsActivity.class), 0);
				} *//*else {*/
					// 进入群聊
					Intent intent = new Intent(GroupsActivity.this, ChatActivity.class);
					// it is group chat
					intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
					intent.putExtra("groupId", groupAdapter.getItem(position - 1).getGroupId());
					startActivityForResult(intent, 0);
				/*}*/
			}

		});
		groupListView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
					if (getCurrentFocus() != null)
						inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
				}
				return false;
			}
		});
		
		progressBar = (View)findViewById(R.id.progress_bar);
		
		syncListener = new SyncListener();
		HXSDKHelper.getInstance().addSyncGroupListener(syncListener);

		if (!HXSDKHelper.getInstance().isGroupsSyncedWithServer()) {
			progressBar.setVisibility(View.VISIBLE);
		} else {
			progressBar.setVisibility(View.GONE);
		}
		
		refresh();
	}

	/**
	 * 进入公开群聊列表
	 */
	public void onPublicGroups(View view) {
		startActivity(new Intent(this, PublicGroupsActivity.class));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onResume() {
		super.onResume();
		grouplist = EMGroupManager.getInstance().getAllGroups();
		groupAdapter = new GroupAdapter(this, 1, grouplist);
		groupListView.setAdapter(groupAdapter);
		groupAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onDestroy() {
		if (syncListener != null) {
			HXSDKHelper.getInstance().removeSyncGroupListener(syncListener);
			syncListener = null;
		}
		super.onDestroy();
		instance = null;
	}
	
	public void refresh() {
		if (groupListView != null && groupAdapter != null) {
			grouplist = EMGroupManager.getInstance().getAllGroups();
			groupAdapter = new GroupAdapter(GroupsActivity.this, 1,
					grouplist);
			groupListView.setAdapter(groupAdapter);
			groupAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * 返回
	 * 
	 * @param view
	 */
	public void back(View view) {
		finish();
	}



	public void parse_HTML_Source_and_Switch_Activity( String HTMLsource ){
		Pattern p_coursename = Pattern.compile("<h3 class=\"coursename\">.*?>(.*?)</a>");
		Matcher m_course = p_coursename.matcher(HTMLsource);

		final ArrayList<String> cname = new ArrayList<String>();
/*		ArrayList<String> cteachers = new ArrayList<String>();*/

		while(m_course.find()){
			String course_name =  m_course.group(1);
			cname.add(course_name);
		}


		new Thread(new Runnable() {

			public void run() {
				try {
					final EMCursorResult<EMGroupInfo> result = EMGroupManager.getInstance().getPublicGroupsFromServer(pagesize, cursor);
					//获取group list
					final List<EMGroupInfo> returnGroups = result.getData();

					if (cname.size() != 0){
						for (String courseName : cname) {
							int countReturn = 0;
							int countJoined = 0;
							String joinId = "";
							if (returnGroups.size() != 0) {
								for (EMGroupInfo mEMGroupInfo : returnGroups) {
									if (mEMGroupInfo.getGroupName().equals(courseName)) {
										joinId = mEMGroupInfo.getGroupId();
										//L.e("111joinId = " +joinId);
										++countReturn;
									}
								}
							}

							if (countReturn == 0) {
								createGroup(courseName);
							} else {
								if (grouplist.size() != 0) {
									for (EMGroup mEMGroup : grouplist) {
										if (mEMGroup.getGroupName().equals(courseName)) {
											joinId = mEMGroup.getGroupId();
											//L.e("222joinId = " +joinId);
											++countJoined;
										}
									}
									if (countJoined == 0) {
										if (!StringUtil.isEmpty(joinId)) {
											//L.e("if===begin to join===");
											joinGroup(joinId);
										}
									}

								} else {
									//L.e("else===begin to join===");
									joinGroup(joinId);
								}


							}

						}
					}


				}catch (EaseMobException e) {
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(GroupsActivity.this, "加载数据失败，请检查网络或稍后重试", Toast.LENGTH_SHORT).show();
						}
					});
				}
			}
		}).start();

	}


	public void createGroup(final String courseName) {
		String st1 = getResources().getString(R.string.Is_to_create_a_group_chat);
		final String st2 = getResources().getString(R.string.Failed_to_create_groups);
/*		//新建群组
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage(st1);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();*/

		new Thread(new Runnable() {
			@Override
			public void run() {
				// 调用sdk创建群组方法
				String groupName = courseName;
				String desc = courseName;
				//String[] members = {DemoApplication.getInstance().getUserName()};
				String[] members = {};
				try {
					/*if(checkBox.isChecked()){
						//创建公开群，此种方式创建的群，可以自由加入
						//创建公开群，此种方式创建的群，用户需要申请，等群主同意后才能加入此群
						*//**needApprovalRequired:如果创建的公开群用需要户自由加入，就传false。否则需要申请，等群主批准后才能加入，传true*//*
						EMGroupManager.getInstance().createPublicGroup(groupName, desc, members, false, 200);
					}else{
						//创建不公开群
						EMGroupManager.getInstance().createPrivateGroup(groupName, desc, members, memberCheckbox.isChecked(),200);
					}*/
					/**needApprovalRequired:如果创建的公开群用需要户自由加入，就传false。否则需要申请，等群主批准后才能加入，传true*/
					EMGroupManager.getInstance().createPublicGroup(groupName, desc, members, false, 200);
				//	groupAdapter.notifyDataSetChanged();
					//swipeRefreshLayout.setRefreshing(true);
				//	refresh();
					runOnUiThread(new Runnable() {
						public void run() {
							//progressDialog.dismiss();
							//setResult(RESULT_OK);
							//finish();
						}
					});
				} catch (final EaseMobException e) {
					runOnUiThread(new Runnable() {
						public void run() {
							//progressDialog.dismiss();
							Toast.makeText(GroupsActivity.this, st2 + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
						}
					});
				}

			}
		}).start();

	}

	public void joinGroup(final String groupId) {


		new Thread(new Runnable() {
			public void run() {
				try {

					EMGroupManager.getInstance().joinGroup(groupId);
					//L.e("joined");
				//	groupAdapter.notifyDataSetChanged();
					//swipeRefreshLayout.setRefreshing(true);
					//refresh();

				} catch (final EaseMobException e) {
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						public void run() {
							L.e("fail to join " + groupId);//Toast.makeText(GroupSimpleDetailActivity.this, st5+e.getMessage(), 0).show();
						}
					});
				}
			}
		}).start();
/*
		try {
			EMGroupManager.getInstance().joinGroup(groupId);
			L.e("joined");
		}catch (final EaseMobException e) {
			e.printStackTrace();
		}*/

	}


}
