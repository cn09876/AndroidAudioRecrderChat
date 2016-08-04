package com.dasudian.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CommonUtils {

	//返回字符串B,C中间的部分
	public static String getPart(String a,String b,String c)
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


	/**
	 * 检测网络是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetWorkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}

		return false;
	}

	/**
	 * 检测Sdcard是否存在
	 * 
	 * @return
	 */
	public static boolean isExitsSdcard() {
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED))
			return true;
		else
			return false;
	}
}
