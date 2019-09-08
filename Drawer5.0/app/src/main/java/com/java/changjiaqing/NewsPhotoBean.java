package com.java.changjiaqing;
/***
 * 新闻对象，包括来源，时间。标题正文等等
 */

import java.io.Serializable;
import java.util.ArrayList;

public class NewsPhotoBean implements Serializable {
    public  ArrayList<String> list;//封面图片的集合
    public  String video;//视频url
    public  ArrayList<String> key_words;//关键词列表
    public  ArrayList<String> persons_list;//相关人物列表
    public  ArrayList<String> locations_list;//相关地点列表
    private int type;//排版类型
    private String f_time;//发布时间
    private String author;//作者
    private String id;//新闻ID
    private String title;//新闻标题
    public  String content;//新闻正文
    public  String category;//新闻类型
    public  int haveBeenRead = 0;
    public  int haveBeenCollected = 0;

    public String getId() {return id;};

    public void setId(String id) {this.id = id;}

    public ArrayList<String> getList() {
        return list;
    }
    public void setList(ArrayList<String> list) {
        this.list = list;
    }

    public String getVideo() {
        return video;
    }
    public void setVideo(String video_list) {
        this.video = video_list;
    }

    public ArrayList<String> getKey_words() {
        return key_words;
    }
    public void setKey_words(ArrayList<String> key_words) {
        this.key_words = key_words;
    }

    public ArrayList<String> getPersons_list() {
        return persons_list;
    }
    public void setPersons_list(ArrayList<String> persons_list) {
        this.persons_list = persons_list;
    }

    public ArrayList<String> getLocations_list() {
        return locations_list;
    }
    public void setLocations_list(ArrayList<String> locations_list) {
        this.locations_list = locations_list;
    }

    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getF_time() {
        return f_time;
    }
    public void setF_time(String f_time) {
        this.f_time = f_time;
    }

    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
}
