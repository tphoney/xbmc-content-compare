package com.android.xbmccontentcompare;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class DisplayResultsActivity extends Activity {

	ArrayList<String> arraylist = new ArrayList<String>();
	ArrayAdapter<String> adapter;
	private VideoLibrary bla;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_results);
		final ListView listView = (ListView) findViewById(R.id.list);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long rowId) {
				onListItemClick(listView, view, position, rowId);
			}
		});
		// set up list view
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, arraylist);
		listView.setAdapter(adapter);

		bla = (VideoLibrary) getIntent().getSerializableExtra("VideoLibrary");
		for (Movie entry : bla.movies) {
			addnewEntry(entry.name);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.activity_display_results, menu);
		return true;
	}

	public void addnewEntry(String entry) {
		arraylist.add( entry);
		adapter.notifyDataSetChanged();
	}

	protected void onListItemClick(final ListView listview, final View view,
			final int position, final long rowId) {
		final String itemClicked = (String) adapter.getItem(position);
		AlertDialog.Builder builder = new AlertDialog.Builder(
				DisplayResultsActivity.this);
		builder.setTitle(itemClicked+":");
		builder.setItems(R.array.display_movie_options,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int whatToDoWithMovie) {
						// Do something with the selection
						decideWhattodoWithMovie(whatToDoWithMovie, position);
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public void decideWhattodoWithMovie(final int whatToDoWithMovie, int position) {
		Movie movie = (Movie) bla.movies.toArray()[position];
		switch (whatToDoWithMovie) {
		case 0:
			// download movie
			 ;
			Toast.makeText(getBaseContext(), "Downloading " + movie.name,
					R.string.default_time_to_display_messages).show();
			
			break;

		case 1:
			// view movie information
			String url = "http://www.imdb.com/title/" + movie.imdb;
			Intent browserIntent = new Intent(Intent.ACTION_VIEW,
					Uri.parse(url));
			startActivity(browserIntent);
			break;
		default:
			break;
		}
	}
}
