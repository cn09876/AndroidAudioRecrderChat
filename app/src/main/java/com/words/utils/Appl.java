package com.words.utils;

import java.io.File;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.PixelFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.facebook.drawee.backends.pipeline.Fresco;

public class Appl extends Application
{
	private static final String TAG = "appl1";
	public double version=3.332;
	public Context frm=null;
	public String strDbPath="/sdcard/ex1.db";
	public String sn="";
	public clsTTS tts=null;
	public clsDB db;
    public static final String DEF_MSG_PREFIX="__msg__";
	
	WindowManager mWindowManager;
	LayoutParams wmParams;

    public static void l(String tag,String s)
    {
        Log.e(tag,s);
    }

	private void createFloatView()  
    {  
        wmParams = new LayoutParams();
        //获取的是WindowManagerImpl.CompatModeWrapper  
        mWindowManager = (WindowManager)getSystemService(WINDOW_SERVICE);  
        Log.i(TAG, "mWindowManager--->" + mWindowManager);  
        //设置window type  
        wmParams.type = LayoutParams.TYPE_PHONE;
        //设置图片格式，效果为背景透明  
        wmParams.format = PixelFormat.RGBA_8888;   
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）  
        wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;        
        //调整悬浮窗显示的停靠位置为左侧置顶
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;         
        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity  
        wmParams.x = 0;
        wmParams.y = 0;
        wmParams.width = 0; 
        wmParams.height =0;
        
       //mWindowManager.addView(new Button(this), wmParams);  
        
        
    } 
	public static void l(String s)
	{
		Appl.l(TAG,s);
	}

	public void reset_pre_dir() {
		try {
			Field field;
			// 获取ContextWrapper对象中的mBase变量。该变量保存了ContextImpl对象
			field = ContextWrapper.class.getDeclaredField("mBase");
			field.setAccessible(true);
			// 获取mBase变量
			Object obj = field.get(this);
			// 获取ContextImpl。mPreferencesDir变量，该变量保存了数据文件的保存路径
			field = obj.getClass().getDeclaredField("mPreferencesDir");
			field.setAccessible(true);
			// 创建自定义路径
			File file = new File("/sdcard");
			// 修改mPreferencesDir变量的值
			field.set(obj, file);
		} catch (Exception e)
		{
		}
	}
	public String ini(String k)
	{
		reset_pre_dir();
		String s="";
		SharedPreferences sp=this.getSharedPreferences("SW_SETTING", MODE_PRIVATE);
		s=sp.getString(k, "");
		return s;
	}

    public boolean ini_bool(String k)
    {
		reset_pre_dir();
        SharedPreferences sp=this.getSharedPreferences("SW_SETTING", MODE_PRIVATE);
        return sp.getBoolean(k, false);
    }

	public void ini(String k,String v)
	{
		reset_pre_dir();
		this.getSharedPreferences("SW_SETTING", MODE_PRIVATE).edit().putString(k, v).commit();
	}
	
	public void ini(String k,int v)
	{
		reset_pre_dir();
		this.getSharedPreferences("SW_SETTING", MODE_PRIVATE).edit().putString(k, v+"").commit();
	}
	
	public static String getMD5(String s)
	{
		// compute md5     
		MessageDigest m = null;   
		try 
		{
			m = MessageDigest.getInstance("MD5");
		}
		catch (NoSuchAlgorithmException e) 
		{
			e.printStackTrace();   
		} 
		m.update(s.getBytes(),0,s.length());   
		
		// get md5 bytes   
		byte p_md5Data[] = m.digest();   
		// create a hex string   
		String m_szUniqueID = new String();   
		for (int i=0;i<p_md5Data.length;i++) 
		{   
		     int b =  (0xFF & p_md5Data[i]);    
		     // if it is a single digit, make sure it have 0 in front (proper padding)    
		     if (b <= 0xF) m_szUniqueID+="0";    
		     // add number to string    
		     m_szUniqueID+=Integer.toHexString(b); 
		}
		return m_szUniqueID.toUpperCase();

	}
	
	
	//生成硬件唯一序列号
	public String getSN()
	{
		TelephonyManager TelephonyMgr = (TelephonyManager)getSystemService(TELEPHONY_SERVICE); 
		String m_szImei = TelephonyMgr.getDeviceId();

		String m_szAndroidID = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
		
		String m_szDevIDShort = "086" + 
		Build.BOARD + Build.BRAND+ Build.CPU_ABI+ Build.DEVICE + Build.DISPLAY + Build.HOST + Build.ID + 
		Build.MANUFACTURER + Build.MODEL + Build.PRODUCT + Build.TAGS + Build.TYPE + Build.USER; //13 digits
		
		WifiManager wm = (WifiManager)getSystemService(Context.WIFI_SERVICE); 
		String m_szWLANMAC = wm.getConnectionInfo().getMacAddress();
		
		BluetoothAdapter m_BluetoothAdapter = null; // Local Bluetooth adapter      
		m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();      
		String m_szBTMAC = "";

		Log.w(TAG,"m_szImei="+m_szImei);
		Log.w(TAG,"m_szDevIDShort="+m_szDevIDShort);
		Log.w(TAG,"m_szAndroidID="+m_szAndroidID);
		Log.w(TAG,"m_szAndroidID="+m_szWLANMAC);
		Log.w(TAG,"m_szBTMAC="+m_szBTMAC);
		
		String m_szLongID = m_szImei + m_szDevIDShort  + m_szAndroidID+ m_szWLANMAC + m_szBTMAC;  
		String ret=getMD5(m_szLongID).toUpperCase();

		return ret;
	}
	
	// 判断手机中是否安装了讯飞语音+
	 private boolean checkSpeechServiceInstall()
	 {
		 String packageName = "com.iflytek.speechcloud";
		 List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
		 for(int i = 0; i < packages.size(); i++)
		 {
			 PackageInfo packageInfo = packages.get(i);
			 if(packageInfo.packageName.equals(packageName))
			 {
				 return true;
			 }
			 else
			 {
				 continue;
			 }
		 }
		 return false;
	 }

	public boolean note_Intent() 
	{
		ConnectivityManager con = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkinfo = con.getActiveNetworkInfo();
		if (networkinfo == null || !networkinfo.isAvailable())
			return false;
		boolean wifi = con.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.isConnectedOrConnecting();
		return wifi;
	}

    public void sendMsg(int msgID,String msg)
    {
        Intent si = new Intent();
        si.setAction(DEF_MSG_PREFIX);
        si.putExtra("msg_id",msgID);
        si.putExtra("msg", msg);
        sendBroadcast(si);
    }

	@Override
	public void onCreate() 
	{
		Fresco.initialize(this);

		this.createFloatView();
		this.sn=getSN();
		this.db=new clsDB(this.strDbPath);
		
        this.db.create("jl", "StuNo,StuName,CardNo");
   
		tts=new clsTTS(this);


		super.onCreate();
	}
	
	public void speak(String s)
	{
		tts.speak(s);
	}
	

	@Override
	public void onTerminate() 
	{
		super.onTerminate();
	}
}
