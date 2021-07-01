package com.saikalyandaroju.appnews.source.local.convertors;

import androidx.room.TypeConverter;

import com.saikalyandaroju.appnews.source.remote.models.Source;

public class Convertor {

    @TypeConverter
    public String fromSource(Source source) {
        return source.getName();
    }

    @TypeConverter
    public Source toSource(String name) {
        return new Source(name, name);
    }
}
