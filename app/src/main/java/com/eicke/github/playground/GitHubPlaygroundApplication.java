package com.eicke.github.playground;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;

import com.eicke.github.playground.dagger.AndroidComponent;
import com.eicke.github.playground.dagger.AppModule;
import com.eicke.github.playground.dagger.DaggerAndroidComponent;
import com.eicke.github.playground.dagger.DaggerNetComponent;
import com.eicke.github.playground.dagger.NetComponent;
import com.eicke.github.playground.dagger.NetModule;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import io.realm.Realm;
import io.realm.RealmConfiguration;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.GINGERBREAD;

public class GitHubPlaygroundApplication extends Application {

    public static final String GITHUB_API_URL = "https://api.github.com";

    private static GitHubPlaygroundApplication application;
    private NetComponent mNetComponent;
    private AndroidComponent mAndroidComponent;
    private RefWatcher refWatcher;

    public static RefWatcher getRefWatcher(Context context) {
        GitHubPlaygroundApplication application = (GitHubPlaygroundApplication) context.getApplicationContext();
        return application.refWatcher;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        refWatcher = LeakCanary.install(this);

        mNetComponent = DaggerNetComponent.builder()
                .appModule(new AppModule(this))
                .netModule(new NetModule(GITHUB_API_URL))
                .build();

        mAndroidComponent = DaggerAndroidComponent.builder()
                .appModule(new AppModule(this))
                .build();


        buildRealm();
        enabledStrictMode();
    }


    private void buildRealm() {
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }


    private void enabledStrictMode() {
        if (SDK_INT >= GINGERBREAD) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }
    }

    public NetComponent getNetComponent() {
        return mNetComponent;
    }

    public AndroidComponent getAndroidComponent() {
        return mAndroidComponent;
    }

    public static GitHubPlaygroundApplication getApplication() {
        return application;
    }
}
