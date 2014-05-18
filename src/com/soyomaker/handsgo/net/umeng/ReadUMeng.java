package com.soyomaker.handsgo.net.umeng;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;

import com.soyomaker.handsgo.Constant;
import com.soyomaker.handsgo.go.ChessManual;
import com.soyomaker.handsgo.parser.FamousChessManualParser;
import com.soyomaker.handsgo.reader.IReadOnlineChessManual;
import com.umeng.analytics.MobclickAgent;

public class ReadUMeng implements IReadOnlineChessManual {

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

	@Override
	public ArrayList<ChessManual> getOnlineChessManuals(Context context)
			throws IOException {
		String json = MobclickAgent.getConfigParams(context,
				String.format(Constant.FAMOUS_PAGE_STRING, page));
		FamousChessManualParser parser = new FamousChessManualParser();
		return parser.parse(json);
	}

}
