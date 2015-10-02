package com.runtime.permissions.runtimepermissions.presenters;


import android.app.Activity;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v13.app.FragmentCompat;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PermissionsPresenter implements IPermissionsPresenter {

    private final IPermissionsHost permissionsHost;
    private final IPermissionsView view;

    private final int[] supportedRequestCodes;

    public PermissionsPresenter(Activity activity, IPermissionsView view) {
        this(new ActivityPermissionsHost(activity), view, null);
    }

    public PermissionsPresenter(Fragment fragment, IPermissionsView view) {
        this(new FragmentPermissionsHost(fragment), view, null);
    }

    public PermissionsPresenter(android.support.v4.app.Fragment fragment, IPermissionsView view) {
        this(new FragmentV4PermissionsHost(fragment), view, null);
    }

    public PermissionsPresenter(IPermissionsHost permissionsHost, IPermissionsView view, int[] supportedRequestCodes) {
        this.permissionsHost = permissionsHost;
        this.view = view;
        this.supportedRequestCodes = supportedRequestCodes;
    }

    @Override
    public void runActionUnderPermissions(int requestCode, boolean forced, @NonNull String... permissions) {
        if(!isRequestCodeSupported(requestCode))
            throw new RuntimeException("Request code does not supported");

        IPermissionsHost permissionsHost = getPermissionsHost();

        if (!isAppropriateVersionCode() || isPermissionsAlreadyAvailable(permissionsHost, permissions)) {
            view.permissionsGrantResult(requestCode, IPermissionsView.PERMISSIONS_GRANT_RESULT_ALLOW_ALL, Arrays.asList(permissions), Arrays.asList(permissions));
            return;
        }

        if (!forced && shouldShowRequestPermissionRationale(permissionsHost, permissions)) {
            view.decideShouldRequestPermissions(requestCode, permissions, permissionRequestDecision);
        } else {
            requestPermissions(permissionsHost, requestCode, permissions);
        }
    }

    @Override
    public void runActionUnderPermissionsNotForced(int requestCode, @NonNull String... permissions) {
        runActionUnderPermissions(requestCode, false, permissions);
    }

    
    /* private methods */

    @Override
    public boolean onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(!isRequestCodeSupported(requestCode))
            return false;

        List<String> requestedPermissions = new ArrayList<>(Arrays.asList(permissions));

        List<String> grantedPermissions = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            String permission = permissions[i];
            int grantResult = grantResults[i];

            if(grantResult == PackageManager.PERMISSION_GRANTED) {
                grantedPermissions.add(permission);
            }
        }

        int permissionsGrantResult;

        if(!grantedPermissions.isEmpty()) {
            if(grantedPermissions.size() < requestedPermissions.size()) {
                permissionsGrantResult = IPermissionsView.PERMISSIONS_GRANT_RESULT_ALLOW_PARTIALLY;
            }
            else {
                permissionsGrantResult = IPermissionsView.PERMISSIONS_GRANT_RESULT_ALLOW_ALL;
            }
        }
        else {
            permissionsGrantResult = IPermissionsView.PERMISSIONS_GRANT_RESULT_DENY_ALL;
        }

        view.permissionsGrantResult(requestCode, permissionsGrantResult, requestedPermissions, grantedPermissions);

        return true;
    }

    private boolean isAppropriateVersionCode() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    private void requestPermissions(IPermissionsHost permissionsHost, int requestCode, String... permissions) {
        permissionsHost.requestPermissions(permissions, requestCode);
    }

    private boolean isPermissionsAlreadyAvailable(IPermissionsHost permissionsHost, String... permissions) {
        // Marshmallow+
        boolean isPermissionsGranted = true;
        for (String permission : permissions) {
            isPermissionsGranted = isPermissionsGranted
                    && (permissionsHost.checkSelfPermission(permission)
                    == PackageManager.PERMISSION_GRANTED);
        }
        return isPermissionsGranted;
    }

    private boolean isRequestCodeSupported(int requestCode) {
        if(supportedRequestCodes == null)
            return true;

        for (int supportedRequestCode : supportedRequestCodes) {
            if(supportedRequestCode == requestCode)
                return true;
        }

        return false;
    }

    private IPermissionsHost getPermissionsHost() {
        return permissionsHost;
    }

    private boolean shouldShowRequestPermissionRationale(IPermissionsHost permissionsHost, String[] permissions) {
        for (String permission : permissions) {
            if(permissionsHost.shouldShowRequestPermissionRationale(permission))
                return true;
        }
        return false;
    }
    
    private final IPermissionsView.IPermissionRequestDecision permissionRequestDecision = new IPermissionsView.IPermissionRequestDecision() {
        @Override
        public void forcePermissionsRequest(int requestCode, Activity activity, String[] permissions) {
            runActionUnderPermissions(requestCode, true, permissions);
        }
    };


    /* inner types */

    private static class ActivityPermissionsHost implements IPermissionsHost {
        private final Activity activity;

        public ActivityPermissionsHost(Activity activity) {
            this.activity = activity;
        }

        @Override
        public int checkSelfPermission(@NonNull String permission) {
            return ActivityCompat.checkSelfPermission(activity, permission);
        }

        @Override
        public boolean shouldShowRequestPermissionRationale(@NonNull String permission) {
            return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
        }

        @Override
        public void requestPermissions(@NonNull String[] permissions, int requestCode) {
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
        }
    }

    private static class FragmentPermissionsHost implements IPermissionsHost {
        private final Fragment fragment;

        public FragmentPermissionsHost(Fragment fragment) {
            this.fragment = fragment;
        }

        @Override
        public int checkSelfPermission(@NonNull String permission) {
            return ActivityCompat.checkSelfPermission(fragment.getContext(), permission);
        }

        @Override
        public boolean shouldShowRequestPermissionRationale(@NonNull String permission) {
            return FragmentCompat.shouldShowRequestPermissionRationale(fragment, permission);
        }

        @Override
        public void requestPermissions(@NonNull String[] permissions, int requestCode) {
            FragmentCompat.requestPermissions(fragment, permissions, requestCode);
        }
    }

    private static class FragmentV4PermissionsHost implements IPermissionsHost {
        private final android.support.v4.app.Fragment fragment;

        public FragmentV4PermissionsHost(android.support.v4.app.Fragment fragment) {
            this.fragment = fragment;
        }

        @Override
        public int checkSelfPermission(@NonNull String permission) {
            return ActivityCompat.checkSelfPermission(fragment.getContext(), permission);
        }

        @Override
        public boolean shouldShowRequestPermissionRationale(@NonNull String permission) {
            return fragment.shouldShowRequestPermissionRationale(permission);
        }

        @Override
        public void requestPermissions(@NonNull String[] permissions, int requestCode) {
            fragment.requestPermissions(permissions, requestCode);
        }
    }

}
