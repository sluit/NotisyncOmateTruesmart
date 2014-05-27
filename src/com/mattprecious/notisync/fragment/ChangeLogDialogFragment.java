
package com.mattprecious.notisync.fragment;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Build;
import android.os.Bundle;

//import com.google.analytics.tracking.android.EasyTracker;
import com.mattprecious.notisync.preferences.SettingsActivity;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ChangeLogDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return SettingsActivity.buildChangeLogDialog(getActivity());
    }

    @Override
    public void onStart() {
        super.onStart();
        //EasyTracker.getInstance().setContext(getActivity());
        //EasyTracker.getTracker().sendView(getClass().getSimpleName());
    }
}
