/*
 * 
 */
package com.soyomaker.handsgo.task;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.Process;

/**
 * 修改AsyncTask，修改线程池大小 《一般异步任务线程池》.
 * 
 * @param <Params>
 *            the generic type
 * @param <Progress>
 *            the generic type
 * @param <Result>
 *            the generic type
 * @author Tsimle
 */
public abstract class AbsNormalAsyncTask<Params, Progress, Result> {

	/** The Constant LOG_TAG. */
	private static final String LOG_TAG = "FictionAsyncTask";

	/** The Constant CORE_POOL_SIZE. */
	private static final int CORE_POOL_SIZE = 2;

	/** The Constant MAXIMUM_POOL_SIZE. */
	private static final int MAXIMUM_POOL_SIZE = 16;

	/** The Constant KEEP_ALIVE. */
	private static final int KEEP_ALIVE = 8;

	/** The Constant sWorkQueue. */
	private static final BlockingQueue<Runnable> sWorkQueue = new LinkedBlockingQueue<Runnable>(
			5);

	/** The Constant sThreadFactory. */
	private static final ThreadFactory sThreadFactory = new ThreadFactory() {
		private final AtomicInteger mCount = new AtomicInteger(1);

		public Thread newThread(Runnable r) {
			return new Thread(r, "AsyncTask #" + mCount.getAndIncrement());
		}
	};

	/** The Constant sExecutor. */
	private static final ThreadPoolExecutor sExecutor = new ThreadPoolExecutor(
			CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS,
			sWorkQueue, sThreadFactory);

	/** The Constant MESSAGE_POST_RESULT. */
	private static final int MESSAGE_POST_RESULT = 0x1;

	/** The Constant MESSAGE_POST_PROGRESS. */
	private static final int MESSAGE_POST_PROGRESS = 0x2;

	/** The Constant MESSAGE_POST_CANCEL. */
	private static final int MESSAGE_POST_CANCEL = 0x3;

	/** The Constant sHandler. */
	private static final InternalHandler sHandler = new InternalHandler();

	/** The m worker. */
	private final WorkerRunnable<Params, Result> mWorker;

	/** The m future. */
	private final FutureTask<Result> mFuture;

	/** The m status. */
	private volatile Status mStatus = Status.PENDING;

	/**
	 * Indicates the current status of the task. Each status will be set only
	 * once during the lifetime of a task.
	 */
	public enum Status {
		/**
		 * Indicates that the task has not been executed yet.
		 */
		PENDING,
		/**
		 * Indicates that the task is running.
		 */
		RUNNING,
		/**
		 * Indicates that {@link AsyncTask#onPostExecute} has finished.
		 */
		FINISHED,
	}

	/**
	 * Creates a new asynchronous task. This constructor must be invoked on the
	 * UI thread.
	 */
	public AbsNormalAsyncTask() {
		mWorker = new WorkerRunnable<Params, Result>() {
			public Result call() throws Exception {
				Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
				return doInBackground(mParams);
			}
		};

		mFuture = new FutureTask<Result>(mWorker) {
			@SuppressWarnings("unchecked")
			@Override
			protected void done() {
				Message message;
				Result result = null;

				try {
					result = get();
				} catch (InterruptedException e) {
					android.util.Log.w(LOG_TAG, e);
				} catch (ExecutionException e) {
					throw new RuntimeException(
							"An error occured while executing doInBackground()",
							e.getCause());
				} catch (CancellationException e) {
					message = sHandler.obtainMessage(MESSAGE_POST_CANCEL,
							new AsyncTaskResult<Result>(
									AbsNormalAsyncTask.this, (Result[]) null));
					message.sendToTarget();
					return;
				} catch (Throwable t) {
					throw new RuntimeException(
							"An error occured while executing "
									+ "doInBackground()", t);
				}

				message = sHandler.obtainMessage(MESSAGE_POST_RESULT,
						new AsyncTaskResult<Result>(AbsNormalAsyncTask.this,
								result));
				message.sendToTarget();
			}
		};
	}

