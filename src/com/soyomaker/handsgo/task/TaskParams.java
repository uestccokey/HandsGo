/*
 * 
 */
package com.soyomaker.handsgo.task;

import java.util.HashMap;

// TODO: Auto-generated Javadoc
/**
 * 调用Task可添加的参数.
 * 
 * @author Tsimle
 */
public class TaskParams {

	/** The params. */
	private HashMap<String, Object> params = null;

	/**
	 * Instantiates a new task params.
	 */
	public TaskParams() {
		params = new HashMap<String, Object>();
	}

	/**
	 * Instantiates a new task params.
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public TaskParams(String key, Object value) {
		this();
		put(key, value);
	}

	/**
	 * Put.
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public void put(String key, Object value) {
		params.put(key, value);
	}

	/**
	 * Gets the.
	 * 
	 * @param key
	 *            the key
	 * @return the object
	 */
	public Object get(String key) {
		return params.get(key);
	}

	/**
	 * Get the string associated with a key.
	 * 
	 * @param key
	 *            A key string.
	 * @return A string which is the value.
	 */
	public String getString(String key) {
		Object object = get(key);
		return object == null ? null : object.toString();
	}

	/**
	 * Determine if params contains a specific key.
	 * 
	 * @param key
	 *            A key string.
	 * @return true if the key exists
	 */
	public boolean has(String key) {
		return this.params.containsKey(key);
	}

}