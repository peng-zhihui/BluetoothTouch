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

public enum UsbIds {
    FTDI        (Vid.FTDI,  0,  DriverType.FTDI),
    ARDUINO     (Vid.AVR,   0,  DriverType.CDCACM),
    MBED        (Vid.NXP,   0,  DriverType.CDCACM);

    int vid;
    int pid;
    int driverType;

    UsbIds(int vid, int pid, int driverType){
        this.vid        = vid;
        this.pid        = pid;
        this.driverType = driverType;
    }

    public class Vid {
        public static final int FTDI        = 0x0403;
        public static final int AVR         = 0x2341;
        public static final int NXP         = 0x0d28;
    }

    public class DriverType {
        public static final int FTDI    = 1;
        public static final int CDCACM  = 2;
    }

}
