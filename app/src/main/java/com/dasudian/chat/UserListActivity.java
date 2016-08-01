package com.dasudian.chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.dasudian.utils.SwHttp;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;


public class UserListActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_userlist);
        RequestParams params=new RequestParams();
        Gson g=new Gson();



        params.add("aaa","bbb");

        SwHttp.get("books.php", params, new AsyncHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
            {

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
            {

            }

            @Override
            public void onProgress(long bytesWritten, long totalSize)
            {

            }
        });
	}
	
	public void onClick1(View view) {
		Intent intent = new Intent(this, ChatActivity.class);
		intent.putExtra("name", "user1");
		startActivity(intent);
	}
	
	public void onClick2(View view) {
		Intent intent = new Intent(this, ChatActivity.class);
		intent.putExtra("name", "user2");
		startActivity(intent);
	}
}
