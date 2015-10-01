package com.runtime.permissions.runtimepermissions.presenters.db;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;


import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.impl.DefaultStorIOSQLite;
import com.runtime.permissions.runtimepermissions.presenters.db.entities.Permission;
import com.runtime.permissions.runtimepermissions.presenters.db.entities.PermissionStorIOSQLiteDeleteResolver;
import com.runtime.permissions.runtimepermissions.presenters.db.entities.PermissionStorIOSQLiteGetResolver;
import com.runtime.permissions.runtimepermissions.presenters.db.entities.PermissionStorIOSQLitePutResolver;


public class DbModule {

    public StorIOSQLite provideStorIOSQLite(@NonNull SQLiteOpenHelper sqLiteOpenHelper) {
        return DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(sqLiteOpenHelper)
                .addTypeMapping(Permission.class, SQLiteTypeMapping.<Permission>builder()
                        .putResolver(new PermissionStorIOSQLitePutResolver()) // object that knows how to perform Put Operation (insert or update)
                        .getResolver(new PermissionStorIOSQLiteGetResolver()) // object that knows how to perform Get Operation
                        .deleteResolver(new PermissionStorIOSQLiteDeleteResolver())  // object that knows how to perform Delete Operation
                        .build())
                .build();
    }

    public SQLiteOpenHelper provideSQLiteOpenHelper(@NonNull Context context) {
        return new DbOpenHelper(context);
    }
}
