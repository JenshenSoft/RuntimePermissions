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
import com.runtime.permissions.runtimepermissions.presenters.db.DbModule;
import com.runtime.permissions.runtimepermissions.presenters.db.entities.Permission;
import com.runtime.permissions.runtimepermissions.presenters.db.tables.PermissionTable;

import java.util.ArrayList;
import java.util.ListIterator;


public class PermissionDialog extends DialogFragment implements View.OnClickListener {

    public static final String FRAGMENT_ARG_PERMISSIONS = "PERMISSIONS";
    public static final String FRAGMENT_ARG_REQUEST_CODE = "REQUEST_CODE";

    private TextView message_textView;
    private CheckBox checkBox;
    private Button deny_button;
    private Button allow_button;
    private Callback callback;
    private ArrayList<Permission> permissions;
    private Permission currentPermission;
    private int requestCode;
    private DbModule dbModule;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_permission, container);
        permissions = (ArrayList<Permission>) getArguments().getSerializable(FRAGMENT_ARG_PERMISSIONS);
        requestCode = getArguments().getInt(FRAGMENT_ARG_REQUEST_CODE);

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
                currentPermission.isGranted = PermissionTable.PERMISSION_GRANTED;
                break;
            case R.id.deny_button:
                currentPermission.isGranted = PermissionTable.PERMISSION_DENIED;
                break;
            case R.id.checkBox:
                if (checkBox.isChecked()) {
                    currentPermission.isNeedToShowRequest = PermissionTable.PERMISSION_NOT_SHOULD_SHOW_REQUEST;
                    allow_button.setEnabled(false);
                } else {
                    currentPermission.isNeedToShowRequest = PermissionTable.PERMISSION_SHOULD_SHOW_REQUEST;
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

    private void onNextPermissions() {
        if (!setNextItem()) {
            dbModule.addPermission(currentPermission);
            setResult();
            return;
        }
        message_textView.setText(currentPermission.permission);
    }


    /* private methods */

    private void setResult() {
        int size = permissions.size();
        String[] permissionsResult = new String[size];
        int[] permissionsGranted = new int[size];
        for (int i = 0; i < size; i++) {
            permissionsResult[i] = permissions.get(i).permission;
            permissionsGranted[i] = permissions.get(i).isGranted;
        }
        callback.onRequestPermissionsResult(requestCode, permissionsResult, permissionsGranted);
        dismiss();
    }

    public boolean setNextItem() {
        if (currentPermission == null) {
            currentPermission = permissions.get(0);
            return true;
        }
        ListIterator<Permission> listIterator = permissions.listIterator();
        while (listIterator.hasNext()) {
            Permission item = listIterator.next();
            if (item.equals(currentPermission)) {
                if (listIterator.hasNext()) {
                    currentPermission = listIterator.next();
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public interface Callback {
        void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);
    }
}
