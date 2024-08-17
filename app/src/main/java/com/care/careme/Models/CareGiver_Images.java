package com.care.careme.Models;

public class CareGiver_Images {
    private String name;
    private String url;

    public CareGiver_Images() {
    }

    public CareGiver_Images(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
