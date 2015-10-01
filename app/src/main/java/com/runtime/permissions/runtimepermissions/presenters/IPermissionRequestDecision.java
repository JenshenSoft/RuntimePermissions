package com.runtime.permissions.runtimepermissions.presenters;


import android.app.Activity;

public interface IPermissionRequestDecision {
    void forcePermissionsRequest(int requestCode, Activity activity, String[] permissions);
}
