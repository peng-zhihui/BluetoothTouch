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

package com.physicaloid.lib.usb.driver.uart;

public class UartConfig {
    public static final int DATA_BITS7          = 7;
    public static final int DATA_BITS8          = 8;

    public static final int STOP_BITS1          = 0;
    public static final int STOP_BITS1_5        = 1;
    public static final int STOP_BITS2          = 2;

    public static final int PARITY_NONE         = 0;
    public static final int PARITY_ODD          = 1;
    public static final int PARITY_EVEN         = 2;
    public static final int PARITY_MARK         = 3;
    public static final int PARITY_SPACE        = 4;

    public static final int FLOW_CONTROL_OFF    = 0;
    public static final int FLOW_CONTROL_ON     = 1;

    public int baudrate;
    public int dataBits;
    public int stopBits;
    public int parity;
    public boolean rtsOn;
    public boolean dtrOn;

    public UartConfig() {
        this.baudrate       = 9600;
        this.dataBits       = DATA_BITS8;
        this.stopBits       = STOP_BITS1;
        this.parity         = PARITY_NONE;
        this.dtrOn          = false;
        this.rtsOn          = false;
    }

    public UartConfig(int baudrate, int dataBits, int stopBits, int parity, boolean dtrOn, boolean rtsOn) {
        this.baudrate       = baudrate;
        this.dataBits       = dataBits;
        this.stopBits       = stopBits;
        this.parity         = parity;
        this.dtrOn          = dtrOn;
        this.rtsOn          = rtsOn;
    }
}
