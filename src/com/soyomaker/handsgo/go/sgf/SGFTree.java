package com.soyomaker.handsgo.go.sgf;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Vector;

/**
 * This is a class wich contains a TreeNode. It used to store complete game
 * trees.
 * 
 */
public class SGFTree {

	protected TreeNode History; // the game history

	private final int maxbuffer = 4096;
	private char[] Buffer = new char[maxbuffer]; // the buffer for reading of
													// files
	private int BufferN;
	private static int lastnl = 0;

	/** initlialize with a specific Node */
	public SGFTree(Node n) {
		History = new TreeNode(n);
		History.node().main(true);
	}

	/** return the top node of this game tree */
	public TreeNode top() {
		return History;
	}

	char readNext(BufferedReader in) throws IOException {
		int c = readChar(in);
		while (c == '\n' || c == '\t' || c == ' ') {
			if (c == -1)
				throw new IOException();
			c = readChar(in);
		}
		return (char) c;
	}

	char readChar(BufferedReader in) throws IOException {
		int c;
		while (true) {
			c = in.read();
			if (c == -1)
				throw new IOException();
			if (c == 13) {
				if (lastnl == 10)
					lastnl = 0;
				else {
					lastnl = 13;
					return '\n';
				}
			} else if (c == 10) {
				if (lastnl == 13)
					lastnl = 0;
				else {
					lastnl = 10;
					return '\n';
				}
			} else {
				lastnl = 0;
				return (char) c;
			}
		}
	}

	// read a node assuming that ; has been found
	// return the character, which did not fit into node properties,
	// usually ;, ( or )
	char readNode(TreeNode p, BufferedReader in) throws IOException {
		boolean sgf = true;
		char c = readNext(in);
		Action a;
		Node n = new Node(((Node) p.content()).number());
		String s;
		loop: while (true) // read all actions
		{
			BufferN = 0;
			while (true) {
				if (c >= 'A' && c <= 'Z')
					store(c);
				// note only capital letters
				else if (c == '(' || c == ';' || c == ')')
					break loop;
				// last property reached
				// BufferN should be 0 then
				else if (c == '[')
					break;
				// end of porperty type, arguments starting
				else if (c < 'a' || c > 'z')
					throw new IOException();
				// this is an error
				c = readNext(in);
			}
			if (BufferN == 0)
				throw new IOException();
			s = new String(Buffer, 0, BufferN);
			if (s.equals("L"))
				a = new LabelAction();
			else if (s.equals("M"))
				a = new MarkAction();
			else
				a = new Action(s);
			while (c == '[') {
				BufferN = 0;
				while (true) {
					c = readChar(in);
					if (c == '\\') {
						c = readChar(in);
						if (sgf && c == '\n') {
							if (BufferN > 1 && Buffer[BufferN - 1] == ' ')
								continue;
							else
								c = ' ';
						}
					} else if (c == ']')
						break;
					store(c);
				}
				c = readNext(in); // prepare next argument
				String s1;
				if (BufferN > 0)
					s1 = new String(Buffer, 0, BufferN);
				else
					s1 = "";
				if (!expand(a, s1))
					a.addArgument(s1);
			}
			// no more arguments
			n.addAction(a);
			if (a.type().equals("B") || a.type().equals("W")) {
				n.number(n.number() + 1);
			}
		} // end of actions has been found
			// append node
		n.main(p);
		TreeNode newp;
		if (((Node) p.content()).actions() == null)
			p.content(n);
		else {
			p.addChild(newp = new TreeNode(n));
			n.main(p);
			p = newp;
			if (p.parentPos() != null && p != p.parentPos().firstChild())
				((Node) p.content()).number(2);
		}
		return c;
	}

	/**
	 * Store c into the Buffer extending its length, if necessary. This is a fix
	 * by Bogdar Creanga from 2000-10-17 (Many Thanks)
	 */
	private void store(char c) {
		try {
			Buffer[BufferN] = c;
			BufferN++;
		} catch (ArrayIndexOutOfBoundsException e) {
			int newLength = Buffer.length + maxbuffer;
			char[] newBuffer = new char[newLength];
			System.arraycopy(Buffer, 0, newBuffer, 0, Buffer.length);
			Buffer = newBuffer;
			Buffer[BufferN++] = c;
		}
	}

	// Check for the terrible compressed point list and expand into
	// single points
	boolean expand(Action a, String s) {
		String t = a.type();
		if (!(t.equals("MA") || t.equals("SQ") || t.equals("TR")
				|| t.equals("CR") || t.equals("AW") || t.equals("AB")
				|| t.equals("AE") || t.equals("SL")))
			return false;
		if (s.length() != 5 || s.charAt(2) != ':')
			return false;
		String s0 = s.substring(0, 2), s1 = s.substring(3);
		int i0 = Field.i(s0), j0 = Field.j(s0);
		int i1 = Field.i(s1), j1 = Field.j(s1);
		if (i1 < i0 || j1 < j0)
			return false;
		int i, j;
		for (i = i0; i <= i1; i++)
			for (j = j0; j <= j1; j++) {
				a.addArgument(Field.string(i, j));
			}
		return true;
	}

	/**
	 * Read the nodes belonging to a tree. this assumes that ( has been found.
	 */
	void readNodes(TreeNode p, BufferedReader in) throws IOException {
		char c = readNext(in);
		while (true) {
			if (c == ';') {
				c = readNode(p, in);
				if (p.hasChildren())
					p = p.lastChild();
				continue;
			} else if (c == '(') {
				readNodes(p, in);
			} else if (c == ')')
				break;
			c = readNext(in);
		}
	}

	/**
	 * Read the tree from an BufferedReader in SGF format. The BoardInterfaces
	 * is only used to determine the "sgfcomments" parameter.
	 */
	public static Vector<SGFTree> load(BufferedReader in) throws IOException {
		Vector<SGFTree> v = new Vector<SGFTree>();
		boolean linestart = true;
		int c;
		reading: while (true) {
			SGFTree T = new SGFTree(new Node(1));
			while (true) // search for ( at line start
			{
				try {
					c = T.readChar(in);
				} catch (IOException ex) {
					break reading;
				}
				if (linestart && c == '(')
					break;
				if (c == '\n')
					linestart = true;
				else
					linestart = false;
			}
			T.readNodes(T.History, in); // read the nodes
			v.addElement(T);
		}
		return v;
	}

	public int getSize() {
		try {
			return Integer.parseInt(History.getAction("SZ"));
		} catch (Exception e) {
			return 19;
		}
	}
}
