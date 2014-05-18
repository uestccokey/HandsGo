/*
 * 
 */
package com.soyomaker.handsgo;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;

import com.soyomaker.handsgo.adapter.FavoriteGroupExpandableListViewAdapter;
import com.soyomaker.handsgo.db.DBService;
import com.soyomaker.handsgo.go.ChessManual;
import com.soyomaker.handsgo.go.Group;
import com.soyomaker.handsgo.util.AsyncTaskUtils;
import com.soyomaker.handsgo.util.AsyncTaskUtils.AsyncTaskListener;
import com.soyomaker.handsgo.util.DialogUtils;
import com.umeng.analytics.MobclickAgent;

/**
 * 收藏界面.
 * 
 * @author cokey
 */
public class FavoriteActivity extends Activity implements AsyncTaskListener {

	/** The m list view. */
	private ExpandableListView mGroupListView;

	/** The m create favorite group button. */
	private Button mCreateFavoriteGroupButton;

	/** The m open local sgf button. */
	private Button mOpenLocalSgfButton;

	/** The m favorite group expandable list adapter. */
	private FavoriteGroupExpandableListViewAdapter mFavoriteGroupExpandableListAdapter;

	/** The m groups. */
	private ArrayList<Group> mGroups = new ArrayList<Group>();

	/** The m handler. */
	protected Handler mHandler;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.favorite);
		initView();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		initData();
	}

	/**
	 * Inits the view.
	 */
	private void initView() {
		mHandler = new Handler();
		mGroupListView = (ExpandableListView) this
				.findViewById(R.id.listview_favorite);
		// 设置item点击的监听器
		mGroupListView
				.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

					@Override
					public boolean onChildClick(ExpandableListView parent,
							View v, int groupPosition, int childPosition,
							long id) {
						ChessManual chessManual = mGroups.get(groupPosition)
								.getChessManuals().get(childPosition);
						Intent intent = new Intent(FavoriteActivity.this,
								MainActivity.class);
						intent.putExtra("ChessManual", chessManual);
						startActivity(intent);
						return false;
					}
				});
		mFavoriteGroupExpandableListAdapter = new FavoriteGroupExpandableListViewAdapter(
				this);
		mFavoriteGroupExpandableListAdapter.setGroups(mGroups);
		mGroupListView.setAdapter(mFavoriteGroupExpandableListAdapter);
		mOpenLocalSgfButton = (Button) this.findViewById(R.id.open_local_sgf);
		mOpenLocalSgfButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(FavoriteActivity.this,
						SDCardActivity.class);
				startActivity(intent);
			}
		});
		mCreateFavoriteGroupButton = (Button) findViewById(R.id.favorite_title_settings);
		mCreateFavoriteGroupButton
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						final EditText editText = new EditText(
								FavoriteActivity.this);
						final Group group = new Group();
						group.setName("新分组");
						editText.setText("新分组");
						new AlertDialog.Builder(FavoriteActivity.this)
								.setTitle("新建分组")
								.setIcon(android.R.drawable.ic_dialog_info)
								.setView(editText)
								.setPositiveButton("确定", new OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										String newName = editText.getText()
												.toString();
										group.setName(newName);
										mGroups.add(group);
										DBService.saveGroup(group);
										mFavoriteGroupExpandableListAdapter
												.notifyDataSetChanged();
									}
								}).setNegativeButton("取消", null).show();
					}
				});
		// 在线参数为true时才会展示广告条
		if ("true".equals(MobclickAgent.getConfigParams(this,
				Constant.AD_ON_STRING))) {
		}
	}

	/**
	 * Inits the data.
	 */
	private void initData() {
		AsyncTaskUtils.create(this, this, false).execute();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.soyomaker.handsgo.util.AsyncTaskUtils.AsyncTaskListener#doInBackground
	 * (com.soyomaker.handsgo.util.AsyncTaskUtils, int)
	 */
	@Override
	public Object doInBackground(AsyncTaskUtils task, int flag) {
		return DBService.getGroupCaches();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.soyomaker.handsgo.util.AsyncTaskUtils.AsyncTaskListener#onPostExecute
	 * (com.soyomaker.handsgo.util.AsyncTaskUtils, int, int)
	 */
	@Override
	public void onPostExecute(AsyncTaskUtils task, Object result, int flag) {
		if (result != null) {
			mGroups.clear();
			mGroups.addAll((ArrayList<Group>) result);
			mFavoriteGroupExpandableListAdapter.notifyDataSetChanged();
		} else {
			DialogUtils.showToast(this, R.string.db_connect_error);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#dispatchKeyEvent(android.view.KeyEvent)
	 */
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			AlertDialog.Builder builder = new Builder(this);
			builder.setMessage("确定要退出吗?");
			builder.setTitle("提示");
			builder.setPositiveButton("确认",
					new android.content.DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							System.exit(0);
						}
					});
			builder.setNegativeButton("取消",
					new android.content.DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			builder.create().show();
			return true;
		}
		return super.dispatchKeyEvent(event);
	}
}
