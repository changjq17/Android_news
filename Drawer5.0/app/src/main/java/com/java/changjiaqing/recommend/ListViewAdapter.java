package com.java.changjiaqing.recommend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.java.changjiaqing.NewsPhotoBean;
import com.java.changjiaqing.R;

import java.util.List;

public class ListViewAdapter extends ArrayAdapter<NewsPhotoBean> {
    private int resourceId;
    public ListViewAdapter(Context context, int textViewResourceId, List<NewsPhotoBean> objects) {
            super(context, textViewResourceId, objects);
            resourceId = textViewResourceId;
}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NewsPhotoBean newsitem = getItem(position); // 获取当前项的新闻实例
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent,false);
        TextView title = (TextView) view.findViewById(R.id.tx_news_mul_photos_title);
        TextView auther = (TextView) view.findViewById(R.id.tx_news_mul_photos_author);
        TextView time = (TextView) view.findViewById(R.id.tx_news_mul_photos_time);
        title.setText(newsitem.getTitle());
        auther.setText(newsitem.getAuthor());
        time.setText(newsitem.getF_time());
        return view;
    }

}
