package com.runtime.permissions.runtimepermissions.presenters.db.tables;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.queries.Query;


public class PermissionTable {

    public static final int TRUE = 1;
    public static final int FALSE = 0;

    @NonNull
    public static final String TABLE = "tables";

    @NonNull
    public static final String COLUMN_ID = "_id";

    @NonNull
    public static final String COLUMN_PERMISSION_NAME = "permission_name";

    @NonNull
    public static final String COLUMN_IS_GRANTED = "isGranted";

    @NonNull
    public static final String COLUMN_IS_NEED_TO_SHOW_REQUEST = "isNeedToShowRequest";

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
                + COLUMN_IS_GRANTED + " INTEGER DEFAULT " + FALSE + ", "
                + COLUMN_IS_NEED_TO_SHOW_REQUEST + " INTEGER DEFAULT " + FALSE
                + ");";
    }
}
