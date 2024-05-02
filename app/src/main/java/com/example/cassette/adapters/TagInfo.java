package com.example.cassette.adapters;

import com.example.cassette.MyClass.Collection;
import com.example.cassette.MyClass.Movie;

public class TagInfo {

    private static TagInfo instance;
    private Collection collection;

    private TagInfo() {

    }

    public static synchronized TagInfo getInstance() {
        if (instance == null) {
            instance = new TagInfo();
        }
        return instance;
    }

    public Collection getCollection() {
        return collection;
    }

    public void setCollection(Collection collection) {
        this.collection = collection;
    }
}
