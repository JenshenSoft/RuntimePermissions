package com.runtime.permissions.runtimepermissions.presenters;

import android.support.annotation.NonNull;

public interface IPermissionsPresenter {
    void runActionUnderPermissions(int requestCode, boolean forced, @NonNull String... permissions);
    void runActionUnderPermissionsNotForced(int requestCode, @NonNull String... permissions);

    boolean onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);

    interface IPermissionsHost {
        int checkSelfPermission(@NonNull String permission);
        boolean shouldShowRequestPermissionRationale(@NonNull String permission);
        void requestPermissions(final @NonNull String[] permissions, final int requestCode);
    }
}
