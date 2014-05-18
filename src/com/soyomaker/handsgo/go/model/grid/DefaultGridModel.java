package com.soyomaker.handsgo.go.model.grid;

import com.soyomaker.handsgo.go.GoPoint;

public class DefaultGridModel implements GridModel {

	protected GoPoint blocks[][];

	protected int cols, rows;

	protected GridModelListener listener = null;

	public DefaultGridModel(int c, int r) {
		cols = c;
		rows = r;

		blocks = new GoPoint[cols][rows];
		for (int i = 0; i < cols; i++) {
			for (int j = 0; j < rows; j++) {
				blocks[i][j] = null;
			}
		}
	}

	/**
	 * AbstractGridModel subclasses must call this method <b>after</b> one or
	 * more chessmen removed.
	 */
	@Override
	public void fireDataChanged() {
		if (listener != null) {
			listener.gridDataChanged();
		}
	}

	@Override
	public int getColumns() {
		return cols;
	}

	@Override
	public int getRows() {
		return rows;
	}

	@Override
	public boolean isEmpty(int x, int y) {
		if (blocks[x][y] == null) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void reset(int c, int r) {
		cols = c;
		rows = r;

		blocks = new GoPoint[cols][rows];
		for (int i = 0; i < cols; i++) {
			for (int j = 0; j < rows; j++) {
				blocks[i][j] = null;
			}
		}
	}

	@Override
	public void setGridModelListener(GridModelListener l) {
		listener = l;
	}

	@Override
	public GoPoint getObject(int x, int y) {
		return blocks[x][y];
	}

	@Override
	public void setObject(int x, int y, GoPoint obj) {
		blocks[x][y] = obj;
		fireDataChanged();
	}
}
