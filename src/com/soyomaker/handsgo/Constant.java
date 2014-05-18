/*
 * 
 */
package com.soyomaker.handsgo;

/**
 * The Class Constant.
 */
public class Constant {

	/** The Constant AppKey. */
	public static final String APP_KEY = "4294077984";

	/** The Constant AppSecret. */
	public static final String APP_SECRET = "d4bc03a8904e678496c3b0e7ee003c5e";

	/** The Constant REDIRECT_URI. */
	public static final String REDIRECT_URI = "http://sns.whalecloud.com/sina2/callback";

	/** 获取用户信息. */
	public static final String URL_GET_USER_INFO = "https://api.weibo.com/2/users/show.json?access_token=%s&uid=%s";

	/** 获取用户uid. */
	public final static String WEIBO_GET_UID = "https://api.weibo.com/2/account/get_uid.json?access_token=%s";

	/** 广告开关. */
	public static final String AD_ON_STRING = "ad_on";

	/** 名局细解页数. */
	public static final String FAMOUS_PAGE_STRING = "famous_page_%d";

	/** 服务器版本. */
	public static final String SERVER_VERSION_STRING = "server_version";

	public static final String APP_TIPS_CONTENT = "tips";

	public static final String APP_TIPS_VERSION = "tips_version";

	public static final int CHESS_PIECE_STYLE_3D = 0;

	public static final int CHESS_PIECE_STYLE_2D = 1;
}
