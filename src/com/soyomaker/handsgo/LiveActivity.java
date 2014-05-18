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
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.soyomaker.handsgo.adapter.ChessManualListViewAdapter;
import com.soyomaker.handsgo.go.ChessManual;
import com.soyomaker.handsgo.net.yygo.ReadYYGO;
import com.soyomaker.handsgo.util.AsyncTaskUtils;
import com.soyomaker.handsgo.util.AsyncTaskUtils.AsyncTaskListener;
import com.soyomaker.handsgo.util.DialogUtils;
import com.soyomaker.handsgo.view.TitleIndicator;
import com.soyomaker.handsgo.view.TitleIndicator.TitleChangedListener;
import com.umeng.analytics.MobclickAgent;

/**
 * 直播界面.
 * 
 * @author cokey
 */
public class LiveActivity extends Activity implements AsyncTaskListener,
		TitleChangedListener {

	/** The m list view. */
	private ListView mListView;

	/** The m refresh button. */
	private Button mRefreshButton;

	/** The Constant REFRESHING_MANUAL_LIST. */
	private static final int REFRESHING_MANUAL_LIST = 1;

	/** The m chess manuals. */
	private ArrayList<ChessManual> mChessManuals = new ArrayList<ChessManual>();

	/** The m chess manual list view adapter. */
	private ChessManualListViewAdapter mChessManualListViewAdapter;

	/** The m progress view. */
	private View mProgressView;

	private ReadYYGO mReadYygo = new ReadYYGO();

	/** The m cur manual source. */
	private int mCurManualSource = AppConstants.LIVE_TOM;

	/** The m curr idxs. */
	private SparseArray<Integer[]> mCurrIdxs = new SparseArray<Integer[]>(2);

	/** The m indicator view. */
	private TitleIndicator mIndicatorView;

	/** The Constant INDEX_XGOO. */
	private static final int INDEX_TOM = 0;

	private Drawable mTransparentSelectorDrawable;

	private Drawable mSelectorDrawable;

	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState
	 *            the saved instance state
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.live);
		initView();
		initData();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		switch (mCurManualSource) {
		case AppConstants.LIVE_TOM:
			mChessManualListViewAdapter.notifyDataSetChanged();
			break;
		}
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

	/**
	 * Inits the view.
	 */
	private void initView() {
		mListView = (ListView) this.findViewById(R.id.listview_live);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				switch (mCurManualSource) {
				case AppConstants.LIVE_TOM:
					if (mChessManuals != null && position >= 0
							&& position < mChessManuals.size()) {
						ChessManual chessManual = mChessManuals.get(position);
						if (chessManual != null) {
							Intent intent = new Intent(LiveActivity.this,
									MainActivity.class);
							intent.putExtra("ChessManual", chessManual);
							startActivity(intent);
						}
					}
					break;
				}
			}

		});
		mChessManualListViewAdapter = new ChessManualListViewAdapter(this);
		mChessManualListViewAdapter.setChessManuals(mChessManuals);
		mListView.setAdapter(mChessManualListViewAdapter);
		mRefreshButton = (Button) this.findViewById(R.id.live_title_refresh);
		mRefreshButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				initData();
			}
		});
		mProgressView = findViewById(R.id.refreshing_progress_live);
		mIndicatorView = (TitleIndicator) findViewById(R.id.source_indicator);
		List<String> titles = new ArrayList<String>();
		String[] strings = getResources().getStringArray(R.array.live_source);
		titles.add(INDEX_TOM, strings[0]);
		mIndicatorView.init(titles, this);
		mTransparentSelectorDrawable = new ColorDrawable(Color.TRANSPARENT);
		mSelectorDrawable = getResources().getDrawable(
				R.drawable.list_item_bg_selected);
	}

	/**
	 * Do load more.
	 */
	private void doLoadMore() {
		switch (mCurManualSource) {
		case AppConstants.LIVE_TOM:
			break;
		}
	}

	/**
	 * Inits the data.
	 */
	private void initData() {
		switch (mCurManualSource) {
		case AppConstants.LIVE_TOM:
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
				case AppConstants.LIVE_TOM:
					return mReadYygo.getOnlineChessManuals(this);
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
				case AppConstants.LIVE_TOM:
					mChessManuals.clear();
					mChessManuals.addAll((ArrayList<ChessManual>) result);
					mChessManualListViewAdapter.notifyDataSetChanged();
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

	@Override
	public void onTitleChanged(int nowPos, int lastPos) {
		mCurManualSource = nowPos;
		int index = mListView.getFirstVisiblePosition();
		View v = mListView.getChildAt(index);
		int top = (v == null) ? 0 : v.getTop();
		mCurrIdxs.put(Integer.valueOf(lastPos),
				new Integer[] { mListView.getFirstVisiblePosition(), top });
		switch (mCurManualSource) {
		case AppConstants.LIVE_TOM:
			mListView.setSelector(mSelectorDrawable);
			updateListViewChessManual(mCurManualSource, mChessManuals,
					mChessManualListViewAdapter);
			break;
		}
	}
}