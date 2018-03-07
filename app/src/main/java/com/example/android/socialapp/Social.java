package com.example.android.socialapp;

/**
 * Created by nipuna on 3/7/2018.
 */

public class Social {
    private String mTitle, mDesc, mImage;

    public Social() {

    }

    public Social(String title, String desc, String image) {
        this.mTitle = title;
        this.mDesc = desc;
        this.mImage = image;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDesc() {
        return mDesc;
    }

    public String getImage() {
        return mImage;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public void setDesc(String desc) {
        this.mDesc = desc;
    }

    public void setImage(String image) {
        this.mImage = image;
    }
}
