package com.soyomaker.handsgo.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.soyomaker.handsgo.R;
import com.soyomaker.handsgo.go.ChessManual;

public class LocalDictListViewAdapter extends BaseAdapter {

	/**
	 * The Class ListViewHolder.
	 */
	private final class ListViewHolder {

		public TextView localDictNameTextView;
	}

	/** The m inflater. */
	private LayoutInflater mInflater;

	/** The m context. */
	private Context mContext;

	/** The m chess manuals. */
	private ArrayList<ChessManual> mChessManuals;

	public LocalDictListViewAdapter(Context context) {
		this.mContext = context;
		this.mInflater = LayoutInflater.from(context);
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
			convertView = mInflater.inflate(R.layout.local_dict_listview_item,
					null);
			viewHolder = new ListViewHolder();
			viewHolder.localDictNameTextView = (TextView) convertView
					.findViewById(R.id.local_dict_name);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ListViewHolder) convertView.getTag();
		}
		final ChessManual chessManual = mChessManuals.get(position);
		viewHolder.localDictNameTextView.setText(chessManual.getMatchName());
		return convertView;
	}

}
