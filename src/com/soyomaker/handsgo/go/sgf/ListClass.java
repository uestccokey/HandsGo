package com.soyomaker.handsgo.go.sgf;

public class ListClass {

	private ListElement mFirstElement, mLastElement; // Pointer to start and end
														// of list.

	/**
	 * Generate an empty list.
	 */
	public ListClass() {
		mFirstElement = null;
		mLastElement = null;
	}

	/**
	 * Append a node to the list
	 */
	public void append(ListElement l) {
		if (mLastElement == null)
			init(l);
		else {
			mLastElement.next(l);
			l.previous(mLastElement);
			mLastElement = l;
			l.next(null);
			l.list(this);
		}
	}

	// prepend a node to the list
	public void prepend(ListElement l) {
		if (mFirstElement == null)
			init(l);
		else {
			mFirstElement.previous(l);
			l.next(mFirstElement);
			mFirstElement = l;
			l.previous(null);
			l.list(this);
		}
	}

	/*
	 * @param l ListElement to be inserted.
	 * 
	 * @param after If null, it works like prepend.
	 */
	public void insert(ListElement l, ListElement after) {
		if (after == mLastElement)
			append(l);
		else if (after == null)
			prepend(l);
		else {
			after.next().previous(l);
			l.next(after.next());
			after.next(l);
			l.previous(after);
			l.list(this);
		}
	}

	/**
	 * initialize the list with a single element.
	 */
	public void init(ListElement l) {
		mLastElement = mFirstElement = l;
		l.previous(null);
		l.next(null);
		l.list(this);
	}

	/**
	 * Remove a node from the list. The node really should be in the list, which
	 * is not checked.
	 */
	public void remove(ListElement l) {
		if (mFirstElement == l) {
			mFirstElement = l.next();
			if (mFirstElement != null)
				mFirstElement.previous(null);
			else
				mLastElement = null;
		} else if (mLastElement == l) {
			mLastElement = l.previous();
			if (mLastElement != null)
				mLastElement.next(null);
			else
				mFirstElement = null;
		} else {
			l.previous().next(l.next());
			l.next().previous(l.previous());
		}
		l.next(null);
		l.previous(null);
		l.list(null);
	}

	/**
	 * Empty the list.
	 */
	public void removeAll() {
		mFirstElement = null;
		mLastElement = null;
	}

	/** remove everything after e */
	public void removeAfter(ListElement e) {
		e.next(null);
		mLastElement = e;
	}

	/**
	 * @return First ListElement.
	 */
	public ListElement first() {
		return mFirstElement;
	}

	/**
	 * @return Last ListElement.
	 */
	public ListElement last() {
		return mLastElement;
	}

	/**
	 * Prints the class
	 */
	@Override
	public String toString() {
		ListElement e = mFirstElement;
		String s = "";
		while (e != null) {
			s = s + e.content().toString() + ", ";
			e = e.next();
		}
		return s;
	}
}
