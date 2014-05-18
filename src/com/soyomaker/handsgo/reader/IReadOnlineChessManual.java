/*
 * 
 */
package com.soyomaker.handsgo.reader;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;

import com.soyomaker.handsgo.go.ChessManual;

/**
 * The Interface IReadOnlineChessManual.
 */
public interface IReadOnlineChessManual {

	/**
	 * Gets the online chess manual web page.
	 * 
	 * @param context
	 *            the context
	 * @return the online chess manual web page
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public ArrayList<ChessManual> getOnlineChessManuals(Context context)
			throws IOException;
}
