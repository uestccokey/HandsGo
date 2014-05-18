/*
 * 
 */
package com.soyomaker.handsgo.task;

// TODO: Auto-generated Javadoc
/**
 * Task执行完毕后返回的结果.
 * 
 * @author Tsimle
 */
public class TaskResult {

	/** The state code. */
	public int stateCode; // 状态码，一般为HTTP的响应码

	/** The task. */
	public GenericTask task; // 任务对象本身

	/** The ret obj. */
	public Object retObj; // 任务处理结果对象。也可以是错误消息

	/**
	 * Instantiates a new task result.
	 */
	public TaskResult() {

	}

	/**
	 * Instantiates a new task result.
	 * 
	 * @param stateCode
	 *            the state code
	 * @param task
	 *            the task
	 * @param result
	 *            the result
	 */
	public TaskResult(int stateCode, GenericTask task, Object result) {
		super();
		this.stateCode = stateCode;
		this.task = task;
		this.retObj = result;
	}

}
