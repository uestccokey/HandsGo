package com.soyomaker.handsgo.go.sgf;

import java.io.PrintWriter;

/**
 * This action class takes special care to print labels in SGF form. Jago notes
 * labes in consecutive letters, but SGF does not have this feature, thus it
 * outputs labels as LB[field:letter].
 */

public class LabelAction extends Action {

	public LabelAction(String arg) {
		super("L", arg);
	}

	public LabelAction() {
		super("L");
	}

	public void print(PrintWriter o) {
		o.println();
		o.print("LB");
		char[] c = new char[1];
		int i = 0;
		ListElement p = Arguments.first();
		while (p != null) {
			c[0] = (char) ('a' + i);
			o.print("[" + (String) (p.content()) + ":" + new String(c) + "]");
			i++;
			p = p.next();
		}
	}
}
