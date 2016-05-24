package com.chenls.orderserve.bean;

import cn.bmob.v3.BmobObject;

public class Notify extends BmobObject {
    String title, content;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {

        return title;
    }

    public String getContent() {
        return content;
    }
}
