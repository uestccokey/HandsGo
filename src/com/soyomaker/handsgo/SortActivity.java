/*
 * 
 */
package com.soyomaker.handsgo;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.soyomaker.handsgo.adapter.ChessManualListViewAdapter;
import com.soyomaker.handsgo.adapter.SinaChessMatchExpandableListViewAdapter;
import com.soyomaker.handsgo.go.ChessManual;
import com.soyomaker.handsgo.net.sina.ReadSina;
import com.soyomaker.handsgo.net.xgoo.ReadXGOO;
import com.soyomaker.handsgo.util.AsyncTaskUtils;
import com.soyomaker.handsgo.util.AsyncTaskUtils.AsyncTaskListener;
import com.soyomaker.handsgo.util.AsyncTaskUtils.TaskResultCode;
import com.soyomaker.handsgo.util.DialogUtils;
import com.umeng.analytics.MobclickAgent;

/**
 * 分类界面.
 * 
 * @author cokey
 */
public class SortActivity extends Activity implements AsyncTaskListener {

	/** The m search edit text. */
	private EditText mSearchEditText;

	/** The m search button. */
	private Button mSearchButton;

	/** The load more gif. */
	private ProgressBar mLoadMoreGIF;

	/** The load more text. */
	private TextView mLoadMoreText;

	/** The footer view. */
	private View mFooterView;

	/** The m list view. */
	private ListView mListView;

	/** The m chess manuals. */
	private ArrayList<ChessManual> mChessManuals = new ArrayList<ChessManual>();

	/** The Constant DOWNLOADING_NEXT_MANUAL_LIST. */
	private static final int SEARCH_DOWNLOADING_MANUAL_LIST = 1;

	/** The Constant DOWNLOADING_NEXT_MANUAL_LIST. */
	private static final int SEARCH_DOWNLOADING_NEXT_MANUAL_LIST = 2;

	/** The Constant MATCH_DOWNLOADING_MANUAL_LIST. */
	private static final int MATCH_DOWNLOADING_MANUAL_LIST = 3;

	/** The Constant MATCH_DOWNLOADING_NEXT_MANUAL_LIST. */
	private static final int MATCH_DOWNLOADING_NEXT_MANUAL_LIST = 4;

	/** The m read xgoo. */
	private ReadXGOO mReadXgoo = new ReadXGOO();

	/** The m read sina. */
	private ReadSina mReadSina = new ReadSina();

	/** The m cur page. */
	private int mCurPage = 1;

	/** The m cur search string. */
	private String mCurSearchString = "";

	/** The m cur match url string. */
	private String mCurMatchUrlString = "";

	/** The m expandable list view. */
	private ExpandableListView mExpandableListView;

	/** The m adapter. */
	private SinaChessMatchExpandableListViewAdapter mAdapter;

	/** The Constant NORMAL_STATE. */
	public static final int NORMAL_STATE = -1;

	/** The Constant MATCH_STATE. */
	public static final int MATCH_STATE = 0;

	/** The Constant SEARCH_STATE. */
	public static final int SEARCH_STATE = 1;

	/** The m state. */
	private int mState = -1;

