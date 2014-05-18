/*
 * 
 */
package com.soyomaker.handsgo.view;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.soyomaker.handsgo.R;

/**
 * The Class PopMenuView.
 */
public class PopMenuView {

	/** The m item list. */
	private ArrayList<String> mItemList;

	/** The m context. */
	private Context mContext;

	/** The m popup window. */
	private PopupWindow mPopupWindow;

	/** The m list view. */
	private ListView mListView;

	/** The m on item click listener. */
	private OnItemClickListener mOnItemClickListener;

	/**
	 * Instantiates a new pop menu view.
	 * 
	 * @param context
	 *            the context
	 */
	public PopMenuView(Context context) {
		this.mContext = context;

		mItemList = new ArrayList<String>(5);

		View view = LayoutInflater.from(context).inflate(
				R.layout.manual_setting_popmenu, null);

		// 设置 listview
		mListView = (ListView) view.findViewById(R.id.listView);
		mListView.setAdapter(new PopMenuViewAdapter());
		// mListView.setFocusableInTouchMode(true);
		// mListView.setFocusable(true);

		mPopupWindow = new PopupWindow(view, context.getResources()
				.getDimensionPixelSize(R.dimen.popmenu_width),
				LayoutParams.WRAP_CONTENT);
	}

	// 设置菜单项点击监听器
	/**
	 * Sets the on item click listener.
	 * 
	 * @param listener
	 *            the new on item click listener
	 */
	public void setOnItemClickListener(OnItemClickListener listener) {
		mOnItemClickListener = listener;
	}

	// 批量添加菜单项
	/**
	 * Adds the items.
	 * 
	 * @param items
	 *            the items
	 */
	public void addItems(String[] items) {
		for (String s : items) {
			mItemList.add(s);
		}
	}

	// 单个添加菜单项
	/**
	 * Adds the item.
	 * 
	 * @param item
	 *            the item
	 */
	public void addItem(String item) {
		mItemList.add(item);
	}

	// 下拉式 弹出 pop菜单 parent 右下角
	/**
	 * Show as drop down.
	 * 
	 * @param parent
	 *            the parent
	 */
	public void showAsDropDown(View parent) {
		// 使其聚集
		mPopupWindow.setFocusable(true);
		// 设置允许在外点击消失
		mPopupWindow.setOutsideTouchable(true);
		// 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景（很神奇的）
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());

		mPopupWindow.showAsDropDown(parent,
				0,
				// 保证尺寸是根据屏幕像素密度来的
				mContext.getResources().getDimensionPixelSize(
						R.dimen.popmenu_yoff));
		// 刷新状态
		mPopupWindow.update();
		// 设置listview监听器
		mListView.setOnItemClickListener(mOnItemClickListener);
	}

	// 隐藏菜单
	/**
	 * Dismiss.
	 */
	public void dismiss() {
		mPopupWindow.dismiss();
	}

	// 适配器
	/**
	 * The Class PopMenuViewAdapter.
	 */
	private final class PopMenuViewAdapter extends BaseAdapter {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getCount()
		 */
		@Override
		public int getCount() {
			return mItemList.size();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getItem(int)
		 */
		@Override
		public Object getItem(int position) {
			return mItemList.get(position);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getItemId(int)
		 */
		@Override
		public long getItemId(int position) {
			return position;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getView(int, android.view.View,
		 * android.view.ViewGroup)
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.popmenu_item, null);
				holder = new ViewHolder();
				convertView.setTag(holder);
				holder.groupItem = (TextView) convertView
						.findViewById(R.id.textView);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.groupItem.setText(mItemList.get(position));
			return convertView;
		}

		/**
		 * The Class ViewHolder.
		 */
		private final class ViewHolder {

			/** The group item. */
			TextView groupItem;
		}
	}
}
