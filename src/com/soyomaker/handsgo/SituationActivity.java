/*
 * 
 */
package com.soyomaker.handsgo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.soyomaker.handsgo.adapter.ChessManualListViewAdapter;
import com.soyomaker.handsgo.go.ChessManual;
import com.soyomaker.handsgo.net.sina.ReadSina;
import com.soyomaker.handsgo.net.tom.ReadTom;
import com.soyomaker.handsgo.net.xgoo.ReadXGOO;
import com.soyomaker.handsgo.util.AsyncTaskUtils;
import com.soyomaker.handsgo.util.AsyncTaskUtils.AsyncTaskListener;
import com.soyomaker.handsgo.util.DialogUtils;
import com.soyomaker.handsgo.view.TitleIndicator;
import com.soyomaker.handsgo.view.TitleIndicator.TitleChangedListener;
import com.umeng.analytics.MobclickAgent;

/**
 * 时局界面.
 * 
 * @author cokey
 */
public class SituationActivity extends Activity implements AsyncTaskListener,
		TitleChangedListener {

	/** The load more gif. */
	private ProgressBar mLoadMoreGIF;

	/** The load more text. */
	private TextView mLoadMoreText;

	/** The footer view. */
	private View mFooterView;

	private View mAppTipsView;

	private TextView mAppTipsTextView;

	private Button mAppTipsHideButton;

	/** The m list view. */
	private ListView mListView;

	/** The m refresh button. */
	private Button mRefreshButton;

	/** The m chess manuals. */
	private ArrayList<ChessManual> mXgooChessManuals = new ArrayList<ChessManual>();

	/** The m chess manuals. */
	private ArrayList<ChessManual> mSinaChessManuals = new ArrayList<ChessManual>();

	/** The m chess manuals. */
	private ArrayList<ChessManual> mTomChessManuals = new ArrayList<ChessManual>();

	/** The Constant REFRESHING_MANUAL_LIST. */
	private static final int REFRESHING_MANUAL_LIST = 1;

	/** The Constant DOWNLOADING_NEXT_MANUAL_LIST. */
	private static final int DOWNLOADING_NEXT_MANUAL_LIST = 2;

	/** The m read xgoo. */
	private ReadXGOO mReadXgoo = new ReadXGOO();

	/** The m read sina. */
	private ReadSina mReadSina = new ReadSina();

	/** The m read tom. */
	private ReadTom mReadTom = new ReadTom();

	/** The chess manual list view adapter. */
	private ChessManualListViewAdapter mXgooChessManualListViewAdapter;

	/** The m sina chess manual list view adapter. */
	private ChessManualListViewAdapter mSinaChessManualListViewAdapter;

	/** The m tom chess manual list view adapter. */
	private ChessManualListViewAdapter mTomChessManualListViewAdapter;

	/** The m cur manual source. */
	private int mCurManualSource = AppConstants.XGOO;

	/** The m indicator view. */
	private TitleIndicator mIndicatorView;

	/** The Constant INDEX_XGOO. */
	private static final int INDEX_XGOO = 0;

	/** The Constant INDEX_SINA. */
	private static final int INDEX_SINA = 1;

	/** The Constant INDEX_TOM. */
	private static final int INDEX_TOM = 2;

	/** The m progress view. */
	private View mProgressView;

	/** The m curr idxs. */
	private SparseArray<Integer[]> mCurrIdxs = new SparseArray<Integer[]>(4);

	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState
	 *            the saved instance state
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.situation);
		initView();
		initData();
	}

	/**
	 * On resume.
	 */
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		switch (mCurManualSource) {
		case AppConstants.XGOO:
			mXgooChessManualListViewAdapter.notifyDataSetChanged();
			break;
		case AppConstants.SINA:
			mSinaChessManualListViewAdapter.notifyDataSetChanged();
			break;
		case AppConstants.TOM:
			mTomChessManualListViewAdapter.notifyDataSetChanged();
			break;
		}
	}

	/**
	 * On pause.
	 */
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	/**
	 * Inits the view.
	 */
	private void initView() {
		mAppTipsView = this.findViewById(R.id.app_tips_layout);
		String appTipsVersionString = MobclickAgent.getConfigParams(this,
				Constant.APP_TIPS_VERSION);
		if (!TextUtils.isEmpty(appTipsVersionString)
				&& TextUtils.isDigitsOnly(appTipsVersionString)) {
			int appTipsVersion = Integer.parseInt(appTipsVersionString);
			int localAppTipsVersion = AppPrefrence.getAppTipsVersion(this);
			// 假如检测到服务器tips的版本更高，则显示tips
			if (appTipsVersion > localAppTipsVersion) {
				AppPrefrence.saveAppTipsHide(this, false);
				AppPrefrence.saveAppTipsVersion(this, appTipsVersion);
			}
		}
		if (!AppPrefrence.getAppTipsHide(this)) {
			String appTipsContentString = MobclickAgent.getConfigParams(this,
					Constant.APP_TIPS_CONTENT);
			if (!TextUtils.isEmpty(appTipsContentString)) {
				mAppTipsView.setVisibility(View.VISIBLE);
				mAppTipsTextView = (TextView) this
						.findViewById(R.id.app_tips_content);
				mAppTipsTextView.setText(appTipsContentString);
				mAppTipsHideButton = (Button) this
						.findViewById(R.id.app_tips_hide_btn);
				mAppTipsHideButton
						.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View arg0) {
								AppPrefrence.saveAppTipsHide(
										SituationActivity.this, true);
								mAppTipsView.setVisibility(View.GONE);
							}
						});
			}
		}
		mListView = (ListView) this.findViewById(R.id.listview_situation);
		mFooterView = View.inflate(this, R.layout.listview_footer, null);
		mListView.addFooterView(mFooterView);
		mXgooChessManualListViewAdapter = new ChessManualListViewAdapter(this);
		mXgooChessManualListViewAdapter.setChessManuals(mXgooChessManuals);
		mSinaChessManualListViewAdapter = new ChessManualListViewAdapter(this);
		mSinaChessManualListViewAdapter.setChessManuals(mSinaChessManuals);
		mTomChessManualListViewAdapter = new ChessManualListViewAdapter(this);
		mTomChessManualListViewAdapter.setChessManuals(mTomChessManuals);
		mListView.setAdapter(mXgooChessManualListViewAdapter);
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
					ArrayList<ChessManual> chessManuals = null;
					ChessManual chessManual = null;
					switch (mCurManualSource) {
					case AppConstants.XGOO:
						chessManuals = mXgooChessManuals;
						break;
					case AppConstants.SINA:
						chessManuals = mSinaChessManuals;
						break;
					case AppConstants.TOM:
						chessManuals = mTomChessManuals;
						break;
					}
					if (chessManuals != null && arg2 >= 0
							&& arg2 < chessManuals.size()) {
						chessManual = chessManuals.get(arg2);
						if (chessManual != null) {
							Intent intent = new Intent(SituationActivity.this,
									MainActivity.class);
							intent.putExtra("ChessManual", chessManual);
							startActivity(intent);
						}
					}

				}
			}
		});
		mRefreshButton = (Button) this
				.findViewById(R.id.situation_title_refresh);
		mRefreshButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				initData();
			}
		});
		mIndicatorView = (TitleIndicator) findViewById(R.id.source_indicator);
		List<String> titles = new ArrayList<String>();
		String[] strings = getResources().getStringArray(R.array.manual_source);
		titles.add(INDEX_XGOO, strings[0]);
		titles.add(INDEX_SINA, strings[1]);
		titles.add(INDEX_TOM, strings[2]);
		mIndicatorView.init(titles, this);
		mProgressView = findViewById(R.id.refreshing_progress_situation);
	}

	/**
	 * Do load more.
	 */
	private void doLoadMore() {
		mLoadMoreGIF.setVisibility(View.VISIBLE);
		mLoadMoreText.setText(getString(R.string.click_to_load_more_running));
		AsyncTaskUtils.create(this, this, false).execute(
				DOWNLOADING_NEXT_MANUAL_LIST);
	}

	/**
	 * Inits the data.
	 */
	private void initData() {
		switch (mCurManualSource) {
		case AppConstants.XGOO:
			mReadXgoo.setPage(1);
			break;
		case AppConstants.SINA:
			mReadSina
					.setUrl("http://duiyi.sina.com.cn/gibo/new_gibo.asp?cur_page=%d");
			mReadSina.setPage(0);
			break;
		case AppConstants.TOM:
			mReadTom.setPage(0);
			break;
		}
		mProgressView.setVisibility(View.VISIBLE);
		AsyncTaskUtils.create(this, this, false)
				.execute(REFRESHING_MANUAL_LIST);
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
		if (flag == REFRESHING_MANUAL_LIST) {
			try {
				switch (mCurManualSource) {
				case AppConstants.XGOO:
					return mReadXgoo.getOnlineChessManuals(this);
				case AppConstants.SINA:
					return mReadSina.getOnlineChessManuals(this);
				case AppConstants.TOM:
					return mReadTom.getOnlineChessManuals(this);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (flag == DOWNLOADING_NEXT_MANUAL_LIST) {
			try {
				switch (mCurManualSource) {
				case AppConstants.XGOO:
					mReadXgoo.setPage(mReadXgoo.getPage() + 1);
					return mReadXgoo.getOnlineChessManuals(this);
				case AppConstants.SINA:
					mReadSina.setPage(mReadSina.getPage() + 1);
					return mReadSina.getOnlineChessManuals(this);
				case AppConstants.TOM:
					mReadTom.setPage(mReadTom.getPage() + 1);
					return mReadTom.getOnlineChessManuals(this);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
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
		if (flag == REFRESHING_MANUAL_LIST) {
			mProgressView.setVisibility(View.GONE);
			if (result != null) {
				switch (mCurManualSource) {
				case AppConstants.XGOO:
					mXgooChessManuals.clear();
					mXgooChessManuals.addAll((ArrayList<ChessManual>) result);
					mXgooChessManualListViewAdapter.notifyDataSetChanged();
					break;
				case AppConstants.SINA:
					mSinaChessManuals.clear();
					mSinaChessManuals.addAll((ArrayList<ChessManual>) result);
					mSinaChessManualListViewAdapter.notifyDataSetChanged();
					break;
				case AppConstants.TOM:
					mTomChessManuals.clear();
					mTomChessManuals.addAll((ArrayList<ChessManual>) result);
					mTomChessManualListViewAdapter.notifyDataSetChanged();
					break;
				}
			} else {
				DialogUtils.showToast(this, R.string.server_connect_error);
			}
		} else if (flag == DOWNLOADING_NEXT_MANUAL_LIST) {
			mLoadMoreGIF.setVisibility(View.GONE);
			mLoadMoreText.setText(getString(R.string.click_to_load_more));
			if (result != null) {
				switch (mCurManualSource) {
				case AppConstants.XGOO:
					mXgooChessManuals.addAll((ArrayList<ChessManual>) result);
					mXgooChessManualListViewAdapter.notifyDataSetChanged();
					break;
				case AppConstants.SINA:
					mSinaChessManuals.addAll((ArrayList<ChessManual>) result);
					mSinaChessManualListViewAdapter.notifyDataSetChanged();
					break;
				case AppConstants.TOM:
					mTomChessManuals.addAll((ArrayList<ChessManual>) result);
					mTomChessManualListViewAdapter.notifyDataSetChanged();
					break;
				}
			} else {
				DialogUtils.showToast(this, R.string.server_connect_error);
			}
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

	/**
	 * Update list view data.
	 * 
	 * @param source
	 *            the source
	 * @param chessManuals
	 *            the chess manuals
	 * @param adapter
	 *            the adapter
	 */
	private void updateListViewData(int source,
			ArrayList<ChessManual> chessManuals, BaseAdapter adapter) {
		mListView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		if (chessManuals != null && !chessManuals.isEmpty()) {
			mListView.setSelectionFromTop(mCurrIdxs.get(source)[0],
					mCurrIdxs.get(source)[1]);
		} else {
			initData();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.soyomaker.handsgo.view.TitleIndicator.TitleChangedListener#onTitleChanged
	 * (int, int)
	 */
	@Override
	public void onTitleChanged(int nowPos, int lastPos) {
		mCurManualSource = nowPos;
		int index = mListView.getFirstVisiblePosition();
		View v = mListView.getChildAt(index);
		int top = (v == null) ? 0 : v.getTop();
		mCurrIdxs.put(Integer.valueOf(lastPos),
				new Integer[] { mListView.getFirstVisiblePosition(), top });
		switch (mCurManualSource) {
		case AppConstants.XGOO:
			updateListViewData(mCurManualSource, mXgooChessManuals,
					mXgooChessManualListViewAdapter);
			break;
		case AppConstants.SINA:
			updateListViewData(mCurManualSource, mSinaChessManuals,
					mSinaChessManualListViewAdapter);
			break;
		case AppConstants.TOM:
			updateListViewData(mCurManualSource, mTomChessManuals,
					mTomChessManualListViewAdapter);
			break;
		}
	}
}