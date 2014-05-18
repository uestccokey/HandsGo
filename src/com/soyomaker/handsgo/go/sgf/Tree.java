package com.soyomaker.handsgo.go.sgf;

/**
 * A node with a list of children trees.
 */
public class Tree {

	private ListClass Children; // list of children, each with Tree as content
	private Object Content; // content
	private ListElement Le; // the listelement containing the tree
	private Tree Parent; // the parent tree

	/** initialize with an object and no children */
	public Tree(Object o) {
		Content = o;
		Children = new ListClass();
		Le = null;
		Parent = null;
	}

	/** add a child tree */
	public void addChild(Tree t) {
		ListElement p = new ListElement(t);
		Children.append(p);
		t.Le = p;
		t.Parent = this;
	}

	/** insert a child tree */
	public void insertChild(Tree t) {
		if (!hasChildren()) // simple case
		{
			addChild(t);
			return;
		}
		// give t my children
		t.Children = Children;
		// make t my only child
		Children = new ListClass();
		ListElement p = new ListElement(t);
		Children.append(p);
		t.Le = p;
		t.Parent = this;
		// fix the parents of all grandchildren
		ListElement le = t.Children.first();
		while (le != null) {
			Tree h = (Tree) (le.content());
			h.Parent = t;
			le = le.next();
		}
	}

	/** remove the specific child tree (must be in the tree!!!) */
	public void remove(Tree t) {
		if (t.parent() != this)
			return;
		Children.remove(t.Le);
	}

	/** remove all children */
	public void removeAll() {
		Children.removeAll();
	}

	// Access Methods:
	public boolean hasChildren() {
		return Children.first() != null;
	}

	public Tree firstchild() {
		return (Tree) Children.first().content();
	}

	public Tree lastchild() {
		return (Tree) Children.last().content();
	}

	public Tree parent() {
		return Parent;
	}

	public ListClass children() {
		return Children;
	}

	public Object content() {
		return Content;
	}

	public void content(Object o) {
		Content = o;
	}

	public ListElement listElement() {
		return Le;
	}
}
