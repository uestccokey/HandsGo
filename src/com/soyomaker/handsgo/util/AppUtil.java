/*
 * 
 */
package com.soyomaker.handsgo.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * The Class AppUtil.
 */
public class AppUtil {

	/**
	 * 获取版本号.
	 * 
	 * @param context
	 *            the context
	 * @return 当前应用的版本号
	 */
	public static String getVersion(Context context) {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(),
					0);
			String version = info.versionName;
			return version;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
