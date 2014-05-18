/*
 * 
 */
package com.soyomaker.handsgo.net.eweiqi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;

import com.soyomaker.handsgo.go.ChessManual;
import com.soyomaker.handsgo.reader.IReadOnlineChessManual;
import com.soyomaker.handsgo.util.WebUtil;

/**
 * The Class ReadEWeiqi.
 */
public class ReadEWeiqi implements IReadOnlineChessManual {

	/** The page. */
	private int page = 1;

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

	/**
	 * 返回eweiqi在线棋谱页面 http://www.eweiqi.com/qipu_more.asp?qpkind=yc&Page=2
	 * 
	 * @param context
	 *            the context
	 * @return the xgoo online chess manual web page
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public ArrayList<ChessManual> getOnlineChessManuals(Context context)
			throws IOException {
		ArrayList<ChessManual> chessManuals = new ArrayList<ChessManual>();
		String url = "http://www.eweiqi.com/qipu_more.asp?qpkind=yc&Page="
				+ page;
		String s = WebUtil.getHttpGet(context, url, "gb2312");
		String regex2 = "<a href=\"qipu_detail\\.asp\\?id=(.*?)\" target=\"_blank\">(.*?)</";
		final Pattern pt2 = Pattern.compile(regex2, Pattern.CASE_INSENSITIVE);
		final Matcher mt2 = pt2.matcher(s);
		int index = 0;
		ChessManual chessManual = null;
		while (mt2.find()) {
			String str = mt2.group(2);
			String str2 = mt2.group(1);
			str = str.replaceAll("&nbsp;", " ");
			switch (index) {
			case 0:
				chessManual = new ChessManual();
				chessManual.setMatchName(str.trim());
				index++;
				break;
			case 1:
				if (chessManual != null) {
					chessManual.setWhiteName(str.trim());
					index++;
				}
				break;
			case 2:
				if (chessManual != null) {
					chessManual.setBlackName(str.trim());
					index++;
				}
				break;
			case 3:
				if (chessManual != null) {
					chessManual.setMatchResult(str.trim());
					index++;
				}
				break;
			case 4:
				if (chessManual != null) {
					chessManual.setMatchTime(str.trim());
					chessManual
							.setSgfUrl("http://www.tygem.com/ewgibo/sgffile.asp?seq="
									+ str2);
					index++;
				}
				break;
			}
			if (index == 5) {
				chessManual.setCharset("gb2312");
				chessManuals.add(chessManual);
				index = 0;
			}
		}
		return chessManuals;
	}
}
