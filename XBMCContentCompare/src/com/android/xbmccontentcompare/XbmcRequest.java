package com.android.xbmccontentcompare;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

public class XbmcRequest extends AsyncTask<String, Void, JSONObject> {
	private MyCallbackInterface mCallback;
	private String ip = "192.168.0.1";
	private String port = "80";

	public interface MyCallbackInterface {
		public void onRequestComplete(JSONObject result);
	}


	public XbmcRequest(MyCallbackInterface callback, String inputIp, String inputPort) {
		mCallback = callback;
		ip = inputIp;
		port = inputPort;
	}

	@Override
	protected JSONObject doInBackground(String... params) {
		return getJSONFromXbmcMethod(params);
	}

	@Override
	protected void onPostExecute(JSONObject result) {
		// In here, call back to Activity or other listener that things are done
		mCallback.onRequestComplete(result);
	}

	protected JSONObject getJSONFromXbmcMethod(final String... params) {
		String httpResponseContents = "";
		String requestUrl = "http://" + ip + ":" + port + "/jsonrpc";
		if (params.length > 0) {
			try {
				HttpClient client = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(requestUrl);
				httpPost.addHeader("Content-Type", "application/json");
				JSONObject jsonObjectPost = new JSONObject();
				jsonObjectPost.put("jsonrpc", "2.0");
				jsonObjectPost.put("method", params[0]);
				if (2 <= params.length) {
					JSONArray jsonArrayProperties = new JSONArray();
					for (int i = 1; i < params.length; i++) {
						jsonArrayProperties.put(params[i]);
					}

					JSONObject jsonObjectProperties = new JSONObject();
					jsonObjectProperties.put("properties", jsonArrayProperties);

					jsonObjectPost.put("params", jsonObjectProperties);
				}
				jsonObjectPost.put("id", "1");

				StringEntity stringEntity = new StringEntity(jsonObjectPost.toString());
				stringEntity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
						"application/json"));
				httpPost.setEntity(stringEntity);

				HttpResponse res = client.execute(httpPost);

				if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					httpResponseContents = EntityUtils.toString(res.getEntity(),
							"UTF-8");
				}

			} catch (Exception e) {
				System.out.println("Exp=" + e);
			}
		}
		JSONObject returnedJson = null;
		try {
			returnedJson = new JSONObject(httpResponseContents);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return returnedJson;
	}

}
