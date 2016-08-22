package com.words.chat;

import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.words.utils.CommonUtils;
import com.words.utils.SwForm;
import com.words.utils.SwHttp;
import com.words.utils.SwJson;
import com.words.utils.voice.AudioPlayer;
import com.words.utils.voice.VoiceRecoder;
import java.io.File;
import java.net.URLEncoder;
import cz.msebera.android.httpclient.Header;
import sw.ui.SwListView;

import static com.words.utils.Tools.cint;
import static com.words.utils.Tools.getPart;

public class frmWordList extends SwForm
{
    class TWords
    {
        String id="0";
        String txt="";
        boolean HaveSound=false;
    }

    private AnimationDrawable animationDrawable;
    private ImageView micImage;
    // 语音输入按钮上的提示文字
    private TextView recordingHint;
    private AudioPlayer audioPlayer = new AudioPlayer();
    private VoiceRecoder voiceRecoder;
    SwListView lstWords;
    MediaPlayer mPlayer=new MediaPlayer();
    public String book_id="0",lx="",dy="";
    // 语音显示布局
    private View recordingContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frm_word_list);

        book_id=app.ini("book_id");
        lx=app.ini("类型");
        dy=app.ini("单元");


        micImage = (ImageView) findViewById(R.id.mic_image);
        animationDrawable = (AnimationDrawable) micImage.getBackground();
        animationDrawable.setOneShot(false);
        recordingHint = (TextView) findViewById(R.id.recording_hint);
        recordingContainer = findViewById(R.id.view_talk);

        lstWords=(SwListView)findViewById(R.id.lstWords);

        lstWords.setSwDelegate(new SwListView.SwDelegate() {
            @Override
            public void OnItemSelect(int pos)
            {
                toast(pos+"");
            }

            @Override
            public View OnGetView(final int position, View convertView, ViewGroup parent) {
                l("lst.ongetview pos="+position+" convertview="+convertView);
                if(convertView==null)
                {
                    l("converview==null create it!");
                    convertView=lstWords.inflate(R.layout.lay_words_item);
                }
                final TWords b=(TWords) lstWords.get(position);
                TextView txt1=(TextView)convertView.findViewById(R.id.txt1);
                txt1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        play(b.txt,b.id);
                    }
                });

                convertView.findViewById(R.id.btnRec).setOnTouchListener(new PressToSpeakListener(b.id));


                String sTxt="";
                sTxt=(position+1)+". <font color=blue>"+b.txt+"</font>";
                txt1.setText(Html.fromHtml(sTxt));
                return convertView;
            }
        });

        init_data();
    }

    void play(final String txt,final String id)
    {
        final String id_=""+cint(id);
        l("txt="+txt+",id="+id+",id_="+id_);
        if(FileExists("/sdcard/word_sound/"+id_+".mp3"))
        {
            playmp3("/sdcard/word_sound/"+id_+".mp3");
        }
        else
        {
            final String ff="http://jy.dalianit.com/word_sound/"+id_+".mp3";
            SwHttp.get(ff, null, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    bytes2file(responseBody,"/sdcard/word_sound/"+id_+".mp3");
                    playmp3("/sdcard/word_sound/"+id_+".mp3");
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    toast("声音文件下载失败 "+statusCode+" "+ff);
                    app.speak(txt);
                }
            });

        }
    }

    void playmp3(String s)
    {
        if(mPlayer.isPlaying())mPlayer.stop();
        try
        {
            l("播放mp3: "+s);
            mPlayer=MediaPlayer.create(frmWordList.this,Uri.fromFile(new File(s)));
            mPlayer.setLooping(false);
            mPlayer.start();
        }
        catch (Exception e)
        {
            toast("播放出错 "+e.getMessage());
        }
    }

    @Override
    public void OnRecvHandler(int what, Object msg) {
        super.OnRecvHandler(what, msg);
        if(what==1)
        {
            setTitle(msg.toString());
        }
    }

    void init_data()
    {
        lstWords.clear();
        SwHttp.get("words.php?book_id="+book_id+"&dy="+URLEncoder.encode(dy)+"&lx="+ URLEncoder.encode(lx)+"&tim=", null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable)
            {
                Toast.makeText(frmWordList.this,"访问网络失败",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString)
            {
                l("-->"+responseString);
                String s=getPart(responseString,"<!--START JSON-->","<!--END JSON-->");
                l("filter.json="+s);
                SwJson x=SwJson.Create(s);
                SwJson.SwDataTable dt=x.table();

                sendHandler(1,x.get("book_name")+dy+lx+"("+dt.RecordCount+")");

                for(SwJson.SwDataRow m:dt.rows)
                {
                    TWords b=new TWords();
                    b.id=m.get("id");
                    b.txt=m.get("txt");
                    b.HaveSound=(m.get("sound").toLowerCase().indexOf("y")>=0);
                    lstWords.add(b);
                    l(" grade="+b.txt );
                }
                l("共"+lstWords.count()+"个记录");
                lstWords.reload();

            }
        });
    }

    /**
     * 按住说话listener
     */
    class PressToSpeakListener implements View.OnTouchListener
    {
        String id="";
        long lTim=-1;
        PressToSpeakListener(String id_)
        {
            l("rec init item");
            id=id_;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    animationDrawable.start();
                    if (!CommonUtils.isExitsSdcard()) {
                        Toast.makeText(frmWordList.this, "发送语音需要sdcard支持", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    lTim=System.currentTimeMillis();
                    v.setPressed(true);

                    // 如果此时正在播放语音，先停止语音播放
                    if (audioPlayer != null) {
                        audioPlayer.stop();
                    }

                    // 显示说话的布局
                    recordingContainer.setVisibility(View.VISIBLE);
                    recordingHint.setText("手指上滑，取消发送");
                    recordingHint.setBackgroundColor(Color.TRANSPARENT);

                    // 开始录音
                    Log.d(TAG, "voiceRecoder.start()");
                    voiceRecoder = new VoiceRecoder("sunwayrec", frmWordList.this);
                    voiceRecoder.start();

                    return true;
                case MotionEvent.ACTION_MOVE:
                    if (event.getY() < 0) {
                        recordingHint.setText("松开手指，取消发送");
                        recordingHint
                                .setBackgroundResource(R.drawable.recording_text_hint_bg);
                    } else {
                        recordingHint.setText("手指上滑，取消发送");
                        recordingHint.setBackgroundColor(Color.TRANSPARENT);
                        animationDrawable.start();
                    }
                    return true;
                case MotionEvent.ACTION_UP:
                    if (animationDrawable.isRunning()) {
                        animationDrawable.stop();
                    }
                    v.setPressed(false);
                    recordingContainer.setVisibility(View.INVISIBLE);
                    if(System.currentTimeMillis()-lTim<500)
                    {
                        voiceRecoder.discard();
                        toast("时间太短");
                        return true;
                    }

                    if (event.getY() < 0) {
                        // 丢弃这次录音
                        if (voiceRecoder != null) {
                            l("voiceRecoder.discard()");
                            voiceRecoder.discard();
                        }
                    } else {
                        // 停止录音并发送录音文件
                        if (voiceRecoder != null) {
                            l("voiceRecoder.stop()");
                            voiceRecoder.stop();
                            sendVoice(voiceRecoder.getVoiceFilePath("name"),id);
                        }
                    }
                    return true;
                default:
                    recordingContainer.setVisibility(View.INVISIBLE);
                    // 丢弃这次录音
                    if (voiceRecoder != null) {
                        l("voiceRecoder.discard()");
                        voiceRecoder.discard();
                    }
                    return false;
            }
        }
    }

    void sendVoice(String f,String id)
    {
        playmp3(f);

        //toast(f);
        RequestParams ps=new RequestParams();
        ps.add("username","孙大花222");

        try
        {
            ps.put("file111", new File(f));
        }
        catch(Exception e){}

        SwHttp.post("upload_mp3.php?id="+id, ps, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable)
            {
                Log.e("xxx","upload_mp3.fail "+statusCode+" "+responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString)
            {
                if(responseString.indexOf("upload ok")>0)
                {
                    toast("上传成功");
                }
                Log.e("xxx","upload_mp3.ok "+statusCode+" "+responseString);
            }
            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                super.onProgress(bytesWritten, totalSize);
                int count = (int) ((bytesWritten * 1.0 / totalSize) * 100);
                l("upload_mp3.pct "+count+"%");
            }
        });
    }

}
