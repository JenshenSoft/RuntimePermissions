/*
* Copyright 2015 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.runtime.permissions.runtimepermissions;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ViewAnimator;


import com.runtime.permissions.R;
import com.runtime.permissions.logger.Log;
import com.runtime.permissions.logger.LogFragment;
import com.runtime.permissions.logger.LogWrapper;
import com.runtime.permissions.logger.MessageOnlyLogFilter;
import com.runtime.permissions.runtimepermissions.camera.CameraPreviewFragment;
import com.runtime.permissions.runtimepermissions.contacts.ContactsFragment;
import com.runtime.permissions.runtimepermissions.presenters.IPermissionRequestDecision;
import com.runtime.permissions.runtimepermissions.presenters.IPermissionsPresenter;
import com.runtime.permissions.runtimepermissions.presenters.IPermissionsView;
import com.runtime.permissions.runtimepermissions.presenters.PermissionsPresenter;

import java.util.List;

/**
 * Launcher Activity that demonstrates the use of runtime permissions for Android M.
 * It contains a summary sample description, sample log and a Fragment that calls callbacks on this
 * Activity to illustrate parts of the runtime permissions API.
 * <p>
 * This Activity requests permissions to access the camera ({@link Manifest.permission#CAMERA})
 * when the 'Show Camera' button is clicked to display the camera preview.
 * Contacts permissions (({@link Manifest.permission#READ_CONTACTS} and ({@link
 * Manifest.permission#WRITE_CONTACTS})) are requested when the 'Show and Add Contacts'
 * button is
 * clicked to display the first contact in the contacts database and to add a dummy contact
 * directly to it. Permissions are verified and requested through compat helpers in the support v4
 * library, in this Activity using {@link ActivityCompat}.
 * First, permissions are checked if they have already been granted through {@link
 * ActivityCompat#checkSelfPermission(Context, String)}.
 * If permissions have not been granted, they are requested through
 * {@link ActivityCompat#requestPermissions(Activity, String[], int)} and the return value checked
 * in
 * a callback to the {@link android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback}
 * interface.
 * <p>
 * Before requesting permissions, {@link ActivityCompat#shouldShowRequestPermissionRationale(Activity,
 * String)}
 * should be called to provide the user with additional context for the use of permissions if they
 * have been denied previously.
 * <p>
 * If this sample is executed on a device running a platform version below M, all permissions
 * declared
 * in the Android manifest file are always granted at install time and cannot be requested at run
 * time.
 * <p>
 * This sample targets the M platform and must therefore request permissions at runtime. Change the
 * targetSdk in the file 'Application/build.gradle' to 22 to run the application in compatibility
 * mode.
 * Now, if a permission has been disable by the system through the application settings, disabled
 * APIs provide compatibility data.
 * For example the camera cannot be opened or an empty list of contacts is returned. No special
 * action is required in this case.
 * <p>
 * (This class is based on the MainActivity used in the SimpleFragment sample template.)
 */
