package com.soyomaker.handsgo.go;

/**
 * 比赛模型.
 * 
 * @author like
 * 
 */
public class Match {

	public static final int NO_TIME_LIMIT = 0;

	public String gameName = "";

	public String gameComment = "";

	public String blackName = "";

	public String blackTeam = "";

	public String blackRank = "";

	public int boardSize = 19;

	public String komi = "7.5";

	public String handicap = "0";

	public String date = "";

	public String matchName = "";

	public String place = "";

	public String result = "";

	public String source = "";

	public String time = "";

	public String whiteName = "";

	public String whiteTeam = "";

	public String whiteRank = "";

	/** 棋谱的内容. */
	public String sgfSource = "";
}
