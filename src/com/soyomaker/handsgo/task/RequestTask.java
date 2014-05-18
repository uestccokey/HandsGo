/*
 * 
 */
package com.soyomaker.handsgo.task;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;

import android.util.Log;

import com.soyomaker.handsgo.HandsGoApplication;
import com.soyomaker.handsgo.parser.IParser;
import com.soyomaker.handsgo.util.HttpUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class RequestTask.
 */
public class RequestTask extends GenericTask {

	/** The Constant TAG. */
	private static final String TAG = "RequestTask";

	/** get请求. */
	public static final String HTTP_GET = "GET";

	/** post请求. */
	public static final String HTTP_POST = "POST";

	/** The Constant PARAM_URL. */
	public static final String PARAM_URL = "url";

	/** The Constant PARAM_HTTP_METHOD. */
	public static final String PARAM_HTTP_METHOD = "httpmethod";

	/** 解析器. */
	private IParser mParser;

	/** The m post params. */
	private List<NameValuePair> mPostParams;

	/** The m type. */
	private String mType;

	/** The m url. */
	private String mUrl;

	/** 相关对象的引用. */
	private Object mExtra;

	/**
	 * Instantiates a new request task.
	 * 
	 * @param parser
	 *            the parser
	 */
	public RequestTask(IParser parser) {
		this.mParser = parser;
	}

	/**
	 * Sets the post params.
	 * 
	 * @param params
	 *            the new post params
	 */
	public void setPostParams(List<NameValuePair> params) {
		mPostParams = params;
	}

	/**
	 * Gets the parser.
	 * 
	 * @return the parser
	 */
	public IParser getParser() {
		return mParser;
	}

	/**
	 * Sets the parser.
	 * 
	 * @param mParser
	 *            the new parser
	 */
	public void setParser(IParser mParser) {
		this.mParser = mParser;
	}

	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	public String getType() {
		return mType;
	}

	/**
	 * Sets the type.
	 * 
	 * @param type
	 *            the new type
	 */
	public void setType(String type) {
		this.mType = type;
	}

	/**
	 * Gets the request url.
	 * 
	 * @return the request url
	 */
	public String getRequestUrl() {
		return mUrl;
	}

	/**
	 * Gets the extra.
	 * 
	 * @return the extra
	 */
	public Object getExtra() {
		return mExtra;
	}

	/**
	 * Sets the extra.
	 * 
	 * @param relativeObj
	 *            the new extra
	 */
	public void setExtra(Object relativeObj) {
		this.mExtra = relativeObj;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.soyomaker.handsgo.task.GenericTask#onCancelled()
	 */
	@Override
	protected void onCancelled() {
		// donothing,当RequestTask被取消后，不再回调listener
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.soyomaker.handsgo.task.GenericTask#onPostExecute(com.soyomaker.handsgo
	 * .task.TaskResult)
	 */
	@Override
	protected void onPostExecute(TaskResult result) {
		// 当task没被取消时，调用父类回调taskFinished
		if (!isCancelled()) {
			super.onPostExecute(result);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.soyomaker.handsgo.task.AbsNormalAsyncTask#doInBackground(Params[])
	 */
	@Override
	protected TaskResult doInBackground(TaskParams... params) {
		TaskResult result = new TaskResult(-1, this, null);
		if (params == null) {
			return result;
		}

		mUrl = params[0].getString(PARAM_URL);
		mType = params[0].getString(PARAM_HTTP_METHOD);
		Log.i(TAG, "request url: " + mUrl);

		HttpClient client = null;
		HttpResponse response = null;
		HttpEntity entity = null;
		try {
			client = HttpUtil.getHttpClient(HandsGoApplication.sContext);
			if (HTTP_POST.equals(mType)) {
				response = HttpUtil.doPostRequest(client, mUrl, mPostParams);
			} else {
				response = HttpUtil.doGetRequest(client, mUrl);
			}
			int stateCode = response.getStatusLine().getStatusCode();
			result.stateCode = stateCode;
			if (stateCode == HttpStatus.SC_OK
					|| stateCode == HttpStatus.SC_PARTIAL_CONTENT) {
				entity = response.getEntity();
				InputStream inputStream = entity.getContent();
				if (inputStream != null && mParser != null) {
					Object obj = mParser.parse(inputStream);
					result.retObj = obj;
				}
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
		return result;
	}
}
