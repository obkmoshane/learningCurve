package com.hair.fleek.model;

import java.io.Serializable;

/**
 * Created by Lincoln on 04/04/16.
 */
public class Image implements Serializable{
    private String name;
    private String small, medium, large;
    private String timestamp;
    private String modelname;

    public Image() {
    }

    public Image(String name, String small, String medium, String large, String timestamp,String modelname) {
        this.name = name;
        this.small = small;
        this.medium = medium;
        this.large = large;
        this.timestamp = timestamp;
        this.modelname= modelname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSmall() {
        return small;
    }

    public void setSmall(String small) {
        this.small = small;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public String getLarge() {
        return large;
    }

    public void setLarge(String large) {
        this.large = large;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getModelname() {return  modelname;}
    public void setModelname(String modelname){ this.modelname = modelname;}
}