	/** The chess manual list view adapter. */
	private ChessManualListViewAdapter mChessManualListViewAdapter;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sort);
		initView();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		mChessManualListViewAdapter.notifyDataSetChanged();
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
	 * @see android.app.Activity#dispatchKeyEvent(android.view.KeyEvent)
	 */
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if (mState == NORMAL_STATE) {
				AlertDialog.Builder builder = new Builder(this);
				builder.setMessage("确定要退出吗?");
				builder.setTitle("提示");
				builder.setPositiveButton("确认",
						new android.content.DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								System.exit(0);
							}
						});
				builder.setNegativeButton("取消",
						new android.content.DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
				builder.create().show();
			} else {
				setState(NORMAL_STATE);
			}
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	/**
	 * Sets the state.
	 * 
	 * @param state
	 *            the new state
	 */
	private void setState(int state) {
		this.mState = state;
		updateState();
	}

	/**
	 * Update state.
	 */
	private void updateState() {
		switch (mState) {
		case NORMAL_STATE: {
			mListView.setVisibility(View.GONE);
			mExpandableListView.setVisibility(View.VISIBLE);
		}
			break;
		case MATCH_STATE: {
			mListView.setVisibility(View.VISIBLE);
			mExpandableListView.setVisibility(View.GONE);
		}
			break;
		case SEARCH_STATE: {
			mListView.setVisibility(View.VISIBLE);
			mExpandableListView.setVisibility(View.GONE);
		}
			break;
		default:
			break;
		}
	}

	/**
	 * Inits the view.
	 */
	private void initView() {
		mSearchEditText = (EditText) findViewById(R.id.search_edit);
		mSearchButton = (Button) findViewById(R.id.search_btn);
		mSearchButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				setState(SEARCH_STATE);
				mCurPage = 1;
				mReadXgoo.setPage(mCurPage);
				mCurSearchString = mSearchEditText.getText().toString().trim();
				mCurSearchString = mCurSearchString.replaceAll("\\s{1,}", "+");
				Log.e("搜索词", mCurSearchString);
				mCurSearchString = Uri.encode(mCurSearchString);
				mReadXgoo.setSearchString(mCurSearchString);
				doSearch();
			}
		});
		mListView = (ListView) this.findViewById(R.id.listview_sort);
		mFooterView = View.inflate(this, R.layout.listview_footer, null);
		mListView.addFooterView(mFooterView);
		mChessManualListViewAdapter = new ChessManualListViewAdapter(this);
		mListView.setAdapter(mChessManualListViewAdapter);
		mLoadMoreText = (TextView) mFooterView.findViewById(R.id.ask_for_more);
		mLoadMoreGIF = (ProgressBar) mFooterView
				.findViewById(R.id.rectangleProgressBar);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * android.widget.AdapterView.OnItemClickListener#onItemClick(android
			 * .widget.AdapterView, android.view.View, int, long)
			 */
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (arg2 == mListView.getCount() - 1) {
					doLoadMore();
				} else {
					ChessManual chessManual = mChessManuals.get(arg2);
					Intent intent = new Intent(SortActivity.this,
							MainActivity.class);
					intent.putExtra("ChessManual", chessManual);
					startActivity(intent);
				}
			}
		});
		mAdapter = new SinaChessMatchExpandableListViewAdapter(this);
		mExpandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
		mExpandableListView.setAdapter(mAdapter);
		// 设置item点击的监听器
		mExpandableListView
				.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

					@Override
					public boolean onChildClick(ExpandableListView parent,
							View v, int groupPosition, int childPosition,
							long id) {
						setState(MATCH_STATE);
						mCurPage = 0;
						mReadSina.setPage(mCurPage);
						mCurMatchUrlString = mAdapter.getMatchUrl(
								groupPosition, childPosition);
						doLoadMatchChessManual();
						return false;
					}
				});
	}

	/**
	 * Do load match chess manual.
	 */
	private void doLoadMatchChessManual() {
		if (mState == MATCH_STATE) {
			AsyncTaskUtils.create(this, this, R.string.loading).execute(
					MATCH_DOWNLOADING_MANUAL_LIST);
		}
	}

	/**
	 * Do search.
	 */
	private void doSearch() {
		if (mState == SEARCH_STATE) {
			AsyncTaskUtils.create(this, this, R.string.searching).execute(
					SEARCH_DOWNLOADING_MANUAL_LIST);
		}
	}

	/**
	 * Do load more.
	 */
	private void doLoadMore() {
		mLoadMoreGIF.setVisibility(View.VISIBLE);
		mLoadMoreText.setText(getString(R.string.click_to_load_more_running));
		if (mState == SEARCH_STATE) {
			AsyncTaskUtils.create(this, this, false).execute(
					SEARCH_DOWNLOADING_NEXT_MANUAL_LIST);
		} else if (mState == MATCH_STATE) {
			AsyncTaskUtils.create(this, this, false).execute(
					MATCH_DOWNLOADING_NEXT_MANUAL_LIST);
		}
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
		if (flag == SEARCH_DOWNLOADING_MANUAL_LIST) {
			mChessManuals.clear();
			try {
				mChessManuals.addAll(mReadXgoo.getOnlineChessManuals(this));
				return TaskResultCode.CODE_OK;
			} catch (IOException e) {
				e.printStackTrace();
				return TaskResultCode.CODE_ERROR;
			}
		} else if (flag == SEARCH_DOWNLOADING_NEXT_MANUAL_LIST) {
			mReadXgoo.setPage(mCurPage + 1);
			try {
				mChessManuals.addAll(mReadXgoo.getOnlineChessManuals(this));
				return TaskResultCode.CODE_OK;
			} catch (IOException e) {
				e.printStackTrace();
				return TaskResultCode.CODE_ERROR;
			}
		} else if (flag == MATCH_DOWNLOADING_MANUAL_LIST) {
			mChessManuals.clear();
			try {
				mReadSina.setUrl(mCurMatchUrlString);
				mChessManuals.addAll(mReadSina.getOnlineChessManuals(this));
				return TaskResultCode.CODE_OK;
			} catch (IOException e) {
				e.printStackTrace();
				return TaskResultCode.CODE_ERROR;
			}
		} else if (flag == MATCH_DOWNLOADING_NEXT_MANUAL_LIST) {
			mReadSina.setPage(mCurPage + 1);
			try {
				mReadSina.setUrl(mCurMatchUrlString);
				mChessManuals.addAll(mReadSina.getOnlineChessManuals(this));
				return TaskResultCode.CODE_OK;
			} catch (IOException e) {
				e.printStackTrace();
				return TaskResultCode.CODE_ERROR;
			}
		}
		return TaskResultCode.CODE_ERROR;
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
		if (flag == SEARCH_DOWNLOADING_MANUAL_LIST) {
			if ((Integer) result == TaskResultCode.CODE_OK) {
				mListView.setAdapter(new ChessManualListViewAdapter(this,
						mChessManuals));
			} else {
				DialogUtils.showToast(this, R.string.server_connect_error);
			}
		} else if (flag == SEARCH_DOWNLOADING_NEXT_MANUAL_LIST) {
			mLoadMoreGIF.setVisibility(View.GONE);
			mLoadMoreText.setText(getString(R.string.click_to_load_more));
			if ((Integer) result == TaskResultCode.CODE_OK) {
				mCurPage++;
				mChessManualListViewAdapter.setChessManuals(mChessManuals);
				mChessManualListViewAdapter.notifyDataSetChanged();
			} else {
				DialogUtils.showToast(this, R.string.server_connect_error);
			}
		} else if (flag == MATCH_DOWNLOADING_MANUAL_LIST) {
			if ((Integer) result == TaskResultCode.CODE_OK) {
				mListView.setAdapter(new ChessManualListViewAdapter(this,
						mChessManuals));
			} else {
				DialogUtils.showToast(this, R.string.server_connect_error);
			}
		} else if (flag == MATCH_DOWNLOADING_NEXT_MANUAL_LIST) {
			mLoadMoreGIF.setVisibility(View.GONE);
			mLoadMoreText.setText(getString(R.string.click_to_load_more));
			if ((Integer) result == TaskResultCode.CODE_OK) {
				mCurPage++;
				mChessManualListViewAdapter.setChessManuals(mChessManuals);
				mChessManualListViewAdapter.notifyDataSetChanged();
			} else {
				DialogUtils.showToast(this, R.string.server_connect_error);
			}
		}
	}
}
