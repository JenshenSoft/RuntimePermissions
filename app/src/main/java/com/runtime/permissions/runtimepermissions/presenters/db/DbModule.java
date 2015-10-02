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

    public void addPermissionsFromManifest(Context context) {
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

    public void addPermissions(List<Permission> permissions) {
        storIOSQLite
                .put()
                .objects(permissions)
                .prepare()
                .executeAsBlocking();
    }

    public void addPermission(Permission permission) {
        storIOSQLite
                .put()
                .object(permission)
                .prepare()
                .executeAsBlocking();
    }

    public List<Permission> getPermissionsByQuery(Query query) {
       return storIOSQLite
                        .get()
                        .listOfObjects(Permission.class)
                        .withQuery(query)
                        .prepare()
                        .executeAsBlocking();
    }

    public List<Permission> getPermissionsByArguments(String column, String ...args) {
        String where = column + " == ?";
        for (int i = 1; i < args.length; i++) {
            where += " OR " + column + " == ?";
        }
        return getPermissionsByQuery(Query.builder()
                .table(PermissionTable.TABLE)
                .where(where)
                .whereArgs(args)
                .build());
    }

    private SQLiteOpenHelper provideSQLiteOpenHelper(@NonNull Context context) {
        return new DbOpenHelper(context);
    }
}