	/**
	 * Returns the current status of this task.
	 * 
	 * @return The current status.
	 */
	public final Status getStatus() {
		return mStatus;
	}

	/**
	 * Override this method to perform a computation on a background thread. The
	 * specified parameters are the parameters passed to {@link #execute} by the
	 * caller of this task.
	 * 
	 * This method can call {@link #publishProgress} to publish updates on the
	 * UI thread.
	 * 
	 * @param params
	 *            The parameters of the task.
	 * 
	 * @return A result, defined by the subclass of this task.
	 * 
	 * @see #onPreExecute()
	 * @see #onPostExecute
	 * @see #publishProgress
	 */
	protected abstract Result doInBackground(Params... params);

	/**
	 * Runs on the UI thread before {@link #doInBackground}.
	 * 
	 * @see #onPostExecute
	 * @see #doInBackground
	 */
	protected void onPreExecute() {
	}

	/**
	 * Runs on the UI thread after {@link #doInBackground}. The specified result
	 * is the value returned by {@link #doInBackground} or null if the task was
	 * cancelled or an exception occured.
	 * 
	 * @param result
	 *            The result of the operation computed by
	 *            {@link #doInBackground}.
	 * 
	 * @see #onPreExecute
	 * @see #doInBackground
	 */
	protected void onPostExecute(Result result) {
	}

	/**
	 * Runs on the UI thread after {@link #publishProgress} is invoked. The
	 * specified values are the values passed to {@link #publishProgress}.
	 * 
	 * @param values
	 *            The values indicating progress.
	 * 
	 * @see #publishProgress
	 * @see #doInBackground
	 */
	protected void onProgressUpdate(Progress... values) {
	}

	/**
	 * Runs on the UI thread after {@link #cancel(boolean)} is invoked.
	 * 
	 * @see #cancel(boolean)
	 * @see #isCancelled()
	 */
	protected void onCancelled() {
	}

	/**
	 * Returns <tt>true</tt> if this task was cancelled before it completed
	 * normally.
	 * 
	 * @return <tt>true</tt> if task was cancelled before it completed
	 * 
	 * @see #cancel(boolean)
	 */
	public final boolean isCancelled() {
		return mFuture.isCancelled();
	}

	/**
	 * Attempts to cancel execution of this task. This attempt will fail if the
	 * task has already completed, already been cancelled, or could not be
	 * cancelled for some other reason. If successful, and this task has not
	 * started when <tt>cancel</tt> is called, this task should never run. If
	 * the task has already started, then the <tt>mayInterruptIfRunning</tt>
	 * parameter determines whether the thread executing this task should be
	 * interrupted in an attempt to stop the task.
	 * 
	 * @param mayInterruptIfRunning
	 *            <tt>true</tt> if the thread executing this task should be
	 *            interrupted; otherwise, in-progress tasks are allowed to
	 *            complete.
	 * 
	 * @return <tt>false</tt> if the task could not be cancelled, typically
	 *         because it has already completed normally; <tt>true</tt>
	 *         otherwise
	 * 
	 * @see #isCancelled()
	 * @see #onCancelled()
	 */
	public final boolean cancel(boolean mayInterruptIfRunning) {
		return mFuture.cancel(mayInterruptIfRunning);
	}

	/**
	 * Waits if necessary for the computation to complete, and then retrieves
	 * its result.
	 * 
	 * @return The computed result.
	 * @throws InterruptedException
	 *             If the current thread was interrupted while waiting.
	 * @throws ExecutionException
	 *             If the computation threw an exception.
	 */
	public final Result get() throws InterruptedException, ExecutionException {
		return mFuture.get();
	}

