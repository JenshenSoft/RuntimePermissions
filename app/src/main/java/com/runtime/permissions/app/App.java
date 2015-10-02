package com.runtime.permissions.app;

import android.app.Application;
import android.os.Build;

import com.runtime.permissions.runtimepermissions.presenters.db.DbModule;

/**
 * Created by user on 02.10.15
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (!isAppropriateVersionCode()) {
            DbModule dbModule = new DbModule(getApplicationContext());
            dbModule.addPermissionsFromManifest(getApplicationContext());
            dbModule.reset();
        }
    }

    private boolean isAppropriateVersionCode() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;// Marshmallow+
    }
}
