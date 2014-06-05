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

package com.mattprecious.notisync.message;

public class CustomMessage extends BaseMessage {
    public final int VERSION_CODE = 1;

    public String tag;
    public String packageName;
    public String appName;
    public String messageTitle;
    public String message;
    public String time;

    private CustomMessage(Builder builder) {
        super();

        tag = builder.tag;
        packageName = builder.packageName;
        appName = builder.appName;
        messageTitle = builder.messageTitle;
        message = builder.message;
        time = builder.time;
    }

    public static class Builder {
        private String tag;
        private String packageName;
        private String appName;
        private String messageTitle;
        private String message;
        private String time;

        public Builder tag(String tag) {
            this.tag = tag;
            return this;
        }

        public Builder appName(String appName) {
            this.appName = appName;
            return this;
        }

        public Builder messageTitle(String messageTitle) {
            this.messageTitle = messageTitle;
            return this;
        }
        
        public Builder message(String message) {
            this.message = message;
            return this;
        }
        
        public Builder time(String time) {
            this.time = time;
            return this;
        }
        
        public Builder packageName(String packageName) {
            this.packageName = packageName;
            return this;
        }
                
        public CustomMessage build() {
            return new CustomMessage(this);
        }
    }
}
