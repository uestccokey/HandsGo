/*
 * 
 */
package com.soyomaker.handsgo;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.soyomaker.handsgo.adapter.SDCardListViewAdapter;
import com.soyomaker.handsgo.go.ChessManual;
import com.soyomaker.handsgo.task.GenericTask;
import com.soyomaker.handsgo.task.TaskParams;
import com.soyomaker.handsgo.task.TaskResult;
import com.soyomaker.handsgo.util.DialogUtils;
import com.soyomaker.handsgo.util.StringUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * The Class SDCardActivity.
 */
public class SDCardActivity extends Activity {

	/** The Constant VIEW_SDCARD. */
	private static final int VIEW_SDCARD = 0;

	/** The Constant VIEW_SCAN. */
	private static final int VIEW_SCAN = 1;

	/** The m file name. */
	private List<String> mFileName = null;

	/** The m file paths. */
	private List<File> mFilePaths = null;

	/** The m root path. */
	private String mRootPath = Environment.getExternalStorageDirectory()
			.getParent();

	/** The m path text view. */
	private TextView mPathTextView;

	/** The m current file path. */
	private String mCurrentFilePath = "";// 当前目录路径信息

	/** The m list view. */
	private ListView mListView;

	/** The m scan button. */
	private Button mScanButton;

	/** The m state. */
	private int mState = VIEW_SDCARD;

	/** The m handler. */
	private Handler mHandler;

	/** The m scan file adapter. */
	private SDCardListViewAdapter mScanFileAdapter;

	/** The m txtfile names. */
	private List<String> mSgfFileNames = new ArrayList<String>();

