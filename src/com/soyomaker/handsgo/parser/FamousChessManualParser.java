/*
 * 
 */
package com.soyomaker.handsgo.parser;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.soyomaker.handsgo.go.ChessManual;

/**
 * The Class FamousChessManualParser.
 */
public class FamousChessManualParser extends BaseParser {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.soyomaker.handsgo.parser.BaseParser#parse(java.lang.String)
	 */
	@Override
	public ArrayList<ChessManual> parse(String json) {
		ArrayList<ChessManual> chessManuals = new ArrayList<ChessManual>();
		try {
			JSONObject obj = new JSONObject(json);
			JSONArray array = obj.optJSONArray("manuals");
			for (int i = 0; i < array.length(); i++) {
				JSONObject item = array.getJSONObject(i);
				ChessManual chessManual = new ChessManual();
				chessManual.setMatchName(item.optString("matchName"));
				chessManual.setBlackName(item.optString("blackName"));
				chessManual.setWhiteName(item.optString("whiteName"));
				chessManual.setCharset(item.optString("sgfCharset"));
				chessManual.setSgfUrl(item.optString("sgfUrl"));
				chessManual.setMatchTime(item.optString("matchTime"));
				chessManual.setMatchResult(item.optString("matchResult"));
				chessManual.setMatchInfo(item.optString("matchInfo"));
				chessManual.setType(ChessManual.NET_CHESS_MANUAL);
				chessManuals.add(chessManual);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return chessManuals;
	}

}
