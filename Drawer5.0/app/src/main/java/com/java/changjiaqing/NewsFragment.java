package com.java.changjiaqing;
/***
 * 结合viewpager实现新闻类型切换
 */

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chanven.lib.cptr.PtrClassicFrameLayout;
import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;
import com.chanven.lib.cptr.recyclerview.RecyclerAdapterWithHF;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.java.changjiaqing.MainActivity.main;
import static java.lang.Math.min;

public class NewsFragment extends Fragment {
    public  String strJson;
    private  RecyclerView recyclerView;
    private  PtrClassicFrameLayout ptrClassicFrameLayout;
    private  Handler handler=new Handler();
    private  int page = 0;
    private  int refleshtime=0;
    private  String lastTime;
    private  List<NewsPhotoBean> list = new ArrayList<NewsPhotoBean>();
    private  MulRecyclerViewAdapter adapter;
    private  RecyclerAdapterWithHF mAdapter;
    public static NewsFragment newInstance(final String type) {
        Bundle bundle = new Bundle();
        NewsFragment fragment = new NewsFragment();
        bundle.putString("type", type);
        fragment.setArguments(bundle);
        return fragment;
    }
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View view = inflater.inflate(R.layout.content_main,container,false);
        final String searchStr = main.searchStr;
        final int searching = main.searching;
        final String s = getArguments().getString("type");
        ptrClassicFrameLayout = (PtrClassicFrameLayout) view.findViewById(R.id.test_list_view_frame);
        recyclerView = (RecyclerView) view.findViewById(R.id.news_recycler_view);
        LinearLayoutManager llm =new LinearLayoutManager(getActivity());
        llm.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),RecyclerView.VERTICAL));
        adapter = new MulRecyclerViewAdapter(getActivity(),list);
        mAdapter = new RecyclerAdapterWithHF(adapter);
        recyclerView.setAdapter(mAdapter);

        //点击recyclerview的事件响应，进入新闻详情页
        adapter.setItemClickListener(new OnRecyclerViewClickListener() {
            public void onItemClickListener(View view,int position) {
                //int position = recyclerView.getChildAdapterPosition(view);
                TextView text_title;
                Intent intent=new Intent(getActivity(),NewsDetailActivity.class);
                Bundle bundle=new Bundle();
                NewsPhotoBean news=list.get(position);
                //已读新闻设置灰色
                if (news.getList().size()==0)
                    text_title=(TextView) view.findViewById(R.id.tx_news_mul_photos_title);
                else
                    text_title=(TextView) view.findViewById(R.id.tx_news_simple_photos_title);

                //已读新闻本地存储
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
                    } catch (IOException e) {Log.e("ERR","0");}

                    try {
                        FileWriter fw = new FileWriter(file);
                        BufferedWriter bw = new BufferedWriter(fw);
                        bw.write(str);
                        bw.flush();
                        //Log.e("Read",str);
                    } catch (Exception e){Log.e("ERR","!");}
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

        ptrClassicFrameLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                ptrClassicFrameLayout.autoRefresh(true);
            }
        }, 150);
        //下拉刷新
        ptrClassicFrameLayout.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        page= 0;
                        list.clear();
                        Thread t1=new Thread(new Runnable(){//子线程获取新闻
                            @Override
                            public void run() {
                                StringBuilder sb=new StringBuilder();
                                try{
                                    //Log.e("type",s);
                                    if (s.equals("read")) {
                                        //Log.e("get","get");
                                        File file = new File(Environment.getExternalStorageDirectory(),"news_read.txt");
                                        try {
                                            if (!file.exists()) {
                                                file.createNewFile();
                                            }
                                        } catch (IOException e) {}

                                        try {
                                            FileReader fd = new FileReader(file);
                                            BufferedReader in = new BufferedReader(fd);
                                            String inputLine;
                                            while ((inputLine = in.readLine()) != null){
                                                sb.append(inputLine); Log.d("TAG","正在读取历史记录");}
                                            in.close();

                                        } catch (Exception e) {}
                                        strJson=sb.toString();

                                    } else if (s.equals("collection")){
                                        File file = new File(Environment.getExternalStorageDirectory(),"news_collected.txt");
                                        try {
                                            if (!file.exists()) {
                                                file.createNewFile();
                                            }
                                        } catch (IOException e) {}

                                        try {
                                            FileReader fd = new FileReader(file);
                                            BufferedReader in = new BufferedReader(fd);
                                            String inputLine;
                                            while ((inputLine = in.readLine()) != null){
                                                sb.append(inputLine); Log.d("TAG","正在读取收藏列表");}
                                            in.close();

                                        } catch (Exception e){}
                                        strJson=sb.toString();

                                    }else{
                                        String s1 = java.net.URLEncoder.encode(s,"UTF-8");
                                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        Date curDate = new Date();
                                        //Log.e("Page1",page+"");
                                        if (page == 0) {
                                            curDate = new Date(System.currentTimeMillis());
                                            //Log.e("Test","1");
                                        } else {
                                            try{
                                                curDate = formatter.parse(lastTime);
                                                //Log.e("Test","2");
                                            }catch (Exception e){}
                                            Calendar cal = Calendar.getInstance();
                                            cal.setTime(curDate);
                                            cal.add(Calendar.SECOND,-1);
                                            curDate = cal.getTime();
                                        }
                                        String ed = formatter.format(curDate);
                                        Log.e("Time",ed);
                                        String s2 = java.net.URLEncoder.encode(ed,"UTF-8");
                                        Log.e("TAG","start");
                                        String api="https://api2.newsminer.net/svc/news/queryNewsList?size=15&startDate=2019-08-01&endDate="+s2;
                                        if (searching == 1) {
                                            String s3 = java.net.URLEncoder.encode(searchStr,"UTF-8");
                                            api = api + "&words=" + s3;
                                        }
                                        api = api +"&categories="+s1;
                                        URL cs = new URL(api);
                                        Log.e("TAG",api);
                                        BufferedReader in = new BufferedReader(new InputStreamReader(cs.openStream(), "UTF-8"));
                                        String inputLine;
                                        while ((inputLine = in.readLine()) != null){
                                            sb.append(inputLine); Log.e("TAG","正在发送网络请求");}
                                        in.close();
                                        strJson=sb.toString();
                                        }
                                    }catch(MalformedURLException e){}
                                catch(IOException e){}
                                //strJson=sb.toString();
                            }
                        });
                        t1.start();
                        try {
                            t1.join();
                        }catch (InterruptedException e){}
                        try {
                            JSONObject jsonObject = new JSONObject(strJson);
                            JSONArray jsonArray  = jsonObject.getJSONArray("data");
                            int pagesize=jsonObject.getInt("pageSize");
                            for(int i = 0;i<pagesize;i++){
                                NewsPhotoBean newsPhotoBean = new NewsPhotoBean();
                                newsPhotoBean.setTitle(jsonArray.getJSONObject(i).getString("title"));
                                newsPhotoBean.setF_time(lastTime = jsonArray.getJSONObject(i).getString("publishTime"));
                                newsPhotoBean.setAuthor(jsonArray.getJSONObject(i).getString("publisher"));
                                newsPhotoBean.setContent(jsonArray.getJSONObject(i).getString("content"));
                                newsPhotoBean.setId(jsonArray.getJSONObject(i).getString("newsID"));
                                newsPhotoBean.setCategory(jsonArray.getJSONObject(i).getString("category"));
                                newsPhotoBean.setVideo(jsonArray.getJSONObject(i).getString("video"));
                                if(!s.equals("read")&&!s.equals("collection"))
                                {
                                    String tmp="{ \"keywords\":"+jsonArray.getJSONObject(i).getString("keywords")+"}";
                                    JSONObject jsonObject2 = new JSONObject(tmp);
                                    JSONArray jsonArray_keywords  = jsonObject2.getJSONArray("keywords");
                                    int size=min(jsonArray_keywords.length(),3);
                                    ArrayList<String> kls=new ArrayList<String>();
                                    for (int j=0;j<size;j++){
                                        kls.add(jsonArray_keywords.getJSONObject(j).getString("word"));
                                    }
                                    newsPhotoBean.setKey_words(kls);
                                }

                                String image_url_list=jsonArray.getJSONObject(i).getString("image");
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
                                list.add(newsPhotoBean);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch(NullPointerException e){
                            e.printStackTrace();
                        }
                        mAdapter.notifyDataSetChanged();
                        ptrClassicFrameLayout.refreshComplete();
                        ptrClassicFrameLayout.setLoadMoreEnable(true);
                        //Log.d("TAG","正在刷新...");
                    }
                }, 500);
            }
        });
        //实现上拉加载更多
        ptrClassicFrameLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void loadMore() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Thread t1=new Thread(new Runnable(){//子线程获取新闻
                            @Override
                            public void run() {
                                page++;
                                StringBuilder sb=new StringBuilder();
                                try{
                                    if (s.equals("read")) {
                                        strJson="";
                                    } else if (s.equals("collection")){
                                        strJson="";
                                    }
                                    else {
                                        String s1 = java.net.URLEncoder.encode(s,"UTF-8");
                                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        Date curDate = new Date();
                                        Log.e("Page2",page+"");
                                        if (page == 0) {
                                            curDate = new Date(System.currentTimeMillis());
                                            Log.e("Test","1");
                                        } else {
                                            try{
                                                curDate = formatter.parse(lastTime);
                                                Log.e("Test","2");
                                            }catch (Exception e){}
                                            Calendar cal = Calendar.getInstance();
                                            cal.setTime(curDate);
                                            cal.add(Calendar.SECOND,-1);
                                            curDate = cal.getTime();
                                        }
                                        String ed = formatter.format(curDate);
                                        Log.e("Time",ed);
                                        String s2 = java.net.URLEncoder.encode(ed,"UTF-8");
                                        String api="https://api2.newsminer.net/svc/news/queryNewsList?size=15&startDate=2019-08-01&endDate="+s2;
                                        if (searching == 1) {
                                            String s3 = java.net.URLEncoder.encode(searchStr,"UTF-8");
                                            api = api + "&words=" + s3;
                                        }
                                        api = api +"&categories="+s1;
                                        URL cs = new URL(api);
                                        Log.e("TAG",api);
                                        BufferedReader in = new BufferedReader(new InputStreamReader(cs.openStream(), "UTF-8"));
                                        String inputLine;
                                        while ((inputLine = in.readLine()) != null){
                                            sb.append(inputLine); Log.e("TAG","正在发送网络请求");}
                                        in.close();
                                    }
                                    }catch (MalformedURLException e) {
                                } catch (IOException e) {
                                }
                                strJson=sb.toString();
                            }
                        });
                        t1.start();
                        try {
                            t1.join();
                        }catch (InterruptedException e){}
                        try {
                            JSONObject jsonObject = new JSONObject(strJson);
                            JSONArray jsonArray  = jsonObject.getJSONArray("data");
                            int pagesize=jsonObject.getInt("pageSize");
                            for(int i = 0;i<pagesize;i++){
                                NewsPhotoBean newsPhotoBean = new NewsPhotoBean();
                                newsPhotoBean.setTitle(jsonArray.getJSONObject(i).getString("title"));
                                newsPhotoBean.setF_time(lastTime=jsonArray.getJSONObject(i).getString("publishTime"));
                                newsPhotoBean.setAuthor(jsonArray.getJSONObject(i).getString("publisher"));
                                newsPhotoBean.setContent(jsonArray.getJSONObject(i).getString("content"));
                                newsPhotoBean.setId(jsonArray.getJSONObject(i).getString("newsID"));
                                newsPhotoBean.setCategory(jsonArray.getJSONObject(i).getString("category"));
                                newsPhotoBean.setVideo(jsonArray.getJSONObject(i).getString("video"));
                                if(!s.equals("read")&&!s.equals("collection")) {
                                    String tmp = "{ \"keywords\":" + jsonArray.getJSONObject(i).getString("keywords") + "}";
                                    JSONObject jsonObject2 = new JSONObject(tmp);
                                    JSONArray jsonArray_keywords = jsonObject2.getJSONArray("keywords");
                                    int size = min(jsonArray_keywords.length(), 3);
                                    ArrayList<String> kls = new ArrayList<String>();
                                    for (int j = 0; j < size; j++) {
                                        kls.add(jsonArray_keywords.getJSONObject(j).getString("word"));
                                    }
                                    newsPhotoBean.setKey_words(kls);
                                }
                                String image_url_list=jsonArray.getJSONObject(i).getString("image");
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
                                list.add(newsPhotoBean);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch(NullPointerException e){
                            e.printStackTrace();
                        }
                        mAdapter.notifyDataSetChanged();
                        ptrClassicFrameLayout.loadMoreComplete(true);
                        //Log.e("TAG","正在加载："+page+"页");
                    }
                }, 200);
            }
        });
        return view;
    }
}
