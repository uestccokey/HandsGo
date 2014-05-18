/*
 * 
 */
package com.soyomaker.handsgo.parser;

import java.io.IOException;
import java.io.InputStream;

import org.json.JSONException;

import android.text.TextUtils;

import com.soyomaker.handsgo.util.StringUtil;

/**
 * The Class BaseParser.
 */
public abstract class BaseParser implements IParser {

	/**
	 * Parses the.
	 * 
	 * @param jsonString
	 *            the json string
	 * @return the object
	 * @throws JSONException
	 *             the jSON exception
	 */
	protected abstract Object parse(String jsonString) throws JSONException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.soyomaker.handsgo.parser.IParser#parse(java.io.InputStream)
	 */
	public Object parse(InputStream in) {
		try {
			if (null == in) {
				return null;
			}
			String jsonString = StringUtil.inputStream2String(in);
			if (TextUtils.isEmpty(jsonString)) {
				return null;
			} else {
				return parse(jsonString);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != in) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
