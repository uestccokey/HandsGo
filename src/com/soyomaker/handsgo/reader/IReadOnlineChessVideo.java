package com.soyomaker.handsgo.reader;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;

import com.soyomaker.handsgo.go.ChessVideo;

public interface IReadOnlineChessVideo {

	public ArrayList<ChessVideo> getOnlineChessVideos(Context context)
			throws IOException;
}
