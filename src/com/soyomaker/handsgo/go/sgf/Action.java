package com.soyomaker.handsgo.go.sgf;

/**
 * Has a type and arguments (as in SGF, e.g. B[ih] of type "B" and Methods
 * include the printing on a PrintWriter.
 */
public class Action {

	String Type; // the type
	ListClass Arguments; // the list of argument strings

	/**
	 * Initialize with type only
	 */
	public Action(String s) {
		Type = s;
		Arguments = new ListClass();
	}

	/**
	 * Initialize with type and one argument to that type tag.
	 */
	public Action(String s, String arg) {
		Type = s;
		Arguments = new ListClass();
		addArgument(arg);
	}

	// add an argument ot the list (at end)
	public void addArgument(String s) {
		Arguments.append(new ListElement(s));
	}

	// add an argument ot the list (at end)
	public void toggleArgument(String s) {
		ListElement ap = Arguments.first();
		while (ap != null) {
			String t = (String) ap.content();
			if (t.equals(s)) {
				Arguments.remove(ap);
				return;
			}
			ap = ap.next();
		}
		Arguments.append(new ListElement(s));
	}

	/** Find an argument */
	public boolean contains(String s) {
		ListElement ap = Arguments.first();
		while (ap != null) {
			String t = (String) ap.content();
			if (t.equals(s))
				return true;
			ap = ap.next();
		}
		return false;
	}

	/**
	 * Test, if this action contains printed information
	 */
	public boolean isRelevant() {
		if (Type.equals("GN") || Type.equals("AP") || Type.equals("FF")
				|| Type.equals("GM") || Type.equals("N") || Type.equals("SZ")
				|| Type.equals("PB") || Type.equals("BR") || Type.equals("PW")
				|| Type.equals("WR") || Type.equals("HA") || Type.equals("KM")
				|| Type.equals("RE") || Type.equals("DT") || Type.equals("TM")
				|| Type.equals("US") || Type.equals("CP") || Type.equals("BL")
				|| Type.equals("WL") || Type.equals("C")) {
			return false;
		} else {
			return true;
		}
	}

	// modifiers
	public void type(String s) {
		Type = s;
	}

	// access methods:
	public String type() {
		return Type;
	}

	public ListElement arguments() {
		return Arguments.first();
	}

	public String argument() {
		if (arguments() == null) {
			return "";
		} else {
			return (String) arguments().content();
		}
	}
}
