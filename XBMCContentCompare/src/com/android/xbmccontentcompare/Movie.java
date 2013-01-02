package com.android.xbmccontentcompare;

import java.io.Serializable;

public class Movie implements Serializable {

	private static final long serialVersionUID = 1L;

	public Movie(String inputName, String inputImdb, String remoteUrl) {
		this.name = inputName;
		this.imdb = inputImdb;
		this.remoteUrl = remoteUrl;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((imdb == null) ? 0 : imdb.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Movie other = (Movie) obj;
		if (imdb == null) {
			if (other.imdb != null)
				return false;
		} else if (!imdb.equals(other.imdb))
			return false;
		return true;
	}

	public String name;
	public String imdb;
	public String remoteUrl;
}
