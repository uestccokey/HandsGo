/*
 * 
 */
package com.soyomaker.handsgo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * The Class ScrollForeverTextView.
 */
public class ScrollForeverTextView extends TextView {

	/**
	 * Instantiates a new scroll forever text view.
	 * 
	 * @param context
	 *            the context
	 */
	public ScrollForeverTextView(Context context) {
		super(context);
	}

	/**
	 * Instantiates a new scroll forever text view.
	 * 
	 * @param context
	 *            the context
	 * @param attrs
	 *            the attrs
	 */
	public ScrollForeverTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * Instantiates a new scroll forever text view.
	 * 
	 * @param context
	 *            the context
	 * @param attrs
	 *            the attrs
	 * @param defStyle
	 *            the def style
	 */
	public ScrollForeverTextView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#isFocused()
	 */
	@Override
	public boolean isFocused() {
		return true;
	}

}