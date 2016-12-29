package com.rpgobjects.runifyoucan;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

public class RunIfYouCan implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = "RunIfYouCan";
    private static final int PERMISSION_REQUEST = 1974;

    private Callback callback;
    //String permission
    private String[] permissions;

    public RunIfYouCan() {

    }

    public interface Callback {
        void onPermission(boolean grantedNow);
    }

    public void run(@NonNull Activity activity, String[] permissions, @StringRes int rationale, @NonNull RunIfYouCan.Callback callback) {
        this.callback = callback;
        String permission = permissions[0];
        this.permissions = permissions;
        Log.d(TAG, "Checking for " + permission + " permission");
        if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                rationaleDialog(activity,rationale);
            } else {
                Log.d(TAG, "Requesting for " + permission + " permission");
                ActivityCompat.requestPermissions(activity, permissions, PERMISSION_REQUEST);
            }
        } else {
            Log.d(TAG, "Already have " + permission + " permission");
            callback.onPermission(false);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        String permission = permissions[0];
        if (requestCode == PERMISSION_REQUEST) {
            if (grantResults.length >= 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, permission +  " permission has now been granted.");
                callback.onPermission(true);
            } else {
                Log.d(TAG, permission + " permission was NOT granted.");
                callback.onPermission(false);
            }
        }

    }

    private void rationaleDialog(@NonNull final Activity activity, @StringRes int rationale) {
        String permission = permissions[0];
        Log.d(TAG, "Showing rationale for " + permission + " permission");
        new MaterialDialog.Builder(activity).content(rationale).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                ActivityCompat.requestPermissions(activity, permissions, PERMISSION_REQUEST);
            }
        }).positiveText("OK").negativeText("Cancel").show();
    }

    public static boolean hasPermission(Context context, String permission) {
        return ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

}
