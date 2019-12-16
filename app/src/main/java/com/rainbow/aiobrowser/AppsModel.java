package com.rainbow.aiobrowser;

import org.json.JSONException;
import org.json.JSONObject;

public class AppsModel {
    private String name, imageUrl, targetUrl, appType;
    int id;

    public AppsModel(String name, String imageUrl, String targetUrl, String appType, int id) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.targetUrl = targetUrl;
        this.appType = appType;
        this.id = id;
    }

    public AppsModel(JSONObject jsonObject) {
        try {
            this.name = jsonObject.getString( "NAME" );
            this.imageUrl = jsonObject.getString( "IMAGE_URL" );
            this.targetUrl = jsonObject.getString( "TARGET_URL" );
            this.appType = jsonObject.getString( "TYPE" );
            this.id = jsonObject.getInt( "ID" );
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }
}
