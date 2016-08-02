package com.dasudian.chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.dasudian.utils.SwHttp;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;


public class UserListActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_userlist);

        RequestParams params=new RequestParams();
        params.add("aaa","bbb");

        SwHttp.get("", null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

            }
        });


        SwHttp.get("books.php", params, new AsyncHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
            {
                String s=new String(responseBody);
                Log.e("xxx","swhttp.success "+statusCode+" "+s);
                Gson g=new Gson();
                List<Map<String,String>> arr=new ArrayList<>();
                arr=g.fromJson(s,new TypeToken<ArrayList<Map<String,String>>>(){}.getType());
                for(Map<String,String> m:arr)
                {
                    Log.e("xx",m.get("name"));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
            {
                Log.e("xxx","swhttp.fail "+statusCode+" ");
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize)
            {
                Log.e("xxx","swhttp.onprogress "+bytesWritten+"/"+totalSize);
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
