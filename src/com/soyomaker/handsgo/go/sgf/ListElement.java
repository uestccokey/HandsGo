package com.soyomaker.handsgo.go.sgf;

//A list node with pointers to previous and next element
//and with a content of type Object.
public class ListElement {

	private ListElement mNextElement, mPreviousElement; // the chain pointers
	private Object mContent; // the content of the node
	private ListClass mListClass; // Belongs to this list

	// get a new Element with the content and null pointers
	public ListElement(Object content) {
		mContent = content;
		mNextElement = mPreviousElement = null;
		mListClass = null;
	}

	// access methods:
	public Object content() {
		return mContent;
	}

	public ListElement next() {
		return mNextElement;
	}

	public ListElement previous() {
		return mPreviousElement;
	}

	public void list(ListClass l) {
		mListClass = l;
	}

	// modifying methods:
	public void content(Object o) {
		mContent = o;
	}

	public void next(ListElement o) {
		mNextElement = o;
	}

	public void previous(ListElement o) {
		mPreviousElement = o;
	}

	public ListClass list() {
		return mListClass;
	}
}
