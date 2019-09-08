package com.java.changjiaqing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.java.changjiaqing.category.listdatasave;

import java.util.List;

public class SearchHistoryActivity extends AppCompatActivity {
    private listdatasave getsearchhistory;
    private List<String> historylist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_history);
        Toolbar toolbar = findViewById(R.id.toolbar4);
        setSupportActionBar(toolbar);
        getsearchhistory=new listdatasave(this,"history");
        historylist=getsearchhistory.getDataList("his");
        ListView listView=findViewById(R.id.history_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,historylist);
        listView.setAdapter(adapter);
    }
}
