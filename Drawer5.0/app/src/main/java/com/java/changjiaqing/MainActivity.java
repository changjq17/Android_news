package com.java.changjiaqing;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import com.java.changjiaqing.category.CategoryActivity;
import com.java.changjiaqing.category.listdatasave;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.os.Environment;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.view.Menu;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private TabLayout news_tablayout;
    private ViewPager NewsViewPager;
    private ViewPagerAdapter myViewPagerAdapter;
    public List<String> news_category,search_history;
    public List<NewsFragment> fragments = new ArrayList<>();
    private listdatasave getcategorydata,getsearchhistory;
    public String searchStr;
    public int searching = 0;

    public ArrayList<String> news_read = new ArrayList<>();
    public JSONArray jsonReadArray = new JSONArray();

    public ArrayList<String> news_collected = new ArrayList<>();
    public JSONArray jsonCollectedArray = new JSONArray();

    public String readOrCollection = "";
    public static MainActivity main = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sp = getSharedPreferences("drawer",MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        Boolean isfirstopen=sp.getBoolean("isfirstopen",true);
        Boolean isdaymode=sp.getBoolean("isdaymode",true);
        if(isfirstopen){
            editor.putBoolean("isfirstopen", false);
            editor.commit();
        }
        else{
        getDelegate().setDefaultNightMode(isdaymode ?
                AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);}
        File file = new File(Environment.getExternalStorageDirectory(),"news_read.txt");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {Log.e("ERR","0");}
        StringBuilder sb=new StringBuilder();
        try {
            FileReader fd = new FileReader(file);
            BufferedReader in = new BufferedReader(fd);
            String inputLine;
            while ((inputLine = in.readLine()) != null){
                sb.append(inputLine);
            }
            in.close();
        } catch (Exception e){Log.e("ERR","!");}
        String strJson=sb.toString();
        try{
            JSONObject jsonObject = new JSONObject(strJson);
            jsonReadArray  = jsonObject.getJSONArray("data");
            int nums = jsonObject.getInt("pageSize");
            for (int i = 0; i < nums; i++) {
                news_read.add(jsonReadArray.getJSONObject(i).getString("newsID"));
            }
        } catch (Exception e){}

        file = new File(Environment.getExternalStorageDirectory(),"news_collected.txt");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {Log.e("ERR","0");}
        sb=new StringBuilder();
        try {
            FileReader fd = new FileReader(file);
            BufferedReader in = new BufferedReader(fd);
            String inputLine;
            while ((inputLine = in.readLine()) != null){
                sb.append(inputLine);
            }
            in.close();
        } catch (Exception e){Log.e("ERR","!");}
        strJson=sb.toString();
        try{
            JSONObject jsonObject = new JSONObject(strJson);
            jsonCollectedArray  = jsonObject.getJSONArray("data");
            int nums = jsonObject.getInt("pageSize");
            for (int i = 0; i < nums; i++) {
                news_collected.add(jsonReadArray.getJSONObject(i).getString("newsID"));
            }
        } catch (Exception e){}

        main = this;
        setContentView(R.layout.activity_main);
        //菜单栏
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //悬浮按钮
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        //搜索
        //抽屉菜单
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        //分类Tab
        news_tablayout=findViewById(R.id.tab_layout);
        NewsViewPager = findViewById(R.id.view_pager);
        getcategorydata=new listdatasave(this,"drawer");
        news_category=new ArrayList<String>();
        if(getcategorydata.getDataList("user").size()==0&&isfirstopen==true)//如果已经是第一次打开，需要初始化列表
        {
            String titlelist[]={"娱乐","军事","教育","文化","健康","财经","体育","汽车","科技","社会"};
            for (int i=0;i<titlelist.length;i++){
                news_category.add(titlelist[i]);
            }
            getcategorydata.setDataList("User",news_category);
            getcategorydata.setDataList("Other",new ArrayList<String>());
        }
        else{
            news_category=getcategorydata.getDataList("User");
            List<String> tmp=getcategorydata.getDataList("Other");
        }
        for (int i=0;i<news_category.size();i++){
            fragments.add(NewsFragment.newInstance(news_category.get(i)));
        }
        myViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),fragments,news_category); //使用适配器将ViewPager与Fragment绑定在一起
        NewsViewPager.setAdapter(myViewPagerAdapter);   //将TabLayout和ViewPager绑定在一起，相互影响
        news_tablayout.setupWithViewPager(NewsViewPager);

    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.news_list_toolbar, menu);
        final SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setQueryHint("输入关键词");
        getsearchhistory=new listdatasave(MainActivity.this,"history");
        if(getsearchhistory.getDataList("his").size()==0)
            search_history=new ArrayList<>();
        else
            search_history=getsearchhistory.getDataList("his");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //当点击搜索按钮时触发该方法
            String midStr;

            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.e("search",query);
                searchStr = query;
                searching = 1;
                search_history.add(query);
                getsearchhistory.setDataList("his",search_history);
                if (searchView != null) {
                    // 得到输入管理对象
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        // 这将让键盘在所有的情况下都被隐藏，但是一般我们在点击搜索按钮后，输入法都会乖乖的自动隐藏的。
                        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0); // 输入法如果是显示状态，那么就隐藏输入法
                    }
                    searchView.clearFocus(); // 不获取焦点
                }

                news_category=getcategorydata.getDataList("User");
                for (int i=0;i<news_category.size();i++){
                    fragments.add(NewsFragment.newInstance(news_category.get(i)));
                }
                myViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),fragments,news_category); //使用适配器将ViewPager与Fragment绑定在一起
                NewsViewPager.setAdapter(myViewPagerAdapter);   //将TabLayout和ViewPager绑定在一起，相互影响
                news_tablayout.setupWithViewPager(NewsViewPager);

                return false;
            }

            //当搜索内容改变时触发该方法
            @Override
            public boolean onQueryTextChange(String newText) {
                if (searching == 1 && newText.equals("") && !midStr.equals("")) {
                    searching = 0;
                    midStr = "";
                    searchStr = "";
                    news_category=getcategorydata.getDataList("User");
                    for (int i=0;i<news_category.size();i++){
                        fragments.add(NewsFragment.newInstance(news_category.get(i)));
                    }
                    myViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),fragments,news_category); //使用适配器将ViewPager与Fragment绑定在一起
                    NewsViewPager.setAdapter(myViewPagerAdapter);   //将TabLayout和ViewPager绑定在一起，相互影响
                    news_tablayout.setupWithViewPager(NewsViewPager);

                }
                midStr = newText;
                Log.e("search",newText);
                return false;
            }

        });

        return true;
    }
    //搜索操作
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    //左侧抽屉菜单，实现分类管理，收藏列表等操作
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_blacklist) {
            readOrCollection = "read";
            Intent intent=new Intent(this,NewsReadActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_collection) {
            readOrCollection = "collection";
            Intent intent=new Intent(this,NewsReadActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_category) {
            Intent intent=new Intent(MainActivity.this, CategoryActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_darkmode) {//夜间模式
            SharedPreferences sp = getSharedPreferences("drawer",MODE_PRIVATE);
            SharedPreferences.Editor edit  = sp.edit();
            int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            getDelegate().setDefaultNightMode(currentNightMode == Configuration.UI_MODE_NIGHT_NO ?
                    AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
            edit.putBoolean("isdaymode",currentNightMode == Configuration.UI_MODE_NIGHT_NO);
            edit.commit();
            startActivity(new Intent(this,MainActivity.class));
            overridePendingTransition(R.anim.animo_alph_open, R.anim.animo_alph_close);
        } else if (id == R.id.nav_layout) {//换肤
            Intent in=new Intent(MainActivity.this,skinlayout.class);
            startActivity(in);
        } else if (id == R.id.nav_about) {
            Intent in=new Intent(MainActivity.this,SearchHistoryActivity.class);
            startActivity(in);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}