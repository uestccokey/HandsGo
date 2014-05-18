package com.soyomaker.handsgo.util;

import android.content.Context;
import android.content.res.Resources;

/**
 * 像素转换工具类
 * 
 * @author MarkMjw
 * 
 */
public class PixelUtil {
	/**
	 * dp转 px
	 * 
	 * @param value
	 * @param context
	 * @return
	 */
	public static int dp2px(float value, Context context) {
		final float scale = context.getResources().getDisplayMetrics().densityDpi;
		return (int) (value * (scale / 160) + 0.5f);
	}

	/**
	 * px转dp
	 * 
	 * @param value
	 * @param context
	 * @return
	 */
	public static int px2dp(float value, Context context) {
		final float scale = context.getResources().getDisplayMetrics().densityDpi;
		return (int) ((value * 160) / scale + 0.5f);
	}

	/**
	 * sp转px
	 * 
	 * @param value
	 * @param context
	 * @return
	 */
	public static int sp2px(float value, Context context) {
		Resources r;
		if (context == null) {
			r = Resources.getSystem();
		} else {
			r = context.getResources();
		}
		float spvalue = value * r.getDisplayMetrics().scaledDensity;
		return (int) (spvalue + 0.5f);
	}

	/**
	 * px转sp
	 * 
	 * @param value
	 * @param context
	 * @return
	 */
	public static int px2sp(float value, Context context) {
		final float scale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (value / scale + 0.5f);
	}

}
