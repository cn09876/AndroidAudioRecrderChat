package com.words.chat;

import android.app.Activity;
import android.media.Image;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.drawee.view.SimpleDraweeView;
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

public class frmBookList extends SwForm
{

    class TBooks
    {
        public String id="0";
        public String img="";
        public String grade="";
        public String term="";
        public String subject="";
        public String name="";
    }

    SwListView lstBooks;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frm_book_list);
        lstBooks=(SwListView)findViewById(R.id.lstBooks);

        findViewById(R.id.reload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                init_data();
            }
        });

        this.setTitle("考记一点通--书本列表");
        init_data();

        lstBooks.setSwDelegate(new SwListView.SwDelegate()
        {
            @Override
            public void OnItemSelect(int pos)
            {
                String sid=((TBooks)lstBooks.get(pos)).id;
                app.ini("book_id",sid);
                show_frm(frmLessionList.class);
            }

            @Override
            public View OnGetView(int position, View convertView, ViewGroup parent)
            {
                if(convertView==null)
                {
                    convertView=lstBooks.inflate(R.layout.lay_books_item);
                }

                TextView txt1=(TextView)convertView.findViewById(R.id.txt1);
                String sUrl="",sTxt="";
                TBooks b=(TBooks) lstBooks.get(position);
                sUrl=b.img;
                sTxt="<font color=red size=16>"+b.subject+"</font><br>"+b.name;
                SimpleDraweeView img1=(SimpleDraweeView)convertView.findViewById(R.id.img1);
                img1.setImageURI(sUrl);
                txt1.setText(Html.fromHtml(sTxt));
                return convertView;
            }
        });
    }

    void init_data()
    {
        lstBooks.clear();
        SwHttp.get("books.php", null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable)
            {
                Toast.makeText(frmBookList.this,"访问网络失败",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString)
            {

                SwJson x=SwJson.Create(responseString);
                l(x.get("Title"));

                for(SwJson.SwDataRow dr:x.table().rows)
                {
                    l(dr.toString());
                    TBooks b=new TBooks();
                    b.img="http://jy.dalianit.com/api_v1/111.jpg";
                    b.id=dr.get("id").toString();
                    b.name=dr.get("name").toString();
                    b.subject=dr.get("subject").toString();
                    b.grade=dr.get("grade").toString();
                    b.term=dr.get("term").toString();
                    lstBooks.add(b);
                }
                lstBooks.reload();
            }
        });
    }

}
