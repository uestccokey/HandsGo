package com.soyomaker.handsgo.go.model.grid;

import com.soyomaker.handsgo.go.GoPoint;

public interface GridModel {

	public void fireDataChanged();

	public int getColumns();

	public int getRows();

	public boolean isEmpty(int col, int row);

	public void reset(int c, int r);

	public void setGridModelListener(GridModelListener l);

	public GoPoint getObject(int col, int row);

	public void setObject(int col, int row, GoPoint c);
}
