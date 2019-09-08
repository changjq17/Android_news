package com.java.changjiaqing;
/***
 Recyclerview的Adapter
 */

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import static com.java.changjiaqing.MainActivity.main;

public class MulRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final int NEW_NOIMAGE = 0;//有图模式
    private static final int NEW_WITHIMAGE = 1;//无图模式
    private Context context;
    private List<NewsPhotoBean> list;
    private OnRecyclerViewClickListener listener;
    //构造函数
    MulRecyclerViewAdapter(Context context, List<NewsPhotoBean> list) {
        this.context = context;
        this.list = list;
    }
    //设置监听事件
    public void setItemClickListener(OnRecyclerViewClickListener itemClickListener) {
        listener = itemClickListener;
    }
    //重写getItemViewType方法,通过此方法来判断应该加载是哪种类型布局
    @Override
    public int getItemViewType(int position) {
        int type = list.get(position).getList().size();
        switch (type) {
            case 0:
                return NEW_NOIMAGE;
            default:
                return NEW_WITHIMAGE;
        }
    }

    //根据不同的item类型来加载不同的viewholder
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        switch (viewType) {
            case NEW_WITHIMAGE:
                return new NewsPhotoViewHolder(inflater.inflate(R.layout.news_item_layout1, parent, false));
            case NEW_NOIMAGE:
                return new NewsPhotosViewHolder(inflater.inflate(R.layout.news_item_layout2, parent, false));
        }

        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        //把对应位置的数据得到
        String title = list.get(position).getTitle();
        String time = list.get(position).getF_time();
        String id = list.get(position).getId();
        String author = list.get(position).getAuthor();
        List<String> ls = list.get(position).getList();//这里是json数据中的图片集合，也就是封面。不同类型item的封面图片数量是不一样的
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //触发自定义监听的单击事件
                Log.d("TAG","recyclerview点击事件test");
                if(listener!=null)
                listener.onItemClickListener(holder.itemView,position);
            }
        });

        //如果有图文
        if (holder instanceof NewsPhotoViewHolder) {

            (((NewsPhotoViewHolder) holder).tx_news_simple_photos_title).setText(title);

            if (main.news_read.contains(id)) {
                (((NewsPhotoViewHolder) holder).tx_news_simple_photos_title).setTextColor(Color.LTGRAY);
            }
            (((NewsPhotoViewHolder) holder).tx_news_simple_photos_time).setText(time);
            TextView tv = ((NewsPhotoViewHolder)holder).tx_news_simple_photos_author;
            if(tv==null){
                Log.e("xtx","null pointer");
            }
            tv.setText(author);
            Glide.with(context).load(ls.get(0)).into(((NewsPhotoViewHolder) holder).img_news_simple_photos_01);
            return;
        }
        //如果无图文
        if (holder instanceof NewsPhotosViewHolder) {
            (((NewsPhotosViewHolder) holder).tx_news_no_photos_title).setText(title);
            if (main.news_read.contains(id)) {
                (((NewsPhotosViewHolder) holder).tx_news_no_photos_title).setTextColor(Color.LTGRAY);
            }
            (((NewsPhotosViewHolder) holder).tx_news_no_photos_time).setText(time);
            (((NewsPhotosViewHolder) holder).tx_news_no_photos_author).setText(author);
            return;
        }
    }
    //具体item数据等于pages*15，每页15条
    @Override
    public int getItemCount() {

        return list.size();
    }

    /**
     * NewsPhotoViewHolder为有图文模式
     */
    class NewsPhotoViewHolder extends RecyclerView.ViewHolder {
        public TextView tx_news_simple_photos_title;//标题
        private ImageView img_news_simple_photos_01;//有图模式的第一张图
        public TextView tx_news_simple_photos_time;//有图模式的更新时间
        public TextView tx_news_simple_photos_author;//有图模式的新闻作者

        public NewsPhotoViewHolder(View itemView) {
            super(itemView);
            tx_news_simple_photos_title = (TextView) itemView.findViewById(R.id.tx_news_simple_photos_title);//标题
            img_news_simple_photos_01 = (ImageView) itemView.findViewById(R.id.tx_news_simple_photos_01);//有图模式的第一张图
            tx_news_simple_photos_time = (TextView) itemView.findViewById(R.id.tx_news_simple_photos_time);//有图模式的更新时间
            tx_news_simple_photos_author = (TextView) itemView.findViewById(R.id.tx_news_simple_photos_author);//有图模式的新闻作者
            if(tx_news_simple_photos_title==null||tx_news_simple_photos_time==null||tx_news_simple_photos_author==null){
                Log.e("xtx","null pointers");
            }
        }
    }

    /**
     * NewsPhotosViewHolder为无图模式
     */
    class NewsPhotosViewHolder extends RecyclerView.ViewHolder {
        public TextView tx_news_no_photos_title;//标题
        public TextView tx_news_no_photos_time;//无图模式的更新时间
        public TextView tx_news_no_photos_author;//无图模式的新闻作者

        public NewsPhotosViewHolder(View itemView) {
            super(itemView);
            tx_news_no_photos_title = (TextView) itemView.findViewById(R.id.tx_news_mul_photos_title);
            tx_news_no_photos_time = (TextView) itemView.findViewById(R.id.tx_news_mul_photos_time);
            tx_news_no_photos_author = (TextView) itemView.findViewById(R.id.tx_news_mul_photos_author);
            if(tx_news_no_photos_title==null||tx_news_no_photos_time==null||tx_news_no_photos_author==null){
                Log.e("xtx","null pointers2");
            }
        }
    }
}

