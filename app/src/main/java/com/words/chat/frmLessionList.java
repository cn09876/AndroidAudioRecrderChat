package com.words.chat;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;
import com.words.utils.SwForm;
import com.words.utils.SwHttp;
import com.words.utils.SwJson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import cz.msebera.android.httpclient.Header;
import sw.ui.SwListView;

import static com.words.utils.Tools.getPart;

public class frmLessionList extends SwForm
{

    class TLession
    {
        public String 单元="",类型="";
    }

    SwListView lstLessions;
    String book_id="0";
    static Map<Integer,Boolean> chks=new HashMap<>();


    @Override
    public void OnRecvHandler(int what, Object msg) {
        super.OnRecvHandler(what, msg);
        if(what==1)
        {
            setTitle(msg.toString());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frm_lession_list);
        lstLessions=(SwListView)findViewById(R.id.lstLessions);
        book_id=app.ini("book_id");
        setTitle("正在通信...");
        init_data();
        lstLessions.setSwDelegate(new SwListView.SwDelegate()
        {
            @Override
            public void OnItemSelect(int pos)
            {
                l("xxxxxxxxx "+pos);
                app.ini("book_id",book_id);
                app.ini("单元",((TLession)lstLessions.get(pos)).单元);
                app.ini("类型",((TLession)lstLessions.get(pos)).类型);
                l("lesssion.item.click "+pos);
                show_frm(frmWordList.class);
            }

            @Override
            public View OnGetView(final int position, View convertView, ViewGroup parent)
            {
                if(convertView==null)
                {
                    convertView=lstLessions.inflate(R.layout.lay_lessions_item);
                }
                final TLession b=(TLession) lstLessions.get(position);

                CheckBox cb1=(CheckBox)convertView.findViewById(R.id.chk1);
                try
                {
                    cb1.setChecked(chks.get(position));
                }
                catch (Exception e)
                {
                    cb1.setChecked(false);
                }

                cb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b1) {
                        chks.put(position,b1);
                        l(chks.toString());
                    }
                });

                LinearLayout ll=(LinearLayout)convertView.findViewById(R.id.row);
                if(position%2==0) {
                    ll.setBackgroundColor(Color.WHITE);
                }
                else
                {
                    ll.setBackgroundColor(Color.rgb(240,240,240));
                }

                TextView txt1=(TextView)convertView.findViewById(R.id.txt1);
                String sTxt="";
                sTxt="<font color=red >"+b.单元+"</font> "+b.类型;
                txt1.setText(Html.fromHtml(sTxt));
                return convertView;
            }
        });


    }

    void init_data()
    {
        lstLessions.clear();
        SwHttp.get("lessions.php?book_id="+book_id+"&tim=", null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable)
            {
                Toast.makeText(frmLessionList.this,"访问网络失败",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString)
            {
                SwJson x=SwJson.Create(responseString);
                SwJson.SwDataTable dt=x.table();
                sendHandler(1,x.get("book_name")+" ("+dt.RecordCount+")");
                for(SwJson.SwDataRow m:dt.rows)
                {
                    TLession b=new TLession();
                    b.单元=m.get("dy");
                    b.类型=m.get("lx");
                    lstLessions.add(b);
                }
                l("共"+lstLessions.count()+"个记录");
                lstLessions.reload();
            }
        });
    }
}
