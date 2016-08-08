package com.words.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.Base64;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by cn09876 on 16/1/18.
 */
public class EventUdpData
{
    public static final String CMD_DISCOVER_电脑端地址广播="server_discover_addr";
    public static final String CMD_RESPONSE="resp";
    public static final String CMD_SET_PARAMS="set_params";
    private int type=0;
    private String s1="";
    private String s2="";
    private String s3="";
    private int i1=0;
    private int i2=0;
    private int i3=0;
    private Object obj1=null;
    public String CMD="";
    public String data="";
    public String FromIP="";
    public List<Map<String,String>> jsonArr=new ArrayList<>();;

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

    String fromBase64(String s)
    {
        return new String(Base64.decode(s.getBytes(), Base64.DEFAULT));
    }


    public EventUdpData(String PeerIP,String data_)
    {
        FromIP=PeerIP;
        CMD=getPart(data_,"<c>","</c>");
        data=getPart(data_,"<d>","</d>");
        if(data.equals(""))data=data_;
        Gson g=new Gson();
        String jsonData=getPart(data,"<json>","</json>");
        try
        {
            //Log.e("udp",jsonData);
            String jsonDecodeBase64=fromBase64(jsonData);
            //Log.e("udp","udp.recv.json="+jsonDecodeBase64);
            jsonArr = g.fromJson(jsonDecodeBase64, new TypeToken<ArrayList<Map<String, String>>>() {
            }.getType());
        }
        catch (Exception e)
        {
            Log.e("udp","udp.recv.jsonParser.error "+e.getMessage());
        }
    }

    public String get(String xmlElement)
    {
        return getPart(data,"<"+xmlElement+">","</"+xmlElement+">");
    }

}
