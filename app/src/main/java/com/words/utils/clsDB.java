package com.words.utils;

import java.util.Hashtable;
import java.util.Iterator;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class clsDB 
{
	private static final String TAG = "SQLITE";
	public boolean eof_=true;
	public int colCount=0;
	public String[] cols;
	private Hashtable<String, String> ht;
	public int recordCount_=0;
	private String sDbName="sys.db";
	private Hashtable[] data;
	public String lastSQL="";
	private int pos_=0;
	private final static byte[] _lock = new byte[0];

	public clsDB(String sDb)
	{		
		sDbName=sDb;
		ht=new Hashtable<String, String>();
	}

	public int recordCount()
	{
		return this.recordCount_;
	}

	public boolean eof()
	{
		return this.eof_;
	}

	public void ini_set(String k,String v)
	{
		try
		{
			this.create("reg", "k,v");
		}
		catch(Exception e)
		{
		}
		
		this.q("delete from reg where k='"+k+"' ");
		this.q("insert into reg (k,v) values ('"+k+"','"+v+"')");
		
	}

	public String ini_get(String k)
	{		
		String s="";
		try
		{
			s=sv("select v from reg where k='"+k+"' ");
		}
		catch(Exception e)
		{

		}
		return s;
	}

	public void field(String k,String v)
	{
		ht.put(k, v);
	}

	public void clear()
	{
		ht.clear();
	}

	public boolean isint(String s)
	{	
		s   =   s.trim();   
		try   
		{   
			Integer.parseInt(s);   
		}   
		catch(NumberFormatException   e)   
		{   
			return   false;   
		}   
		return   true;   
	}

	public void add(String table)
	{
		String _val="",_fld="",s="",k="",v="";
		_val="";
		_fld="";
		
		try
		{
			for(Iterator<String> it=ht.keySet().iterator();it.hasNext();)   
			{	
				k   =   it.next(); 
				v   =   ht.get(k); 
				_fld+=" "+k+", ";
				_val+=" '"+v+"', ";
			}
			_fld=_fld.substring(0,_fld.length()-2);
			_val=_val.substring(0,_val.length()-2);
		}
		catch(Exception e)
		{
			Log.e(TAG,"clsdb.add.error: "+e.toString());
			return;
		}
		
		s="insert into "+table+" ("+_fld+") values ("+_val+") ";	//gen insert sql statement
		this.q(s);
			
	}

	public void update(String table,String where)
	{
		
		if(isint(where))where=" id='"+where+"' ";

		String _fld="",s="",k="",v="";
		_fld="";
		for(Iterator<String>   it   =   ht.keySet().iterator();it.hasNext();   )   
		{ 
			k   =   it.next(); 
			v   =   ht.get(k); 
			_fld+=k+"='"+v+"', ";
		}	
		_fld=_fld.substring(0,_fld.length()-2);

		s="update "+table+" set "+_fld+" where "+where;			//gen insert sql statement
		
		this.q(s);
	}

	public void create(String tbl,String flds)
	{
		String s="";
		if(flds.length()==0)return;
		s="create table "+tbl+" (id integer primary key,"+flds+",ptime datetime,utime datetime);";
		s+="create trigger "+tbl+"_insert after insert on "+tbl+" begin update "+tbl+" set ptime=datetime('now','localtime'),utime=datetime('now','localtime') where id=new.id; end;";
		s+="create trigger "+tbl+"_update after update on "+tbl+" begin update "+tbl+" set utime=datetime('now','localtime') where id=new.id; end;";
		this.q_(s);
	}

	public void q(String sql)
	{
		q_(sql);
	}

	public void q_(String sql)
	{
		lastSQL=sql;
		synchronized(_lock)
		{
			SQLiteDatabase db = null;
			try
			{
				db = SQLiteDatabase.openOrCreateDatabase(sDbName,  null);
				db.execSQL(sql);
			}
			catch(Exception e)
			{
				l("SQLITE严重错误:"+e.getMessage());
			}
			finally
			{
				db.close();
			}
		}
	}

	public void l(String s)
	{
		Log.e(TAG,s);
	}

	public void query(String sql)
	{
		lastSQL=sql;
		SQLiteDatabase db = null;
		data=null;
		Cursor cc=null;
		pos_=0;
		synchronized(_lock)
		{
			try
			{
				db = SQLiteDatabase.openOrCreateDatabase(sDbName,  null);
				cc=db.rawQuery(sql, null);
				this.colCount=cc.getColumnCount();
				this.cols=cc.getColumnNames();
				this.eof_=cc.isLast();
				this.recordCount_=cc.getCount();
				if(this.recordCount_<=0)this.eof_=true;
				data=new Hashtable[this.recordCount_];
				int idx=0;
				if(this.recordCount_>0)
				{
					while(!cc.isLast())
					{
						cc.moveToNext();
						Hashtable<String, String> ht=new Hashtable<String, String>();
						for(int j=0;j<this.colCount;j++)
						{
							ht.put(j+"", cc.getString(j)+"");
							ht.put(cc.getColumnName(j).toLowerCase(), cc.getString(j)+"");
						}
						data[idx]=ht;
						idx++;
					}
				}
				cc.close();
			}
			catch(Exception e)
			{
				Log.e(TAG,"clsdb.query.error: "+e.getMessage());
				try{cc.close();}catch(Exception e1){}
			}
			finally
			{	
				db.close();
			}
		}
	}

	public void next()
	{
		if(this.eof_)
		{
			return;
		}
		pos_++;
		this.eof_=this.pos_>=this.recordCount_;
	}

	public void close()
	{
		pos_=0;
	}

	public String value(String fld)
	{
		if(data.length==0)return "";
		return (String) data[pos_].get(fld.toLowerCase());
	}

	public String sv(String sql)
	{
		SQLiteDatabase db = null;
		String ret = "";
		Cursor c=null;
		synchronized(_lock)
		{
			try
			{
				db = SQLiteDatabase.openOrCreateDatabase(sDbName,  null);
				c = db.rawQuery(sql, null);
				if (c.moveToNext())ret = c.getString(0);
				c.close();
			}
			catch(Exception e)
			{
				try{c.close();}catch(Exception e1){}
			}
			finally
			{			
				db.close();
			}
		}
		return ret;
	}
}
