/*
 * Copyright 2012 Roman Nurik
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

package com.mattprecious.notisync.wizardpager.model;

import android.content.Context;

import com.mattprecious.notisync.R;

public class WizardModel extends AbstractWizardModel {

    public WizardModel(Context context) {
        super(context);
    }

    @Override
    protected PageList onNewRootPageList() {
        return new PageList(
                new WelcomePage(this, "Welcome"),
                new ModePage(this, "Mode", mContext)
                        .setPrimaryBranch(new DevicePage(this, "Devices"),
                                new AccessibilityPage(this, "Accessibility")),
                new CustomProfilesPage(this, "CustomProfiles")
                        .setLayout(R.layout.wizard_custom_profiles),
                new BluetoothBugPage(this, "BluetoothBug"),
                new LayoutPage(this, "Done")
                        .setLayout(R.layout.wizard_done));
    }
}
