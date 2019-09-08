package com.java.changjiaqing;
/***
 * 新闻详情页的Activity
 */

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.java.changjiaqing.recommend.ListViewAdapter;
import com.java.changjiaqing.recommend.MyListView;
import com.woaigmz.share.ShareChannel;
import com.woaigmz.share.ShareStatus;
import com.woaigmz.share.model.ShareBean;
import com.woaigmz.share.view.ShareActivity;
import com.woaigmz.share.view.ShareDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.java.changjiaqing.MainActivity.main;
import static java.lang.Math.min;

public class NewsDetailActivity extends AppCompatActivity{
    public String strJson[];
    private TextView tv1,tv2,tv3,tv4;
    private LinearLayout linearLayout;
    private List<NewsPhotoBean> recommend_news_List;
    private ArrayList<String> keywords;
    private ArrayList<String> images;
    private String title,ID,time,name,content,videourl,category;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        Intent i=getIntent();
        Bundle b=i.getExtras();
        tv1= findViewById(R.id.details_title);
        tv2= findViewById(R.id.details_time);
        tv3= findViewById(R.id.details_name);
        linearLayout = findViewById(R.id.details_text_image);
        ID = b.getString("newsID");
        title=b.getString("title");
        time=b.getString("publishTime");
        name=b.getString("publisher");
        tv1.setText(title);
        tv2.setText(b.getString("publishTime"));
        tv3.setText(b.getString("publisher"));
        content=b.getString("content");
        String paragraph[]=content.split("\n\n");
        images=b.getStringArrayList("image");
        try {
        for (int j=0;j<images.size();j++){
            ImageView imageView = new ImageView(this);
            TextView textView = new TextView(this);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));  //设置图片宽高
            Glide.with(this).load(images.get(j)).into(imageView);
            textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            textView.setText(paragraph[j]);
            textView.setTextSize(20);
            linearLayout.addView(textView);
            linearLayout.addView(imageView); //动态添加图片
        }
            tv4 = new TextView(this);
            tv4.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            tv4.setText(paragraph[images.size()]);
            tv4.setTextSize(20);
            linearLayout.addView(tv4);
        }catch (ArrayIndexOutOfBoundsException e){
            //e.printStackTrace();
        }
        videourl=b.getString("video");
        VideoView videoView = findViewById(R.id.videoView1);
        if(!videourl.equals("")) {
            videoView.setVideoURI(Uri.parse(videourl));
            MediaController mediaController = new MediaController(this);
            videoView.setMediaController(mediaController);
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    //Log.i("通知", "完成");
                    Toast.makeText( NewsDetailActivity.this, "播放完成", Toast.LENGTH_SHORT).show();
                }
            });

            videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    //Log.i("通知", "播放中出现错误");
                    Toast.makeText( NewsDetailActivity.this, "播放错误", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
        }
        else {
            RelativeLayout wrapvideo=findViewById(R.id.wrap_video);
            wrapvideo.setVisibility(View.GONE);
        }
        category=b.getString("category");
        keywords=new ArrayList<>();
        keywords=b.getStringArrayList("keywords");
        recommend_news_List=new ArrayList<>();
        strJson=new String[3];
        //推荐新闻的列表
        //initView();
        Thread t1=new Thread(new Runnable(){
            @Override
            public void run() {
                for(int i=0;i<1;i++){
                    StringBuilder sb=new StringBuilder();
                    try{
                        String api;
                        if(keywords.size()==0) api="https://api2.newsminer.net/svc/news/queryNewsList?size=8&startDate=2019-08-01&endDate=2019-09-04&categories="+category;
                        else api="https://api2.newsminer.net/svc/news/queryNewsList?size=8&startDate=2019-08-01&endDate=2019-09-04&words="+keywords.get(i);
                        URL cs = new URL(api);
                        BufferedReader in = new BufferedReader(new InputStreamReader(cs.openStream(), "UTF-8"));
                        String inputLine;
                        while ((inputLine = in.readLine()) != null){
                            sb.append(inputLine); }
                        in.close();
                    }catch(MalformedURLException e){}
                    catch(IOException e){}
                    catch(NullPointerException e){}
                    strJson[i]=sb.toString();
                }
            }
        });
        t1.start();
        try{
        t1.join();
        }catch (InterruptedException e){}
        for (int m=0;m<1;m++){
            try {
                JSONObject jsonObject = new JSONObject(strJson[m]);
                JSONArray jsonArray  = jsonObject.getJSONArray("data");
                for(int k = 0;k<jsonArray.length();k++){
                    NewsPhotoBean newsPhotoBean = new NewsPhotoBean();
                    newsPhotoBean.setTitle(jsonArray.getJSONObject(k).getString("title"));
                    newsPhotoBean.setF_time(jsonArray.getJSONObject(k).getString("publishTime"));
                    newsPhotoBean.setAuthor(jsonArray.getJSONObject(k).getString("publisher"));
                    newsPhotoBean.setContent(jsonArray.getJSONObject(k).getString("content"));
                    newsPhotoBean.setId(jsonArray.getJSONObject(k).getString("newsID"));
                    newsPhotoBean.setCategory(jsonArray.getJSONObject(k).getString("category"));
                    newsPhotoBean.setVideo(jsonArray.getJSONObject(k).getString("video"));
                    String tmp="{ \"keywords\":"+jsonArray.getJSONObject(k).getString("keywords")+"}";
                    JSONObject jsonObject2 = new JSONObject(tmp);
                    JSONArray jsonArray_keywords  = jsonObject2.getJSONArray("keywords");
                    int size=min(jsonArray_keywords.length(),3);
                    ArrayList<String> kls=new ArrayList<String>();
                    for (int j=0;j<size;j++){
                        kls.add(jsonArray_keywords.getJSONObject(j).getString("word"));
                    }
                    newsPhotoBean.setKey_words(kls);

                    String image_url_list=jsonArray.getJSONObject(k).getString("image");
                    if(image_url_list.length()!=0)
                    image_url_list=image_url_list.substring(1,image_url_list.length()-1);
                    ArrayList<String> ls = new ArrayList<>();
                    if(image_url_list.length()!=0){
                        int index=image_url_list.indexOf(",");
                        while(index!=-1){
                            ls.add(image_url_list.substring(0,index));
                            image_url_list=image_url_list.substring(index+2,image_url_list.length());
                            index=image_url_list.indexOf(",");
                        }
                        ls.add(image_url_list);
                    }
                    newsPhotoBean.setList(ls);
                    recommend_news_List.add(newsPhotoBean);
                }
            } catch (JSONException e) {

            } catch(NullPointerException e){

            } catch(IndexOutOfBoundsException e){

            }
        }
        //Log.d("TAG","一共收到"+recommend_news_List.size()+"条推荐");
        if(recommend_news_List.size()==0){
            Thread t2=new Thread(new Runnable(){
                @Override
                public void run() {
                        StringBuilder sb=new StringBuilder();
                        try{
                            String api="https://api2.newsminer.net/svc/news/queryNewsList?size=8&startDate=2019-08-01&endDate=2019-09-04&categories="+category;
                            URL cs = new URL(api);
                            BufferedReader in = new BufferedReader(new InputStreamReader(cs.openStream(), "UTF-8"));
                            String inputLine;
                            while ((inputLine = in.readLine()) != null){
                                sb.append(inputLine); }
                            in.close();
                        }catch(MalformedURLException e){
                        }catch(IOException e){}
                        strJson[0]=sb.toString();
                    }
                });
            t2.start();
            try{
                t2.join();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
                try {
                    JSONObject jsonObject = new JSONObject(strJson[0]);
                    JSONArray jsonArray  = jsonObject.getJSONArray("data");
                    for(int k = 0;k<jsonArray.length();k++){
                        NewsPhotoBean newsPhotoBean = new NewsPhotoBean();
                        newsPhotoBean.setTitle(jsonArray.getJSONObject(k).getString("title"));
                        newsPhotoBean.setF_time(jsonArray.getJSONObject(k).getString("publishTime"));
                        newsPhotoBean.setAuthor(jsonArray.getJSONObject(k).getString("publisher"));
                        newsPhotoBean.setContent(jsonArray.getJSONObject(k).getString("content"));
                        newsPhotoBean.setId(jsonArray.getJSONObject(k).getString("newsID"));
                        newsPhotoBean.setCategory(jsonArray.getJSONObject(k).getString("category"));
                        newsPhotoBean.setVideo(jsonArray.getJSONObject(k).getString("video"));
                        String tmp="{ \"keywords\":"+jsonArray.getJSONObject(k).getString("keywords")+"}";
                        JSONObject jsonObject2 = new JSONObject(tmp);
                        JSONArray jsonArray_keywords  = jsonObject2.getJSONArray("keywords");
                        int size=min(jsonArray_keywords.length(),3);
                        ArrayList<String> kls=new ArrayList<String>();
                        for (int j=0;j<size;j++){
                            kls.add(jsonArray_keywords.getJSONObject(j).getString("word"));
                        }
                        newsPhotoBean.setKey_words(kls);

                        String image_url_list=jsonArray.getJSONObject(k).getString("image");
                        if(image_url_list.length()!=0)
                        image_url_list=image_url_list.substring(1,image_url_list.length()-1);
                        ArrayList<String> ls = new ArrayList<>();
                        if(image_url_list.length()!=0){
                            int index=image_url_list.indexOf(",");
                            while(index!=-1){
                                ls.add(image_url_list.substring(0,index));
                                image_url_list=image_url_list.substring(index+2,image_url_list.length());
                                index=image_url_list.indexOf(",");
                            }
                            ls.add(image_url_list);
                        }
                        newsPhotoBean.setList(ls);
                        recommend_news_List.add(newsPhotoBean);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch(NullPointerException e){
                    e.printStackTrace();
                } catch(IndexOutOfBoundsException e){
                    e.printStackTrace();
                }
            }
        ListViewAdapter adapter = new ListViewAdapter(NewsDetailActivity.this, R.layout.news_item_layout2, recommend_news_List);
        MyListView listView = (MyListView) findViewById(R.id.recommend_news);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    TextView text_title;
                    Intent intent=new Intent(NewsDetailActivity.this,NewsDetailActivity.class);
                    Bundle bundle=new Bundle();
                    NewsPhotoBean news=recommend_news_List.get(position);
                    text_title=(TextView) view.findViewById(R.id.tx_news_mul_photos_title);
                    text_title.setTextColor(Color.LTGRAY);
                    if (!main.news_read.contains(news.getId())) {
                        main.news_read.add(news.getId());
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("title",news.getTitle());
                            jsonObject.put("content",news.getContent());
                            jsonObject.put("publishTime",news.getF_time());
                            jsonObject.put("publisher",news.getAuthor());
                            jsonObject.put("newsID",news.getId());
                            jsonObject.put("category",news.getCategory());
                            jsonObject.put("video",news.getVideo());
                            jsonObject.put("keywords",news.getKey_words());
                            jsonObject.put("image",news.getList());
                        } catch (Exception e){}
                        main.jsonReadArray.put(jsonObject);

                        JSONObject jsonObject2 = new JSONObject();
                        try {
                            jsonObject2.put("pageSize",main.jsonReadArray.length());
                            jsonObject2.put("data",main.jsonReadArray);
                        }catch (Exception e){}
                        String str = jsonObject2.toString();
                        File file = new File(Environment.getExternalStorageDirectory(),"news_read.txt");
                        try {
                            if (!file.exists()) {
                                file.createNewFile();
                            }
                        } catch (IOException e) {}

                        try {
                            FileWriter fw = new FileWriter(file);
                            BufferedWriter bw = new BufferedWriter(fw);
                            bw.write(str);
                            bw.flush();
                            //Log.e("Read",str);
                        } catch (Exception e){}
                    }
                    bundle.putString("title",news.getTitle());
                    bundle.putString("content",news.getContent());
                    bundle.putString("publishTime",news.getF_time());
                    bundle.putString("publisher",news.getAuthor());
                    bundle.putString("category",news.getCategory());
                    bundle.putString("video",news.getVideo());
                    bundle.putString("newsID",news.getId());
                    bundle.putStringArrayList("image",news.getList());
                    bundle.putStringArrayList("keywords",news.getKey_words());
                    intent.putExtras(bundle);
                    startActivity(intent);
            }
        });
    }
    public void initView(){

    }
    //加载菜单栏
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.news_detail_toolbar, menu);
        return true;
    }
    //菜单栏响应事件，包括返回，收藏，分享
    @Override
    public boolean onOptionsItemSelected(MenuItem item) throws NullPointerException {
        switch (item.getItemId()) {
            case R.id.return_1:
                finish();
                break;
            case R.id.collect:
                //在这里填入收藏的动作
                if (!main.news_collected.contains(ID)) {
                    Toast.makeText(this, "已收藏", Toast.LENGTH_SHORT).show();
                    main.news_collected.add(ID);
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("title",title);
                        jsonObject.put("content",content);
                        jsonObject.put("publishTime",time);
                        jsonObject.put("publisher",name);
                        jsonObject.put("newsID",ID);
                        jsonObject.put("category",category);
                        jsonObject.put("video",videourl);
                        jsonObject.put("keywords",keywords);
                        jsonObject.put("image",images);
                    } catch (Exception e){}
                    main.jsonCollectedArray.put(jsonObject);

                    JSONObject jsonObject2 = new JSONObject();
                    try {
                        jsonObject2.put("pageSize",main.jsonCollectedArray.length());
                        jsonObject2.put("data",main.jsonCollectedArray);
                    }catch (Exception e){}
                    String str = jsonObject2.toString();
                    File file = new File(Environment.getExternalStorageDirectory(),"news_collected.txt");
                    try {
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                    } catch (IOException e) {
                        }

                    try {
                        FileWriter fw = new FileWriter(file);
                        BufferedWriter bw = new BufferedWriter(fw);
                        bw.write(str);
                        bw.flush();

                        Log.e("Read",str);
                    } catch (Exception e){}
                } else {
                    Toast.makeText(this, "已取消收藏", Toast.LENGTH_SHORT).show();
                    int pos = main.news_collected.indexOf(ID);
                    main.news_collected.remove(pos);
                    main.jsonCollectedArray.remove(pos);
                    JSONObject jsonObject2 = new JSONObject();
                    try {
                        jsonObject2.put("pageSize",main.jsonCollectedArray.length());
                        jsonObject2.put("data",main.jsonCollectedArray);
                    }catch (Exception e){}
                    String str = jsonObject2.toString();
                    File file = new File(Environment.getExternalStorageDirectory(),"news_collected.txt");
                    try {
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                    } catch (IOException e) {
                        }

                    try {
                        FileWriter fw = new FileWriter(file);
                        BufferedWriter bw = new BufferedWriter(fw);
                        bw.write(str);
                        bw.flush();

                        Log.e("Read",str);
                    } catch (Exception e){}
                }

                break;
            case R.id.share:
                //分享操作，暂时不用管
                try {
                    String imgurl;
                    if (images.size() == 0)
                        imgurl = "http://wx3.sinaimg.cn/large/006nLajtly1fkegnmnwuxj30dw0dw408.jpg";
                    else imgurl = images.get(0);
                    new ShareDialogBuilder()
                            .setContext(this)
                            .setShareChannels(new int[]{ShareChannel.CHANNEL_QQ, ShareChannel.CHANNEL_WECHAT, ShareChannel.CHANNEL_WEIBO})
                            .setColumn(3)
                            .setModel(new ShareBean("标题",
                                    title,
                                    imgurl,
                                    0,
                                    "https://www.baidu.com"))
                            .build()
                            .show(getSupportFragmentManager());
                }catch (NullPointerException e){}
                break;
                default:
        }
        return true;
    }
    //分享结果，暂时不用管
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) throws NullPointerException {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            int status = data.getIntExtra(ShareActivity.RESULT_STATUS, -1);
            switch (status) {
                case ShareStatus.SHARE_STATUS_COMPLETE:
                    Toast.makeText(this, "分享成功", Toast.LENGTH_SHORT).show();
                    break;
                case ShareStatus.SHARE_STATUS_ERROR:
                    Toast.makeText(this, "分享失败", Toast.LENGTH_SHORT).show();
                    break;
                case ShareStatus.SHARE_STATUS_CANCEL:
                    //Toast.makeText(this, "分享取消", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}