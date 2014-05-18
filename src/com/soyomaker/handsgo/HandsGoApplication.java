/*
 * 
 */
package com.soyomaker.handsgo;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.soyomaker.handsgo.db.DBService;
import com.soyomaker.handsgo.go.Group;

/**
 * The Class HandsGoApplication.
 */
public class HandsGoApplication extends Application {

	/** Application的context. */
	public static Context sContext;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Application#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		HandsGoApplication.sContext = this;
		ImageLoader.getInstance().init(
				ImageLoaderConfiguration.createDefault(this));
		DBService.init(this);
		// 创建默认分组
		if (DBService.getGroupCaches().isEmpty()) {
			Log.e("HandsGoApplication", "创建默认分组");
			Group defaultGroup = new Group();
			defaultGroup.setName("默认分组");
			DBService.saveGroup(defaultGroup);
		}
	}
}
