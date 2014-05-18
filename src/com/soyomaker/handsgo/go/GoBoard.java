package com.soyomaker.handsgo.go;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.soyomaker.handsgo.AppPrefrence;
import com.soyomaker.handsgo.Constant;
import com.soyomaker.handsgo.R;
import com.soyomaker.handsgo.analyze.TerrainAnalyze;
import com.soyomaker.handsgo.go.model.grid.Grid;

/**
 * 
 * @author like
 * 
 */
public class GoBoard extends Grid {

	protected float density = 1.0f;

	protected Bitmap blackCubic = null;

	protected Bitmap whiteCubic = null;

	protected int basicTextSize = 9;

	protected int defaultBackgroundColor = 0xffEE9A00;

	public GoBoard(Context context, AttributeSet attrs, GoBoardModel gm,
			int leftBorder, int topBorder, int cellWidth, int cellHeight,
			float density) {
		super(context, attrs, gm.getGridModel(), leftBorder, topBorder,
				cellWidth, cellHeight);
		this.density = density;
		initView();
	}

	public GoBoard(Context context, GoBoardModel gm, int leftBorder,
			int topBorder, int cellWidth, int cellHeight, float density) {
		super(context, gm.getGridModel(), leftBorder, topBorder, cellWidth,
				cellHeight);
		this.density = density;
		initView();
	}

