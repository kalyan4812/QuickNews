package com.saikalyandaroju.appnews.source.remote.models;

import com.google.gson.internal.$Gson$Preconditions;

public class Source {
    private Object id;


    private String name;

    public Source(Object id, String name) {
        this.id = id;
        this.name = name;
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
