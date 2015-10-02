package com.runtime.permissions.runtimepermissions.presenters.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;

import com.runtime.permissions.runtimepermissions.presenters.db.entities.Permission;
import com.runtime.permissions.runtimepermissions.presenters.db.tables.PermissionTable;

import java.util.ArrayList;
import java.util.List;

public class PackageUtil {

    public static List<Permission> getAllPermissions(Context context) {
        List<Permission> permissions = new ArrayList<>();
        PackageManager pm = context.getPackageManager();

        List<PermissionGroupInfo> lstGroups = pm.getAllPermissionGroups(0);
        for (PermissionGroupInfo pgi : lstGroups) {
            try {
                List<PermissionInfo> lstPermissions = pm.queryPermissionsByGroup(pgi.name, 0);
                for (PermissionInfo pi : lstPermissions) {
                    permissions.add(Permission.newPermission(pi.name, PermissionTable.PERMISSION_DENIED, PermissionTable.PERMISSION_SHOULD_SHOW_REQUEST));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return permissions;
    }
}
