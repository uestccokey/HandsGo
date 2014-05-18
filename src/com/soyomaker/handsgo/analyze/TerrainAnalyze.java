package com.soyomaker.handsgo.analyze;

/**
 * 形势分析 采用简单的影响力算法，不考虑死活，对某个空白点影响力高过阈值时，则该点属于某方。
 * 
 * @author cokey
 * 
 */
public class TerrainAnalyze {

	public static final float THRESHOLD = 0.25f;

	public static final float MIN_THRESHOLD = 0.05f;

	/**
	 * -1是白，0是空，1是黑
	 * 
	 * @param states
	 * @return
	 */
	public static float[][] terrainAnalyze(int[][] states) {
		float[][] analyzes = new float[states.length][states[0].length];
		for (int i = 0; i < states.length; i++) {
			for (int j = 0; j < states[0].length; j++) {
				if (states[i][j] != 0) {
					analyzes[i][j] = states[i][j] * Float.MAX_VALUE;
					updateTerrain(analyzes, states[i][j], j, i);
				}
			}
		}
		return analyzes;
	}

	private static void updateTerrain(float[][] analyzes, int state, int x,
			int y) {
		for (int i = 0; i < analyzes.length; i++) {
			for (int j = 0; j < analyzes[0].length; j++) {
				double distance = (i - y) * (i - y) + (j - x) * (j - x);
				if (distance == 0) {
					continue;
				}
				double num = state / distance;
				if (Math.abs(num) >= MIN_THRESHOLD) {
					analyzes[i][j] += num;
				}
			}
		}
		float lose = 0.5f;
		// 边界反射，影响力除以lose
		// 上
		for (int i = 0; i < analyzes.length; i++) {
			for (int j = 0; j < analyzes[0].length; j++) {
				double distance = (i + y) * (i + y) + (j - x) * (j - x);
				if (distance == 0) {
					continue;
				}
				double num = state / distance / lose;
				if (Math.abs(num) >= MIN_THRESHOLD) {
					analyzes[i][j] += num;
				}
			}
		}
		// 下
		for (int i = 0; i < analyzes.length; i++) {
			for (int j = 0; j < analyzes[0].length; j++) {
				double distance = ((analyzes.length - 1 - y) + (analyzes.length - 1 - i))
						* ((analyzes.length - 1 - y) + (analyzes.length - 1 - i))
						+ (j - x) * (j - x);
				if (distance == 0) {
					continue;
				}
				double num = state / distance / lose;
				if (Math.abs(num) >= MIN_THRESHOLD) {
					analyzes[i][j] += num;
				}
			}
		}
		// 左
		for (int i = 0; i < analyzes.length; i++) {
			for (int j = 0; j < analyzes[0].length; j++) {
				double distance = (i - y) * (i - y) + (j + x) * (j + x);
				if (distance == 0) {
					continue;
				}
				double num = state / distance / lose;
				if (Math.abs(num) >= MIN_THRESHOLD) {
					analyzes[i][j] += num;
				}
			}
		}
		// 右
		for (int i = 0; i < analyzes.length; i++) {
			for (int j = 0; j < analyzes[0].length; j++) {
				double distance = (i - y)
						* (i - y)
						+ ((analyzes[0].length - 1 - x) + (analyzes[0].length - 1 - j))
						* ((analyzes[0].length - 1 - x) + (analyzes[0].length - 1 - j));
				if (distance == 0) {
					continue;
				}
				double num = state / distance / lose;
				if (Math.abs(num) >= MIN_THRESHOLD) {
					analyzes[i][j] += num;
				}
			}
		}
	}
}
