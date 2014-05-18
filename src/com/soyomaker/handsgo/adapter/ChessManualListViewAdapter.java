/*
 * 
 */
package com.soyomaker.handsgo.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.soyomaker.handsgo.R;
import com.soyomaker.handsgo.db.DBService;
import com.soyomaker.handsgo.go.ChessManual;
import com.soyomaker.handsgo.go.Group;
import com.soyomaker.handsgo.util.DialogUtils;
import com.soyomaker.handsgo.util.DialogUtils.ItemSelectedListener;

/**
 * The Class ListViewAdapter.
 */
public class ChessManualListViewAdapter extends BaseAdapter {

	/**
	 * The Class ListViewHolder.
	 */
	private final class ListViewHolder {

		/** The match name text view. */
		public TextView matchNameTextView;

		/** The match white name text view. */
		public TextView matchWhiteNameTextView;

		/** The match black name text view. */
		public TextView matchBlackNameTextView;

		/** The match time text view. */
		public TextView matchTimeTextView;

		/** The img down load. */
		public Button favoriteBtn;

		/** The img new. */
		public ImageView imgNew;
	}

	/** The m dateformat. */
	private SimpleDateFormat mXgooDateformat = new SimpleDateFormat(
			"yyyy-MM-dd");

	/** The m sina dateformat. */
	private SimpleDateFormat mSinaDateformat = new SimpleDateFormat(
			"yyyy.MM.dd");

	/** The m inflater. */
	private LayoutInflater mInflater;

	/** The m context. */
	private Context mContext;

	/** The m chess manuals. */
	private ArrayList<ChessManual> mChessManuals;

	/**
	 * Instantiates a new list view adapter.
	 * 
	 * @param ctx
	 *            the ctx
	 * @param chessManuals
	 *            the chess manuals
	 */
	public ChessManualListViewAdapter(Context ctx,
			ArrayList<ChessManual> chessManuals) {
		this.mContext = ctx;
		this.mChessManuals = chessManuals;
		this.mInflater = LayoutInflater.from(ctx);
	}

	/**
	 * Instantiates a new list view adapter.
	 * 
	 * @param ctx
	 *            the ctx
	 */
	public ChessManualListViewAdapter(Context ctx) {
		this.mContext = ctx;
		this.mInflater = LayoutInflater.from(ctx);
		mChessManuals = new ArrayList<ChessManual>();
	}

	/**
	 * Gets the chess manuals.
	 * 
	 * @return the chess manuals
	 */
	public ArrayList<ChessManual> getChessManuals() {
		return mChessManuals;
	}

	/**
	 * Sets the chess manuals.
	 * 
	 * @param mChessManuals
	 *            the new chess manuals
	 */
	public void setChessManuals(ArrayList<ChessManual> mChessManuals) {
		this.mChessManuals = mChessManuals;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return mChessManuals.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int arg0) {
		return mChessManuals.get(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {
		ListViewHolder viewHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.listview_item, null);
			viewHolder = new ListViewHolder();
			viewHolder.matchNameTextView = (TextView) convertView
					.findViewById(R.id.text_match_name);
			viewHolder.matchBlackNameTextView = (TextView) convertView
					.findViewById(R.id.text_black_name);
			viewHolder.matchWhiteNameTextView = (TextView) convertView
					.findViewById(R.id.text_white_name);
			viewHolder.matchTimeTextView = (TextView) convertView
					.findViewById(R.id.text_match_time);
			viewHolder.favoriteBtn = (Button) convertView
					.findViewById(R.id.image_item_3);
			viewHolder.imgNew = (ImageView) convertView
					.findViewById(R.id.img_new);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ListViewHolder) convertView.getTag();
		}
		final ChessManual chessManual = mChessManuals.get(position);
		viewHolder.matchNameTextView.setText(chessManual.getMatchName());
		if (DBService.isHistoryChessManual(chessManual)) {
			viewHolder.matchNameTextView.setTextColor(Color
					.parseColor("#ff543210"));
		} else {
			viewHolder.matchNameTextView.setTextColor(Color
					.parseColor("#ff000000"));
		}
		viewHolder.matchBlackNameTextView.setText(chessManual.getBlackName());
		viewHolder.matchWhiteNameTextView.setText(chessManual.getWhiteName());
		viewHolder.matchTimeTextView.setText(chessManual.getMatchTime());
		viewHolder.favoriteBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (DBService.isFavoriteChessManual(chessManual)) {
					AlertDialog.Builder builder = new Builder(mContext);
					builder.setMessage("确定要取消收藏该棋谱吗?");
					builder.setTitle("提示");
					builder.setPositiveButton(
							"确认",
							new android.content.DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
									boolean flag = DBService
											.deleteFavoriteChessManual(chessManual);
									if (flag) {
										DialogUtils
												.showToast(
														mContext,
														R.string.delete_manual_favorite_success);
										notifyDataSetChanged();
									} else {
										DialogUtils
												.showToast(
														mContext,
														R.string.delete_manual_favorite_error);
									}
								}
							});
					builder.setNegativeButton("取消", null);
					builder.create().show();
				} else {
					final ArrayList<Group> groups = DBService.getGroupCaches();
					String[] groupsNames = new String[groups.size()];
					for (int i = 0; i < groups.size(); i++) {
						groupsNames[i] = groups.get(i).getName();
					}
					DialogUtils.showItemsDialog(mContext, "选择分组", groupsNames,
							new ItemSelectedListener() {

								@Override
								public void onItemSelected(
										DialogInterface dialog, String text,
										int which) {
									dialog.dismiss();
									chessManual.setId(-1);
									chessManual.setGroupId(groups.get(which)
											.getId());
									boolean flag = DBService
											.saveFavoriteChessManual(chessManual);
									notifyDataSetChanged();
									if (flag) {
										DialogUtils
												.showToast(
														mContext,
														R.string.manual_favorite_success);
										notifyDataSetChanged();
									} else {
										DialogUtils.showToast(mContext,
												R.string.manual_favorite_error);
									}
								}
							});
				}
			}
		});
		if (DBService.isFavoriteChessManual(chessManual)) {
			viewHolder.favoriteBtn
					.setBackgroundResource(R.drawable.star_full_large);
		} else {
			viewHolder.favoriteBtn
					.setBackgroundResource(R.drawable.star_empty_large);
		}

		String matchTimeString = chessManual.getMatchTime();
		if (matchTimeString != null) {
			Date date = null;
			try {
				date = mXgooDateformat.parse(matchTimeString);
			} catch (ParseException e) {
				try {
					date = mSinaDateformat.parse(matchTimeString);
				} catch (ParseException e1) {
				}
			}
			Date today = new Date();
			if (date != null) {
				if (date.getYear() >= today.getYear()
						&& date.getMonth() >= today.getMonth()
						&& date.getDate() >= today.getDate()) {
					viewHolder.imgNew.setVisibility(View.VISIBLE);
				} else {
					viewHolder.imgNew.setVisibility(View.GONE);
				}
			} else {
				viewHolder.imgNew.setVisibility(View.GONE);
			}
		} else {
			viewHolder.imgNew.setVisibility(View.GONE);
		}
		return convertView;
	}

}