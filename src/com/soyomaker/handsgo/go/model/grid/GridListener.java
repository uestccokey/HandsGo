/*
 * 
 */
package com.soyomaker.handsgo.go.model.grid;

public interface GridListener {

	public void touchPressed(int col, int row);

	public void touchReleased(int col, int row);

	public void touchMoved(int col, int row);
}
