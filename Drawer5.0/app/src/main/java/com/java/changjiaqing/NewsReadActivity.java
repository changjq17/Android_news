package com.java.changjiaqing;
/***
 * 新闻详情页的Activity
 */

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.java.changjiaqing.MainActivity.main;

public class NewsReadActivity extends AppCompatActivity {
    public NewsFragment fragment;
    public List<NewsFragment> fragments = new ArrayList<>();
    private ViewPager NewsViewPager;
    private ViewPagerAdapter myViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_read);
        Toolbar toolbar = findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        ArrayList<String> news_category=new ArrayList<>();
        news_category.add(main.readOrCollection);
        fragment = NewsFragment.newInstance(main.readOrCollection);
        fragments.add(fragment);
        NewsViewPager = findViewById(R.id.view_pager);
        myViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),fragments,news_category);
        NewsViewPager.setAdapter(myViewPagerAdapter);   //将TabLayout和ViewPager绑定在一起，相互影响

    }
    //加载菜单栏
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.news_read_toolbar, menu);

        return true;
    }
    //菜单栏响应事件，包括返回，收藏，分享
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.return_1:
                finish();
                break;
            case R.id.media_route_menu_item:
                File file = new File("");
                String str = "";
                if (main.readOrCollection.equals("read")) {
                    main.news_read.clear();
                    main.jsonReadArray = new JSONArray();
                    JSONObject jsonObject2 = new JSONObject();
                    try {
                        jsonObject2.put("pageSize",main.jsonReadArray.length());
                        jsonObject2.put("data",main.jsonReadArray);
                    }catch (Exception e){}
                    str = jsonObject2.toString();
                    file = new File(Environment.getExternalStorageDirectory(),"news_read.txt");
                } else if (main.readOrCollection.equals("collection")) {
                    main.news_collected.clear();
                    main.jsonCollectedArray = new JSONArray();
                    JSONObject jsonObject2 = new JSONObject();
                    try {
                        jsonObject2.put("pageSize",main.jsonCollectedArray.length());
                        jsonObject2.put("data",main.jsonCollectedArray);
                    }catch (Exception e){}
                    str = jsonObject2.toString();
                    file = new File(Environment.getExternalStorageDirectory(),"news_collected.txt");
                }


                try {
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                } catch (IOException e) {
                    Log.e("ERR","0");}

                try {
                    FileWriter fw = new FileWriter(file);
                    BufferedWriter bw = new BufferedWriter(fw);
                    bw.write(str);
                    bw.flush();
                    Log.e("Read",str);
                } catch (Exception e){Log.e("ERR","!");}
                ArrayList<String> news_category=new ArrayList<>();
                news_category.add(main.readOrCollection);
                fragment = NewsFragment.newInstance(main.readOrCollection);
                fragments.add(fragment);
                NewsViewPager = findViewById(R.id.view_pager);
                myViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),fragments,news_category);
                NewsViewPager.setAdapter(myViewPagerAdapter);   //将TabLayout和ViewPager绑定在一起，相互影响

                break;
            default:
        }
        return true;
    }
}