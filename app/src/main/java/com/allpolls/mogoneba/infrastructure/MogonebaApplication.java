package com.allpolls.mogoneba.infrastructure;

import android.app.Application;
import android.net.Uri;

import com.allpolls.mogoneba.services.Module;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.otto.Bus;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class MogonebaApplication extends Application {
    //public static final Uri API_ENDPOINT = Uri.parse("http://yora-playground.3dbuzz.com");
    public static final Uri API_ENDPOINT = Uri.parse("http://10.0.3.2:3000");
    public static final String TOKEN = "2de72d6a4f784a28837f0557486cc23a";

    private Auth auth;
    private Bus bus;
    private Picasso authedPicasso;

    public MogonebaApplication(){
        bus = new Bus();
    }

    @Override
    public void onCreate(){
        super.onCreate();
        auth = new Auth(this);
        createAuthedPicasso();
        Module.register(this);
    }

    private void createAuthedPicasso() {
        OkHttpClient client = new OkHttpClient();
        client.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer " + getAuth().getAuthToken())
                        .addHeader("X-Student", TOKEN)
                        .build();

                return chain.proceed(newRequest);
            }
        });

        authedPicasso = new Picasso.Builder(this)
                .downloader(new OkHttpDownloader(client))
                .build();
    }

    public Picasso getAuthedPicasso(){
        return authedPicasso;
    }

    public Auth getAuth(){
        return this.auth;
    }

    public Bus getBus(){
        return bus;
    }
}
