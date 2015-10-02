package com.runtime.permissions.app;

import android.app.Application;

import com.runtime.permissions.runtimepermissions.presenters.db.DbModule;
import com.runtime.permissions.runtimepermissions.presenters.db.entities.Permission;
import com.runtime.permissions.runtimepermissions.presenters.utils.PackageUtil;

import java.util.List;

/**
 * Created by user on 02.10.15
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DbModule dbModule = new DbModule(getApplicationContext());
        dbModule.addPermissions(getApplicationContext());
        dbModule.reset();
    }
}
