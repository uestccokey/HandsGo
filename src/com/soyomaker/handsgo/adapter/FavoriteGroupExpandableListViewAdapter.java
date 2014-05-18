package com.soyomaker.handsgo.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.soyomaker.handsgo.R;
import com.soyomaker.handsgo.db.DBService;
import com.soyomaker.handsgo.go.ChessManual;
import com.soyomaker.handsgo.go.Group;
import com.soyomaker.handsgo.util.DialogUtils;
import com.soyomaker.handsgo.util.DialogUtils.ItemSelectedListener;

/**
 * The Class FavoriteGroupExpandableListAdapter.
 */
public class FavoriteGroupExpandableListViewAdapter extends
		BaseExpandableListAdapter {

	/** The m groups. */
	private ArrayList<Group> mGroups = new ArrayList<Group>();

	/** The m context. */
	private Context mContext;

	/** The m dateformat. */
	private SimpleDateFormat mXgooDateformat = new SimpleDateFormat(
			"yyyy-MM-dd");

	/** The m sina dateformat. */
	private SimpleDateFormat mSinaDateformat = new SimpleDateFormat(
			"yyyy.MM.dd");

	/** The m inflater. */
	private LayoutInflater mInflater;

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

	private final class GroupHolder {

		/** The match name text view. */
		public TextView groupNameTextView;

		/** The img down load. */
		public Button editBtn;

		public Button deleteBtn;
	}

	/**
	 * Instantiates a new chess match expandable list adapter.
	 * 
	 * @param context
	 *            the context
	 */
	public FavoriteGroupExpandableListViewAdapter(Context context) {
		this.mContext = context;
		this.mInflater = LayoutInflater.from(mContext);
	}

	/**
	 * Instantiates a new favorite group expandable list adapter.
	 * 
	 * @param context
	 *            the context
	 * @param groups
	 *            the groups
	 */
	public FavoriteGroupExpandableListViewAdapter(Context context,
			ArrayList<Group> groups) {
		this.mContext = context;
		this.mGroups = groups;
		this.mInflater = LayoutInflater.from(mContext);
	}

	/**
	 * Gets the groups.
	 * 
	 * @return the groups
	 */
	public ArrayList<Group> getGroups() {
		return mGroups;
	}

	/**
	 * Sets the groups.
	 * 
	 * @param mGroups
	 *            the new groups
	 */
	public void setGroups(ArrayList<Group> mGroups) {
		this.mGroups = mGroups;
	}

	// 重写ExpandableListAdapter中的各个方法
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getGroupCount()
	 */
	@Override
	public int getGroupCount() {
		return mGroups.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getGroup(int)
	 */
	@Override
	public Object getGroup(int groupPosition) {
		return mGroups.get(groupPosition);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getGroupId(int)
	 */
	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getChildrenCount(int)
	 */
	@Override
	public int getChildrenCount(int groupPosition) {
		return mGroups.get(groupPosition).getChessManuals().size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getChild(int, int)
	 */
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return mGroups.get(groupPosition).getChessManuals().get(childPosition);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getChildId(int, int)
	 */
	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#hasStableIds()
	 */
	@Override
	public boolean hasStableIds() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getGroupView(int, boolean,
	 * android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		GroupHolder viewGroupHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.group_item, null);
			viewGroupHolder = new GroupHolder();
			viewGroupHolder.groupNameTextView = (TextView) convertView
					.findViewById(R.id.group_name);
			viewGroupHolder.editBtn = (Button) convertView
					.findViewById(R.id.edit_group);
			viewGroupHolder.deleteBtn = (Button) convertView
					.findViewById(R.id.delete_group);
			convertView.setTag(viewGroupHolder);
		} else {
			viewGroupHolder = (GroupHolder) convertView.getTag();
		}
		final Group group = (Group) getGroup(groupPosition);
		viewGroupHolder.groupNameTextView.setText(group.toString());
		if (group.getId() != Group.DEFAULT_GROUP) {
			viewGroupHolder.editBtn.setVisibility(View.VISIBLE);
			viewGroupHolder.deleteBtn.setVisibility(View.VISIBLE);
		} else {
			viewGroupHolder.editBtn.setVisibility(View.GONE);
			viewGroupHolder.deleteBtn.setVisibility(View.GONE);
		}
		viewGroupHolder.editBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (group.getId() != Group.DEFAULT_GROUP) {
					final EditText editText = new EditText(mContext);
					editText.setText(group.getName());
					new AlertDialog.Builder(mContext).setTitle("编辑分组名称")
							.setIcon(android.R.drawable.ic_dialog_info)
							.setView(editText)
							.setPositiveButton("确定", new OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									String newName = editText.getText()
											.toString();
									group.setName(newName);
									DBService.saveGroup(group);
									notifyDataSetChanged();
									dialog.dismiss();
								}
							}).setNegativeButton("取消", null).show();
				}
			}
		});
		viewGroupHolder.deleteBtn
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						if (group.getId() != Group.DEFAULT_GROUP) {
							AlertDialog.Builder builder = new Builder(mContext);
							builder.setMessage("确定要删除该分组及分组中的棋谱吗？");
							builder.setTitle("提示");
							builder.setPositiveButton(
									"确认",
									new android.content.DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											mGroups.remove(group);
											// 删除分组中的棋谱
											for (ChessManual chessManual : group
													.getChessManuals()) {
												DBService
														.deleteFavoriteChessManual(chessManual);
											}
											// 删除分组
											DBService.deleteGroup(group);
											notifyDataSetChanged();
											dialog.dismiss();
										}
									});
							builder.setNegativeButton("取消", null);
							builder.create().show();
						}
					}
				});
		return convertView;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getChildView(int, int, boolean,
	 * android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
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
		final ChessManual chessManual = mGroups.get(groupPosition)
				.getChessManuals().get(childPosition);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#isChildSelectable(int, int)
	 */
	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}
