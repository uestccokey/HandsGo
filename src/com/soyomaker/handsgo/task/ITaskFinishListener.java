/*
 * 
 */
package com.soyomaker.handsgo.task;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving ITaskFinish events. The class that is
 * interested in processing a ITaskFinish event implements this interface, and
 * the object created with that class is registered with a component using the
 * component's <code>addITaskFinishListener<code> method. When
 * the ITaskFinish event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see ITaskFinishEvent
 */
public interface ITaskFinishListener {

	/**
	 * 上报的任务处理完成事件.
	 * 
	 * @param taskResult
	 *            the task result
	 */
	public void onTaskFinished(TaskResult taskResult);
}
