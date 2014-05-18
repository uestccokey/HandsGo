///*
// * 
// */
//package com.soyomaker.handsgo.adapter;
//
//import java.util.Vector;
//
//import android.content.Context;
//import android.text.TextUtils;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.TextView;
//
//import com.soyomaker.handsgo.R;
//import com.soyomaker.handsgo.go.Movement;
//import com.soyomaker.handsgo.go.Variation;
//
///**
// * The Class VariationListViewAdapter.
// */
//public class VariationListViewAdapter extends BaseAdapter {
//
//	/**
//	 * The Class ListViewHolder.
//	 */
//	private final class ListViewHolder {
//
//		/** The variation comment text view. */
//		public TextView variationCommentTextView;
//	}
//
//	/** The m inflater. */
//	private LayoutInflater mInflater;
//
//	/** The m chess manuals. */
//	private Vector<Variation> mVariations;
//
//	/**
//	 * Instantiates a new list view adapter.
//	 * 
//	 * @param ctx
//	 *            the ctx
//	 * @param variations
//	 *            the variations
//	 */
//	public VariationListViewAdapter(Context ctx, Vector<Variation> variations) {
//		this.mVariations = variations;
//		this.mInflater = LayoutInflater.from(ctx);
//	}
//
//	/**
//	 * Instantiates a new list view adapter.
//	 * 
//	 * @param ctx
//	 *            the ctx
//	 */
//	public VariationListViewAdapter(Context ctx) {
//		this.mInflater = LayoutInflater.from(ctx);
//		mVariations = new Vector<Variation>();
//	}
//
//	/**
//	 * Gets the chess manuals.
//	 * 
//	 * @return the chess manuals
//	 */
//	public Vector<Variation> getVariations() {
//		return mVariations;
//	}
//
//	/**
//	 * Sets the chess manuals.
//	 * 
//	 * @param variations
//	 *            the new variations
//	 */
//	public void setVariations(Vector<Variation> variations) {
//		this.mVariations = variations;
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see android.widget.Adapter#getCount()
//	 */
//	@Override
//	public int getCount() {
//		return mVariations.size();
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see android.widget.Adapter#getItem(int)
//	 */
//	@Override
//	public Object getItem(int arg0) {
//		return mVariations.get(arg0);
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see android.widget.Adapter#getItemId(int)
//	 */
//	@Override
//	public long getItemId(int arg0) {
//		return arg0;
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see android.widget.Adapter#getView(int, android.view.View,
//	 * android.view.ViewGroup)
//	 */
//	@Override
//	public View getView(final int position, View convertView, ViewGroup arg2) {
//		ListViewHolder viewHolder;
//		if (convertView == null) {
//			convertView = mInflater.inflate(R.layout.variation_list_item, null);
//			viewHolder = new ListViewHolder();
//			viewHolder.variationCommentTextView = (TextView) convertView
//					.findViewById(R.id.text_varition_comment);
//			convertView.setTag(viewHolder);
//		} else {
//			viewHolder = (ListViewHolder) convertView.getTag();
//		}
//		final Variation variation = mVariations.get(position);
//		Movement ownerMovement = variation.reset();
//		String listItem = ownerMovement.getComment();
//		if (TextUtils.isEmpty(listItem)) {
//			listItem = "变化图";
//		}
//		viewHolder.variationCommentTextView.setText(listItem);
//		return convertView;
//	}
// }
