/*
 * 
 */
package com.soyomaker.handsgo.go;

import com.soyomaker.handsgo.go.sgf.TreeNode;

/**
 * 棋子模型类.
 * 
 * @author like
 * 
 */
public class GoPoint {

	public final static int BOTH = -2;
	public final static int EMPTY = -1;
	public final static int BLACK = 1;
	public final static int WHITE = 2;

	public final static int STYLE_GENERAL = 0;
	public final static int STYLE_HIGHLIGHT = 1;

	public final static int NONE = -1;
	public final static int TRIANGLE = -2;
	public final static int SQUARE = -3;
	public final static int CIRCLE = -4;
	public final static int CROSS = -5;

	private int number = NONE;// 棋子上的手数
	private int player = EMPTY;
	private int style = STYLE_GENERAL;
	private int mark = NONE;
	private int letter = NONE;
	private String label;
	private float terrain;// 点目的形势
	private TreeNode treeNode;

	public static boolean isEnemy(GoPoint p1, GoPoint p2) {
		int c1 = p1.getPlayer();
		int c2 = p2.getPlayer();
		if ((c1 == GoPoint.EMPTY) || (c2 == GoPoint.EMPTY) || (c1 == c2)) {
			return false;
		} else {
			return true;
		}
	}

	public static boolean isEnemy(int c1, int c2) {
		if ((c1 == GoPoint.EMPTY) || (c2 == GoPoint.EMPTY) || (c1 == c2)) {
			return false;
		} else {
			return true;
		}
	}

	public TreeNode getTreeNode() {
		return treeNode;
	}

	public void setTreeNode(TreeNode treeNode) {
		this.treeNode = treeNode;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String tip) {
		this.label = tip;
	}

	public float getTerrain() {
		return terrain;
	}

	public void setTerrain(float terrain) {
		this.terrain = terrain;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int num) {
		number = num;
	}

	public int getPlayer() {
		return player;
	}

	public void setPlayer(int s) {
		this.player = s;
	}

	public int getStyle() {
		return style;
	}

	public void setStyle(int style) {
		this.style = style;
	}

	public int getMark() {
		return mark;
	}

	public void setMark(int t) {
		mark = t;
	}

	public int getLetter() {
		return letter;
	}

	public void setLetter(int letter) {
		this.letter = letter;
	}
}
