package com.words.utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import android.util.Log;


public class Tools 
{
    //GBK格式的BCD码转UTF字符串，用于IC卡中姓名的转换
    public static String gbk_(String s)
    {
        int iLen=s.length()/2;
        //Log.e(TAG,"iLen="+iLen+"");
        byte bb[]=new byte[iLen];

        for(int i=0;i<iLen;i++)
        {
            String s1=s.substring((i+1)*2-2, (i+1)*2);
            bb[i]=(byte) Integer.parseInt(s1,16);
            //Log.e(TAG,i+".="+s1);
        }
        String x="";
        //Log.e(TAG,"1111111");

        try
        {
            x=new String(bb,"gbk");
        }
        catch (UnsupportedEncodingException e)
        {
            Log.e("Tools",bb.toString()+"\n\n"+ e.getMessage());
        }
        return x;

    }

	//byte 转十六进制
		public static String Bytes2HexString(byte[] b, int size) 
		{
		    String ret = "";
		    for (int i = 0; i < size; i++) 
		    {
		      String hex = Integer.toHexString(b[i] & 0xFF);
		      if (hex.length() == 1) {
		        hex = "0" + hex;
		      }
		      ret += hex.toUpperCase();
		    }
		    return ret;
		  }
		
		public static byte uniteBytes(byte src0, byte src1) {
		    byte _b0 = Byte.decode("0x" + new String(new byte[]{src0})).byteValue();
		    _b0 = (byte)(_b0 << 4);
		    byte _b1 = Byte.decode("0x" + new String(new byte[]{src1})).byteValue();
		    byte ret = (byte)(_b0 ^ _b1);
		    return ret;
		  }
		
		//十六进制转byte
		public static byte[] HexString2Bytes(String src) {
			int len = src.length() / 2;
			byte[] ret = new byte[len];
			byte[] tmp = src.getBytes();

			for (int i = 0; i < len; i++) {
				ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
			}
			return ret;
		}
		
		
		/**
		 * 检验接收的数据长度
		 * @param dataLen 接收数据的长度
		 * @param data 数据
		 * @return
		 */
		public static int  checkData(String dataLen, String data){
			int length = Integer.parseInt(dataLen, 16);
			int flag = 0;
			if((length + 9) == data.length()/2){
				flag = 1;
			}else if((length + 9) < data.length()/2){
				flag = -1;
			}
			return flag;
		}
		
		//GBK格式的BCD码转UTF字符串，用于IC卡中姓名的转换
		public static String gbk(String s)
		{
			int iLen=s.length()/2;
			//Log.e(TAG,"iLen="+iLen+"");
			byte bb[]=new byte[iLen];
			
			for(int i=0;i<iLen;i++)
			{
				String s1=s.substring((i+1)*2-2, (i+1)*2);
				bb[i]=(byte) Integer.parseInt(s1,16);
				//Log.e(TAG,i+".="+s1);
			}
			String x="";
			//Log.e(TAG,"1111111");
			
			try 
			{
				 x=new String(bb,"gbk");
			} 
			catch (UnsupportedEncodingException e) 
			{
			}
			return x;
		}

		/**
		 * 
		 *@return String
		 *@Method Description: 获取14443Auid
		 *@Autor Jimmy
		 *@Date 2013-11-21
		 */
		public static String resolveUID(String respData){
			if(respData.length() < 6){
				return null;
			}
			int length = Integer.parseInt(respData.substring(2, 4),16);
			if(respData.length() < (16 + length)){
				return null;
			}
			return respData.substring(14, 14 + length*2);
		}
		
		/**
		 *@return String
		 *@Method Description:
		 *@Autor Jimmy
		 *@Date 2013-11-27
		 */
		public static String resolve15693UID(String respData){
			if(respData.length() < 6){
				return null;
			}
			return respData.substring(6, respData.length());
		}
		
		/**
		 *@return String
		 *@Method Description:
		 *@Autor Jimmy
		 *@Date 2013-11-27
		 */
		public static String resolve15693sysyInfo(String respData){
			return respData.substring(24, 28);
		}
		
		public static String resolve15693Read(String respData){
			return respData.substring(2, respData.length());
		}
		
		//返回字符串B,C中间的部分
		public static String getPart(String a,String b,String c)
		{
			String ret="";
			int i1=a.indexOf(b);
			int i2=a.indexOf(c);
			Log.e("xxx","i1="+i1+",i2="+i2);
			if(i1<0)return  "";
			if (i2<0)return "";
			Log.e("xxx","a="+(i1+b.length())+",b="+(i2-i1));
			ret=a.substring(i1+b.length(), i2);
			return ret;
		}
		
		public static float cdbl(String s)
		{
			float ret=0;
			try
			{
				ret=Float.valueOf(s);
			}
			catch(Exception e)
			{
				ret=0;
			}
			return ret;
		}


		public static int cint(String s)
		{
			try
			{
				return Integer.parseInt(s);
			}
			catch(Exception e)
			{
				return 0;
			}
		}

		
}
