/*
 * 
 */
package com.soyomaker.handsgo.net.yygo;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.soyomaker.handsgo.go.ChessManual;
import com.soyomaker.handsgo.reader.IReadOnlineChessManual;
import com.soyomaker.handsgo.util.WebUtil;

/**
 * The Class ReadYYGO.
 */
public class ReadYYGO implements IReadOnlineChessManual {

	/**
	 * 返回yyoo在线棋谱页面 http://www.weiqihome.com/yygo_java/live_list.php
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
		String url = "http://www.weiqihome.com/yygo_java/live_list.php";
		String s = WebUtil.getHttpGet(context, url);
		try {
			JSONArray jsonArray = new JSONArray(s);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject2 = (JSONObject) jsonArray.opt(i);
				ChessManual chessManual = new ChessManual();
				chessManual.setMatchName(jsonObject2.getString("event").trim());
				chessManual.setBlackName(jsonObject2.getString("blackName")
						.trim());
				chessManual.setWhiteName(jsonObject2.getString("whiteName")
						.trim());
				chessManual.setSgfUrl(jsonObject2.getString("link").trim());
				chessManual.setMatchTime("");
				chessManual.setMatchResult("");
				chessManual.setCharset("gb2312");
				chessManual.setLive(true);
				chessManuals.add(chessManual);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return chessManuals;
	}
}
