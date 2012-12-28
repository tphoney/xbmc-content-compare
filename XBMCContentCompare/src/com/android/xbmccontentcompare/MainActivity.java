package com.android.xbmccontentcompare;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {
	
	ArrayList<String> arraylist = new ArrayList<String>();
	ArrayAdapter<String> adapter;
	 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

		StrictMode.setThreadPolicy(policy);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final ListView listview = (ListView) findViewById(R.id.listScannedItems);

		// set up list view
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, arraylist);
		listview.setAdapter(adapter);
		
		getInfoFromProvider("");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	public void getInfoFromProvider(String upc) {
		final String url = "http://192.168.0.5:80/jsonrpc";
		

		String googleResponse = "";
		try {
			HttpClient client = new DefaultHttpClient();
			HttpPost request = new HttpPost(url);
			request.addHeader("Content-Type", "application/json");
			JSONObject json = new JSONObject();
			json.put("jsonrpc", "2.0");
			json.put("method", "VideoLibrary.GetMovies");
			json.put("id", "1");
			
			StringEntity se = new StringEntity( json.toString());  
            se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            request.setEntity(se);
			
			HttpResponse res = client.execute(request);

			if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				googleResponse = EntityUtils.toString(res.getEntity(), "UTF-8");
			}

		} catch (Exception e) {
			System.out.println("Exp=" + e);
		}

		JSONObject jObject;
		try {
			jObject = new JSONObject(googleResponse);
			JSONObject result = jObject.getJSONObject("result");
			
			JSONArray movies = result.getJSONArray("movies");
			for (int i =0 ; i < movies.length(); i++) {
				addnewEntry(movies.getJSONObject(i).getString("label"));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addnewEntry(String entry) {
		arraylist.add(0, entry);
		adapter.notifyDataSetChanged();
	}

}
