package com.words.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by uosim on 16/8/8.
 */

public class SwJson
{
    public class SwDataRow
    {
        private Map<String,String> m=new HashMap<>();
        public String get(String k)
        {
            return m.get(k);
        }
        public SwDataRow set(String k,String v)
        {
            m.put(k,v);
            return this;
        }
    }

    public class SwDataTable
    {
        public int RecordCount=0;
        public String TableName="";
        public List<SwDataRow> rows=new ArrayList<>();

        public SwDataRow add()
        {
            SwDataRow r=new SwDataRow();
            rows.add(r);
            return r;
        }
    }

    Map<String,Object> arr=new HashMap<String, Object>();

    public static SwJson Create(String s)
    {
        return new SwJson(s);
    }

    public String toJsonWithEncode()
    {
        return toJson();
    }

    public String toJson()
    {
        Map<String,Object> jjObj=new HashMap<String,Object>();
        Iterator iter = arr.entrySet().iterator();
        while (iter.hasNext())
        {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = entry.getKey().toString();
            Object val = entry.getValue();
            if(val.getClass()==SwDataTable.class)
            {
                SwDataTable dt=(SwDataTable)val;
                List<Map<String,String>> ds=new ArrayList<Map<String,String>>();
                for(SwDataRow r:dt.rows)
                {
                    ds.add(r.m);
                }
                jjObj.put(key,ds);
            }
            else
            {
                jjObj.put(key,val);
            }
        }

        String ret="";
        jjObj.put("tim",System.currentTimeMillis());
        Gson g=new GsonBuilder().disableHtmlEscaping().create();
        ret=g.toJson(jjObj);
        return ret;
    }

    public Map<String,Object> add()
    {
        if(!arr.containsKey("rows"))arr.put("rows",new ArrayList<Map<String,Object>>());
        List<Map<String,Object>> rs=(List<Map<String,Object>>)arr.get("rows");
        Map<String,Object> r=new HashMap<String,Object>();
        rs.add(r);
        return r;
    }


    void l(String s)
    {
        Log.e("json",s);
    }

    public String get(String k)
    {
        try
        {
            return arr.get(k).toString();
        }
        catch (Exception e)
        {
            return "";
        }
    }

    public SwJson set(String k,String v)
    {
        arr.put(k,v);
        return this;
    }


    public SwDataTable table()
    {
        return table("rows");
    }

    public SwDataTable table(String k)
    {
        List<Map<String,Object>> lst=new ArrayList<Map<String,Object>>();
        SwDataTable dt=new SwDataTable();
        dt.TableName=k;

        try
        {
            if(arr.get(k).getClass()==ArrayList.class)
            {
                lst = (ArrayList<Map<String, Object>>) arr.get(k);
                dt.RecordCount = lst.size();
                for (Map<String, Object> m : lst) {
                    SwDataRow dr = new SwDataRow();
                    Iterator iter = m.entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry entry = (Map.Entry) iter.next();
                        Object key = entry.getKey();
                        Object val = entry.getValue();
                        dr.set(key.toString(), val.toString());
                    }
                    dt.rows.add(dr);
                }
                arr.put(k, dt);
            }
            else
            {
                return (SwDataTable) arr.get(k);
            }
        }
        catch (Exception e)
        {
            arr.put(k,dt);
        }
        return dt;
    }


    public SwJson(){}

    public SwJson(String s)
    {
        if(s==null)return;
        if(s.equals(""))return;
        Gson g=new Gson();
        if(s.indexOf("<!--START JSON-->")>=0 && s.indexOf("<!--END JSON-->")>10)
        {
            s=Tools.getPart(s,"<!--START JSON-->","<!--END JSON-->");
        }

        try
        {
            arr = g.fromJson(s, new TypeToken<Map<String, Object>>() {}.getType());
        }
        catch (Exception e)
        {
            l("解析JSON发生错误 "+e.getMessage());
        }
    }


}
