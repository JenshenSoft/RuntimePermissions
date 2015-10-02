package com.runtime.permissions.runtimepermissions.presenters.db;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;


import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.impl.DefaultStorIOSQLite;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.runtime.permissions.runtimepermissions.presenters.db.entities.Permission;
import com.runtime.permissions.runtimepermissions.presenters.db.entities.PermissionStorIOSQLiteDeleteResolver;
import com.runtime.permissions.runtimepermissions.presenters.db.entities.PermissionStorIOSQLiteGetResolver;
import com.runtime.permissions.runtimepermissions.presenters.db.entities.PermissionStorIOSQLitePutResolver;
import com.runtime.permissions.runtimepermissions.presenters.db.tables.PermissionTable;
import com.runtime.permissions.runtimepermissions.presenters.utils.PackageUtil;

import java.util.List;


public class DbModule {

    public final SQLiteOpenHelper sqLiteOpenHelper;
    public final StorIOSQLite storIOSQLite;

    public DbModule(@NonNull Context context) {
        sqLiteOpenHelper = provideSQLiteOpenHelper(context);
        storIOSQLite = provideStorIOSQLite();
    }

    private StorIOSQLite provideStorIOSQLite() {
        return DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(sqLiteOpenHelper)
                .addTypeMapping(Permission.class, SQLiteTypeMapping.<Permission>builder()
                        .putResolver(new PermissionStorIOSQLitePutResolver()) // object that knows how to perform Put Operation (insert or update)
                        .getResolver(new PermissionStorIOSQLiteGetResolver()) // object that knows how to perform Get Operation
                        .deleteResolver(new PermissionStorIOSQLiteDeleteResolver())  // object that knows how to perform Delete Operation
                        .build())
                .build();
    }

    public StorIOSQLite getStorIOSQLite() {
        return storIOSQLite;
    }

    public void reset() {
        sqLiteOpenHelper.close();
    }

    public void addPermissions(Context context) {
        List<Permission> permissions = getPermissionsByQuery(PermissionTable.QUERY_ALL);
        if (permissions.isEmpty()) {
            permissions = PackageUtil.getAllPermissions(context);
            storIOSQLite
                    .put()
                    .objects(permissions)
                    .prepare()
                    .executeAsBlocking();
        }
    }

    public List<Permission> getPermissionsByQuery(Query query) {
       return storIOSQLite
                        .get()
                        .listOfObjects(Permission.class)
                        .withQuery(query)
                        .prepare()
                        .executeAsBlocking();
    }

    private SQLiteOpenHelper provideSQLiteOpenHelper(@NonNull Context context) {
        return new DbOpenHelper(context);
    }
}
