package com.runtime.permissions.runtimepermissions.presenters.db.tables;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.queries.Query;


public class PermissionTable {

    @NonNull
    public static final String TABLE = "tables";

    @NonNull
    public static final String COLUMN_ID = "_id";

    @NonNull
    public static final String COLUMN_PERMISSION_NAME = "permission_name";

    @NonNull
    public static final String COLUMN_STATE = "state";

    @NonNull
    public static final Query QUERY_ALL = Query.builder()
            .table(TABLE)
            .build();

    private PermissionTable() {
        throw new IllegalStateException("No instances please");
    }

    @NonNull
    public static String getCreateTableQuery() {
        return "CREATE TABLE " + TABLE + "("
                + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY, "
                + COLUMN_PERMISSION_NAME + " INTEGER NOT NULL, "
                + COLUMN_STATE + " INTEGER DEFAULT 0"
                + ");";
    }
}