public class MainActivity extends SampleActivityBase
        implements ActivityCompat.OnRequestPermissionsResultCallback, IPermissionsView {

    public static final String TAG = "MainActivity";

    /**
     * Id to identify a camera permission request.
     */
    private static final int REQUEST_CAMERA = 0;

    /**
     * Id to identify a contacts permission request.
     */
    private static final int REQUEST_CONTACTS = 1;


    private boolean mLogShown;

    /**
     * Root of the layout of this Activity.
     */
    private View mLayout;

    /**
     * Called when the 'show camera' button is clicked.
     * Callback is defined in resource layout definition.
     */
    private final IPermissionsPresenter presenter;
    public MainActivity() {
        presenter = new PermissionsPresenter(MainActivity.this , this);
    }
    public void showCamera(View view) {
        Log.i(TAG, "Show camera button pressed. Checking permission.");
        presenter.runActionUnderPermissionsNotForced(REQUEST_CAMERA, Manifest.permission.CAMERA);
    }

    /**
     * Called when the 'show camera' button is clicked.
     * Callback is defined in resource layout definition.
     */
    public void showContacts(View v) {
        Log.i(TAG, "Show contacts button pressed. Checking permissions.");
        presenter.runActionUnderPermissions(REQUEST_CONTACTS, false,
                Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS);
    }


    /**
     * Display the {@link CameraPreviewFragment} in the content area if the required Camera
     * permission has been granted.
     */
    private void showCameraPreview() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.sample_content_fragment, CameraPreviewFragment.newInstance())
                .addToBackStack("contacts")
                .commit();
    }

    /**
     * Display the {@link ContactsFragment} in the content area if the required contacts
     * permissions
     * have been granted.
     */
    private void showContactDetails() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.sample_content_fragment, ContactsFragment.newInstance())
                .addToBackStack("contacts")
                .commit();
    }


    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (presenter.onRequestPermissionsResult(requestCode, permissions, grantResults))
            return;

        /*
        if (requestCode == REQUEST_CAMERA) {
            // BEGIN_INCLUDE(permission_result)
            // Received permission result for camera permission.
            Log.i(TAG, "Received response for Camera permission request.");

            // Check if the only required permission has been granted
            if (presenter.verifyPermissions(grantResults)) {
                // Camera permission has been granted, preview can be displayed
                Log.i(TAG, "CAMERA permission has now been granted. Showing preview.");
                Snackbar.make(mLayout, R.string.permision_available_camera,
                        Snackbar.LENGTH_SHORT).show();
            } else {
                Log.i(TAG, "CAMERA permission was NOT granted.");
                Snackbar.make(mLayout, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT).show();

            }
            // END_INCLUDE(permission_result)

        } else if (requestCode == REQUEST_CONTACTS) {
            Log.i(TAG, "Received response for contact permissions request.");

            // We have requested multiple permissions for contacts, so all of them need to be
            // checked.
            if (presenter.verifyPermissions(grantResults)) {
                // All required permissions have been granted, display contacts fragment.
                Snackbar.make(mLayout, R.string.permision_available_contacts,
                        Snackbar.LENGTH_SHORT)
                        .show();
            } else {
                Log.i(TAG, "Contacts permissions were NOT granted.");
                Snackbar.make(mLayout, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT)
                        .show();
            }

        } else {*/
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        /*}*/
    }

    /* Note: Methods and definitions below are only used to provide the UI for this sample and are
    not relevant for the execution of the runtime permissions API. */


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem logToggle = menu.findItem(R.id.menu_toggle_log);
        logToggle.setVisible(findViewById(R.id.sample_output) instanceof ViewAnimator);
        logToggle.setTitle(mLogShown ? R.string.sample_hide_log : R.string.sample_show_log);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_toggle_log:
                mLogShown = !mLogShown;
                ViewAnimator output = (ViewAnimator) findViewById(R.id.sample_output);
                if (mLogShown) {
                    output.setDisplayedChild(1);
                } else {
                    output.setDisplayedChild(0);
                }
                supportInvalidateOptionsMenu();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Create a chain of targets that will receive log data
     */
    @Override
    public void initializeLogging() {
        // Wraps Android's native log framework.
        LogWrapper logWrapper = new LogWrapper();
        // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
        Log.setLogNode(logWrapper);

        // Filter strips out everything except the message text.
        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
        logWrapper.setNext(msgFilter);

        // On screen logging via a fragment with a TextView.
        LogFragment logFragment = (LogFragment) getSupportFragmentManager()
                .findFragmentById(R.id.log_fragment);
        msgFilter.setNext(logFragment.getLogView());
    }

    public void onBackClick(View view) {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLayout = findViewById(R.id.sample_main_layout);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            RuntimePermissionsFragment fragment = new RuntimePermissionsFragment();
            transaction.replace(R.id.sample_content_fragment, fragment);
            transaction.commit();
        }

        // This method sets up our custom logger, which will print all log messages to the device
        // screen, as well as to adb logcat.
        initializeLogging();
    }

    @Override
    public void permissionsGrantResult(int requestCode, int permissionsGrantResult, List<String> requestedPermissions, List<String> grantedPermissions) {
        /*switch (permissionsGrantResult) {
            case IPermissionsView.PERMISSIONS_ARE_ALREADY_GRANTED:
                onPermissionsAlreadyGranted(requestCode);
                break;
            default:
                showMessageAboutGrantedPermissions(requestCode, permissionsGrantResult);
        }*/
    }

    @Override
    public void decideShouldRequestPermissions(final int requestCode, final String[] permissions, final IPermissionRequestDecision decision) {
        int messageId;
        switch (requestCode) {
            case REQUEST_CAMERA:
                messageId = R.string.permission_camera_rationale;
                break;
            default:
                messageId = R.string.permission_contacts_rationale;
                break;
        }
        Snackbar.make(mLayout, messageId,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        decision.forcePermissionsRequest(requestCode, MainActivity.this, permissions);
                    }
                })
                .show();
    }

    public void onPermissionsAlreadyGranted(int requestCode) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                showCameraPreview();
                break;
            case REQUEST_CONTACTS:
                showContactDetails();
                break;
        }
    }

    public void showMessageAboutGrantedPermissions(int requestCode, int permissionsGrantResult) {
        int resId;
        switch (requestCode) {

            case REQUEST_CAMERA:
                Log.i(TAG, "Received response for Camera permission request.");
                // Check if the only required permission has been granted
                switch (permissionsGrantResult) {
                    case IPermissionsView.PERMISSIONS_GRANT_RESULT_ALLOW_ALL:
                        Log.i(TAG, "CAMERA permission has now been granted. Showing preview.");
                        resId = R.string.permision_available_camera;
                        break;
                    default:
                        Log.i(TAG, "CAMERA permission was NOT granted.");
                        resId = R.string.permissions_not_granted;
                }
                showMessage(resId, Snackbar.LENGTH_LONG);
                break;
            case REQUEST_CONTACTS:
                Log.i(TAG, "Received response for contact permissions request.");
                // We have requested multiple permissions for contacts, so all of them need to be
                // checked.
                Log.i(TAG, "Received response for Camera permission request.");
                // Check if the only required permission has been granted
                switch (permissionsGrantResult) {
                    case IPermissionsView.PERMISSIONS_GRANT_RESULT_ALLOW_ALL:
                        Log.i(TAG, "Contacts permissions has now been granted. Showing preview.");
                        resId = R.string.permision_available_contacts;
                        break;
                    default:
                        Log.i(TAG, "Contacts permissions were NOT granted.");
                        resId = R.string.permissions_not_granted;
                }
                showMessage(resId, Snackbar.LENGTH_LONG);
        }
    }

    private void showMessage(int messageResId, int length) {
        Snackbar.make(mLayout, messageResId, length)
                .show();
    }
}
