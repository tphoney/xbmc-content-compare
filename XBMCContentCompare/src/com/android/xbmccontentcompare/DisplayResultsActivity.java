package com.android.xbmccontentcompare;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class DisplayResultsActivity extends Activity {

	ArrayList<String> arraylist = new ArrayList<String>();
	ArrayAdapter<String> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_results);
		final ListView listview = (ListView) findViewById(R.id.listResults);

		// set up list view
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, arraylist);
		listview.setAdapter(adapter);

		VideoLibrary bla = (VideoLibrary) getIntent().getSerializableExtra(
				"VideoLibrary");
		for (Movie entry : bla.movies) {
			addnewEntry("duplicates: " + entry.name + ", " + entry.imdb);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.activity_display_results, menu);
		return true;
	}

	public void addnewEntry(String entry) {
		arraylist.add(0, entry);
		adapter.notifyDataSetChanged();
	}

}
