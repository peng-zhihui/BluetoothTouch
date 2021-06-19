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

import android.content.Context;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.util.Log;
import android.util.SparseArray;

import com.physicaloid.BuildConfig;

public class UsbCdcConnection {
    private static final boolean DEBUG_SHOW = true && BuildConfig.DEBUG;

    private static final String TAG = UsbCdcConnection.class.getSimpleName();

    private UsbAccessor mUsbAccess;

    SparseArray<UsbCdcConnectionEp> mUsbConnectionEp;

    public UsbCdcConnection(Context context) {
        mUsbAccess = UsbAccessor.INSTANCE;
        mUsbAccess.init(context);
        mUsbConnectionEp = new SparseArray<UsbCdcConnection.UsbCdcConnectionEp>();
    }

    /**
     * Open first device with VID and PID
     * @param ids vid and pid
     * @return true : open successful, false : open fail
     */
    public boolean open(UsbVidPid ids) {
        return open(ids,false, 0);
    }

    /**
     * Open first CDC-ACM device with VID and PID
     * @param ids vid and pid
     * @param isCdcAcm true then search only cdc-acm
     * @return true : open successful, false : open fail
     */
    public boolean open(UsbVidPid ids, boolean isCdcAcm) {
        return open(ids, isCdcAcm, 0);
    }

    /**
     * Open channel-th device with VID and PID
     * @param ids vid and pid
     * @param ch channel
     * @return true : open successful, false : open fail
     */
    public boolean open(UsbVidPid ids, boolean isCdcAcm, int ch) {
        if(ids == null) return false;

        int devNum  = 0;
        int chNum   = 0;
        for(UsbDevice usbdev : mUsbAccess.manager().getDeviceList().values()) {
            if(usbdev.getVendorId() == ids.getVid()) {
                if(ids.getPid() == 0 || ids.getPid() == usbdev.getProductId()) {
                    for(int intfNum=0; intfNum < usbdev.getInterfaceCount(); intfNum++) {

                        if( (isCdcAcm && (usbdev.getInterface(intfNum).getInterfaceClass() == UsbConstants.USB_CLASS_CDC_DATA))
                                || !isCdcAcm) {
                            if(ch == chNum) {
                                if(!mUsbAccess.deviceIsConnected(devNum)) {
                                    if(mUsbAccess.openDevice(devNum,intfNum,ch)) {
                                        if(DEBUG_SHOW){ Log.d(TAG, "Find VID:"+Integer.toHexString(usbdev.getVendorId())+", PID:"+Integer.toHexString(usbdev.getProductId())+", DevNum:"+devNum+", IntfNum:"+intfNum); }
                                        mUsbConnectionEp.put(ch,new UsbCdcConnectionEp(mUsbAccess.connection(ch), getEndpoint(devNum, intfNum, UsbConstants.USB_DIR_IN), getEndpoint(devNum, intfNum, UsbConstants.USB_DIR_OUT)));
                                        return true;
                                    }
                                }
                                chNum++;
                            } // end of if
                        }// end of if
                    } // end of for
                } // end of if
            } // end of if
            devNum++;
        } //end of for
        if(DEBUG_SHOW){ Log.d(TAG, "Cannot find VID:"+ids.getVid()+", PID:"+ids.getPid()); }
        return false;
    }

    private UsbEndpoint getEndpoint(int devNum, int intfNum, int usbDir) {
        UsbInterface intf = mUsbAccess.intface(devNum, intfNum);
        if(intf == null) {return null;}

        for(int i=0; i<intf.getEndpointCount(); i++) {
            UsbEndpoint ep = mUsbAccess.endpoint(devNum, intfNum, i);
            if(ep==null) return null;
            if(ep.getDirection() == usbDir) {
                return ep;
            }
        }
        return null;
    }

    /**
     * Closes devices
     */
    public boolean close() {
        mUsbConnectionEp.clear();
        return mUsbAccess.closeAll();
    }

    /**
     * Gets UsbDeviceConnection for CDC
     * @return UsbDeviceConnection or null
     */
    public UsbDeviceConnection getConnection() {
        return getConnection(0);
    }

    /**
     * Gets UsbDeviceConnection for CDC
     * @param ch channel
     * @return UsbDeviceConnection or null
     */
    public UsbDeviceConnection getConnection(int ch) {
        UsbCdcConnectionEp con = mUsbConnectionEp.get(ch);
        if(con == null) return null;
        return con.connection;
    }
    /**
     * Gets IN UsbEndpoint for CDC
     * @return UsbEndpoint or null
     */
    public UsbEndpoint getEndpointIn() {
        return getEndpointIn(0);
    }

    /**
     * Gets IN UsbEndpoint for CDC
     * @param ch channel
     * @return UsbEndpoint or null
     */
    public UsbEndpoint getEndpointIn(int ch) {
        UsbCdcConnectionEp con = mUsbConnectionEp.get(ch);
        if(con == null) return null;
        return con.endpointIn;
    }
    /**
     * Gets OUT UsbEndpoint for CDC
     * @return UsbEndpoint or null
     */
    public UsbEndpoint getEndpointOut() {
        return getEndpointOut(0);
    }

    /**
     * Gets OUT UsbEndpoint for CDC
     * @param ch channel
     * @return UsbEndpoint or null
     */
    public UsbEndpoint getEndpointOut(int ch) {
        UsbCdcConnectionEp con = mUsbConnectionEp.get(ch);
        if(con == null) return null;
        return con.endpointOut;
    }

    class UsbCdcConnectionEp {
        public UsbDeviceConnection connection;
        public UsbEndpoint endpointIn;
        public UsbEndpoint endpointOut;
        public UsbCdcConnectionEp(UsbDeviceConnection connection, UsbEndpoint endpointIn, UsbEndpoint endpointOut) {
            this.connection = connection;
            this.endpointIn = endpointIn;
            this.endpointOut = endpointOut;
        }
    }
}
