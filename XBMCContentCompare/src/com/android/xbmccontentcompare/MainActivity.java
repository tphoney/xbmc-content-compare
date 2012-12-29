package com.android.xbmccontentcompare;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

import com.android.xbmccontentcompare.XbmcRequest.MyCallbackInterface;

public class MainActivity extends Activity implements MyCallbackInterface {

	VideoLibrary homeLibrary = new VideoLibrary();
	VideoLibrary remoteLibrary = new VideoLibrary();
	public VideoLibrary resultsLibrary = new VideoLibrary();
	String homeIp;
	String homePort;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(MainActivity.this);
		homeIp = prefs.getString("IP", "192.168.0.1");
		homePort = prefs.getString("Port", "80");

		remoteLibrary.addMovie(new Movie("500 Days of Summer", "tt1022603"));

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.menu_settings:
			startActivity(new Intent(this, PrefsActivity.class));
			return true;
		case R.id.menu_scan_home:
			XbmcRequest parser = new XbmcRequest(this, homeIp, homePort);
			parser.execute("VideoLibrary.GetMovies", "title", "imdbnumber");
			return true;
		case R.id.menu_duplicates:
			resultsLibrary = remoteLibrary.findDuplicates(homeLibrary);
			Intent displayResultsIntent = new Intent(this,
					DisplayResultsActivity.class);
			displayResultsIntent.putExtra("VideoLibrary", resultsLibrary);
			startActivity(displayResultsIntent);
			return true;
		}
		return false;
	}

	@Override
	public void onRequestComplete(JSONObject result) {
		JSONObject object;
		if (null != result) {
			try {
				object = result.getJSONObject("result");
				JSONArray movies = object.getJSONArray("movies");
				for (int i = 0; i < movies.length(); i++) {
					// addnewEntry(movies.getJSONObject(i).getString("label"));
					homeLibrary.addMovie(new Movie(movies.getJSONObject(i)
							.getString("label"), movies.getJSONObject(i)
							.getString("imdbnumber")));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}