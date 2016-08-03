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
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.ExceptionLogger;
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
                s=getPart(s,"<!--START JSON-->","<!--END JSON-->");
                Log.e("xxx","filter.json="+s);
                Gson g=new Gson();
                List<Map<String,String>> arr=new ArrayList<>();
                arr=g.fromJson(s,new TypeToken<ArrayList<Map<String,String>>>(){}.getType());
                for(Map<String,String> m:arr)
                {
                    Log.e("xx"," grade="+m.get("grade")+",name="+m.get("name"));
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

    //返回字符串B,C中间的部分
    public String getPart(String a,String b,String c)
    {
        String ret="";
        int i1=a.indexOf(b);
        int i2=a.indexOf(c);
        if(i1<0)return "";
        if(i2<0)return "";
        if(i2<i1)return "";
        ret=a.substring(i1+b.length(),i2);
        return ret;
    }

	public void onClick1(View view) {
		Intent intent = new Intent(this, ChatActivity.class);
		intent.putExtra("name", "user1");
		startActivity(intent);
	}

    public void onClick3(View view)
    {

        SwHttp.get("111.jpg", null, new BinaryHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] binaryData) {
                bytes2file(binaryData,"/sdcard/2.jpg");
                Log.e("xxx","download.ok "+statusCode);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] binaryData, Throwable error) {
                Log.e("xxx","download.fail "+statusCode);
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);
                int count = (int) ((bytesWritten * 1.0 / totalSize) * 100);
                Log.e("xxx","download.pct "+count+"%");

            }
        });
    }

    public static File bytes2file(byte[] b, String outputFile)
    {
        BufferedOutputStream stream = null;
        File file = null;
        try {
            file = new File(outputFile);
            FileOutputStream fstream = new FileOutputStream(file);
            stream = new BufferedOutputStream(fstream);
            stream.write(b);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
        return file;
    }

	public void onClick2(View view)
    {
        RequestParams ps=new RequestParams();
        ps.add("username","孙大花222");
        ps.add("password","Aa123456");
        try
        {
            ps.put("file111", new File("/sdcard/111.jpg"));
        }
        catch(Exception e){}

        SwHttp.post("upload_mp3.php?id=123", ps, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable)
            {
                Log.e("xxx","upload_mp3.fail "+statusCode+" "+responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString)
            {
                Log.e("xxx","upload_mp3.ok "+statusCode+" "+responseString);
            }
            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);
                int count = (int) ((bytesWritten * 1.0 / totalSize) * 100);
                Log.e("xxx","upload_mp3.pct "+count+"%");
            }
        });
	}
}
