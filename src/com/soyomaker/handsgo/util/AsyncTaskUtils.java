/*
 * 
 */
package com.soyomaker.handsgo.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;

/**
 * 封装AsyncTask类，用于执行异步耗时操作。.
 * 
 * @author 张代松 2012-7-24
 */
public final class AsyncTaskUtils extends AsyncTask<Integer, Integer, Object>
		implements OnCancelListener {

	/** The Constant FLAG_DEFAULT. */
	public static final int FLAG_DEFAULT = 0;

	/** The Constant DEFAULT_PROGRESS_MESSAGE. */
	private static final String DEFAULT_PROGRESS_MESSAGE = "Please waiting...";

	/** Progress dialog. */
	private ProgressDialog progressDialog;

	/** The context. */
	private Context context;

	/** The show progress. */
	private boolean showProgress;

	/** The progress cancelable. */
	private boolean progressCancelable;

	/** The progress title. */
	private String progressTitle;

	/** The progress message. */
	private String progressMessage;

	/** The on cancel listener. */
	private AsyncTaskCancelListener onCancelListener;

	/** The cancel. */
	private boolean cancel;

	/** The listener. */
	private AsyncTaskListener listener;

	/** The flag. */
	private int flag;

	/**
	 * Private constructor.
	 * 
	 * @param context
	 *            the context
	 * @param listener
	 *            the listener
	 * @param showProgress
	 *            the show progress
	 */
	private AsyncTaskUtils(Context context, AsyncTaskListener listener,
			boolean showProgress) {
		this.context = context;
		this.listener = listener;
		this.showProgress = showProgress;
	}

	/**
	 * Creates the.
	 * 
	 * @param context
	 *            the context
	 * @param listener
	 *            the listener
	 * @param showProgress
	 *            the show progress
	 * @return the async task utils
	 */
	public static AsyncTaskUtils create(Context context,
			AsyncTaskListener listener, boolean showProgress) {
		AsyncTaskUtils task = new AsyncTaskUtils(context, listener,
				showProgress);
		return task;
	}

	/**
	 * Creates the.
	 * 
	 * @param context
	 *            the context
	 * @param listener
	 *            the listener
	 * @param progressMsgId
	 *            the progress msg id
	 * @return the async task utils
	 */
	public static AsyncTaskUtils create(Context context,
			AsyncTaskListener listener, int progressMsgId) {
		AsyncTaskUtils task = new AsyncTaskUtils(context, listener, true);
		task.setProgressDialog(null, progressMsgId, true);
		return task;
	}

	/**
	 * Creates the.
	 * 
	 * @param context
	 *            the context
	 * @param listener
	 *            the listener
	 * @param progressMsgId
	 *            the progress msg id
	 * @param cancleable
	 *            the cancleable
	 * @return the async task utils
	 */
	public static AsyncTaskUtils create(Context context,
			AsyncTaskListener listener, int progressMsgId, boolean cancleable) {
		AsyncTaskUtils task = new AsyncTaskUtils(context, listener, true);
		task.setProgressDialog(null, progressMsgId, cancleable);
		return task;
	}

	/**
	 * Creates the.
	 * 
	 * @param context
	 *            the context
	 * @param listener
	 *            the listener
	 * @param progressMsg
	 *            the progress msg
	 * @return the async task utils
	 */
	public static AsyncTaskUtils create(Context context,
			AsyncTaskListener listener, String progressMsg) {
		AsyncTaskUtils task = new AsyncTaskUtils(context, listener, true);
		task.setProgressDialog(null, progressMsg, false);
		return task;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Object doInBackground(Integer... params) {
		return listener.doInBackground(this, params[0]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(Object result) {
		super.onPostExecute(result);

		if (progressDialog != null) {
			progressDialog.dismiss();
		}

		if (!cancel) {
			listener.onPostExecute(this, result, flag);
		} else if (onCancelListener != null) {
			onCancelListener.onCancel(this);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		if (showProgress) {
			progressDialog = ProgressDialog.show(context, progressTitle,
					progressMessage == null ? DEFAULT_PROGRESS_MESSAGE
							: progressMessage, true, progressCancelable, this);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.content.DialogInterface.OnCancelListener#onCancel(android.content
	 * .DialogInterface)
	 */
	@Override
	public void onCancel(DialogInterface dialog) {
		cancel = true;
	}

	/**
	 * Executes the task with the no parameters, flag is default 0.
	 */
	public void execute() {
		this.execute(0);
	}

	/**
	 * Executes the task with the specify flag.
	 * 
	 * @param flag
	 *            the flag
	 */
	public void execute(int flag) {
		this.flag = flag;
		this.cancel = false;

		super.execute(flag);
	}

	/**
	 * Sets the progress dialog.
	 * 
	 * @param title
	 *            the title
	 * @param msg
	 *            the msg
	 * @param cancelable
	 *            the cancelable
	 * @return the async task utils
	 */
	public AsyncTaskUtils setProgressDialog(String title, String msg,
			boolean cancelable) {
		this.progressTitle = title;
		this.progressMessage = msg;
		this.progressCancelable = cancelable;

		return this;
	}

	/**
	 * Sets the progress dialog.
	 * 
	 * @param title
	 *            the title
	 * @param msg
	 *            the msg
	 * @param cancelable
	 *            the cancelable
	 * @param onCancelListener
	 *            the on cancel listener
	 * @return the async task utils
	 */
	public AsyncTaskUtils setProgressDialog(String title, String msg,
			boolean cancelable, AsyncTaskCancelListener onCancelListener) {
		this.progressTitle = title;
		this.progressMessage = msg;
		this.progressCancelable = cancelable;
		this.onCancelListener = onCancelListener;

		return this;
	}

	/**
	 * Sets the progress dialog.
	 * 
	 * @param title
	 *            the title
	 * @param msgId
	 *            the msg id
	 * @param cancelable
	 *            the cancelable
	 * @return the async task utils
	 */
	public AsyncTaskUtils setProgressDialog(String title, int msgId,
			boolean cancelable) {
		this.progressTitle = title;
		this.progressMessage = context.getText(msgId).toString();
		this.progressCancelable = cancelable;

		return this;
	}

	/**
	 * Sets the progress dialog.
	 * 
	 * @param title
	 *            the title
	 * @param msgId
	 *            the msg id
	 * @param cancelable
	 *            the cancelable
	 * @param onCancelListener
	 *            the on cancel listener
	 * @return the async task utils
	 */
	public AsyncTaskUtils setProgressDialog(String title, int msgId,
			boolean cancelable, AsyncTaskCancelListener onCancelListener) {
		this.progressTitle = title;
		this.progressMessage = context.getText(msgId).toString();
		this.progressCancelable = cancelable;
		this.onCancelListener = onCancelListener;

		return this;
	}

	/**
	 * Get the field cancel.
	 * 
	 * @return the cancel
	 */
	public boolean isCancel() {
		return cancel;
	}

	/**
	 * Set the field cancel.
	 * 
	 * @param cancel
	 *            the cancel to set
	 */
	public void setCancel(boolean cancel) {
		this.cancel = cancel;
	}

	/**
	 * The listener interface for receiving asyncTask events. The class that is
	 * interested in processing a asyncTask event implements this interface, and
	 * the object created with that class is registered with a component using
	 * the component's <code>addAsyncTaskListener<code> method. When
	 * the asyncTask event occurs, that object's appropriate
	 * method is invoked.
	 * 
	 * @author 张代松 2012-7-24
	 */
	public interface AsyncTaskListener {

		/**
		 * Do in background.
		 * 
		 * @param task
		 *            the task
		 * @param flag
		 *            the flag
		 * @return the int
		 */
		Object doInBackground(AsyncTaskUtils task, int flag);

		/**
		 * On post execute.
		 * 
		 * @param task
		 *            the task
		 * @param result
		 *            the result
		 * @param flag
		 *            the flag
		 */
		void onPostExecute(AsyncTaskUtils task, Object result, int flag);
	}

	/**
	 * The listener interface for receiving asyncTaskCancel events. The class
	 * that is interested in processing a asyncTaskCancel event implements this
	 * interface, and the object created with that class is registered with a
	 * component using the component's
	 * <code>addAsyncTaskCancelListener<code> method. When
	 * the asyncTaskCancel event occurs, that object's appropriate
	 * method is invoked.
	 * 
	 * @author 张代松 2012-7-24
	 */
	public interface AsyncTaskCancelListener {

		/**
		 * On cancel.
		 * 
		 * @param task
		 *            the task
		 */
		void onCancel(AsyncTaskUtils task);
	}

	/**
	 * 定义异步任务结果code， 用于判断成功或者失败类型.
	 * 
	 * @author 张代松 2012-7-25
	 */
	public static interface TaskResultCode {
		// 操作成功
		/** The code ok. */
		int CODE_OK = 0;

		// 网络相关
		/** The code network error. */
		int CODE_NETWORK_ERROR = -1;

		/** The code server error. */
		int CODE_SERVER_ERROR = -2;

		// 操作失败
		/** The code parameter error. */
		int CODE_PARAMETER_ERROR = -3;

		/** The code error. */
		int CODE_ERROR = -4;

		/** The code not login error. */
		int CODE_NOT_LOGIN_ERROR = -5;

		// 网络返回数据
		/** The code response data error. */
		int CODE_RESPONSE_DATA_ERROR = -6;

		/** The code response data empty error. */
		int CODE_RESPONSE_DATA_EMPTY_ERROR = -7;

		/** The code response data parse error. */
		int CODE_RESPONSE_DATA_PARSE_ERROR = -8;

	}
}
