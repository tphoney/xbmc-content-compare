package com.android.xbmccontentcompare;

import java.util.ArrayList;
import java.util.Collection;

public class VideoLibrary {
	public Collection<Movie> movies;
	
	public VideoLibrary() {
		// TODO Auto-generated constructor stub
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
