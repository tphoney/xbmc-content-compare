package com.android.xbmccontentcompare;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.xbmccontentcompare.XbmcRequest.MyCallbackInterface;

public class MainActivity extends Activity implements MyCallbackInterface {

	ArrayList<String> arraylist = new ArrayList<String>();
	ArrayAdapter<String> adapter;
	VideoLibrary storedLibrary = new VideoLibrary();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		storedLibrary.addMovie(new Movie("500 Days of Summer", "tt1022603"));
		// StrictMode.ThreadPolicy policy = new
		// StrictMode.ThreadPolicy.Builder()
		// .permitAll().build();

		// StrictMode.setThreadPolicy(policy);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final ListView listview = (ListView) findViewById(R.id.listScannedItems);

		// set up list view
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, arraylist);
		listview.setAdapter(adapter);

		// getInfoFromProvider("");
		XbmcRequest parser = new XbmcRequest(this);
		parser.execute("VideoLibrary.GetMovies", "title", "imdbnumber");
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
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		}

		return false;

	}

	public void addnewEntry(String entry) {
		arraylist.add(0, entry);
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onRequestComplete(JSONObject result) {
		JSONObject object;
		if (null != result) {
			VideoLibrary requestedLibrary = new VideoLibrary();
			try {
				object = result.getJSONObject("result");
				JSONArray movies = object.getJSONArray("movies");
				for (int i = 0; i < movies.length(); i++) {
					// addnewEntry(movies.getJSONObject(i).getString("label"));
					requestedLibrary.addMovie(new Movie(movies.getJSONObject(i)
							.getString("label"), movies.getJSONObject(i)
							.getString("imdbnumber")));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			VideoLibrary duplicates = storedLibrary
					.findDuplicates(requestedLibrary);
			for (Movie bla : duplicates.movies) {
				addnewEntry("duplicates: " + bla.name + ", " + bla.imdb);
			}

		}

	}
}
