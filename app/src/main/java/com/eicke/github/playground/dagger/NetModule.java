package com.eicke.github.playground.dagger;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import com.eicke.github.playground.data.GitHubService;
import com.eicke.github.playground.ui.MainActivity;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class NetModule {

    String mBaseUrl;

    public NetModule(String baseUrl) {
        this.mBaseUrl = baseUrl;
    }

    @Provides
    @Singleton
    Cache provideOkHttpCache(Application application) {
        int cacheSize = 50 * 1024 * 1024; // 50 MiB
        return new Cache(application.getCacheDir(), cacheSize);
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(Cache cache) {
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cache(cache)
                .build();
        return okHttpClient;
    }

    @Provides
    @Singleton
    Picasso providePicasso(OkHttpClient client, Application application) {
        final Picasso picasso = new Picasso.Builder(application)
                .downloader(new OkHttp3Downloader(client))
                .listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                        Log.e(MainActivity.TAG, String.format("Failed to load image: %s", uri));
                    }
                }).build();
        return picasso;
    }

    @Provides
    @Singleton
    Retrofit provideRetrofit(OkHttpClient client) {
        final Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mBaseUrl)
                .build();
        return retrofit;
    }

    @Provides
    @Singleton
    GitHubService provideGitHubService(Retrofit retrofit) {
        return retrofit.create(GitHubService.class);
    }
}
