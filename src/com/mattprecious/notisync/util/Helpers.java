/*
 * Copyright 2013 Matthew Precious
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mattprecious.notisync.util;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import com.mattprecious.notisync.R;

/**
 * Miscellaneous helpers that don't have a better home
 */
public class Helpers {
    private static final String TAG = "Helpers";

    public static boolean hasBluetoothIssue() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

    /**
     * From AOSP BluetoothPreference
     */
    public static int getBtClassDrawable(BluetoothDevice device) {
        BluetoothClass btClass = device.getBluetoothClass();
        if (btClass != null) {
            switch (btClass.getMajorDeviceClass()) {
                case BluetoothClass.Device.Major.COMPUTER:
                    return R.drawable.ic_bt_laptop;

                case BluetoothClass.Device.Major.PHONE:
                    return R.drawable.ic_bt_cellphone;

                case BluetoothClass.Device.Major.IMAGING:
                    // return R.drawable.ic_bt_imaging;
                    break;

                case BluetoothClass.Device.Major.AUDIO_VIDEO:
                    return R.drawable.ic_bt_headphones;

                default:
                    // unrecognized device class; continue
            }
        } else {
            MyLog.w(TAG, "btClass is null");
        }

        return 0;
    }

    public static void openSupportPage(Context context) {
        Uri supportUri = Uri.parse("https://notisync.uservoice.com");
        Intent intent = new Intent(Intent.ACTION_VIEW, supportUri);
        context.startActivity(intent);
    }
}
