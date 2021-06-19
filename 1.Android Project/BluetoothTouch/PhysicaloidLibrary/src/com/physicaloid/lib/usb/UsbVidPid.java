/*
 * Copyright (C) 2013 Keisuke SUZUKI
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * Distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.physicaloid.lib.usb;

public class UsbVidPid {
    @SuppressWarnings("unused")
    private static final String TAG = UsbVidPid.class.getSimpleName();

    private int vid;
    private int pid;

    public UsbVidPid(int vid, int pid) {
        this.vid = vid;
        this.pid = pid;
    }

    public void setVid(int vid) {this.vid = vid;}
    public void setPid(int pid) {this.pid = pid;}
    public int getVid() {return this.vid;}
    public int getPid() {return this.pid;}
}
