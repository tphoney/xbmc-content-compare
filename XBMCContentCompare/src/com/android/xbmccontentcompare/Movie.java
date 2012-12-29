package com.android.xbmccontentcompare;

import java.io.Serializable;

public class Movie implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Movie(String inputName, String inputImdb) {
		this.name = inputName;
		this.imdb = inputImdb;
	}

	public String name;
	public String imdb;
}
