package com.runtime.permissions.runtimepermissions.presenters.ui;


import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.runtime.permissions.R;
import com.runtime.permissions.runtimepermissions.presenters.IPermissionsView;
import com.runtime.permissions.runtimepermissions.presenters.db.DbModule;
import com.runtime.permissions.runtimepermissions.presenters.db.entities.Permission;
import com.runtime.permissions.runtimepermissions.presenters.db.tables.PermissionTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PermissionDialog extends DialogFragment implements View.OnClickListener {

    public static final String FRAGMENT_ARG_PERMISSIONS = "PERMISSIONS";
    public static final String FRAGMENT_ARG_REQUEST_CODE = "REQUEST_CODE";

    private TextView message_textView;
    private CheckBox checkBox;
    private Button deny_button;
    private Button allow_button;
    private Callback callback;
    private ArrayList<Permission> permissions;
    private List<String> requestedPermissions = new ArrayList<>();
    private List<String> grantedPermissions = new ArrayList<>();
    private Permission currentPermission;
    private int requestCode;
    private DbModule dbModule;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_permission, container);
        permissions = (ArrayList<Permission>) getArguments().getSerializable(FRAGMENT_ARG_PERMISSIONS);
        requestCode = getArguments().getInt(FRAGMENT_ARG_REQUEST_CODE);
        for (Permission permission : permissions) {
            requestedPermissions.add(permission.permission);
        }
        message_textView = (TextView) view.findViewById(R.id.message_textView);
        deny_button = (Button) view.findViewById(R.id.deny_button);
        allow_button = (Button) view.findViewById(R.id.allow_button);
        checkBox = (CheckBox) view.findViewById(R.id.checkBox);
        checkBox.setOnClickListener(this);
        deny_button.setOnClickListener(this);
        allow_button.setOnClickListener(this);
        dbModule = new DbModule(getActivity().getApplicationContext());
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        onNextPermissions();
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.allow_button:
                grantedPermissions.add(currentPermission.permission);
                currentPermission.isGranted = PermissionTable.TRUE;
                break;
            case R.id.deny_button:
                currentPermission.isGranted = PermissionTable.FALSE;
                break;
            case R.id.checkBox:
                if (checkBox.isChecked()) {
                    currentPermission.isNeedToShowRequest = PermissionTable.FALSE;
                    allow_button.setEnabled(false);
                } else {
                    currentPermission.isNeedToShowRequest = PermissionTable.TRUE;
                    allow_button.setEnabled(true);
                }
                dbModule.addPermission(currentPermission);
                return;
        }
        onNextPermissions();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dbModule.reset();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void permissionsGrantResult(int requestCode, int permissionsGrantResult, List<String> requestedPermissions, java.util.List<String> grantedPermissions);
    }


    /* private methods */

    private void onNextPermissions() {
        if (permissions.isEmpty()) {
            dbModule.addPermission(currentPermission);
            setResult();
            return;
        }
        currentPermission = permissions.get(0);
        permissions.remove(0);
        message_textView.setText(currentPermission.permission);
    }

    private void setResult() {
        int permissionsGrantResult;
        if (requestedPermissions.size() == grantedPermissions.size()) {
            permissionsGrantResult = IPermissionsView.PERMISSIONS_GRANT_RESULT_ALLOW_ALL;
        } else if (grantedPermissions.isEmpty()) {
            permissionsGrantResult = IPermissionsView.PERMISSIONS_GRANT_RESULT_DENY_ALL;
        } else {
            permissionsGrantResult = IPermissionsView.PERMISSIONS_GRANT_RESULT_ALLOW_PARTIALLY;
        }
        callback.permissionsGrantResult(requestCode, permissionsGrantResult, requestedPermissions, grantedPermissions);
        dismiss();
    }
}
