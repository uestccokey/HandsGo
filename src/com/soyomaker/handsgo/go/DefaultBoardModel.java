/*
 * 
 */
package com.soyomaker.handsgo.go;

import com.soyomaker.handsgo.go.model.grid.DefaultGridModel;
import com.soyomaker.handsgo.go.model.grid.GridModel;

/**
 * 
 * @author like
 * 
 */
public class DefaultBoardModel implements GoBoardModel {

	private DefaultGridModel mGridModel;

	public DefaultBoardModel(int bsize) {
		mGridModel = new DefaultGridModel(bsize, bsize);
		for (int i = 0; i < getBoardSize(); i++) {
			for (int j = 0; j < getBoardSize(); j++) {
				mGridModel.setObject(i, j, new GoPoint());
			}
		}
	}

	@Override
	public void forcePut(int x, int y, GoPoint point) {
		if (!validatePoint(x, y)) {
			return;
		}

		GoPoint p = getPoint(x, y);
		if (p.getPlayer() != GoPoint.EMPTY) {
			return;
		}

		p.setPlayer(point.getPlayer());
		p.setNumber(point.getNumber());
	}

	@Override
	public void forceRemove(int x, int y) {
		if (!validatePoint(x, y)) {
			return;
		}

		GoPoint p = getPoint(x, y);
		if (p.getPlayer() == GoPoint.EMPTY) {
			return;
		}

		p.setPlayer(GoPoint.EMPTY);
		p.setNumber(GoPoint.NONE);
	}

	@Override
	public int getBoardSize() {
		return mGridModel.getRows();
	}

	@Override
	public GridModel getGridModel() {
		return mGridModel;
	}

	@Override
	public GoPoint getPoint(int col, int row) {
		return (GoPoint) mGridModel.getObject(col, row);
	}

	@Override
	public void performPut(int x, int y, GoPoint point) throws GoException {
		if (!validatePoint(x, y)) {
			return;
		}

		int player = point.getPlayer();
		if ((player != GoPoint.BLACK) && (player != GoPoint.WHITE)) {
			return;
		}

		GoPoint p = getPoint(x, y);
		if (p.getPlayer() != GoPoint.EMPTY) {
			throw new GoException("There is already one chessman");
		}

		p.setPlayer(point.getPlayer());
		p.setNumber(point.getNumber());
	}

	@Override
	public void performRemove(int x, int y) throws GoException {
		if (!validatePoint(x, y)) {
			throw new GoException("Invalid point");
		}

		GoPoint p = getPoint(x, y);
		if (p.getPlayer() == GoPoint.EMPTY) {
			throw new GoException("No chessman to remove");
		}

		p.setPlayer(GoPoint.EMPTY);
		p.setNumber(GoPoint.NONE);
	}

	private boolean validatePoint(int col, int row) {
		if ((col < 0) || (col >= getBoardSize()) || (row < 0)
				|| (row >= getBoardSize())) {
			return false;
		} else {
			return true;
		}
	}

	public void reset() {
		for (int i = 0; i < getBoardSize(); i++) {
			for (int j = 0; j < getBoardSize(); j++) {
				mGridModel.setObject(i, j, new GoPoint());
			}
		}
		mGridModel.fireDataChanged();
	}

	public void reset(int bsize) {
		mGridModel.reset(bsize, bsize);
		reset();
	}
}
