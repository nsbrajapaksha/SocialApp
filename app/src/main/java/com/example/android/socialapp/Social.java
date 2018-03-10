package com.example.android.socialapp;

/**
 * Created by nipuna on 3/7/2018.
 */

public class Social {
    private String mTitle, mDesc, mImage, mUsername;

    public Social() { //empty constructor needed for firebase

    }

    public Social(String title, String desc, String image, String username) {
        this.mTitle = title;
        this.mDesc = desc;
        this.mImage = image;
        this.mUsername = username;
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

    public String getUsername() {
        return mUsername;
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

    public void setmUsername(String username) {
        this.mUsername = username;
    }
}
