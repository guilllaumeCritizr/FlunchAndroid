package com.CritizrSDKExample;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.CritizrSDK.CritizrListener;
import com.CritizrSDK.CritizrSDK;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class MainActivity extends Activity implements CritizrListener{
	
	public static final String DEBUG_TAG = "CRITIZR_SDK";


	private void performUrlCall(String url){
		RequestQueue queue = Volley.newRequestQueue(this);

		StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						JSONObject object = null;
						try {
							object = new JSONObject(response);
							if (object != null){
								String shorten  = object.getString("short");
								String storeId  = object.getString("storeId");
								JSONObject params = object.getJSONObject("params");
								String apiKey = MainActivity.this.getResources().getString(R.string.critizr_api_key);
								CritizrSDK.getInstance(apiKey).openFeedbackActivity(MainActivity.this, MainActivity.this, storeId, params);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		});
		queue.add(stringRequest);
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("INFO", "I'm in onCreate method");
		super.onCreate(savedInstanceState);
		String refreshedToken = FirebaseInstanceId.getInstance().getToken();
		Log.i("INFO", "token => " + refreshedToken);
		setContentView(R.layout.activity_main);
		this.getActionBar().hide();
		Intent intent = getIntent();
		if (intent != null){
			if (intent.getExtras() != null){
				String czlink = getIntent().getExtras().getString("czlink");
				if (czlink == null){
					Log.i("INFO", "There was no czlink on the payload");
				} else {
					Log.i("INFO", "Link => " + czlink);
					performUrlCall(czlink);
				}
			}
			String action = intent.getAction();
			if (action != null){
				Uri data = intent.getData();
				if (data != null && data.getAuthority() != null){
					Log.i("INFO", data.getAuthority());
				}
				if (data != null && data.getAuthority() != null && data.getAuthority().startsWith("critizr.herokuapp.com")){
					Log.i("INFO", data.toString());
					performUrlCall(data.toString());
				}
			}
		}
	}

	@Override
	protected void onNewIntent(Intent intent){
		Log.i("INFO", "I'm in onNewIntent method");
		if (intent != null){
			if (intent.getExtras() != null){
				try {
					String czlink = intent.getExtras().getString("czlink");
					if (czlink == null){
						Log.i("INFO", "There was no czlink on the payload");
					} else {
						Log.i("INFO", "Link => " + czlink);
						performUrlCall(czlink);
					}
				}catch (Exception e){
					e.printStackTrace();
				}
			}
			String action = intent.getAction();
			if (action != null){
				Uri data = intent.getData();
				if (data != null && data.getAuthority() != null){
					Log.i("INFO", data.getAuthority());
				}
				if (data != null && data.getAuthority() != null && data.getAuthority().startsWith("critizr.herokuapp.com")){
					Log.i("INFO", data.toString());
					performUrlCall(data.toString());
				}
			}
		}
	}
	
	public void MainClickMethod(View view) {
		String apiKey = this.getResources().getString(R.string.critizr_api_key);

		if(view.getId() == R.id.storelocator_btn){
            JSONObject object = new JSONObject();
            try {
                object.put("mode", "feedback");
                object.put("user", "YXJuYXVkfGFybmF1ZC5sYW5jZWxvdEBjcml0aXpyLmNvbQ=="); // arnaud|arnaud.lancelot@critizr.com en BASE64
            } catch (JSONException e) {
                e.printStackTrace();
            }
			CritizrSDK.getInstance(apiKey).openFeedbackActivity(this, this, object);
		}else if(view.getId() == R.id.my_store_btn){
			MainActivity.this.startActivity(new Intent(this, MyStoreActivity.class));
		}
		
	}

	@Override
	public void onFeedbackSent() {
		Log.d(DEBUG_TAG, "Feedback sent");
	}


	@Override
	public void onRatingResult(double arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRatingError() {
		Log.i("INFO", "Error while fetching the place rating");
	}

}