	private void initView() {
		blackCubic = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.b2);
		whiteCubic = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.w2);
		defaultBackgroundColor = AppPrefrence.getChessBoardColor(getContext());
	}

	public int getBoardBackgroundColor() {
		return defaultBackgroundColor;
	}

	public void setBoardBackgroundColor(int color) {
		defaultBackgroundColor = color;
	}

	@Override
	public void drawBackground(Canvas g) {
		int b = getLeftBorder();
		int cb = getCubicWidth();
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		// 1，清屏
		paint.setColor(0xffffffff);
		g.drawRect(0, 0, getWidth(), getHeight(), paint);
		// 2，绘制棋盘背景色
		paint.setColor(defaultBackgroundColor);
		g.drawRect(0, 0, cb * model.getColumns() + 2 * b,
				cb * model.getColumns() + 2 * b, paint);

		if (AppPrefrence.getShowCoordinate(getContext())) {
			// 3，绘制棋盘坐标
			paint.setColor(0xff000000);
			paint.setTextSize(Math.round(basicTextSize * density));
			for (int i = 0; i < model.getRows(); i++) {
				g.drawText("" + (i + 1), 2 * density, (model.getRows() - i)
						* cb + 4, paint);
			}
			char c = 'A';
			for (int i = 0; i < model.getColumns(); i++) {
				char cc = (char) (c + i);
				if (cc >= 'I') {
					g.drawText("" + (char) (cc + 1), (i + 1) * cb - 4, cb - 4,
							paint);
				} else {
					g.drawText("" + cc, (i + 1) * cb - 4, cb - 4, paint);
				}
			}
		}

		// 4，绘制棋盘线
		paint.setColor(0xff000000);
		g.translate(b + cb / 2, b + cb / 2);
		for (int i = 0; i < model.getRows(); i++) {
			g.drawLine(0, i * cb, cb * (model.getColumns() - 1), i * cb, paint);
			g.drawLine(i * cb, 0, i * cb, cb * (model.getColumns() - 1), paint);
		}
		g.translate(-(b + cb / 2), -(b + cb / 2));
	}

	private void drawPiece(Canvas g, int col, int row) {
		GoPoint p = (GoPoint) getModel().getObject(col, row);
		int cb = getCubicWidth();
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		if (p.getPlayer() == GoPoint.BLACK) {
			// 绘制棋子
			switch (AppPrefrence.getChessPieceStyle(getContext())) {
			case Constant.CHESS_PIECE_STYLE_3D: {
				g.drawBitmap(blackCubic, new Rect(0, 0, blackCubic.getWidth(),
						blackCubic.getHeight()), new Rect(cb / 24, cb / 24,
						23 * cb / 24, 23 * cb / 24), paint);
			}
				break;
			case Constant.CHESS_PIECE_STYLE_2D: {
				paint.setColor(Color.BLACK);
				paint.setStyle(Style.FILL);
				g.drawArc(new RectF(cb / 24, cb / 24, cb * 23 / 24,
						cb * 23 / 24), 0, 360, true, paint);
			}
				break;
			}
			// 绘制手数
			if (p.getNumber() > 0
					&& AppPrefrence.getShowNumber(this.getContext())) {
				paint.setColor(Color.WHITE);
				TextPaint tp = new TextPaint();
				tp.set(paint);
				tp.setTextSize(Math.round(basicTextSize * density));
				float width = tp.measureText("" + p.getNumber());
				g.drawText("" + p.getNumber(), cb / 2 - width / 2 - 1, cb / 2
						+ Math.round(6 * density) / 2, tp);
			}
		} else if (p.getPlayer() == GoPoint.WHITE) {
			// 绘制棋子
			switch (AppPrefrence.getChessPieceStyle(getContext())) {
			case Constant.CHESS_PIECE_STYLE_3D: {
				g.drawBitmap(whiteCubic, new Rect(0, 0, whiteCubic.getWidth(),
						whiteCubic.getHeight()), new Rect(cb / 24, cb / 24,
						23 * cb / 24, 23 * cb / 24), paint);
			}
				break;
			case Constant.CHESS_PIECE_STYLE_2D: {
				paint.setColor(Color.WHITE);
				paint.setStyle(Style.FILL);
				g.drawArc(new RectF(cb / 24, cb / 24, cb * 23 / 24,
						cb * 23 / 24), 0, 360, true, paint);
				paint.setColor(Color.BLACK);
				paint.setStyle(Style.STROKE);
				g.drawArc(new RectF(cb / 24, cb / 24, cb * 23 / 24,
						cb * 23 / 24), 0, 360, true, paint);
			}
				break;
			}
			// 绘制手数
			if (p.getNumber() > 0
					&& AppPrefrence.getShowNumber(this.getContext())) {
				paint.setColor(Color.BLACK);
				TextPaint tp = new TextPaint();
				tp.set(paint);
				tp.setTextSize(Math.round(basicTextSize * density));
				float width = tp.measureText("" + p.getNumber());
				g.drawText("" + p.getNumber(), cb / 2 - width / 2 - 1, cb / 2
						+ Math.round(6 * density) / 2, tp);
			}
		} else {
			// 绘制星位
			if (((col == 3) || (col == 9) || (col == 15))
					&& ((row == 3) || (row == 9) || (row == 15))) {
				paint.setColor(0xff000000);
				paint.setStyle(Style.FILL);
				g.drawArc(new RectF(cb / 2 - 4, cb / 2 - 4, cb / 2 + 4,
						cb / 2 + 4), 0, 360, true, paint);
			}
		}
	}

	private void drawTreeNode(Canvas g, int col, int row) {
		GoPoint p = (GoPoint) getModel().getObject(col, row);
		int cb = getCubicWidth();
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		if (p.getTreeNode() != null) {
			paint.setColor(Color.GREEN);
			paint.setStyle(Style.STROKE);
			g.drawArc(new RectF(cb / 3, cb / 3, cb * 2 / 3, cb * 2 / 3), 0,
					360, true, paint);
		}
	}

	private void drawTerrain(Canvas g, int col, int row) {
		GoPoint p = (GoPoint) getModel().getObject(col, row);
		int cb = getCubicWidth();
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		if (p.getTerrain() >= TerrainAnalyze.THRESHOLD) {
			paint.setColor(0xff000000);
			paint.setStyle(Style.FILL);
			g.drawArc(new RectF(cb / 3, cb / 3, cb * 2 / 3, cb * 2 / 3), 0,
					360, true, paint);
		} else if (p.getTerrain() <= -TerrainAnalyze.THRESHOLD) {
			paint.setColor(0xffffffff);
			paint.setStyle(Style.FILL);
			g.drawArc(new RectF(cb / 3, cb / 3, cb * 2 / 3, cb * 2 / 3), 0,
					360, true, paint);
		}
	}

	private void drawStyle(Canvas g, int col, int row) {
		GoPoint p = (GoPoint) getModel().getObject(col, row);
		int cb = getCubicWidth();
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		if (p.getStyle() == GoPoint.STYLE_HIGHLIGHT) {
			paint.setColor(0xffff0000);
			paint.setStyle(Style.FILL);
			g.drawArc(
					new RectF(cb / 2 - 6, cb / 2 - 6, cb / 2 + 6, cb / 2 + 6),
					0, 360, true, paint);
		}
	}

	private void drawMark(Canvas g, int col, int row) {
		GoPoint p = (GoPoint) getModel().getObject(col, row);
		int cb = getCubicWidth();
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		int mark = p.getMark();
		if (mark != GoPoint.NONE) {
			paint.setColor(Color.RED);
			paint.setStyle(Style.FILL);
			if (mark == GoPoint.TRIANGLE) {
				// 绘制这个三角形,你可以绘制任意多边形
				Path path = new Path();
				// 此点为多边形的起点
				path.moveTo(cb / 2, cb / 5);
				path.lineTo(cb * 3 / 4, cb * 3 / 5);
				path.lineTo(cb / 4, cb * 3 / 5);
				// 使这些点构成封闭的多边形
				path.close();
				g.drawPath(path, paint);
			} else if (mark == GoPoint.SQUARE) {
				g.drawRect(cb / 4, cb / 4, cb * 3 / 4, cb * 3 / 4, paint);
			} else if (mark == GoPoint.CIRCLE) {
				g.drawArc(new RectF(cb / 4, cb / 4, cb * 3 / 4, cb * 3 / 4), 0,
						360, true, paint);
			} else if (mark == GoPoint.CROSS) {
				g.drawLine(cb / 4, cb / 4, cb * 3 / 4, cb * 3 / 4, paint);
				g.drawLine(cb / 4, cb * 3 / 4, cb * 3 / 4, cb / 4, paint);
			}
		}
	}

	private void drawLetter(Canvas g, int col, int row) {
		GoPoint p = (GoPoint) getModel().getObject(col, row);
		int cb = getCubicWidth();
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		int letter = p.getLetter();
		if (letter != GoPoint.NONE) {
			paint.setColor(Color.MAGENTA);
			char cp = (char) letter;
			TextPaint tp = new TextPaint();
			tp.set(paint);
			tp.setTextSize(Math.round(12 * density));
			float width = tp.measureText("" + cp);
			g.drawText("" + cp, cb / 2 - width / 2,
					cb / 2 + Math.round(8 * density) / 2, tp);
		}
	}

	private void drawLabel(Canvas g, int col, int row) {
		GoPoint p = (GoPoint) getModel().getObject(col, row);
		int cb = getCubicWidth();
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		String label = p.getLabel();
		if (!TextUtils.isEmpty(label)) {
			paint.setColor(Color.MAGENTA);
			TextPaint tp = new TextPaint();
			tp.set(paint);
			tp.setTextSize(Math.round(12 * density));
			float width = tp.measureText(label);
			g.drawText(label, cb / 2 - width / 2,
					cb / 2 + Math.round(8 * density) / 2, tp);
		}
	}

	@Override
	public void drawCubic(Canvas g, int col, int row) {
		// 绘制棋子和手数
		drawPiece(g, col, row);
		// 绘制分支位置
		drawTreeNode(g, col, row);
		// 绘制点目形势
		drawTerrain(g, col, row);
		// 绘制当前高亮状态
		drawStyle(g, col, row);
		// 绘制标记
		drawMark(g, col, row);
		// 绘制字母
		drawLetter(g, col, row);
		// 绘制标签
		drawLabel(g, col, row);
	}
}
