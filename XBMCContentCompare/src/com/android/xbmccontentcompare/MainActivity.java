package com.android.xbmccontentcompare;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.android.xbmccontentcompare.XbmcRequest.MyCallbackInterface;

public class MainActivity extends Activity implements MyCallbackInterface,
		RemoteXbmcDialog.NoticeDialogListener {

	public VideoLibrary homeLibrary = new VideoLibrary();
	public VideoLibrary remoteLibrary = new VideoLibrary();
	public VideoLibrary resultsLibrary = new VideoLibrary();
	String homeIp, remoteIp, homePort, remotePort;
	Boolean homeRequest = true;
	CheckBox homeXbmcStatus, remoteXbmcStatus;
	Button btnDuplicates, btnScanRemote, btnUniqueRemote, btnUniqueHome;
	MenuItem scanHomeDir;
	final int PICK_FILE_RESULT_CODE = 99;
	final int TIME_TO_DISPLAY_MESSAGES = 5000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// prefs and screen init
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(MainActivity.this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		homeIp = prefs.getString("IP", getString(R.string.default_ip));
		homePort = prefs.getString("Port", getString(R.string.default_port));

		// homeLibrary.addMovie(new Movie("500 Days of Summer", "tt1022603"));
		// homeLibrary.addMovie(new Movie("Unique_home", "t1t3234"));

		// remoteLibrary.addMovie(new Movie("500 Days of Summer", "tt1022603"));
		// remoteLibrary.addMovie(new Movie("Unique_remote", "t1t"));
		// ui work
		homeXbmcStatus = (CheckBox) findViewById(R.id.checkBoxHomeXBMC);
		remoteXbmcStatus = (CheckBox) findViewById(R.id.checkBoxRemoteXBMC);
		btnDuplicates = (Button) findViewById(R.id.btnDuplicates);
		btnUniqueRemote = (Button) findViewById(R.id.btnUniqueRemote);
		btnUniqueHome = (Button) findViewById(R.id.btnUniqueHome);
		btnScanRemote = (Button) findViewById(R.id.btnScanRemote);

		// buttons and listeners = need to clean up
		btnDuplicates.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				resultsLibrary = remoteLibrary.findDuplicates(homeLibrary);
				resultsLibrary.ip = remoteIp;
				resultsLibrary.port = remotePort;
				Intent displayResultsIntent = new Intent(MainActivity.this,
						DisplayResultsActivity.class);
				displayResultsIntent.putExtra("VideoLibrary", resultsLibrary);
				startActivity(displayResultsIntent);
			}
		});

		btnUniqueRemote.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				resultsLibrary = remoteLibrary.findUniques(homeLibrary);
				resultsLibrary.ip = remoteIp;
				resultsLibrary.port = remotePort;
				Intent displayResultsIntent = new Intent(MainActivity.this,
						DisplayResultsActivity.class);
				displayResultsIntent.putExtra("VideoLibrary", resultsLibrary);
				startActivity(displayResultsIntent);

			}
		});

		btnUniqueHome.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				resultsLibrary = homeLibrary.findUniques(remoteLibrary);
				resultsLibrary.ip = homeIp;
				resultsLibrary.port = homePort;
				Intent displayResultsIntent = new Intent(MainActivity.this,
						DisplayResultsActivity.class);
				displayResultsIntent.putExtra("VideoLibrary", resultsLibrary);
				startActivity(displayResultsIntent);

			}
		});

		btnScanRemote.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				homeRequest = false;
				AlertDialog.Builder builder = new AlertDialog.Builder(
						MainActivity.this);
				builder.setTitle("Make your selection");
				builder.setItems(R.array.json_retrieval_methods,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int item) {
								// Do something with the selection
								decideRemoteImportMethod(item);
							}
						});
				AlertDialog alert = builder.create();
				alert.show();
			}
		});
		// update status
		updateStatuses();
	}

	public void decideRemoteImportMethod(final int selected) {
		switch (selected) {
		case 0:
			// show screen for inputing the remote xbmc information
			DialogFragment dialog = new RemoteXbmcDialog();
			dialog.show(getFragmentManager(), "NoticeDialogFragment");
			break;
		case 1:
			//display file manager
			try {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("file/");

				startActivityForResult(intent, PICK_FILE_RESULT_CODE);
			} catch (ActivityNotFoundException exp) {
				Toast.makeText(getBaseContext(),
						"No File (Manager / Explorer)etc Found In Your Device",
						TIME_TO_DISPLAY_MESSAGES).show();
			}
			break;

		default:
			break;
		}
	}

	private void updateStatuses() {
		if (homeLibrary.movies.size() > 0) {
			homeXbmcStatus.setChecked(true);
			scanHomeDir.setEnabled(true);
		} else {
			homeXbmcStatus.setChecked(false);
			// scanHomeDir.setEnabled(false);
		}
		if (remoteLibrary.movies.size() > 0) {
			remoteXbmcStatus.setChecked(true);
		} else {
			remoteXbmcStatus.setChecked(false);
		}
		if (remoteLibrary.movies.size() > 0 && homeLibrary.movies.size() > 0) {
			btnDuplicates.setEnabled(true);
			btnUniqueRemote.setEnabled(true);
			btnUniqueHome.setEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		scanHomeDir = menu.getItem(4);
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
			parser.execute("VideoLibrary.GetMovies", "title", "imdbnumber",
					"file");
			return true;
		case R.id.menu_scan_home_json:
			homeRequest = true;
			String file = android.os.Environment.getExternalStorageDirectory()
					.getAbsolutePath()
					+ "/XBMCContentCompare/"
					+ getString(R.string.filename_home);
			onXbmcRequestComplete(FileRequest.readJsonFromFile(file));
			return true;
		case R.id.menu_save_home:
			FileRequest.writeToSDFile(homeLibrary.json,
					getString(R.string.filename_home));
			return true;
		case R.id.menu_display_home:
			VideoLibrary tmpLibrary = new VideoLibrary();
			tmpLibrary.ip = homeLibrary.ip;
			tmpLibrary.port = homeLibrary.port;
			tmpLibrary.movies = homeLibrary.movies;
			Intent displayResultsIntent = new Intent(MainActivity.this,
					DisplayResultsActivity.class);
			displayResultsIntent.putExtra("VideoLibrary", tmpLibrary);
			startActivity(displayResultsIntent);
			return true;
		}
		return false;
	}

	@Override
	public void onXbmcRequestComplete(JSONObject result) {
		JSONObject objectToProcess;
		VideoLibrary tmpLibrary = new VideoLibrary();
		if (null != result) {
			try {
				objectToProcess = result.getJSONObject("result");
				JSONArray movies = objectToProcess.getJSONArray("movies");
				for (int i = 0; i < movies.length(); i++) {
					// addnewEntry(movies.getJSONObject(i).getString("label"));
					tmpLibrary.addMovie(new Movie(movies.getJSONObject(i)
							.getString("label"), movies.getJSONObject(i)
							.getString("imdbnumber"), movies.getJSONObject(i)
							.getString("file")));
				}
				if (homeRequest) {
					homeLibrary = tmpLibrary;
					homeLibrary.ip = homeIp;
					homeLibrary.port = homePort;
					homeLibrary.json = result;
				} else {
					remoteLibrary = tmpLibrary;
					remoteLibrary.ip = remoteIp;
					remoteLibrary.port = remotePort;
					remoteLibrary.json = result;
				}
			} catch (JSONException e) {
				Toast.makeText(getBaseContext(),
						"Unable to read the remote XBMC",
						R.string.default_time_to_display_messages).show();
			}
		}
		updateStatuses();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case PICK_FILE_RESULT_CODE:
			if (resultCode == RESULT_OK) {
				String FilePath = data.getData().getPath();
				onXbmcRequestComplete(FileRequest.readJsonFromFile(FilePath));
			}
			break;
		}
	}

	@Override
	public void onRemoteXbmcDialogPositiveClick(DialogFragment dialog,
			String ip, String port) {
		// validate ip and port ????
		XbmcRequest parser = new XbmcRequest(this, ip, port);
		parser.execute("VideoLibrary.GetMovies", "title", "imdbnumber", "file");
		remoteIp = ip;
		remotePort = port;
	}
}