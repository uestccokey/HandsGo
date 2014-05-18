/*
 * 
 */
package com.soyomaker.handsgo.task;

// TODO: Auto-generated Javadoc
/**
 * The Class GenericTask.
 */
public abstract class GenericTask extends
		AbsNormalAsyncTask<TaskParams, Object, TaskResult> {

	/** The m task finish listener. */
	private ITaskFinishListener mTaskFinishListener;

	/** The m result. */
	protected TaskResult mResult = new TaskResult(-1, this, null);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.soyomaker.handsgo.task.AbsNormalAsyncTask#onPostExecute(java.lang
	 * .Object)
	 */
	@Override
	protected void onPostExecute(TaskResult result) {
		super.onPostExecute(result);
		if (mTaskFinishListener != null) {
			mTaskFinishListener.onTaskFinished(result);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.soyomaker.handsgo.task.AbsNormalAsyncTask#onCancelled()
	 */
	@Override
	protected void onCancelled() {
		super.onCancelled();
		if (mTaskFinishListener != null) {
			mTaskFinishListener.onTaskFinished(null);
		}
	}

	/**
	 * Gets the task finish listener.
	 * 
	 * @return the task finish listener
	 */
	public ITaskFinishListener getTaskFinishListener() {
		return mTaskFinishListener;
	}

	/**
	 * Sets the task finish listener.
	 * 
	 * @param taskFinishListener
	 *            the new task finish listener
	 */
	public void setTaskFinishListener(ITaskFinishListener taskFinishListener) {
		this.mTaskFinishListener = taskFinishListener;
	}
}
