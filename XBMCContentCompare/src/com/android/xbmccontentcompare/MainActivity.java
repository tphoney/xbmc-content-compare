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
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.android.xbmccontentcompare.XbmcRequest.MyCallbackInterface;

public class MainActivity extends Activity implements MyCallbackInterface {

	VideoLibrary homeLibrary = new VideoLibrary();
	VideoLibrary remoteLibrary = new VideoLibrary();
	public VideoLibrary resultsLibrary = new VideoLibrary();
	String homeIp;
	String homePort;
	Boolean homeRequest = true;
	CheckBox homeXbmcStatus, remoteXbmcStatus;
	Button btnDuplicates, btnScanRemote;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// prefs and screen init
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(MainActivity.this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		homeIp = prefs.getString("IP", "192.168.0.1");
		homePort = prefs.getString("Port", "80");

		// remoteLibrary.addMovie(new Movie("500 Days of Summer", "tt1022603"));
		// ui work
		homeXbmcStatus = (CheckBox) findViewById(R.id.checkBoxHomeXBMC);
		remoteXbmcStatus = (CheckBox) findViewById(R.id.checkBoxRemoteXBMC);
		btnDuplicates = (Button) findViewById(R.id.btnDuplicates);

		// buttons and listeners = need to clean up
		btnDuplicates.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				resultsLibrary = remoteLibrary.findDuplicates(homeLibrary);
				Intent displayResultsIntent = new Intent(MainActivity.this,
						DisplayResultsActivity.class);
				displayResultsIntent.putExtra("VideoLibrary", resultsLibrary);
				startActivity(displayResultsIntent);
			}
		});

		btnScanRemote = (Button) findViewById(R.id.btnScanRemote);
		btnScanRemote.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				homeRequest = false;
				XbmcRequest parser = new XbmcRequest(MainActivity.this,
						"192.168.0.3", homePort);
				parser.execute("VideoLibrary.GetMovies", "title", "imdbnumber");
			}
		});
		// update status
		updateStatuses();
	}

	private void updateStatuses() {
		if (homeLibrary.movies.size() > 0) {
			homeXbmcStatus.setChecked(true);
		} else {
			homeXbmcStatus.setChecked(false);
		}
		if (remoteLibrary.movies.size() > 0) {
			remoteXbmcStatus.setChecked(true);
		} else {
			remoteXbmcStatus.setChecked(false);
		}
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
			homeRequest = true;
			XbmcRequest parser = new XbmcRequest(this, homeIp, homePort);
			parser.execute("VideoLibrary.GetMovies", "title", "imdbnumber");
			return true;
		}
		return false;
	}

	@Override
	public void onRequestComplete(JSONObject result) {
		JSONObject object;
		VideoLibrary tmpLibrary = new VideoLibrary();
		if (null != result) {
			try {
				object = result.getJSONObject("result");
				JSONArray movies = object.getJSONArray("movies");
				for (int i = 0; i < movies.length(); i++) {
					// addnewEntry(movies.getJSONObject(i).getString("label"));
					tmpLibrary.addMovie(new Movie(movies.getJSONObject(i)
							.getString("label"), movies.getJSONObject(i)
							.getString("imdbnumber")));
				}
				if (homeRequest) {
					homeLibrary = tmpLibrary;
				} else {
					remoteLibrary = tmpLibrary;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		updateStatuses();
	}
}