/*
 * 
 */
package com.soyomaker.handsgo.go;

import com.soyomaker.handsgo.go.model.grid.GridModel;

public interface GoBoardModel {

	public void forcePut(int x, int y, GoPoint point);

	public void forceRemove(int x, int y);

	public void performPut(int x, int y, GoPoint point) throws GoException;

	public void performRemove(int x, int y) throws GoException;

	public int getBoardSize();

	public GridModel getGridModel();

	public GoPoint getPoint(int x, int y);
}
