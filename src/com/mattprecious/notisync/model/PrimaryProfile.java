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

package com.mattprecious.notisync.model;

import android.os.Parcel;
import android.os.Parcelable;

public class PrimaryProfile implements Parcelable {

    private int id;
    private String name;
    private String tag;
    private String packageName;
    private boolean enabled;

    public PrimaryProfile() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("id: ").append(id);
        buffer.append(", name: ").append(name);
        buffer.append(", tag: ").append(tag);
        buffer.append(", packageName: ").append(packageName);
        buffer.append(", enabled: ").append(enabled);

        buffer.insert(0, "PrimaryProfile {").append("}");

        return buffer.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(tag);
        dest.writeString(packageName);
        dest.writeByte((byte) (enabled ? 1 : 0));
    }

    public PrimaryProfile(Parcel in) {
        id = in.readInt();
        name = in.readString();
        tag = in.readString();
        packageName = in.readString();
        enabled = in.readByte() == 1;
    }

    public static final Parcelable.Creator<PrimaryProfile> CREATOR = new Parcelable.Creator<PrimaryProfile>() {

        @Override
        public PrimaryProfile createFromParcel(Parcel source) {
            return new PrimaryProfile(source);
        }

        @Override
        public PrimaryProfile[] newArray(int size) {
            return new PrimaryProfile[size];
        }

    };

}
