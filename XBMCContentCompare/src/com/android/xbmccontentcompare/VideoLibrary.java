package com.android.xbmccontentcompare;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class VideoLibrary implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1321811786084352158L;
	public Collection<Movie> movies;
	
	public VideoLibrary() {
		movies = new ArrayList<Movie>();
	}
	
	VideoLibrary findDuplicates(final VideoLibrary another ) {
		VideoLibrary  returnVal = new VideoLibrary();
		returnVal.movies = this.movies;
		returnVal.movies.removeAll(another.movies); 
			
		return returnVal;
	}
	
	void addMovie (final Movie input) {
		movies.add(input);
	}
}
