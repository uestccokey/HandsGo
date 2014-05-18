/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.soyomaker.handsgo.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;

import android.content.Context;
import android.util.Log;

/**
 * The Class WebUtil.
 * 
 * @author Administrator
 */
public class WebUtil {

	/**
	 * Gets the http get.
	 * 
	 * @param context
	 *            the context
	 * @param url
	 *            the url
	 * @param charset
	 *            the charset
	 * @return the http get
	 */
	public static String getHttpGet(Context context, String url, String charset) {
		Log.e("URL", url);
		String resultString = "";
		HttpClient client = null;
		HttpResponse response = null;
		HttpEntity entity = null;
		try {
			client = HttpUtil.getHttpClient(context);
			response = HttpUtil.doGetRequest(client, url);
			int stateCode = response.getStatusLine().getStatusCode();
			if (stateCode == HttpStatus.SC_OK
					|| stateCode == HttpStatus.SC_PARTIAL_CONTENT) {
				entity = response.getEntity();
				InputStream inputStream = entity.getContent();
				resultString = StringUtil.inputStream2String(inputStream,
						charset);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (entity != null) {
				try {
					entity.consumeContent();
				} catch (IOException e) {
					e.printStackTrace();
				}
				entity = null;
			}
			if (client != null) {
				client.getConnectionManager().shutdown();
			}
		}
		return resultString;
	}

	/**
	 * Gets the http get.
	 * 
	 * @param context
	 *            the context
	 * @param url
	 *            the url
	 * @return the http get
	 */
	public static String getHttpGet(Context context, String url) {
		return getHttpGet(context, url, "utf-8");
	}
}
