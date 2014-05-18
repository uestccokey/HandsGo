/*
 * 
 */
package com.soyomaker.handsgo.go;

import java.io.Serializable;

/**
 * 围棋棋谱模型.
 * 
 * @author like
 * 
 */
public class ChessManual implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final int NET_CHESS_MANUAL = 0;
	public static final int SDCARD_CHESS_MANUAL = 1;

	private int id = -1;

	private String sgfUrl = null;

	private String blackName = null;

	private String whiteName = null;

	private String matchName = null;

	private String matchResult = null;

	private String matchTime = null;

	private String matchInfo;

	/** 如果是网页棋谱，则表示url页面的字符集，如果是本地棋谱，则表示本地文件的字符集. */
	private String charset = null;

	private String sgfContent = null;

	private int type = NET_CHESS_MANUAL;

	private boolean isLive = false;

	private int mGroupId = 1;

	public int getGroupId() {
		return mGroupId;
	}

	public void setGroupId(int groupId) {
		this.mGroupId = groupId;
	}

	public String getMatchInfo() {
		return matchInfo;
	}

	public void setMatchInfo(String matchInfo) {
		this.matchInfo = matchInfo;
	}

	public boolean isLive() {
		return isLive;
	}

	public void setLive(boolean isLive) {
		this.isLive = isLive;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getSgfContent() {
		return sgfContent;
	}

	public void setSgfContent(String sgfContent) {
		this.sgfContent = sgfContent;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public String getBlackName() {
		return blackName;
	}

	public void setBlackName(String blackName) {
		this.blackName = blackName;
	}

	public String getWhiteName() {
		return whiteName;
	}

	public void setWhiteName(String whiteName) {
		this.whiteName = whiteName;
	}

	public String getMatchName() {
		return matchName;
	}

	public void setMatchName(String matchName) {
		this.matchName = matchName;
	}

	public String getMatchResult() {
		return matchResult;
	}

	public void setMatchResult(String matchResult) {
		this.matchResult = matchResult;
	}

	public String getMatchTime() {
		return matchTime;
	}

	public void setMatchTime(String matchTime) {
		this.matchTime = matchTime;
	}

	public String getSgfUrl() {
		return sgfUrl;
	}

	public void setSgfUrl(String sgfUrl) {
		this.sgfUrl = sgfUrl;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sgfUrl == null) ? 0 : sgfUrl.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChessManual other = (ChessManual) obj;
		if (sgfUrl == null) {
			if (other.sgfUrl != null)
				return false;
		} else if (!sgfUrl.equals(other.sgfUrl))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "时间 " + this.matchTime + " 比赛 " + this.matchName + " 黑方 "
				+ this.blackName + " 白方 " + this.whiteName + " 结果 "
				+ this.matchResult;
	}
}
