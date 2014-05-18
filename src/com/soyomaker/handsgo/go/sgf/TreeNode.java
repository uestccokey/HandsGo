package com.soyomaker.handsgo.go.sgf;

/**
 * This is a child class of Tree, with some help functions for the content type
 * Node.
 * 
 */
public class TreeNode extends Tree {

	/** initialize with an empty node with the specified number */
	public TreeNode(int number) {
		super(new Node(number));
	}

	/** initialize with a given Node */
	public TreeNode(Node n) {
		super(n);
	}

	public Node node() {
		return ((Node) content());
	}

	/**
	 * Set the action type in the node to the string s.
	 * 
	 * @param flag
	 *            determines, if the action is to be added, even of s is emtpy.
	 */
	public void setAction(String type, String s, boolean flag) {
		node().setAction(type, s, flag);
	}

	public void setAction(String type, String s) {
		node().setAction(type, s);
	}

	/** add this action to the node */
	public void addAction(Action a) {
		node().addAction(a);
	}

	/** get the value of the action of this type */
	public String getAction(String type) {
		return node().getAction(type);
	}

	/** @return true if it is a main node */
	public boolean isMain() {
		return node().isMain();
	}

	/** @return true if it the last node in the main tree */
	public boolean isLastMain() {
		return !hasChildren() && isMain();
	}

	/** set the main flag in the node */
	public void main(boolean flag) {
		node().main(flag);
	}

	public TreeNode parentPos() {
		return (TreeNode) parent();
	}

	public TreeNode firstChild() {
		return (TreeNode) firstchild();
	}

	public TreeNode lastChild() {
		return (TreeNode) lastchild();
	}
}