	/**
	 * Waits if necessary for at most the given time for the computation to
	 * complete, and then retrieves its result.
	 * 
	 * @param timeout
	 *            Time to wait before cancelling the operation.
	 * @param unit
	 *            The time unit for the timeout.
	 * @return The computed result.
	 * @throws InterruptedException
	 *             If the current thread was interrupted while waiting.
	 * @throws ExecutionException
	 *             If the computation threw an exception.
	 * @throws TimeoutException
	 *             If the wait timed out.
	 */
	public final Result get(long timeout, TimeUnit unit)
			throws InterruptedException, ExecutionException, TimeoutException {
		return mFuture.get(timeout, unit);
	}

	/**
	 * Executes the task with the specified parameters. The task returns itself
	 * (this) so that the caller can keep a reference to it.
	 * 
	 * This method must be invoked on the UI thread.
	 * 
	 * @param params
	 *            The parameters of the task.
	 * @return This instance of AsyncTask. {@link AsyncTask.Status#RUNNING} or
	 *         {@link AsyncTask.Status#FINISHED}.
	 */
	public final AbsNormalAsyncTask<Params, Progress, Result> execute(
			Params... params) {
		if (mStatus != Status.PENDING) {
			switch (mStatus) {
			case RUNNING:
				throw new IllegalStateException("Cannot execute task:"
						+ " the task is already running.");
			case FINISHED:
				throw new IllegalStateException("Cannot execute task:"
						+ " the task has already been executed "
						+ "(a task can be executed only once)");
			default:
				break;
			}
		}

		mStatus = Status.RUNNING;

		onPreExecute();

		mWorker.mParams = params;
		sExecutor.execute(mFuture);

		return this;
	}

	/**
	 * This method can be invoked from {@link #doInBackground} to publish
	 * updates on the UI thread while the background computation is still
	 * running. Each call to this method will trigger the execution of
	 * 
	 * @param values
	 *            The progress values to update the UI with.
	 *            {@link #onProgressUpdate} on the UI thread.
	 * @see #onProgressUpdate
	 * @see #doInBackground
	 */
	protected final void publishProgress(Progress... values) {
		sHandler.obtainMessage(MESSAGE_POST_PROGRESS,
				new AsyncTaskResult<Progress>(this, values)).sendToTarget();
	}

	/**
	 * Finish.
	 * 
	 * @param result
	 *            the result
	 */
	private void finish(Result result) {
		if (isCancelled())
			result = null;
		onPostExecute(result);
		mStatus = Status.FINISHED;
	}

	/**
	 * The Class InternalHandler.
	 */
	private static class InternalHandler extends Handler {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.Handler#handleMessage(android.os.Message)
		 */
		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public void handleMessage(Message msg) {
			AsyncTaskResult result = (AsyncTaskResult) msg.obj;
			switch (msg.what) {
			case MESSAGE_POST_RESULT:
				// There is only one result
				result.mTask.finish(result.mData[0]);
				break;
			case MESSAGE_POST_PROGRESS:
				result.mTask.onProgressUpdate(result.mData);
				break;
			case MESSAGE_POST_CANCEL:
				result.mTask.onCancelled();
				break;
			}
		}
	}

	/**
	 * The Class WorkerRunnable.
	 * 
	 * @param <Params>
	 *            the generic type
	 * @param <Result>
	 *            the generic type
	 */
	private static abstract class WorkerRunnable<Params, Result> implements
			Callable<Result> {

		/** The m params. */
		Params[] mParams;
	}

	/**
	 * The Class AsyncTaskResult.
	 * 
	 * @param <Data>
	 *            the generic type
	 */
	@SuppressWarnings("rawtypes")
	private static class AsyncTaskResult<Data> {

		/** The m task. */
		final AbsNormalAsyncTask mTask;

		/** The m data. */
		final Data[] mData;

		/**
		 * Instantiates a new async task result.
		 * 
		 * @param task
		 *            the task
		 * @param data
		 *            the data
		 */
		AsyncTaskResult(AbsNormalAsyncTask task, Data... data) {
			mTask = task;
			mData = data;
		}
	}
}