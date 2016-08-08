package com.words.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.words.utils.EventUdpData;
import com.words.utils.SwCheck1;
import com.words.utils.SwHttp;
import com.words.utils.UdpHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.Base64;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import cz.msebera.android.httpclient.Header;
import de.greenrobot.event.EventBus;


public class UserListActivity extends Activity {

    @SwCheck1(value = 1,txt="")
    String ip="",ssSend="";
    int iSendCount=0,iRecvCount=0;
    Button btn4;

    void l(String s)
    {
        Log.e("xxx",s);
    }

    public void onEventMainThread(Msg1 x)
    {
        btn4.setText(iSendCount+"/"+iRecvCount);
    }

    public void onEventMainThread(EventUdpData u)
    {
        l(u.FromIP+"="+u.data.length()+"bytes");
        if(u.data.indexOf("discover")>0)ip=u.FromIP;

        if(u.jsonArr!=null)
        {
            iRecvCount++;
            l(u.jsonArr.toString());
        }
    }

    @Override
	protected void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_userlist);
        EventBus.getDefault().register(this);


        btn4=(Button)findViewById(R.id.btn4);
        ssSend="<start>";
        for(int i=1;i<=100;i++)ssSend+="888888888888";
        ssSend+="</start>";

        WifiManager wm=(WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        new Thread(new UdpHelper(wm)).start();

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

    String genSendInfo()
    {

        String ret="";
        List<Map<String,String>> arr=new ArrayList<>();
        Map<String,String> m=new HashMap<>();
        m.put("RecordCount","101");
        m.put("Auth","默认认证方式3A");
        arr.add(m);
        for(int i=1;i<=10;i++)
        {
            Map<String,String> m1=new HashMap<>();
            m1.put("id","No."+i);
            m1.put("name","姓名"+i+"忐忑喆**&^/'");
            arr.add(m1);
        }
        Gson g=new GsonBuilder().disableHtmlEscaping().create();
        ret=g.toJson(arr);

        ret=toBase64(ret);

        return "<json>"+ret+"</json>";
    }

    public void  onClick4(View v)
    {
        //iSendCount++;
        //UdpHelper.send(genSendInfo(),ip);

        if(true) {
            Timer t = new Timer();
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    iSendCount++;
                    EventBus.getDefault().post(new Msg1());
                    UdpHelper.send(genSendInfo(), ip);
                }
            }, 100, 100);
        }

    }

    String toBase64(String s)
    {
        return Base64.encodeToString(s.getBytes(), Base64.NO_WRAP);
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

