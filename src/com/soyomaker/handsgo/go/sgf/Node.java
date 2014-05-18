package com.soyomaker.handsgo.go.sgf;

/**
 * A node has
 * <UL>
 * <LI>a list of actions and a number counter (the number is the number of the
 * next expected move in the game tree),
 * <LI>a flag, if the node is in the main game tree,
 * <LI>a list of changes in this node to be able to undo the node,
 * <LI>the changes in the prisoner count in this node.
 * </UL>
 * 
 */
public class Node {

	private ListClass mActions; // actions and variations
	private int mNextExpectedNumber; // next expected number
	private boolean mIsMain; // belongs to main variation
	private ListClass mChanges;
	public int Pw, Pb; // changes in prisoners in this node

	/** initialize with the expected number */
	public Node(int n) {
		mActions = new ListClass();
		mNextExpectedNumber = n;
		mIsMain = false;
		mChanges = new ListClass();
		Pw = Pb = 0;
	}

	/** add an action (at end) */
	public void addAction(Action a) {
		mActions.append(new ListElement(a));
	}

	/** expand an action of the same type as a, else generate a new action */
	public void expandAction(Action a) {
		ListElement p = find(a.type());
		if (p == null)
			addAction(a);
		else {
			Action pa = (Action) p.content();
			pa.addArgument(a.argument());
		}
	}

	/**
	 * Expand an action of the same type as a, else generate a new action. If
	 * the action is already present with the same argument, delete that
	 * argument from the action.
	 */
	public void toggleAction(Action a) {
		ListElement p = find(a.type());
		if (p == null)
			addAction(a);
		else {
			Action pa = (Action) p.content();
			pa.toggleArgument(a.argument());
		}
	}

	/** find the list element containing the action of type s */
	ListElement find(String s) {
		ListElement p = mActions.first();
		while (p != null) {
			Action a = (Action) p.content();
			if (a.type().equals(s))
				return p;
			p = p.next();
		}
		return null;
	}

	/** find the action and a specified tag */
	public boolean contains(String s, String argument) {
		ListElement p = find(s);
		if (p == null)
			return false;
		Action a = (Action) p.content();
		return a.contains(argument);
	}

	/** see if the list contains an action of type s */
	public boolean contains(String s) {
		return find(s) != null;
	}

	/** add an action (at front) */
	public void prependAction(Action a) {
		mActions.prepend(new ListElement(a));
	}

	/**
	 * Insert an action after p. p <b>must</b> have content type action.
	 */
	public void insertAction(Action a, ListElement p) {
		mActions.insert(new ListElement(a), p);
	}

	/** remove an action */
	public void removeAction(ListElement la) {
		mActions.remove(la);
	}

	/**
	 * If there is an action of the type: Remove it, if arg is "", else set its
	 * argument to arg. Else add a new action in front (if it is true)
	 */
	public void setAction(String type, String arg, boolean front) {
		ListElement l = mActions.first();
		while (l != null) {
			Action a = (Action) l.content();
			if (a.type().equals(type)) {
				if (arg.equals("")) {
					mActions.remove(l);
					return;
				} else {
					ListElement la = a.arguments();
					if (la != null)
						la.content(arg);
					else
						a.addArgument(arg);
				}
				return;
			}
			l = l.next();
		}
		if (front)
			prependAction(new Action(type, arg));
		else
			addAction(new Action(type, arg));
	}

	/** set the action of this type to this argument */
	public void setAction(String type, String arg) {
		setAction(type, arg, false);
	}

	/** get the argument of this action (or "") */
	public String getAction(String type) {
		ListElement l = mActions.first();
		while (l != null) {
			Action a = (Action) l.content();
			if (a.type().equals(type)) {
				ListElement la = a.arguments();
				if (la != null)
					return (String) la.content();
				else
					return "";
			}
			l = l.next();
		}
		return "";
	}

	/** remove all actions */
	public void removeActions() {
		mActions = new ListClass();
	}

	/** add a new change to this node */
	public void addChange(Change c) {
		mChanges.append(new ListElement(c));
	}

	/** clear the list of changes */
	public void clearChanges() {
		mChanges.removeAll();
	}

	// modification methods:
	public void main(boolean m) {
		mIsMain = m;
	}

	/**
	 * Set the Main flag
	 * 
	 * @param Tree
	 *            is the tree, which contains this node on root.
	 */
	public void main(Tree p) {
		mIsMain = false;
		try {
			if (((Node) p.content()).isMain()) {
				mIsMain = (this == ((Node) p.firstchild().content()));
			} else if (p.parent() == null)
				mIsMain = true;
		} catch (Exception e) {
		}
	}

	public void number(int n) {
		mNextExpectedNumber = n;
	}

	/**
	 * Copy an action from another node.
	 */
	public void copyAction(Node n, String action) {
		if (n.contains(action)) {
			expandAction(new Action(action, n.getAction(action)));
		}
	}

	// access methods:
	public ListElement actions() {
		return mActions.first();
	}

	public ListElement lastAction() {
		return mActions.last();
	}

	public ListElement changes() {
		return mChanges.first();
	}

	public ListElement lastChange() {
		return mChanges.last();
	}

	public int number() {
		return mNextExpectedNumber;
	}

	public boolean isMain() {
		return mIsMain;
	}
}
