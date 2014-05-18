/*
 * 
 */
package com.soyomaker.handsgo;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * The Class AppPrefrence.
 */
public class AppPrefrence {

	/** The Constant SHOW_NUMBER_KEY. */
	private static final String SHOW_NUMBER_KEY = "show_number";

	/** The Constant SHOW_COORDINATE_KEY. */
	private static final String SHOW_COORDINATE_KEY = "show_coordinate";

	/** The Constant AUTO_NEXT_KEY. */
	private static final String AUTO_NEXT_KEY = "auto_next";

	/** The Constant LAZI_SOUND_STRING_KEY. */
	private static final String LAZI_SOUND_STRING_KEY = "lazy_sound";

	/** The Constant AUTO_NEXT_INTERVAL_KEY. */
	private static final String AUTO_NEXT_INTERVAL_KEY = "AUTO_NEXT_INTERVAL";

	/** The Constant CHESS_BOARD_COLOR. */
	private static final String CHESS_BOARD_COLOR_KEY = "CHESS_BOARD_COLOR";

	private static final String CHESS_PIECE_STYLE_KEY = "CHESS_PIECE_STYLE";

	private static final String APP_TIPS_VERSION_KEY = "APP_TIPS_VERSION";

	private static final String APP_TIPS_HIDE_KEY = "APP_TIPS_HIDE";

	/**
	 * 
	 * @param context
	 * @param tipsVersion
	 */
	public static final void saveAppTipsVersion(Context context, int tipsVersion) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor editor = preferences.edit();
		editor.putInt(APP_TIPS_VERSION_KEY, tipsVersion);
		editor.commit();
	}

	/**
	 * 
	 * @param context
	 * @return
	 */
	public static final int getAppTipsVersion(Context context) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		return preferences.getInt(APP_TIPS_VERSION_KEY, 0);
	}

	/**
	 * 
	 * @param context
	 * @param hide
	 */
	public static final void saveAppTipsHide(Context context, boolean hide) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor editor = preferences.edit();
		editor.putBoolean(APP_TIPS_HIDE_KEY, hide);
		editor.commit();
	}

	/**
	 * 
	 * @param context
	 * @return
	 */
	public static final boolean getAppTipsHide(Context context) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		return preferences.getBoolean(APP_TIPS_HIDE_KEY, false);
	}

	/**
	 * 
	 * @param context
	 * @param style
	 */
	public static final void saveChessPieceStyle(Context context, int style) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor editor = preferences.edit();
		editor.putInt(CHESS_PIECE_STYLE_KEY, style);
		editor.commit();
	}

	/**
	 * 
	 * @param context
	 * @return
	 */
	public static final int getChessPieceStyle(Context context) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		return preferences.getInt(CHESS_PIECE_STYLE_KEY, 0);
	}

	/**
	 * 设置棋盘颜色
	 * 
	 * @param context
	 * @param color
	 */
	public static final void saveChessBoardColor(Context context, int color) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor editor = preferences.edit();
		editor.putInt(CHESS_BOARD_COLOR_KEY, color);
		editor.commit();
	}

	/**
	 * 获取棋盘颜色
	 * 
	 * @param context
	 * @return
	 */
	public static final int getChessBoardColor(Context context) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		return preferences.getInt(CHESS_BOARD_COLOR_KEY, 0xffEE9A00);
	}

	/**
	 * Save lazy sound.
	 * 
	 * @param context
	 *            the context
	 * @param lazySound
	 *            the lazy sound
	 */
	public static void saveLazySound(Context context, boolean lazySound) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor editor = preferences.edit();
		editor.putBoolean(LAZI_SOUND_STRING_KEY, lazySound);
		editor.commit();
	}

	/**
	 * Gets the lazy sound.
	 * 
	 * @param context
	 *            the context
	 * @return the lazy sound
	 */
	public static boolean getLazySound(Context context) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		return preferences.getBoolean(LAZI_SOUND_STRING_KEY, false);
	}

	/**
	 * Save auto next interval.
	 * 
	 * @param context
	 *            the context
	 * @param autoNextInterval
	 *            the auto next interval
	 */
	public static void saveAutoNextInterval(Context context,
			String autoNextInterval) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor editor = preferences.edit();
		editor.putString(AUTO_NEXT_INTERVAL_KEY, autoNextInterval);
		editor.commit();
	}

	/**
	 * Gets the auto next interval.
	 * 
	 * @param context
	 *            the context
	 * @return the auto next interval
	 */
	public static String getAutoNextInterval(Context context) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		return preferences.getString(AUTO_NEXT_INTERVAL_KEY, "2000");
	}

	/**
	 * Save auto next.
	 * 
	 * @param context
	 *            the context
	 * @param autoNext
	 *            the auto next
	 */
	public static void saveAutoNext(Context context, boolean autoNext) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor editor = preferences.edit();
		editor.putBoolean(AUTO_NEXT_KEY, autoNext);
		editor.commit();
	}

	/**
	 * Gets the auto next.
	 * 
	 * @param context
	 *            the context
	 * @return the auto next
	 */
	public static boolean getAutoNext(Context context) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		return preferences.getBoolean(AUTO_NEXT_KEY, false);
	}

	/**
	 * Save show number.
	 * 
	 * @param context
	 *            the context
	 * @param showNumber
	 *            the show number
	 */
	public static void saveShowNumber(Context context, boolean showNumber) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor editor = preferences.edit();
		editor.putBoolean(SHOW_NUMBER_KEY, showNumber);
		editor.commit();
	}

	/**
	 * Gets the show number.
	 * 
	 * @param context
	 *            the context
	 * @return the show number
	 */
	public static boolean getShowNumber(Context context) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		return preferences.getBoolean(SHOW_NUMBER_KEY, false);
	}

	/**
	 * Save show coordinate.
	 * 
	 * @param context
	 *            the context
	 * @param showCoordinate
	 *            the show coordinate
	 */
	public static void saveShowCoordinate(Context context,
			boolean showCoordinate) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor editor = preferences.edit();
		editor.putBoolean(SHOW_COORDINATE_KEY, showCoordinate);
		editor.commit();
	}

	/**
	 * Gets the show coordinate.
	 * 
	 * @param context
	 *            the context
	 * @return the show coordinate
	 */
	public static boolean getShowCoordinate(Context context) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		return preferences.getBoolean(SHOW_COORDINATE_KEY, false);
	}
}
