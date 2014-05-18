package com.soyomaker.handsgo.go;

import java.io.Serializable;

/**
 * 围棋视频模型.
 * 
 * @author like
 * 
 */
public class ChessVideo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String mVideoTitle;

	private String mVideoImageUrl;

	private String mVideoUrl;

	private String mVideoId;

	public String getVideoId() {
		return mVideoId;
	}

	public void setVideoId(String mVideoId) {
		this.mVideoId = mVideoId;
	}

	public String getVideoTitle() {
		return mVideoTitle;
	}

	public void setVideoTitle(String mVideoTitle) {
		this.mVideoTitle = mVideoTitle;
	}

	public String getVideoImageUrl() {
		return mVideoImageUrl;
	}

	public void setVideoImageUrl(String mVideoImageUrl) {
		this.mVideoImageUrl = mVideoImageUrl;
	}

	public String getVideoUrl() {
		return mVideoUrl;
	}

	public void setVideoUrl(String mVideoUrl) {
		this.mVideoUrl = mVideoUrl;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((mVideoId == null) ? 0 : mVideoId.hashCode());
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
		ChessVideo other = (ChessVideo) obj;
		if (mVideoId == null) {
			if (other.mVideoId != null)
				return false;
		} else if (!mVideoId.equals(other.mVideoId))
			return false;
		return true;
	}
}
