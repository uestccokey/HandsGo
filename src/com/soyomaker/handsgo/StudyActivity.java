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
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
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

import com.soyomaker.handsgo.adapter.FamousChessManualListViewAdapter;
import com.soyomaker.handsgo.adapter.WeiQiTVChessVideoListViewAdapter;
import com.soyomaker.handsgo.go.ChessManual;
import com.soyomaker.handsgo.go.ChessVideo;
import com.soyomaker.handsgo.net.umeng.ReadUMeng;
import com.soyomaker.handsgo.net.weiqitv.ReadWeiQiTV;
import com.soyomaker.handsgo.util.AsyncTaskUtils;
import com.soyomaker.handsgo.util.AsyncTaskUtils.AsyncTaskListener;
import com.soyomaker.handsgo.util.DialogUtils;
import com.soyomaker.handsgo.view.TitleIndicator;
import com.soyomaker.handsgo.view.TitleIndicator.TitleChangedListener;
import com.umeng.analytics.MobclickAgent;

/**
 * 名局细解界面.
 */
public class StudyActivity extends Activity implements AsyncTaskListener,
		TitleChangedListener {

	/** The load more gif. */
	private ProgressBar mLoadMoreGIF;

	/** The load more text. */
	private TextView mLoadMoreText;

	/** The footer view. */
	private View mFooterView;

	/** The m list view. */
	private ListView mListView;

	/** The m refresh button. */
	private Button mRefreshButton;

	/** The m chess manual list view adapter. */
	private FamousChessManualListViewAdapter mFamousChessManualListViewAdapter;

	private WeiQiTVChessVideoListViewAdapter mWeiQiTVChessVideoListViewAdapter;

	/** The m chess manuals. */
	private ArrayList<ChessManual> mFamousChessManuals = new ArrayList<ChessManual>();

	private ArrayList<ChessVideo> mWeiQiTVChessVideos = new ArrayList<ChessVideo>();

	/** The m handler. */
	protected Handler mHandler;

	/** The Constant REFRESHING_MANUAL_LIST. */
	private static final int REFRESHING_MANUAL_LIST = 1;

	/** The Constant DOWNLOADING_NEXT_MANUAL_LIST. */
	private static final int DOWNLOADING_NEXT_MANUAL_LIST = 2;

	private ReadWeiQiTV mReadWeiQiTV = new ReadWeiQiTV();

	private ReadUMeng mReadUMeng = new ReadUMeng();

	/** The m cur manual source. */
	private int mCurManualSource = AppConstants.STUDY_FAMOUS;

	/** The m indicator view. */
	private TitleIndicator mIndicatorView;

	/** The Constant INDEX_XGOO. */
	private static final int INDEX_FAMOUS = 0;

	/** The Constant INDEX_EWEIQI. */
	private static final int INDEX_WEIQITV = 1;

	/** The m progress view. */
	private View mProgressView;

	/** The m curr idxs. */
	private SparseArray<Integer[]> mCurrIdxs = new SparseArray<Integer[]>(3);

	private Drawable mTransparentSelectorDrawable;

	private Drawable mSelectorDrawable;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.study);
		initView();
		initData();
	}

	/**
	 * Inits the view.
	 */
	private void initView() {
		mHandler = new Handler();
		mListView = (ListView) this.findViewById(R.id.listview_study);
		mFooterView = View.inflate(this, R.layout.listview_footer, null);
		mListView.addFooterView(mFooterView);
		mFamousChessManualListViewAdapter = new FamousChessManualListViewAdapter(
				this);
		mFamousChessManualListViewAdapter.setChessManuals(mFamousChessManuals);
		mWeiQiTVChessVideoListViewAdapter = new WeiQiTVChessVideoListViewAdapter(
				this);
		mWeiQiTVChessVideoListViewAdapter.setChessVideos(mWeiQiTVChessVideos);
		mListView.setAdapter(mFamousChessManualListViewAdapter);
		mLoadMoreText = (TextView) mFooterView.findViewById(R.id.ask_for_more);
		mLoadMoreGIF = (ProgressBar) mFooterView
				.findViewById(R.id.rectangleProgressBar);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == mListView.getCount() - 1) {
					doLoadMore();
				} else {
					switch (mCurManualSource) {
					case AppConstants.STUDY_FAMOUS:
						if (mFamousChessManuals != null && position >= 0
								&& position < mFamousChessManuals.size()) {
							ChessManual chessManual = mFamousChessManuals
									.get(position);
							if (chessManual != null) {
								Intent intent = new Intent(StudyActivity.this,
										MainActivity.class);
								intent.putExtra("ChessManual", chessManual);
								startActivity(intent);
							}
						}
						break;
					case AppConstants.STUDY_WEIQITV:
						// 点击事件在adapter中处理
						break;
					}
				}
			}

		});
		mRefreshButton = (Button) this.findViewById(R.id.study_title_refresh);
		mRefreshButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				initData();
			}
		});
		mIndicatorView = (TitleIndicator) findViewById(R.id.source_indicator);
		List<String> titles = new ArrayList<String>();
		String[] strings = getResources().getStringArray(R.array.study_source);
		titles.add(INDEX_FAMOUS, strings[0]);
		titles.add(INDEX_WEIQITV, strings[1]);
		mIndicatorView.init(titles, this);
		mProgressView = findViewById(R.id.refreshing_progress_study);
		mTransparentSelectorDrawable = new ColorDrawable(Color.TRANSPARENT);
		mSelectorDrawable = getResources().getDrawable(
				R.drawable.list_item_bg_selected);
		mReadWeiQiTV.setType(ReadWeiQiTV.CHAO_HAO_YONG_BU_JU);
	}

	/**
	 * Do load more.
	 */
	private void doLoadMore() {
		switch (mCurManualSource) {
		case AppConstants.STUDY_FAMOUS:
			mLoadMoreGIF.setVisibility(View.VISIBLE);
			mLoadMoreText
					.setText(getString(R.string.click_to_load_more_running));
			AsyncTaskUtils.create(this, this, false).execute(
					DOWNLOADING_NEXT_MANUAL_LIST);
			break;
		case AppConstants.STUDY_WEIQITV:
			if (mReadWeiQiTV.getPage() >= mReadWeiQiTV.getTotalPage()) {
				mLoadMoreGIF.setVisibility(View.GONE);
				mLoadMoreText.setText("没有更多了");
			} else {
				mLoadMoreGIF.setVisibility(View.VISIBLE);
				mLoadMoreText
						.setText(getString(R.string.click_to_load_more_running));
				AsyncTaskUtils.create(this, this, false).execute(
						DOWNLOADING_NEXT_MANUAL_LIST);
			}
			break;
		}
	}

	/**
	 * Inits the data.
	 */
	private void initData() {
		switch (mCurManualSource) {
		case AppConstants.STUDY_FAMOUS:
			break;
		case AppConstants.STUDY_WEIQITV:
			mReadWeiQiTV.setPage(1);
			break;
		}
		mProgressView.setVisibility(View.VISIBLE);
		AsyncTaskUtils.create(this, this, false)
				.execute(REFRESHING_MANUAL_LIST);
	}

	/**
	 * On resume.
	 */
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		switch (mCurManualSource) {
		case AppConstants.STUDY_FAMOUS:
			mFamousChessManualListViewAdapter.notifyDataSetChanged();
			break;
		case AppConstants.STUDY_WEIQITV:
			mWeiQiTVChessVideoListViewAdapter.notifyDataSetChanged();
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
				case AppConstants.STUDY_FAMOUS:
					return mReadUMeng.getOnlineChessManuals(this);
				case AppConstants.STUDY_WEIQITV:
					return mReadWeiQiTV.getOnlineChessVideos(this);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (flag == DOWNLOADING_NEXT_MANUAL_LIST) {
			try {
				switch (mCurManualSource) {
				case AppConstants.STUDY_FAMOUS:
					mReadUMeng.setPage(mReadUMeng.getPage() + 1);
					mFamousChessManuals.addAll(mReadUMeng
							.getOnlineChessManuals(this));
					return mReadUMeng.getOnlineChessManuals(this);
				case AppConstants.STUDY_WEIQITV:
					mReadWeiQiTV.setPage(mReadWeiQiTV.getPage() + 1);
					return mReadWeiQiTV.getOnlineChessVideos(this);
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
				case AppConstants.STUDY_FAMOUS:
					mFamousChessManuals.clear();
					mFamousChessManuals.addAll((ArrayList<ChessManual>) result);
					mFamousChessManualListViewAdapter.notifyDataSetChanged();
					break;
				case AppConstants.STUDY_WEIQITV:
					mWeiQiTVChessVideos.clear();
					mWeiQiTVChessVideos.addAll((ArrayList<ChessVideo>) result);
					mWeiQiTVChessVideoListViewAdapter.notifyDataSetChanged();
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
				case AppConstants.STUDY_FAMOUS:
					mFamousChessManuals.addAll((ArrayList<ChessManual>) result);
					mFamousChessManualListViewAdapter.notifyDataSetChanged();
					break;
				case AppConstants.STUDY_WEIQITV:
					mWeiQiTVChessVideos.addAll((ArrayList<ChessVideo>) result);
					mWeiQiTVChessVideoListViewAdapter.notifyDataSetChanged();
					break;
				}
			} else {
				DialogUtils.showToast(this, R.string.server_connect_error);
			}
		}
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
	private void updateListViewChessManual(int source,
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

	private void updateListViewChessVideo(int source,
			ArrayList<ChessVideo> chessVideos, BaseAdapter adapter) {
		mListView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		if (chessVideos != null && !chessVideos.isEmpty()) {
			mListView.setSelectionFromTop(mCurrIdxs.get(source)[0],
					mCurrIdxs.get(source)[1]);
		} else {
			initData();
		}
	}

	@Override
	public void onTitleChanged(int nowPos, int lastPos) {
		mCurManualSource = nowPos;
		int index = mListView.getFirstVisiblePosition();
		View v = mListView.getChildAt(index);
		int top = (v == null) ? 0 : v.getTop();
		mCurrIdxs.put(Integer.valueOf(lastPos),
				new Integer[] { mListView.getFirstVisiblePosition(), top });
		switch (mCurManualSource) {
		case AppConstants.STUDY_FAMOUS:
			mListView.setSelector(mSelectorDrawable);
			updateListViewChessManual(mCurManualSource, mFamousChessManuals,
					mFamousChessManualListViewAdapter);
			break;
		case AppConstants.STUDY_WEIQITV:
			mListView.setSelector(mTransparentSelectorDrawable);
			updateListViewChessVideo(mCurManualSource, mWeiQiTVChessVideos,
					mWeiQiTVChessVideoListViewAdapter);
			break;
		}
	}
}
