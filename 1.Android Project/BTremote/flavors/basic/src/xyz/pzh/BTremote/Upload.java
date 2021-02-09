/*************************************************************************************
 * Copyright (C) 2012 Kristian Lauszus, TKJ Electronics. All rights reserved.
 *
 * This software may be distributed and modified under the terms of the GNU
 * General Public License version 2 (GPL2) as published by the Free Software
 * Foundation and appearing in the file GPL2.TXT included in the packaging of
 * this file. Please note that GPL2 Section 2[b] requires that all works based
 * on this software must also be made publicly available under the terms of
 * the GPL2 ("Copyleft").
 *
 * Contact information
 * -------------------
 *
 * Kristian Lauszus, TKJ Electronics
 * Web      :  http://www.tkjelectronics.com
 * e-mail   :  kristianl@pzh.com
 *
 ************************************************************************************/

package xyz.pzh.BTremote;

/**
 * This is just a dummy class used for the Basic flavor, so the code will compile.
 */
public class Upload {
    public static void close() {
        // Empty in this flavor
    }

    public static boolean uploadFirmware() {
        return false; // Always return false
    }

    public static boolean isUsbHostAvailable() {
        return false; // Always return false
    }
}