/*
 * 
 */
package com.soyomaker.handsgo.net.tom;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;

import com.soyomaker.handsgo.go.ChessManual;
import com.soyomaker.handsgo.reader.IReadOnlineChessManual;
import com.soyomaker.handsgo.util.WebUtil;

/**
 * 首先查询weiqi.sports.tom.com/zhuantinew/24.html 匹配类似<a href=
 * "javascript:newwindow('http://weiqi.sports.tom.com/2012-10-18/0045/00789675.html')"
 * ><font color=red>第14届农心杯擂台赛第3局 檀啸执白中盘胜伊田笃史</font></a><br>
 * 这样的源码可以得出直播的内容，然后进入newwindow括号中的网址
 * http://weiqi.sports.tom.com/2012-10-17/0045/68284402.html，匹配sgf可以得出直播的棋谱
 * 
 * <img src="http://weiqi.sports.tom.com/images/0zhibo.gif" border="0"> <a
 * target='_blank' href="http://weiqi.sports.tom.com/zhuantinew/23.html"> <font
 * color=red>围甲第17轮 古力vs时越 檀啸vs朴廷桓</font></a> 在这个网页中才是直播的棋谱！！！！！ <img
 * src="http://weiqi.sports.tom.com/images/0zhibo.gif" border="0"><a href=
 * "javascript:newwindow('http://weiqi.sports.tom.com/2012-10-21/0052/04370132.html')"
 * ><font color=red>围甲第17轮 李喆vs李轩豪</font></a>　<a href=
 * "javascript:newwindow('http://weiqi.sports.tom.com/2012-10-21/0052/07565588.html')"
 * ><font color=red>柯洁vs黄奕中</font></a><br>
 * <img src="http://weiqi.sports.tom.com/images/0zhibo.gif" border="0"><a href=
 * "javascript:newwindow('http://weiqi.sports.tom.com/2012-10-21/0052/04424179.html')"
 * ><font color=red>围甲第17轮 谢赫vs邬光亚</font></a>　<a href=
 * "javascript:newwindow('http://weiqi.sports.tom.com/2012-10-21/0052/07395180.html')"
 * ><font color=red>周贺玺vs毛睿龙</font></a><br>
 * <img src="http://weiqi.sports.tom.com/images/0zhibo.gif" border="0"><a href=
 * "javascript:newwindow('http://weiqi.sports.tom.com/2012-10-21/0052/04396346.html')"
 * ><font color=red>围甲第17轮 孔杰vs范廷钰</font></a>　<a href=
 * "javascript:newwindow('http://weiqi.sports.tom.com/2012-10-21/0052/07376940.html')"
 * ><font color=red>汪涛vs王昊洋</font></a><br>
 * <img src="http://weiqi.sports.tom.com/images/0zhibo.gif" border="0"><a href=
 * "javascript:newwindow('http://weiqi.sports.tom.com/2012-10-21/0052/07359948.html')"
 * ><font color=red>围甲第17轮 连笑vs陶欣然</font></a>　<a href=
 * "javascript:newwindow('http://weiqi.sports.tom.com/2012-10-21/0052/04522242.html')"
 * ><font color=red>江维杰vs陈耀烨</font></a><br>
 * <br>
 * <img src="http://weiqi.sports.tom.com/images/0zhibo.gif" border="0"><a href=
 * "javascript:newwindow('http://weiqi.sports.tom.com/2012-10-21/0052/04506657.html')"
 * ><font color=red>围甲第17轮 蔡竞vs金志锡</font></a>　<a href=
 * "javascript:newwindow('http://weiqi.sports.tom.com/2012-10-21/0052/04446195.html')"
 * ><font color=red>刘星vs崔哲瀚</font></a><br>
 * <img src="http://weiqi.sports.tom.com/images/0zhibo.gif" border="0"><a href=
 * "javascript:newwindow('http://weiqi.sports.tom.com/2012-10-21/0052/04487151.html')"
 * ><font color=red>金立智能手机杯围甲第17轮 邱峻vs赵汉乘</font></a><br>
 * <img src="http://weiqi.sports.tom.com/images/0zhibo.gif" border="0"><a href=
 * "javascript:newwindow('http://weiqi.sports.tom.com/2012-10-21/0052/04551629.html')"
 * ><font color=red>金立智能手机杯围甲第17轮 檀啸vs朴廷桓</font></a><br>
 * <img src="http://weiqi.sports.tom.com/images/0zhibo.gif" border="0"><a href=
 * "javascript:newwindow('http://weiqi.sports.tom.com/2012-10-21/0052/04462392.html')"
 * ><font color=red>金立智能手机杯围甲第17轮 古力vs时越</font></a><br>
 * 
 * Tom棋谱 http://weiqi.sports.tom.com/php/listqipu.html
 * http://weiqi.sports.tom.com/php/listqipu_02.html 。。。
 * 
 * 2011~2000年的棋谱 http://weiqi.sports.tom.com/php/listqipu2011.html
 * http://weiqi.sports.tom.com/php/listqipu2010.html
 * http://weiqi.sports.tom.com/php/listqipu2009.html
 * http://weiqi.sports.tom.com/php/listqipu2008.html
 * http://weiqi.sports.tom.com/php/listqipu2007.html
 * http://weiqi.sports.tom.com/php/listqipu2006.html
 * http://weiqi.sports.tom.com/php/listqipu2005.html
 * http://weiqi.sports.tom.com/php/listqipu2000.html
 * 
 * @author Administrator
 */
public class ReadTom implements IReadOnlineChessManual {

	/** The page. */
	private int page = 0;

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.soyomaker.handsgo.net.IReadOnlineChessManual#getOnlineChessManualWebPage
	 * (android.content.Context)
	 */
	public ArrayList<ChessManual> getOnlineChessManuals(Context context)
			throws IOException {
		ArrayList<ChessManual> chessManuals = new ArrayList<ChessManual>();
		String url = "http://weiqi.sports.tom.com/php/listqipu"
				+ ((page == 0) ? "" : (page < 10) ? "_0" + (page + 1) : "_"
						+ (page + 1)) + ".html";
		String s = WebUtil.getHttpGet(context, url, "gb2312");
		String regex = "<li class=\"a\"><a href=\"javascript:newwindow\\('http://weiqi.sports.tom.com/.*?'\\)\" >(.*?)</a></li>\\s*?"
				+ "<li class=\"b\">(.*?)</li>\\s*?"
				+ "<li class=\"c\"><a href=\"../../(.*?)\">下载</a></li>";
		final Pattern pt = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		final Matcher mt = pt.matcher(s);
		while (mt.find()) {
			ChessManual chessManual = new ChessManual();
			chessManual.setCharset("gb2312");
			chessManual.setMatchName(mt.group(1)
					.replaceAll("<font color=red>", "")
					.replaceAll("</font>", "").trim());
			chessManual.setMatchTime(mt.group(2).trim());
			chessManual.setSgfUrl(("http://weiqi.tom.com/" + mt.group(3))
					.trim());
			chessManuals.add(chessManual);
		}
		return chessManuals;
	}
}
