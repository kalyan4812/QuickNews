package com.saikalyandaroju.appnews.Utils;

import android.app.Application;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
    }
    public static boolean isInternetAvailable() {
        InetAddress inetAddress = null;
        try {
            Future<InetAddress> future = Executors.newSingleThreadExecutor().submit(new Callable<InetAddress>() {
                @Override
                public InetAddress call() {
                    try {
                        return InetAddress.getByName("www.google.com");
                    } catch (UnknownHostException e) {
                        return null;
                    }
                }
            });
            inetAddress = future.get(2000, TimeUnit.MILLISECONDS);
            future.cancel(true);
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
        } catch (TimeoutException e) {
        }
        return inetAddress != null && !inetAddress.equals("");
    }
}
