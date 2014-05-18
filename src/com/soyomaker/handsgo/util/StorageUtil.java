/*
 * 
 */
package com.soyomaker.handsgo.util;

import java.io.File;

import android.os.Environment;
import android.os.StatFs;

/**
 * 存储卡管理类.
 * 
 * @author MaXingliang
 */
public class StorageUtil {

	/** The Constant EXTERNAL_STORAGE. */
	public static final String EXTERNAL_STORAGE = Environment
			.getExternalStorageDirectory().toString();

	/**
	 * Instantiates a new storage util.
	 */
	private StorageUtil() {
	}

	/**
	 * Checks for external storage.
	 * 
	 * @return true, if successful
	 */
	public static boolean hasExternalStorage() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	/**
	 * 判断存储空间是否足够.
	 * 
	 * @param needSize
	 *            the need size
	 * @return true, if successful
	 */
	public static boolean checkExternalSpace(float needSize) {
		boolean flag = false;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File sdcardDir = Environment.getExternalStorageDirectory();
			StatFs sf = new StatFs(sdcardDir.getPath());
			long blockSize = sf.getBlockSize();
			long availCount = sf.getAvailableBlocks();
			long restSize = availCount * blockSize;
			if (restSize > needSize) {
				flag = true;
			}
		}
		return flag;
	}
}
