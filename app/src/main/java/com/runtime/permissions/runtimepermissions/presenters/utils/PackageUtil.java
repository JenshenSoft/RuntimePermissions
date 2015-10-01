package com.runtime.permissions.runtimepermissions.presenters.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.util.Log;

import java.util.List;

/**
 * Created by user on 01.10.15.
 */
public class PackageUtil {

    public static List<String> getAllPermissions(Context context) {
        PackageManager pm = context.getPackageManager();
        CharSequence csPermissionGroupLabel;
        CharSequence csPermissionLabel;

        List<PermissionGroupInfo> lstGroups = pm.getAllPermissionGroups(0);
        for (PermissionGroupInfo pgi : lstGroups) {
            csPermissionGroupLabel = pgi.loadLabel(pm);
            Log.e("perm", pgi.name + ": " + csPermissionGroupLabel.toString());

            try {
                List<PermissionInfo> lstPermissions = pm.queryPermissionsByGroup(pgi.name, 0);
                for (PermissionInfo pi : lstPermissions) {
                    csPermissionLabel = pi.loadLabel(pm);
                    Log.e("perm", "   " + pi.name + ": " + csPermissionLabel.toString());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    return null;
    }

}
