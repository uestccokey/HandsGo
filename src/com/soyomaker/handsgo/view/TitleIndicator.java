package com.soyomaker.handsgo.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.soyomaker.handsgo.R;
import com.soyomaker.handsgo.util.PixelUtil;

/**
 * 仿Actionbar
 * 
 * @author Tsimle
 * 
 */
public class TitleIndicator extends LinearLayout {
	private FrameLayout mBottomLine;
	private LinearLayout mPageIndicatorContainer;
	private LinearLayout mCursorLayout;
	private ArrayList<TextView> mPageIndicator;

	private int mCount;
	private int mCurrIndex = 0;
	private int mLastIndex = 0;
	private int mMoveOffset;
	private int mInitOffset;

	/**
	 * 选择栏标题默认的颜色
	 */
	private int mIndicatorDefColor;
	/**
	 * 选择栏标题选中的颜色
	 */
	private int mIndicatorLightColor;
	/**
	 * 标题栏字体大小
	 */
	private float mIndicatorTextSize;
	/**
	 * 下方线条左右的padding比例
	 */
	private int mLinePercent;

	private TitleChangedListener mListener;

	public TitleIndicator(Context context) {
		super(context);
		initViews();
	}

	public TitleIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.TitleIndicator);
		mLinePercent = a.getInt(R.styleable.TitleIndicator_linePaddingPercent,
				0);
		initViews();
	}

	private void initViews() {
		LayoutInflater.from(getContext()).inflate(R.layout.vw_title_indicator,
				this);
		mPageIndicatorContainer = (LinearLayout) findViewById(R.id.titleIndicator);
		mCursorLayout = (LinearLayout) findViewById(R.id.cursor_layout);

		mIndicatorTextSize = 16;
		mIndicatorDefColor = getResources().getColor(
				R.color.title_indicator_def);
		mIndicatorLightColor = getResources().getColor(
				R.color.title_indicator_light);
	}

	public void init(List<String> titles, TitleChangedListener listener) {
		init(titles, listener, 0);
	}

	/**
	 * 确保init时，viewPager已设置了adapter
	 */
	public void init(List<String> titles, TitleChangedListener listener,
			int initialPosition) {
		if (titles == null || titles.size() == 0) {
			throw new IllegalStateException("put into null titles");
		}

		mListener = listener;
		mCurrIndex = initialPosition;
		mLastIndex = mCurrIndex;
		mCount = titles.size();

		// init title text
		mPageIndicator = new ArrayList<TextView>(mCount);
		for (int i = 0; i < mCount; i++) {
			TextView tv = new TextView(getContext());
			tv.setText(titles.get(i));
			tv.setTextSize(mIndicatorTextSize);
			if (i == initialPosition) {
				tv.setTextColor(mIndicatorLightColor);
			} else {
				tv.setTextColor(mIndicatorDefColor);
			}
			tv.setGravity(Gravity.CENTER);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 1);
			tv.setOnClickListener(new IndicatorClickListener(i));
			mPageIndicatorContainer.addView(tv, lp);
			mPageIndicator.add(tv);
		}

		// init title line
		int screenWidth = getResources().getDisplayMetrics().widthPixels;
		int lineWidth = (int) (screenWidth / mCount);
		int indicatorPadding = 0;

		int textWidth = titles.get(0).length()
				* PixelUtil.sp2px(mIndicatorTextSize, getContext()) + 2;
		int maxPadding = (lineWidth - textWidth) / 2;
		if (maxPadding < 0) {
			maxPadding = 0;
		}
		if (mLinePercent > 0) {
			indicatorPadding = lineWidth / mLinePercent;
			if (indicatorPadding > maxPadding) {
				indicatorPadding = maxPadding;
			}
		} else {
			indicatorPadding = maxPadding;
		}

		mBottomLine = new FrameLayout(getContext());
		mBottomLine.setPadding(indicatorPadding, 0, indicatorPadding, 0);
		ImageView imageView = new ImageView(getContext());
		imageView.setImageResource(R.color.title_indicator_line);
		mBottomLine.addView(imageView);

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				lineWidth, LayoutParams.FILL_PARENT);
		mMoveOffset = (int) (screenWidth / mCount);
		mInitOffset = mMoveOffset * initialPosition;
		layoutParams.leftMargin = mInitOffset;
		mCursorLayout.addView(mBottomLine, layoutParams);
	}

	public int getCurPos() {
		return mCurrIndex;
	}

	public int getLastPos() {
		return mLastIndex;
	}

	private void onPageChanged(int to) {
		int from = mCurrIndex;
		Animation animation = new TranslateAnimation(mMoveOffset * from
				- mInitOffset, mMoveOffset * to - mInitOffset, 0, 0);
		mPageIndicator.get(from).setTextColor(mIndicatorDefColor);
		mPageIndicator.get(to).setTextColor(mIndicatorLightColor);
		animation.setFillAfter(true);
		animation.setDuration(100);
		mBottomLine.startAnimation(animation);
		mLastIndex = mCurrIndex;
		mCurrIndex = to;
	}

	private class IndicatorClickListener implements View.OnClickListener {
		private int index = 0;

		public IndicatorClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			if (mCurrIndex == index) {
				return;
			}
			if (mListener != null) {
				mListener.onTitleChanged(index, mCurrIndex);
			}
			onPageChanged(index);
		}
	};

	/**
	 * 接受title变化的事件
	 * 
	 * @author Tsimle
	 * 
	 */
	public static interface TitleChangedListener {
		public void onTitleChanged(int nowPos, int lastPos);
	}
}
