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

package com.physicaloid.lib.framework;

import android.content.Context;
import android.hardware.usb.UsbDevice;

import com.physicaloid.lib.UsbVidList;
import com.physicaloid.lib.usb.UsbAccessor;
import com.physicaloid.lib.usb.driver.uart.UartCdcAcm;
import com.physicaloid.lib.usb.driver.uart.UartCp210x;
import com.physicaloid.lib.usb.driver.uart.UartFtdi;

public class AutoCommunicator {
    @SuppressWarnings("unused")
    private static final String TAG = AutoCommunicator.class.getSimpleName();

    public AutoCommunicator() {
    }

    public SerialCommunicator getSerialCommunicator(Context context) {
        UsbAccessor usbAccess = UsbAccessor.INSTANCE;
        usbAccess.init(context);

        for(UsbDevice device : usbAccess.manager().getDeviceList().values()) {
            int vid = device.getVendorId();
            for(UsbVidList usbVid : UsbVidList.values()) {
                if(vid == usbVid.getVid()) {
                    if(vid == UsbVidList.FTDI.getVid()) {
                        return new UartFtdi(context);
                    } else if(vid == UsbVidList.CP210X.getVid()) {
                        return new UartCp210x(context);
                    } else {
                        return new UartCdcAcm(context);
                    }
                }
            }
        }

        return null;
    }
}
