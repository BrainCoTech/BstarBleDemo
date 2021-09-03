package tech.brainco.bstardemo;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;

import tech.brainco.bstarblesdk.core.BstarSDK;
import timber.log.Timber;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree() {
                @Override
                protected void log(int priority, String tag, @NonNull String message, Throwable t) {
                    if (priority >= Log.INFO) {
                        super.log(priority, tag, message, t);
                    }
                }
            });
        } else {
            Timber.plant();
        }
        BstarSDK.init(this, () -> {

        });
    }
}
