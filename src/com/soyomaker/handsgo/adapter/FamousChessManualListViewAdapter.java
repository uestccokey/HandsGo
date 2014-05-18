/*
 * 
 */
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

/**
 * The Class FamousChessManualListViewAdapter.
 */
public class FamousChessManualListViewAdapter extends BaseAdapter {

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

		/** The match info text view. */
		public TextView matchInfoTextView;
	}

	/** The m inflater. */
	private LayoutInflater mInflater;

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
	public FamousChessManualListViewAdapter(Context ctx,
			ArrayList<ChessManual> chessManuals) {
		this.mChessManuals = chessManuals;
		this.mInflater = LayoutInflater.from(ctx);
	}

	/**
	 * Instantiates a new list view adapter.
	 * 
	 * @param ctx
	 *            the ctx
	 */
	public FamousChessManualListViewAdapter(Context ctx) {
		mChessManuals = new ArrayList<ChessManual>();
		this.mInflater = LayoutInflater.from(ctx);
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
			convertView = mInflater
					.inflate(R.layout.famous_listview_item, null);
			viewHolder = new ListViewHolder();
			viewHolder.matchNameTextView = (TextView) convertView
					.findViewById(R.id.text_match_name);
			viewHolder.matchBlackNameTextView = (TextView) convertView
					.findViewById(R.id.text_black_name);
			viewHolder.matchWhiteNameTextView = (TextView) convertView
					.findViewById(R.id.text_white_name);
			viewHolder.matchTimeTextView = (TextView) convertView
					.findViewById(R.id.text_match_time);
			viewHolder.matchInfoTextView = (TextView) convertView
					.findViewById(R.id.text_match_info);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ListViewHolder) convertView.getTag();
		}
		final ChessManual chessManual = mChessManuals.get(position);
		viewHolder.matchNameTextView.setText(chessManual.getMatchName());
		viewHolder.matchBlackNameTextView.setText(chessManual.getBlackName());
		viewHolder.matchWhiteNameTextView.setText(chessManual.getWhiteName());
		viewHolder.matchTimeTextView.setText(chessManual.getMatchTime());
		viewHolder.matchInfoTextView.setText(chessManual.getMatchInfo());
		return convertView;
	}

}