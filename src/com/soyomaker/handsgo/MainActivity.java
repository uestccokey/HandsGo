/*
 * 
 */
package com.soyomaker.handsgo;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.soyomaker.handsgo.db.DBService;
import com.soyomaker.handsgo.go.ChessManual;
import com.soyomaker.handsgo.go.DefaultBoardModel;
import com.soyomaker.handsgo.go.GoBoard;
import com.soyomaker.handsgo.go.GoException;
import com.soyomaker.handsgo.go.GoPoint;
import com.soyomaker.handsgo.go.Group;
import com.soyomaker.handsgo.go.Match;
import com.soyomaker.handsgo.go.sgf.Action;
import com.soyomaker.handsgo.go.sgf.Change;
import com.soyomaker.handsgo.go.sgf.Field;
import com.soyomaker.handsgo.go.sgf.ListElement;
import com.soyomaker.handsgo.go.sgf.Node;
import com.soyomaker.handsgo.go.sgf.Position;
import com.soyomaker.handsgo.go.sgf.SGFTree;
import com.soyomaker.handsgo.go.sgf.TreeNode;
import com.soyomaker.handsgo.util.AsyncTaskUtils;
import com.soyomaker.handsgo.util.AsyncTaskUtils.AsyncTaskListener;
import com.soyomaker.handsgo.util.DialogUtils;
import com.soyomaker.handsgo.util.DialogUtils.ItemSelectedListener;
import com.soyomaker.handsgo.util.PlaySoundPool;
import com.soyomaker.handsgo.util.StringUtil;
import com.soyomaker.handsgo.util.WebUtil;
import com.soyomaker.handsgo.view.ColorPickerDialog;
import com.soyomaker.handsgo.view.ColorPickerDialog.OnColorChangedListener;
import com.soyomaker.handsgo.view.PopMenuView;
import com.umeng.analytics.MobclickAgent;

/**
 * The Class ManualActivity.
 */
public class MainActivity extends Activity implements AsyncTaskListener {

	private static final String TAG = "MainActivity";

	private static final String PICTURE_PATH = Environment
			.getExternalStorageDirectory().toString() + "/handsgo/screenshot/";

	private GoBoard board;

	private DefaultBoardModel boardModel;

	private Match match;

	private Button prevVar;

	private Button nextVar;

	private Button nextStep;

	private Button prevStep;

	private Button fastNextStep;

	private Button fastPrevStep;

	private Button firstStep;

	private Button lastStep;

	private RelativeLayout mBoardLayout;

	private TextView mBlackNameTextView;

	private TextView mWhiteNameTextView;

	private TextView mMatchNameTextView;

	private TextView mMatchCommentTextView;

	private Button mSettingButton;

	private ChessManual mChessManual;

	private static final int DOWNLOADING_CHESS_MANUAL_FLAG = 0;

	private PopMenuView mPopMenuView;

	public static final int CHESSMANUAL_INFO = 0;

	public static final int SHOW_NUMBER = 1;

	public static final int SHOW_COORDINATE = 2;

	public static final int LOCAL_FAVORITE = 3;

	public static final int AUTO_NEXT = 4;

	public static final int LAZI_SOUND = 5;

	public static final int SHARE_CHESSMANUAL = 6;

	private static final int CHESS_BOARD_COLOR = 7;

	private static final int CHESS_PIECE_STYLE = 8;

	private static final String[] SETTINGS_STRINGS = new String[] { "胜负信息",
			"手数显示", "坐标显示", "本地收藏", "自动打谱", "落子音效", "分享棋谱", "棋盘颜色", "棋子样式" };

	protected Handler mHandler;

	protected PowerManager mPowerManager;

	protected WakeLock mWakeLock;

	protected PlaySoundPool mPlaySoundPool;

	private View mProgressView;

