package com.runtime.permissions.runtimepermissions.presenters;


import android.app.Activity;

import java.util.List;

public interface IPermissionsView {
    int PERMISSIONS_GRANT_RESULT_DENY_ALL = 0;
    int PERMISSIONS_GRANT_RESULT_ALLOW_PARTIALLY = 1;
    int PERMISSIONS_GRANT_RESULT_ALLOW_ALL = 2;

    void permissionsGrantResult(int requestCode, int permissionsGrantResult, List<String> requestedPermissions, List<String> grantedPermissions);

    void decideShouldRequestPermissions(int requestCode, String[] permissions, IPermissionRequestDecision decision);

    Activity getActivity();
}