	/** The m txtfile paths. */
	private List<File> mSgfFilePaths = new ArrayList<File>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sdcard);
		mHandler = new Handler();
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
		mPathTextView = (TextView) findViewById(R.id.path_text);
		mListView = (ListView) findViewById(R.id.file_list);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (mState == VIEW_SDCARD) {
					if (position >= 0 && position < mFilePaths.size()) {
						File file = mFilePaths.get(position);
						if (file.isDirectory()) {// 如果是文件夹，则直接进入该文件夹，查看文件目录
							initFileListInfo(file.getAbsolutePath());
						} else {// 如果是文件，则用相应的打开方式打开
							openFile(file);
						}
					}
				} else if (mState == VIEW_SCAN) {
					if (position >= 0 && position < mSgfFilePaths.size()) {
						File file = mSgfFilePaths.get(position);
						if (file.isDirectory()) {// 如果是文件夹，则直接进入该文件夹，查看文件目录
							initFileListInfo(file.getAbsolutePath());
						} else {// 如果是文件，则用相应的打开方式打开
							openFile(file);
						}
					}
				}
			}

		});
		mScanButton = (Button) findViewById(R.id.scan_btn);
		mScanButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mState = VIEW_SCAN;
				mSgfFileNames.clear();
				mSgfFilePaths.clear();
				mScanButton.setVisibility(View.INVISIBLE);
				mScanFileAdapter = new SDCardListViewAdapter(
						SDCardActivity.this, mSgfFileNames, mSgfFilePaths);
				mListView.setAdapter(mScanFileAdapter);
				scanFile();
			}
		});
		initFileListInfo(mRootPath);
	}

	/**
	 * Scan file.
	 */
	private void scanFile() {
		final GenericTask scanTask = new GenericTask() {
			int sgfFileCount = 0;

			@Override
			protected TaskResult doInBackground(TaskParams... params) {
				refreshScanResults(mCurrentFilePath);
				return null;
			}

			private void refreshScanResults(String nowDir) {
				File file = new File(nowDir);
				File[] files = file.listFiles(new SGFOrDIRSelector());
				for (final File f : files) {
					if (isCancelled()) {
						return;
					}
					if (f.isDirectory()) {
						refreshScanResults(f.getAbsolutePath());
					} else {
						mHandler.post(new Runnable() {

							@Override
							public void run() {
								if (f.getName().endsWith(".sgf")) {
									sgfFileCount++;
									mSgfFileNames.add(f.getName());
									mSgfFilePaths.add(f);
								}
								mScanFileAdapter.notifyDataSetChanged();
								DialogUtils.updateProgressDialog(String.format(
										"扫描中\n发现%d个sgf文件", sgfFileCount));
							}
						});
					}
				}
			}

			protected void onPostExecute(TaskResult result) {
				DialogUtils.dismissProgressDialog();
			}
		};
		DialogUtils
				.showProgressDialog(SDCardActivity.this, String.format(
						getResources().getString(R.string.scaning), 0, 0),
						true, new OnCancelListener() {

							@Override
							public void onCancel(DialogInterface dialog) {
								if (scanTask != null) {
									scanTask.cancel(true);
								}
							}

						}, null);
		scanTask.execute();
	}

	/**
	 * 调用系统的方法，来打开文件的方法.
	 * 
	 * @param file
	 *            the file
	 */
	private void openFile(File file) {
		long time = System.currentTimeMillis();
		String charset = StringUtil.getCharset(file);
		Log.e("SDCard",
				"分析字符集耗时:" + charset + " "
						+ (System.currentTimeMillis() - time));
		ChessManual chessManual = new ChessManual();
		chessManual.setCharset(charset);
		chessManual.setType(ChessManual.SDCARD_CHESS_MANUAL);
		chessManual.setSgfUrl(file.getAbsolutePath());
		Intent intent = new Intent(SDCardActivity.this, MainActivity.class);
		intent.putExtra("ChessManual", chessManual);
		startActivity(intent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#dispatchKeyEvent(android.view.KeyEvent)
	 */
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if (mState == VIEW_SCAN) {
				mState = VIEW_SDCARD;
				mScanButton.setVisibility(View.VISIBLE);
				initFileListInfo(mCurrentFilePath);
			} else if (mState == VIEW_SDCARD) {
				File file = new File(mCurrentFilePath);
				if (file.getParent() != null
						&& !file.getAbsolutePath().equals(mRootPath)) {
					initFileListInfo(file.getParent());
				} else {
					finish();
				}
			}
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	/**
	 * 根据给定的一个文件夹路径字符串遍历出这个文 件夹中包含的文件名称并配置到ListView列表中.
	 * 
	 * @param filePath
	 *            the file path
	 */
	private void initFileListInfo(String filePath) {
		mCurrentFilePath = filePath;
		mPathTextView.setText(filePath);
		mFileName = new ArrayList<String>();
		mFilePaths = new ArrayList<File>();
		File mFile = new File(filePath);
		File[] mFiles = mFile.listFiles(new SGFOrDIRSelector());

		if (!mCurrentFilePath.equals(mRootPath)) {
			initAddBackUp(filePath, mRootPath);
		}

		/* 将所有文件信息添加到集合中 */
		for (File mCurrentFile : mFiles) {
			mFileName.add(mCurrentFile.getName());
			mFilePaths.add(mCurrentFile);
		}

		/* 适配数据 */
		mListView.setAdapter(new SDCardListViewAdapter(SDCardActivity.this,
				mFileName, mFilePaths));
	}

	/**
	 * 根据点击“手机”还是“SD卡”来加“返回根目录”和“返回上一级”.
	 * 
	 * @param filePath
	 *            the file path
	 * @param phone_sdcard
	 *            the phone_sdcard
	 */
	private void initAddBackUp(String filePath, String phone_sdcard) {
		if (!filePath.equals(phone_sdcard)) {
			mFileName.add("BacktoUp");
			mFilePaths.add(new File(filePath).getParentFile());
		}
	}

	/**
	 * The Class FileNameSelector.
	 */
	class SGFOrDIRSelector implements FileFilter {

		/** The sgf end. */
		private static final String SGF_END = ".sgf";
		/** 支持0KB~2048KB的文件. */
		private static final int MIN_FILE_LENGTH_LIMIT = 0;
		private static final int MAX_FILE_LENGTH_LIMIT = 2 * 1024 * 1024;

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.io.FileFilter#accept(java.io.File)
		 */
		@Override
		public boolean accept(File pathname) {

			if (pathname.isDirectory() && pathname.canRead()) {
				return true;
			}
			if (pathname.isFile() && pathname.canRead()) {
				if (pathname.length() > MIN_FILE_LENGTH_LIMIT
						&& pathname.length() < MAX_FILE_LENGTH_LIMIT
						&& pathname.getName().toLowerCase().endsWith(SGF_END)) {
					return true;
				}
			}
			return false;
		}

	}
}