	private ColorPickerDialog mChessBoardColorPickerDialog;

	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState
	 *            the saved instance state
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manual);
		this.mPowerManager = (PowerManager) this
				.getSystemService(Context.POWER_SERVICE);
		this.mWakeLock = this.mPowerManager.newWakeLock(
				PowerManager.FULL_WAKE_LOCK, "My Lock");
		this.mPlaySoundPool = new PlaySoundPool(this);
		this.mPlaySoundPool.loadSfx(R.raw.stone, 1);
		initView();
		initData();
		doAutoNext();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		mWakeLock.acquire();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	public void onPause() {
		super.onPause();
		mHandler.removeCallbacks(doAutoNextRunnable);
		MobclickAgent.onPause(this);
		mWakeLock.release();
	}

	/** The do auto next runnable. */
	private Runnable doAutoNextRunnable = new Runnable() {

		@Override
		public void run() {
			boolean flag = AppPrefrence.getAutoNext(MainActivity.this);
			if (flag) {
				boolean hasNext = next();
				boolean flag2 = AppPrefrence.getLazySound(MainActivity.this);
				if (flag2 && hasNext) {
					mPlaySoundPool.play(1, 0);
				}
				board.postInvalidate();
				doAutoNext();
			}
		}
	};

	/**
	 * Do auto next.
	 */
	private void doAutoNext() {
		String interval = AppPrefrence.getAutoNextInterval(this);
		int intervalInt = 2000;
		try {
			intervalInt = Integer.valueOf(interval);
		} catch (Exception e) {
			intervalInt = 2000;
		}
		if (intervalInt <= 0) {
			intervalInt = 1;
		}
		mHandler.postDelayed(doAutoNextRunnable, intervalInt);
	}

	/**
	 * Gets the result.
	 * 
	 * @param result
	 *            the result
	 * @return the result
	 */
	private String getResult(String result) {
		if (result == null || result.equals("")) {
			result = "无数据";
		} else if ("B+R".equalsIgnoreCase(result)) {
			result = "黑棋中盘胜";
		} else if ("W+R".equalsIgnoreCase(result)) {
			result = "白棋中盘胜";
		} else if ("B+T".equalsIgnoreCase(result)) {
			result = "黑棋超时胜";
		} else if ("W+T".equalsIgnoreCase(result)) {
			result = "白棋超时胜";
		} else if (result.startsWith("B+")) {
			result = "黑胜" + result.substring(2) + "目";
		} else if (result.startsWith("W+")) {
			result = "白胜" + result.substring(2) + "目";
		}
		return result;
	}

	/**
	 * Inits the view.
	 */
	private void initView() {
		mHandler = new Handler();
		DisplayMetrics dm = getResources().getDisplayMetrics();
		float density = dm.density; // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
		mBoardLayout = (RelativeLayout) this.findViewById(R.id.board_layout);
		mBlackNameTextView = (TextView) this
				.findViewById(R.id.manual_title_match_black_name);
		mWhiteNameTextView = (TextView) this
				.findViewById(R.id.manual_title_match_white_name);
		mMatchNameTextView = (TextView) this
				.findViewById(R.id.manual_title_match_name);
		mMatchCommentTextView = (TextView) this.findViewById(R.id.text_comment);
		mPopMenuView = new PopMenuView(this);
		mPopMenuView.addItems(SETTINGS_STRINGS);
		mPopMenuView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case CHESSMANUAL_INFO: {
					if (match != null) {
						// 双保险
						String result = getResult(match.result);
						String result2 = getResult(mChessManual
								.getMatchResult());
						if (result.equals("无数据")) {
							result = result2;
						}
						DialogUtils.showMessageDialog(MainActivity.this,
								"胜负信息", result, null);
					}
				}
					break;
				case SHOW_NUMBER: {
					boolean flag = AppPrefrence
							.getShowNumber(MainActivity.this);
					AppPrefrence.saveShowNumber(MainActivity.this, !flag);
					board.postInvalidate();
				}
					break;
				case SHOW_COORDINATE: {
					boolean flag = AppPrefrence
							.getShowCoordinate(MainActivity.this);
					AppPrefrence.saveShowCoordinate(MainActivity.this, !flag);
					board.postInvalidate();
				}
					break;
				case LOCAL_FAVORITE: {
					if (match != null) {
						final ArrayList<Group> groups = DBService
								.getGroupCaches();
						String[] groupsNames = new String[groups.size()];
						for (int i = 0; i < groups.size(); i++) {
							groupsNames[i] = groups.get(i).getName();
						}
						DialogUtils.showItemsDialog(MainActivity.this, "选择分组",
								groupsNames, new ItemSelectedListener() {

									@Override
									public void onItemSelected(
											DialogInterface dialog,
											String text, int which) {
										dialog.dismiss();
										mChessManual.setId(-1);
										mChessManual.setGroupId(groups.get(
												which).getId());
										boolean flag = DBService
												.saveFavoriteChessManual(mChessManual);
										if (flag) {
											DialogUtils
													.showToast(
															MainActivity.this,
															R.string.manual_favorite_success);
										} else {
											DialogUtils
													.showToast(
															MainActivity.this,
															R.string.manual_favorite_error);
										}
									}
								});
					}
				}
					break;
				case AUTO_NEXT: {
					boolean flag = AppPrefrence.getAutoNext(MainActivity.this);
					AppPrefrence.saveAutoNext(MainActivity.this, !flag);
					doAutoNext();
				}
					break;
				case LAZI_SOUND: {
					boolean flag = AppPrefrence.getLazySound(MainActivity.this);
					AppPrefrence.saveLazySound(MainActivity.this, !flag);
				}
					break;
				case SHARE_CHESSMANUAL: {
					if (match != null) {
						board.destroyDrawingCache();
						board.setDrawingCacheEnabled(true);
						Bitmap bm = Bitmap.createBitmap(board.getDrawingCache());
						FileOutputStream m_fileOutPutStream = null;
						File dir = new File(PICTURE_PATH);
						if (!dir.exists()) {
							dir.mkdirs();
						}
						File file = new File(dir, System.currentTimeMillis()
								+ ".png");
						try {
							m_fileOutPutStream = new FileOutputStream(file);
							bm.compress(CompressFormat.PNG, 50,
									m_fileOutPutStream);
							m_fileOutPutStream.flush();
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} finally {
							if (m_fileOutPutStream != null) {
								try {
									m_fileOutPutStream.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
						Intent intent = new Intent(Intent.ACTION_SEND);
						intent.setType("image/*");
						intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
						intent.putExtra(
								Intent.EXTRA_TEXT,
								match.matchName
										+ " "
										+ match.blackName
										+ "vs"
										+ match.whiteName
										+ "( @掌中围棋，与您分享精彩棋谱！http://www.appchina.com/app/com.soyomaker.handsgo/ )");
						startActivity(Intent.createChooser(intent, getTitle()));
					}
				}
					break;
				case CHESS_BOARD_COLOR: {
					showChooseChessBoardColorDialog();
				}
					break;
				case CHESS_PIECE_STYLE: {
					showChooseChessPieceStyleDialog();
				}
					break;
				}
				mPopMenuView.dismiss();
			}
		});
		mSettingButton = (Button) this.findViewById(R.id.manual_title_settings);
		mSettingButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mPopMenuView.showAsDropDown(v);
			}
		});

		nextStep = (Button) this.findViewById(R.id.next_step);
		nextStep.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean hasNext = next();
				boolean flag = AppPrefrence.getLazySound(MainActivity.this);
				if (flag && hasNext) {
					mPlaySoundPool.play(1, 0);
				}
				board.postInvalidate();
			}
		});
		prevStep = (Button) this.findViewById(R.id.prev_step);
		prevStep.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				prev();
				board.postInvalidate();
			}
		});
		fastPrevStep = (Button) this.findViewById(R.id.fast_prev_step);
		fastPrevStep.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				fastPrev();
				board.postInvalidate();
			}
		});
		fastNextStep = (Button) this.findViewById(R.id.fast_next_step);
		fastNextStep.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				fastNext();
				board.postInvalidate();
			}
		});
		firstStep = (Button) this.findViewById(R.id.first_step);
		firstStep.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				first();
				board.postInvalidate();
			}
		});
		lastStep = (Button) this.findViewById(R.id.last_step);
		lastStep.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				last();
				board.postInvalidate();
			}
		});
		nextVar = (Button) this.findViewById(R.id.next_var);
		nextVar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				nextVar();
				board.postInvalidate();
			}
		});
		prevVar = (Button) this.findViewById(R.id.prev_var);
		prevVar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				prevVar();
				board.postInvalidate();
			}
		});
		boardModel = new DefaultBoardModel(19);
		int cellWidth = Math.round(dm.widthPixels / 20);
		board = new GoBoard(this, boardModel, cellWidth / 2, cellWidth / 2,
				cellWidth, cellWidth, density);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				dm.widthPixels, dm.widthPixels);
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		board.setFocusable(true);
		board.setClickable(true);
		board.setFocusableInTouchMode(true);
		board.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					board.pointerReleased(Math.round(event.getX()),
							Math.round(event.getY()));
				} else if (event.getAction() == MotionEvent.ACTION_DOWN) {
					board.pointerPressed(Math.round(event.getX()),
							Math.round(event.getY()));
				} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
					board.pointerMoved(Math.round(event.getX()),
							Math.round(event.getY()));
				}
				return false;
			}
		});
		mBoardLayout.addView(board, layoutParams);
		mProgressView = findViewById(R.id.refreshing_progress_manual);
	}

	private void showChooseChessPieceStyleDialog() {
		DialogUtils.showSingleChoiceItemsDialog(this,
				R.string.chess_piece_style_choose_dialog_title, new String[] {
						"3d棋子", "2d棋子" },
				AppPrefrence.getChessPieceStyle(this),
				new ItemSelectedListener() {

					@Override
					public void onItemSelected(DialogInterface dialog,
							String text, int which) {
						AppPrefrence.saveChessPieceStyle(MainActivity.this,
								which);
						board.invalidate();
					}
				});
	}

	private void showChooseChessBoardColorDialog() {
		if (mChessBoardColorPickerDialog == null) {
			mChessBoardColorPickerDialog = new ColorPickerDialog(this,
					getString(R.string.chess_board_color_picker_dialog_title),
					board.getBoardBackgroundColor(),
					new OnColorChangedListener() {

						@Override
						public void colorChanged(int color) {
							mChessBoardColorPickerDialog.cancel();
							AppPrefrence.saveChessBoardColor(MainActivity.this,
									color);
							board.setBoardBackgroundColor(color);
							board.invalidate();
						}
					});
		}
		mChessBoardColorPickerDialog.show();
	}

	private void initData() {
		Intent intent = this.getIntent();
		if (intent != null) {
			Bundle bundle = intent.getExtras();
			mChessManual = (ChessManual) bundle.getSerializable("ChessManual");
			if (mChessManual != null) {
				mProgressView.setVisibility(View.VISIBLE);
				AsyncTaskUtils.create(this, this, false).execute(
						DOWNLOADING_CHESS_MANUAL_FLAG);
			}
		}
	}

	private void prevVar() {
		ListElement l = treeNode.listElement();
		if (l == null)
			return;
		if (l.previous() == null)
			return;
		TreeNode newpos = (TreeNode) l.previous().content();
		goBack();
		treeNode = newpos;
		setNode();
		updateComment();
	}

	private void nextVar() {
		ListElement l = treeNode.listElement();
		if (l == null)
			return;
		if (l.next() == null)
			return;
		TreeNode newpos = (TreeNode) l.next().content();
		goBack();
		treeNode = newpos;
		setNode();
		updateComment();
	}

	// go one move forward
	private void goForward() {
		if (!treeNode.hasChildren())
			return;
		treeNode = treeNode.firstChild();
		setNode();
		setLast();
	}

	// go one move back
	private void goBack() {
		if (treeNode.parentPos() == null)
			return;
		undoNode();
		treeNode = treeNode.parentPos();
		setLast();
	}

	// Undo everything that has been changed in the node.
	// (This will not correct the last move marker!)
	private void undoNode() {
		Node n = treeNode.node();
		// clearrange();
		ListElement p = n.lastChange();
		while (p != null) {
			Change c = (Change) p.content();
			P.color(c.I, c.J, c.C);
			P.number(c.I, c.J, c.N);
			forcePut(c.I, c.J, c.C, c.N);
			update(c.I, c.J);
			p = p.previous();
		}
		n.clearChanges();
		Pw -= n.Pw;
		Pb -= n.Pb;
	}

	// interpret all actions of a node
	private void setNode() {
		Node n = treeNode.node();
		number = n.number();
		ListElement p = n.actions();
		if (p == null)
			return;
		Action a;
		while (p != null) {
			a = (Action) p.content();
			p = p.next();
		}
		n.clearChanges();
		n.Pw = n.Pb = 0;
		p = n.actions();
		while (p != null) {
			a = (Action) p.content();
			if (a.type().equals("B")) {
				setAction(n, a, 1);
			} else if (a.type().equals("W")) {
				setAction(n, a, -1);
			}
			if (a.type().equals("AB")) {
				placeAction(n, a, 1);
			}
			if (a.type().equals("AW")) {
				placeAction(n, a, -1);
			} else if (a.type().equals("AE")) {
				emptyAction(n, a);
			}
			p = p.next();
		}
	}

	private void performPut(int i, int j, int c, int number) {
		GoPoint point = new GoPoint();
		if (c == 1) {
			try {
				point.setPlayer(GoPoint.BLACK);
				point.setNumber(number);
				boardModel.performPut(i, j, point);
			} catch (GoException e) {
				e.printStackTrace();
			}
		} else if (c == -1) {
			try {
				point.setPlayer(GoPoint.WHITE);
				point.setNumber(number);
				boardModel.performPut(i, j, point);
			} catch (GoException e) {
				e.printStackTrace();
			}
		} else if (c == 0) {
			try {
				boardModel.performRemove(i, j);
			} catch (GoException e) {
				e.printStackTrace();
			}
		}
	}

	private void forcePut(int i, int j, int c, int number) {
		GoPoint point = new GoPoint();
		if (c == 1) {
			point.setPlayer(GoPoint.BLACK);
			point.setNumber(number);
			boardModel.forcePut(i, j, point);
		} else if (c == -1) {
			point.setPlayer(GoPoint.WHITE);
			point.setNumber(number);
			boardModel.forcePut(i, j, point);
		} else if (c == 0) {
			boardModel.forceRemove(i, j);
		}
	}

	// interpret a set move action, update the last move marker,
	// c being the color of the move.
	private void setAction(Node n, Action a, int c) {
		String s = (String) a.arguments().content();
		int i = Field.i(s);
		int j = Field.j(s);
		if (!valid(i, j))
			return;
		n.addChange(new Change(i, j, P.color(i, j), P.number(i, j)));
		P.color(i, j, c);
		P.number(i, j, n.number() - 1);
		performPut(i, j, c, n.number() - 1);
		showlast = false;
		update(lasti, lastj);
		showlast = true;
		lasti = i;
		lastj = j;
		update(i, j);
		P.color(-c);
		capture(i, j, n);
	}

	// interpret a set move action, update the last move marker,
	// c being the color of the move.
	private void placeAction(Node n, Action a, int c) {
		int i, j;
		ListElement larg = a.arguments();
		while (larg != null) {
			String s = (String) larg.content();
			i = Field.i(s);
			j = Field.j(s);
			if (valid(i, j)) {
				n.addChange(new Change(i, j, P.color(i, j), P.number(i, j)));
				P.color(i, j, c);
				forcePut(i, j, c, GoPoint.NONE);
				update(i, j);
			}
			larg = larg.next();
		}
	}

	// interpret a remove stone action
	private void emptyAction(Node n, Action a) {
		int i, j, r = 1;
		ListElement larg = a.arguments();
		while (larg != null) {
			String s = (String) larg.content();
			i = Field.i(s);
			j = Field.j(s);
			if (valid(i, j)) {
				n.addChange(new Change(i, j, P.color(i, j), P.number(i, j)));
				if (P.color(i, j) < 0) {
					n.Pw++;
					Pw++;
				} else if (P.color(i, j) > 0) {
					n.Pb++;
					Pb++;
				}
				P.color(i, j, 0);
				forcePut(i, j, 0, GoPoint.NONE);
				update(i, j);
			}
			larg = larg.next();
		}
	}

	int captured = 0, capturei, capturej;

	// capture neighboring groups without liberties
	// capture own group on suicide
	private void capture(int i, int j, Node n) {
		int c = -P.color(i, j);
		captured = 0;
		if (i > 0)
			captureGroup(i - 1, j, c, n);
		if (j > 0)
			captureGroup(i, j - 1, c, n);
		if (i < size - 1)
			captureGroup(i + 1, j, c, n);
		if (j < size - 1)
			captureGroup(i, j + 1, c, n);
		if (P.color(i, j) == -c) {
			captureGroup(i, j, -c, n);
		}
		if (captured == 1 && P.count(i, j) != 1)
			captured = 0;
	}

	// Used by capture to determine the state of the groupt at (i,j)
	// Remove it, if it has no liberties and note the removals
	// as actions in the current node.
	private void captureGroup(int i, int j, int c, Node n) {
		int ii, jj;
		Action a;
		if (P.color(i, j) != c)
			return;
		if (!P.markgrouptest(i, j, 0)) // liberties?
		{
			for (ii = 0; ii < size; ii++)
				for (jj = 0; jj < size; jj++) {
					if (P.marked(ii, jj)) {
						n.addChange(new Change(ii, jj, P.color(ii, jj), P
								.number(ii, jj)));
						if (P.color(ii, jj) > 0) {
							Pb++;
							n.Pb++;
						} else {
							Pw++;
							n.Pw++;
						}
						P.color(ii, jj, 0);
						forcePut(ii, jj, 0, GoPoint.NONE);
						update(ii, jj); // redraw the field (offscreen)
						captured++;
						capturei = ii;
						capturej = jj;
					}
				}
		}
	}

	private boolean valid(int i, int j) {
		return i >= 0 && i < size && j >= 0 && j < size;
	}

	// update the last move marker applying all
	// set move actions in the node
	private void setLast() {
		Node n = treeNode.node();
		ListElement l = n.actions();
		Action a;
		String s;
		int i = lasti, j = lastj;
		lasti = -1;
		lastj = -1;
		update(i, j);
		while (l != null) {
			a = (Action) l.content();
			if (a.type().equals("B") || a.type().equals("W")) {
				s = (String) a.arguments().content();
				i = Field.i(s);
				j = Field.j(s);
				if (valid(i, j)) {
					lasti = i;
					lastj = j;
					update(lasti, lastj);
					P.color(-P.color(i, j));
				}
			}
			l = l.next();
		}
		// number = n.number();
		// Log.e(TAG, "setlast number:" + number);
	}

	private void update(int x, int y) {

	}

	private void updateComment() {
		Node n = treeNode.node();
		// number = n.number();
		int i, j;
		// delete all marks and variations
		for (i = 0; i < size; i++)
			for (j = 0; j < size; j++) {
				if (P.tree(i, j) != null) {
					P.tree(i, j, null);
					boardModel.getPoint(i, j).setTreeNode(null);
					update(i, j);
				}
				if (P.marker(i, j) != Field.NONE) {
					P.marker(i, j, Field.NONE);
					boardModel.getPoint(i, j).setMark(GoPoint.NONE);
					update(i, j);
				}
				if (P.letter(i, j) != 0) {
					P.letter(i, j, 0);
					boardModel.getPoint(i, j).setMark(GoPoint.NONE);
					update(i, j);
				}
				if (P.haslabel(i, j)) {
					P.clearlabel(i, j);
					boardModel.getPoint(i, j).setLabel("");
					update(i, j);
				}
			}
		ListElement la = n.actions();
		Action a;
		String s;
		String sc = "";
		int let = 1;
		while (la != null) // setup the marks and letters
		{
			a = (Action) la.content();
			if (a.type().equals("C")) {
				sc = (String) a.arguments().content();
			} else if (a.type().equals("SQ") || a.type().equals("SL")) {
				ListElement larg = a.arguments();
				while (larg != null) {
					s = (String) larg.content();
					i = Field.i(s);
					j = Field.j(s);
					if (valid(i, j)) {
						P.marker(i, j, Field.SQUARE);
						boardModel.getPoint(i, j).setMark(GoPoint.SQUARE);
						update(i, j);
					}
					larg = larg.next();
				}
			} else if (a.type().equals("MA") || a.type().equals("M")
					|| a.type().equals("TW") || a.type().equals("TB")) {
				ListElement larg = a.arguments();
				while (larg != null) {
					s = (String) larg.content();
					i = Field.i(s);
					j = Field.j(s);
					if (valid(i, j)) {
						P.marker(i, j, Field.CROSS);
						boardModel.getPoint(i, j).setMark(GoPoint.CROSS);
						update(i, j);
					}
					larg = larg.next();
				}
			} else if (a.type().equals("TR")) {
				ListElement larg = a.arguments();
				while (larg != null) {
					s = (String) larg.content();
					i = Field.i(s);
					j = Field.j(s);
					if (valid(i, j)) {
						P.marker(i, j, Field.TRIANGLE);
						boardModel.getPoint(i, j).setMark(GoPoint.TRIANGLE);
						update(i, j);
					}
					larg = larg.next();
				}
			} else if (a.type().equals("CR")) {
				ListElement larg = a.arguments();
				while (larg != null) {
					s = (String) larg.content();
					i = Field.i(s);
					j = Field.j(s);
					if (valid(i, j)) {
						P.marker(i, j, Field.CIRCLE);
						boardModel.getPoint(i, j).setMark(GoPoint.CIRCLE);
						update(i, j);
					}
					larg = larg.next();
				}
			} else if (a.type().equals("L")) {
				ListElement larg = a.arguments();
				while (larg != null) {
					s = (String) larg.content();
					i = Field.i(s);
					j = Field.j(s);
					if (valid(i, j)) {
						P.letter(i, j, let);
						boardModel.getPoint(i, j).setMark(let);
						let++;
						update(i, j);
					}
					larg = larg.next();
				}
			} else if (a.type().equals("LB")) {
				ListElement larg = a.arguments();
				while (larg != null) {
					s = (String) larg.content();
					i = Field.i(s);
					j = Field.j(s);
					if (valid(i, j) && s.length() >= 4 && s.charAt(2) == ':') {
						P.setlabel(i, j, s.substring(3));
						boardModel.getPoint(i, j).setLabel(s.substring(3));
						update(i, j);
					}
					larg = larg.next();
				}
			}
			la = la.next();
		}
		TreeNode p;
		ListElement l = null;
		p = treeNode.parentPos();
		if (p != null) {
			l = p.firstChild().listElement();
		}
		while (l != null) {
			p = (TreeNode) l.content();
			if (p != treeNode) {
				la = p.node().actions();
				while (la != null) {
					a = (Action) la.content();
					if (a.type().equals("W") || a.type().equals("B")) {
						s = (String) a.arguments().content();
						i = Field.i(s);
						j = Field.j(s);
						if (valid(i, j)) {
							P.tree(i, j, p);
							boardModel.getPoint(i, j).setTreeNode(p);
							update(i, j);
						}
						break;
					}
					la = la.next();
				}
			}
			l = l.next();
		}
		final String comment = sc;
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				mMatchCommentTextView.setText(comment);
			}
		});
	}

	private Vector<SGFTree> trees;

	private SGFTree tree;

	private TreeNode treeNode; // the current board position in this
								// presentation

	private int size;

	private int number = 1; // number of the next move

	private int lasti = -1, lastj = 0; // last move (used to highlight the move)

	private Position P; // current board position

	private boolean showlast; // internal flag, if last move is to be
								// highlighted

	private int Pw, Pb; // Prisoners (white and black)

	private int CurrentTree; // the currently displayed tree

	private boolean next() {
		goForward();
		updateComment();
		return true;
	}

	private boolean prev() {
		goBack();
		updateComment();
		return true;
	}

	private void fastNext() {
		for (int i = 0; i < 10; i++) {
			goForward();
		}
		updateComment();
	}

	private void fastPrev() {
		for (int i = 0; i < 10; i++) {
			goBack();
		}
		updateComment();
	}

	private void first() {
		while (treeNode.parentPos() != null) {
			goBack();
		}
		updateComment();
	}

	private void last() {
		while (treeNode.hasChildren()) {
			goForward();
		}
		updateComment();
	}

	private void refreshMatchTitle() {
		String blackName = mChessManual.getBlackName();
		String whiteName = mChessManual.getWhiteName();
		String matchName = mChessManual.getMatchName();
		if (!TextUtils.isEmpty(blackName)) {
			mBlackNameTextView.setText(blackName);
		} else {
			mBlackNameTextView.setText("无数据");
		}
		if (!TextUtils.isEmpty(whiteName)) {
			mWhiteNameTextView.setText(whiteName);
		} else {
			mWhiteNameTextView.setText("无数据");
		}
		if (!TextUtils.isEmpty(matchName)) {
			mMatchNameTextView.setText(matchName);
		} else {
			mMatchNameTextView.setText("无数据");
		}
	}

	@Override
	public Object doInBackground(AsyncTaskUtils task, int flag) {
		if (flag == DOWNLOADING_CHESS_MANUAL_FLAG) {
			if (mChessManual != null) {
				ChessManual historyChessManual = DBService
						.getHistoryChessManual(mChessManual);
				if (historyChessManual != null) {
					String newSgfContentString = historyChessManual
							.getSgfContent();
					int type = mChessManual.getType();
					if (type == ChessManual.SDCARD_CHESS_MANUAL) {
						if (!TextUtils.isEmpty(newSgfContentString)) {
							InputStream stream = new ByteArrayInputStream(
									newSgfContentString.getBytes());
							getSgfFromeInputStream(stream);
							match.sgfSource = newSgfContentString;
						} else {
							getSgfFromFile();
						}
					} else if (type == ChessManual.NET_CHESS_MANUAL) {
						// 没有result或者结果中包含直播，一般为棋谱不全或者比赛尚未结束，应该重新去更新
						if (!TextUtils.isEmpty(newSgfContentString)
								&& !TextUtils.isEmpty(historyChessManual
										.getMatchResult())
								&& !historyChessManual.getMatchResult()
										.contains("直播")) {
							InputStream stream = new ByteArrayInputStream(
									newSgfContentString.getBytes());
							getSgfFromeInputStream(stream);
							match.sgfSource = newSgfContentString;
						} else {
							getSgfFromNet();
						}
					}
				} else {
					ChessManual favoriteChessManual = DBService
							.getFavoriteChessManual(mChessManual);
					if (favoriteChessManual != null) {
						String newSgfContentString = favoriteChessManual
								.getSgfContent();
						int type = mChessManual.getType();
						if (type == ChessManual.SDCARD_CHESS_MANUAL) {
							if (!TextUtils.isEmpty(newSgfContentString)) {
								InputStream stream = new ByteArrayInputStream(
										newSgfContentString.getBytes());
								getSgfFromeInputStream(stream);
								match.sgfSource = newSgfContentString;
							} else {
								getSgfFromFile();
								if (match != null) {
									favoriteChessManual
											.setSgfContent(match.sgfSource);
									// 需要更新棋谱数据
									DBService
											.saveFavoriteChessManual(favoriteChessManual);
								}
							}
						} else if (type == ChessManual.NET_CHESS_MANUAL) {
							if (!TextUtils.isEmpty(newSgfContentString)
									&& !TextUtils.isEmpty(favoriteChessManual
											.getMatchResult())
									&& !favoriteChessManual.getMatchResult()
											.contains("直播")) {
								InputStream stream = new ByteArrayInputStream(
										newSgfContentString.getBytes());
								getSgfFromeInputStream(stream);
								match.sgfSource = newSgfContentString;
							} else {
								getSgfFromNet();
								if (match != null) {
									favoriteChessManual
											.setSgfContent(match.sgfSource);
									// 需要更新棋谱数据
									DBService
											.saveFavoriteChessManual(favoriteChessManual);
								}
							}
						}
					} else {
						int type = mChessManual.getType();
						if (type == ChessManual.SDCARD_CHESS_MANUAL) {
							getSgfFromFile();
						} else if (type == ChessManual.NET_CHESS_MANUAL) {
							getSgfFromNet();
						}
					}
				}
				if (match != null) {
					if (!TextUtils.isEmpty(match.blackName)) {
						mChessManual.setBlackName(match.blackName);
					}
					if (!TextUtils.isEmpty(match.whiteName)) {
						mChessManual.setWhiteName(match.whiteName);
					}
					if (!TextUtils.isEmpty(match.matchName)) {
						mChessManual.setMatchName(match.matchName);
					}
					if (!TextUtils.isEmpty(match.result)) {
						mChessManual.setMatchResult(match.result);
					}
					if (!TextUtils.isEmpty(match.date)) {
						mChessManual.setMatchTime(match.date);
					}
					if (!TextUtils.isEmpty(match.sgfSource)) {
						mChessManual.setSgfContent(match.sgfSource);
					}
					DBService.saveHistoryChessManual(mChessManual);
				}
			}
		}
		return 0;
	}

	private void loadSGFTree(BufferedReader br) throws IOException {
		long time = System.currentTimeMillis();
		trees = SGFTree.load(br);
		Log.e(TAG, "加载棋谱耗时:" + (System.currentTimeMillis() - time));
		showlast = false;
		update(lasti, lastj);
		showlast = true;
		lasti = lastj = -1;
		tree = (SGFTree) trees.elementAt(0);
		CurrentTree = 0;
		size = tree.getSize();
		P = new Position(size);
		treeNode = tree.top();
		Node n = treeNode.node();
		match = new Match();
		match.gameName = n.getAction("GN");
		match.date = n.getAction("DT");
		match.blackName = n.getAction("PB");
		match.whiteName = n.getAction("PW");
		match.blackTeam = n.getAction("BT");
		match.whiteTeam = n.getAction("WT");
		match.result = n.getAction("RE");
		match.blackRank = n.getAction("BR");
		match.whiteRank = n.getAction("WR");
		match.komi = n.getAction("KM");
		match.handicap = n.getAction("HA");
		match.time = n.getAction("TM");
		match.matchName = n.getAction("EV");
		match.place = n.getAction("PC");
		match.gameComment = n.getAction("GC");
		match.source = n.getAction("SO");
		match.boardSize = size;

		Log.e(TAG, "gameName:" + n.getAction("GN"));
		Log.e(TAG, "date:" + n.getAction("DT"));
		Log.e(TAG, "playerBlack:" + n.getAction("PB"));
		Log.e(TAG, "playerWhite:" + n.getAction("PW"));
		Log.e(TAG, "blackTeam:" + n.getAction("BT"));
		Log.e(TAG, "whiteTeam:" + n.getAction("WT"));
		Log.e(TAG, "result:" + n.getAction("RE"));
		Log.e(TAG, "blackRand:" + n.getAction("BR"));
		Log.e(TAG, "whiteRank:" + n.getAction("WR"));
		Log.e(TAG, "komi:" + n.getAction("KM"));
		Log.e(TAG, "handicap:" + n.getAction("HA"));
		Log.e(TAG, "time:" + n.getAction("TM"));
		Log.e(TAG, "event:" + n.getAction("EV"));
		Log.e(TAG, "place:" + n.getAction("PC"));
		Log.e(TAG, "gameComment:" + n.getAction("GC"));
		Log.e(TAG, "source:" + n.getAction("SO"));
		Log.e(TAG, "size:" + size);
		setNode();
		updateComment();
	}

	private void getSgfFromFile() {
		Log.e("DEBUG", "从SDCard获取棋谱内容:" + mChessManual.getSgfUrl()
				+ " charset:" + mChessManual.getCharset());
		// 本地文件的棋谱内容，以文件自身编码格式去读
		BufferedReader br = null;
		try {
			String s = StringUtil.inputStream2String(new FileInputStream(
					mChessManual.getSgfUrl()), mChessManual.getCharset());
			br = new BufferedReader(new InputStreamReader(
					new ByteArrayInputStream(s.getBytes())));
			loadSGFTree(br);
			match.sgfSource = s;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void getSgfFromNet() {
		Log.e("DEBUG", "从网络获取棋谱内容:" + mChessManual.getSgfUrl() + " charset:"
				+ mChessManual.getCharset());
		BufferedReader br = null;
		try {
			String s = WebUtil.getHttpGet(this, mChessManual.getSgfUrl(),
					mChessManual.getCharset());
			br = new BufferedReader(new InputStreamReader(
					new ByteArrayInputStream(s.getBytes())));
			loadSGFTree(br);
			match.sgfSource = s;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void getSgfFromeInputStream(InputStream stream) {
		Log.e("DEBUG", "从数据库获取棋谱内容");
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(stream));
			loadSGFTree(br);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void onPostExecute(AsyncTaskUtils task, Object result, int flag) {
		if (flag == DOWNLOADING_CHESS_MANUAL_FLAG) {
			mProgressView.setVisibility(View.GONE);
			if (match != null) {
				refreshMatchTitle();
			}
		}
	}
}