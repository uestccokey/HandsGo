/*
 * 
 */
package com.soyomaker.handsgo.go.model.grid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

public abstract class Grid extends View implements GridModelListener {

	protected int cubicWidth, cubicHeight;

	protected int leftBorder, topBorder;

	protected GridListener listener = null;

	protected Canvas memGraphics;

	protected Bitmap memImage;

	protected GridModel model;

	public Grid(Context context, GridModel m, int lb, int tb, int cw, int ch) {
		super(context);
		model = m;
		model.setGridModelListener(this);

		leftBorder = lb;
		topBorder = tb;

		cubicWidth = cw;
		cubicHeight = ch;

		memImage = null;
	}

	public Grid(Context context, AttributeSet attrs, GridModel m, int lb,
			int tb, int cw, int ch) {
		super(context, attrs);
		model = m;
		model.setGridModelListener(this);

		leftBorder = lb;
		topBorder = tb;

		cubicWidth = cw;
		cubicHeight = ch;

		memImage = null;
	}

	public abstract void drawBackground(Canvas g);

	public abstract void drawCubic(Canvas g, int col, int row);

	public int getCubicHeight() {
		return cubicHeight;
	}

	public int getCubicWidth() {
		return cubicWidth;
	}

	public int getLeftBorder() {
		return leftBorder;
	}

	public GridModel getModel() {
		return model;
	}

	public int getTopBorder() {
		return topBorder;
	}

	@Override
	public void gridDataChanged() {
		this.postInvalidate();
	}

	@Override
	public void onDraw(Canvas g) {
		// 双缓冲
		if (memImage == null) {
			memImage = Bitmap.createBitmap(this.getWidth(), this.getHeight(),
					Config.RGB_565);
			memGraphics = new Canvas(memImage);
		}
		drawBackground(memGraphics);
		int cols = model.getColumns();
		int rows = model.getRows();
		for (int i = 0; i < cols; i++) {
			for (int j = 0; j < rows; j++) {
				memGraphics.translate(leftBorder + i * cubicWidth, topBorder
						+ j * cubicHeight);
				drawCubic(memGraphics, i, j);
				memGraphics.translate(-(leftBorder + i * cubicWidth),
						-(topBorder + j * cubicHeight));
			}
		}
		g.drawBitmap(memImage, 0, 0, null);
	}

	public void pointerMoved(int x, int y) {
		if (listener == null) {
			return;
		}

		int cols = model.getColumns();
		int rows = model.getRows();

		int i = (x - leftBorder) / cubicWidth;
		int j = (y - topBorder) / cubicHeight;

		if ((i < 0) || (i >= cols) || (j < 0) || (j >= rows)) {
			return;
		}

		listener.touchMoved(i, j);
	}

	public void pointerPressed(int x, int y) {
		if (listener == null) {
			return;
		}

		int cols = model.getColumns();
		int rows = model.getRows();

		int i = (x - leftBorder) / cubicWidth;
		int j = (y - topBorder) / cubicHeight;

		if ((i < 0) || (i >= cols) || (j < 0) || (j >= rows)) {
			return;
		}

		listener.touchPressed(i, j);
	}

	public void pointerReleased(int x, int y) {
		if (listener == null) {
			return;
		}

		int cols = model.getColumns();
		int rows = model.getRows();

		int i = (x - leftBorder) / cubicWidth;
		int j = (y - topBorder) / cubicHeight;

		if ((i < 0) || (i >= cols) || (j < 0) || (j >= rows)) {
			return;
		}

		listener.touchReleased(i, j);
	}

	public void setCubicHeight(int cubicHeight) {
		this.cubicHeight = cubicHeight;
	}

	public void setCubicWidth(int cubicWidth) {
		this.cubicWidth = cubicWidth;
	}

	public void setGridListener(GridListener l) {
		listener = l;
	}

	public void setLeftBorder(int leftBorder) {
		this.leftBorder = leftBorder;
	}

	public void setModel(GridModel m) {
		if (model != null) {
			model.setGridModelListener(null);
		}

		model = m;
		model.setGridModelListener(this);
	}

	public void setTopBorder(int topBorder) {
		this.topBorder = topBorder;
	}
}