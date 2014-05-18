/*
 * 
 */
package com.soyomaker.handsgo.parser;

import java.io.InputStream;

// TODO: Auto-generated Javadoc
/**
 * 解析器接口.
 */
public interface IParser {

	/**
	 * 解析输入流.
	 * 
	 * @param in
	 *            ：被解析的输入流
	 * @return the object
	 */
	public Object parse(InputStream in);
}
