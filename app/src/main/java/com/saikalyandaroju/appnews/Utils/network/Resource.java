package com.saikalyandaroju.appnews.Utils.network;

public abstract class Resource<T> {
    private T data;
    private String message;

    public Resource(T data, String message) {
        this.data = data;
        this.message = message;
    }

    public class Success<T> extends Resource<T> {

        public Success(T data, String message) {
            super(data, "");

        }
    }

    public class Error<T> extends Resource<T> {

        public Error(T data, String message) {
            super(data, message);
        }
    }

    public class Loading<T> extends Resource<T> {

        public Loading(T data, String message) {
            super(data, message);
        }
    }


}
