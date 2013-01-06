package com.android.xbmccontentcompare;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import org.json.JSONObject;

public class VideoLibrary implements Serializable {

	private static final long serialVersionUID = -1321811786084352158L;
	public Collection<Movie> movies;
	public JSONObject json;
	public String ip = null;
	public String port = null;

	public VideoLibrary() {
		movies = new ArrayList<Movie>();
	}

	VideoLibrary findDuplicates(final VideoLibrary another) {
		ArrayList<Movie> remove = new ArrayList<Movie>(this.movies);
		remove.retainAll(another.movies);
		VideoLibrary returnVal = new VideoLibrary();
		returnVal.movies = remove;
		return returnVal;
	}

	VideoLibrary findUniques(final VideoLibrary another) {
		ArrayList<Movie> remove = new ArrayList<Movie>(this.movies);
		remove.removeAll(another.movies);
		VideoLibrary returnVal = new VideoLibrary();
		returnVal.movies = remove;
		return returnVal;
	}

	void addMovie(final Movie input) {
		movies.add(input);
	}
	
	public Movie findMovie(final String imdb) {
		Movie returnVal = null;
		for (Movie entry : this.movies) {
			if (entry.imdb.equalsIgnoreCase(imdb)){
				returnVal = entry;
			}
		}
		return returnVal;
	}
}
