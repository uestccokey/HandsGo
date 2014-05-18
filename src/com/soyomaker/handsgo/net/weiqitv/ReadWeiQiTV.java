package com.soyomaker.handsgo.net.weiqitv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.text.TextUtils;

import com.soyomaker.handsgo.go.ChessVideo;
import com.soyomaker.handsgo.reader.IReadOnlineChessVideo;
import com.soyomaker.handsgo.util.WebUtil;

/**
 * 首先访问 http://www.weiqitv.com/class.php?cid=25&order=postdate&page=1 匹配源码中的<a
 * href="read.php?vid=68" title="超好用布局第三季大雪不崩04"><img
 * src="image/pic/201308/thumb/68.jpg" alt="超好用布局第三季大雪不崩04"/></a>
 * 字符串，获取vid=68，根据vid
 * ，访问http://www.weiqitv.com/play.php?vid=68&playgroup=1&index=0，
 * 匹配源码中的http://www.yunsp.com.cn:8080/dispatch/video/get/37429_265_0.ovp，获取到播放链接
 * 
 * @author like
 * 
 */
public class ReadWeiQiTV implements IReadOnlineChessVideo {

	public static final int CHAO_HAO_YONG_BU_JU = 25;

	public static final int ZUI_QIAN_XIAN = 5;

	public static final int ZHI_BO_JIAN = 39;

	/** The page. */
	private int page = 1;

	/**
	 * 类型 25 为 教学频道/超好用布局 类型 5 为 最前线 类型 39 为 直播间
	 * 
	 */
	private int type = CHAO_HAO_YONG_BU_JU;

	private int totalPage = 1;

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	/**
	 * Gets the page.
	 * 
	 * @return the page
	 */
	public int getPage() {
		return page;
	}

	/**
	 * Sets the page.
	 * 
	 * @param page
	 *            the new page
	 */
	public void setPage(int page) {
		this.page = page;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public ArrayList<ChessVideo> getOnlineChessVideos(Context context)
			throws IOException {
		ArrayList<ChessVideo> chessVideos = new ArrayList<ChessVideo>();
		String url = "http://www.weiqitv.com/class.php?cid=" + type + "&page="
				+ page;
		String s = WebUtil.getHttpGet(context, url);
		{
			String regex = "<a href=\"read\\.php\\?vid=(.*?)\" title=\"(.*?)\"><img src=\"(.*?)\" alt=\".*?\"/></a>";
			final Pattern pt = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
			final Matcher mt = pt.matcher(s);
			while (mt.find()) {
				String str1 = mt.group(1);
				String str2 = mt.group(2);
				String str3 = mt.group(3);
				ChessVideo chessVideo = new ChessVideo();
				chessVideo.setVideoImageUrl("http://www.weiqitv.com/" + str3);
				chessVideo.setVideoTitle(str2);
				chessVideo.setVideoId(str1);
				if (!chessVideos.contains(chessVideo)) {
					chessVideos.add(chessVideo);
				}
			}
		}
		{
			String regex = "Pages: \\( .*?/(.*?) total \\)";
			final Pattern pt = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
			final Matcher mt = pt.matcher(s);
			while (mt.find()) {
				String str1 = mt.group(1);
				if (TextUtils.isDigitsOnly(str1)) {
					totalPage = Integer.parseInt(str1);
				}
			}
		}
		return chessVideos;
	}

	public static ChessVideo getChessVideoUrl(Context context,
			ChessVideo chessVideo) {
		if (chessVideo != null && TextUtils.isEmpty(chessVideo.getVideoUrl())) {
			String url = "http://www.weiqitv.com/play.php?vid="
					+ chessVideo.getVideoId() + "&playgroup=1&index=0";
			String s = WebUtil.getHttpGet(context, url);
			// 匹配一次
			{
				String regex = "(http://v.*?\\.weiqitv\\.com/.*?/.*?\\.mp4)";
				final Pattern pt = Pattern.compile(regex,
						Pattern.CASE_INSENSITIVE);
				final Matcher mt = pt.matcher(s);
				while (mt.find()) {
					String str = mt.group();
					chessVideo.setVideoUrl(str);
				}
			}
			// 匹配两次
			if (TextUtils.isEmpty(chessVideo.getVideoUrl())) {
				String regex = "(http://www\\.yunsp\\.com\\.cn:8080/dispatch/video/get/.*?\\.ovp)";
				final Pattern pt = Pattern.compile(regex,
						Pattern.CASE_INSENSITIVE);
				final Matcher mt = pt.matcher(s);
				while (mt.find()) {
					String str = mt.group();
					chessVideo.setVideoUrl(str);
				}
			}
		}
		return chessVideo;
	}
}
