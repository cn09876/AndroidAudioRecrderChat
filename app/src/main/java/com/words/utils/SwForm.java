package com.words.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import com.words.EventMsg;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import de.greenrobot.event.EventBus;

public class SwForm extends Activity 
{
	public String TAG = "SwForm";
	public Appl app;
	public clsDB db;
    public SwForm_BroadcastRecv brRecv;
    public Handler mmHandler;

    
	public void setFullScreen()
	{
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

	}

    public void toast(String s)
    {
        Toast.makeText(SwForm.this,s,Toast.LENGTH_LONG).show();
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

    public void l(String s)
	{
		Log.e(TAG, s);
	}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unregisterReceiver(brRecv);
    }

    @Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		app=((Appl)getApplication());
		app.frm=this;
        db=new clsDB(app.strDbPath);            
		super.onCreate(savedInstanceState);

        brRecv=new SwForm_BroadcastRecv();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Appl.DEF_MSG_PREFIX);
        registerReceiver(brRecv, filter);

        EventBus.getDefault().register(this);

        mmHandler=new Handler(getMainLooper())
        {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                OnRecvHandler(msg.what,msg.obj);
            }
        };
    }

    public void onEvent(EventMsg event)
    {
        l("OnEvent-->"+Thread.currentThread().getId());
    }

    /**
     * 使用onEventMainThread来接收事件，那么不论分发事件在哪个线程运行，接收事件永远在UI线程执行，
     * 这对于android应用是非常有意义的
     * @param event
     */
    public void onEventMainThread(EventMsg event)
    {
        l("onEventMainThread-->"+Thread.currentThread().getId());
    }

    /**
     * 使用onEventBackgroundThread来接收事件，如果分发事件在子线程运行，那么接收事件直接在同样线程
     * 运行，如果分发事件在UI线程，那么会启动一个子线程运行接收事件
     * @param event
     */
    public void onEventBackgroundThread(EventMsg event)
    {
        l("onEventBackgroundThread-->"+Thread.currentThread().getId());
    }

    /**
     * 使用onEventAsync接收事件，无论分发事件在（UI或者子线程）哪个线程执行，接收都会在另外一个子线程执行
     * @param event
     */
    public void onEventAsync(EventMsg event)
    {
        l("onEventAsync-->"+Thread.currentThread().getId());
    }

    public void OnRecvHandler(int what,Object msg)
    {

    }

    public void sendHandler(int what,String msg)
    {
        sendHandler(what,(Object)msg);
    }
    public void sendHandler(int what,Object obj)
    {
        mmHandler.sendMessage(mmHandler.obtainMessage(what,obj));
    }


    public void sendMsg(int msgID,String msg)
    {
        app.sendMsg(msgID,msg);
    }

    public void OnRecvBroad(int msg_id,String msg,Intent it)
    {

    }

    public void show_frm(Class<?> cls)
    {
        SwForm.this.startActivity(new Intent(SwForm.this, cls));
    }

    public boolean FileExists(String fs)
    {
        File f=new File(fs);
        return f.exists();
    }

    public void msgbox(String msg)
    {
        new AlertDialog.Builder(this).setTitle("提示").setMessage(msg).setCancelable(false)
                .setNeutralButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    private class SwForm_BroadcastRecv extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            int msg_id=intent.getIntExtra("msg_id",0);
            String msg=intent.getStringExtra("msg");
            OnRecvBroad(msg_id,msg,intent);
        }

    }
}
