/*
 * 
 */
package com.soyomaker.handsgo.net.qiyun;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;

import com.soyomaker.handsgo.go.ChessManual;
import com.soyomaker.handsgo.reader.IReadOnlineChessManual;
import com.soyomaker.handsgo.util.WebUtil;

/**
 * view-source:http://go.flyingfolder.com/search/?srh=%E5%8F%A4%E5%8A%9B&cmtol=1
 * 即可搜索出古力的带解说的棋谱
 * 
 * @author cokey
 * 
 */
public class ReadQiyun implements IReadOnlineChessManual {
	/** The page. */
	private int page = 1;

	/** The search string. */
	private String searchString = "";

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
	 * Gets the search string.
	 * 
	 * @return the search string
	 */
	public String getSearchString() {
		return searchString;
	}

	/**
	 * Sets the search string.
	 * 
	 * @param searchString
	 *            the new search string
	 */
	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

	/*
	 * (non-Javadoc)
	 * http://go.flyingfolder.com/search/?srh=%E5%8F%A4%E5%8A%9B&cmtol=1
	 * 
	 * @see
	 * com.soyomaker.handsgo.net.IReadOnlineChessManual#getOnlineChessManualWebPage
	 * (android.content.Context)
	 */
	public ArrayList<ChessManual> getOnlineChessManuals(Context context)
			throws IOException {
		ArrayList<ChessManual> chessManuals = new ArrayList<ChessManual>();
		String url = "http://go.flyingfolder.com/search/?cmtol=1&srh="
				+ searchString + "&pgi=" + page;
		String s = WebUtil.getHttpGet(context, url);
		return chessManuals;
	}
}
