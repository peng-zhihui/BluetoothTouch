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

package com.physicaloid.lib;

public enum UsbVidList {
    ARDUINO                         (0x2341),
    FTDI                            (0x0403),
    MBED_LPC1768                    (0x0d28),
    MBED_LPC11U24                   (0x0d28),
    MBED_FRDM_KL25Z_OPENSDA_PORT    (0x1357),
    MBED_FRDM_KL25Z_KL25Z_PORT      (0x15a2),
    CP210X                          (0x10C4);

    int vid;
    private UsbVidList(int vid) {
        this.vid = vid;
    }

    public int getVid() {
        return vid;
    }
}
