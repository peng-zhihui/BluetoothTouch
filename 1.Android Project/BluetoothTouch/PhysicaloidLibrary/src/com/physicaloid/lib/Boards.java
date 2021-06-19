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

public enum Boards {
    // Arduino Series
    ARDUINO_UNO                 (1, "auno",ChipTypes.M328P,     UploadProtocols.STK500,  115200, ComProtocols.UART, "Arduino Uno"),
    ARDUINO_DUEMILANOVE_328     (1, "duem",ChipTypes.M328P,     UploadProtocols.STK500,   57600, ComProtocols.UART, "Arduino Duemilanove ATmega328"),
    ARDUINO_DUEMILANOVE_168     (1, "diec",ChipTypes.M168,      UploadProtocols.STK500,   19200, ComProtocols.UART, "Arduino Diecimila or Duemilanove ATmega168"),
    ARDUINO_NANO_328            (0, "na32",ChipTypes.M328P,     UploadProtocols.STK500,   57600, ComProtocols.UART, "Arduino Nano ATmega328"),
    ARDUINO_NANO_168            (0, "na16",ChipTypes.M168,      UploadProtocols.STK500,   57600, ComProtocols.UART, "Arduino Nano ATmega168"),
    ARDUINO_MEGA_2560_ADK       (1, "mg25",ChipTypes.M2560,     UploadProtocols.STK500V2,115200, ComProtocols.UART, "Arduino Mega 2560 or ADK"),
    ARDUINO_MEGA_1280           (0, "mg16",ChipTypes.M1280,     UploadProtocols.STK500,   57600, ComProtocols.UART, "Arduino Mega (ATmega1280)"),
//    ARDUINO_LEONARD             (0, "leon",ChipTypes.ATMEGA32U4,UploadProtocols.AVR109,   57600, ComProtocols.UART, "Arduino Leonardo"),
//    ARDUINO_ESPLORA             (0, "espl",ChipTypes.ATMEGA32U4,UploadProtocols.AVR109,   57600, ComProtocols.UART, "Arduino Esplora"),
//    ARDUINO_MICRO               (0, "micr",ChipTypes.ATMEGA32U4,UploadProtocols.AVR109,   57600, ComProtocols.UART, "Arduino Micro"),
    ARDUINO_MINI_328            (0, "mn32",ChipTypes.M328P,     UploadProtocols.STK500,   57600, ComProtocols.UART, "Arduino Mini ATmega328"),
    ARDUINO_MINI_168            (0, "mn16",ChipTypes.M168,      UploadProtocols.STK500,   57600, ComProtocols.UART, "Arduino Mini ATmega168"),
    ARDUINO_ETHERNET            (0, "ethe",ChipTypes.M328P,     UploadProtocols.STK500,  115200, ComProtocols.UART, "Arduino Ethernet"),
    ARDUINO_FIO                 (0, "afio",ChipTypes.M328P,     UploadProtocols.STK500,   57600, ComProtocols.UART, "Arduino Fio"),
    ARDUINO_BT_328              (0, "bt32",ChipTypes.M328P,     UploadProtocols.STK500,   19200, ComProtocols.UART, "Arduino BT ATmega328"),
    ARDUINO_BT_168              (0, "bt16",ChipTypes.M168,      UploadProtocols.STK500,   19200, ComProtocols.UART, "Arduino BT ATmega168"),
//    ARDUINO_LILYPAD_USB         (0, "lpus",ChipTypes.ATMEGA32U4,UploadProtocols.AVR109,   57600, ComProtocols.UART, "LilyPad Arduino USB"),
    ARDUINO_LILYPAD_328         (0, "lp32",ChipTypes.M328P,     UploadProtocols.STK500,   57600, ComProtocols.UART, "LilyPad Arduino ATmega328"),
    ARDUINO_LILYPAD_168         (0, "lp16",ChipTypes.M168,      UploadProtocols.STK500,   19200, ComProtocols.UART, "LilyPad Arduino ATmega168"),
    ARDUINO_PRO_5V_328          (1, "pm53",ChipTypes.M328P,     UploadProtocols.STK500,   57600, ComProtocols.UART, "Arduino Pro or Pro Mini (5V, 16MHz) ATmega328"),
    ARDUINO_PRO_5V_168          (1, "pm51",ChipTypes.M168,      UploadProtocols.STK500,   19200, ComProtocols.UART, "Arduino Pro or Pro Mini (5V, 16MHz) ATmega168"),
    ARDUINO_PRO_33V_328         (1, "pm33",ChipTypes.M328P,     UploadProtocols.STK500,   57600, ComProtocols.UART, "Arduino Pro or Pro Mini (3.3V, 8MHz) ATmega328"),
    ARDUINO_PRO_33V_168         (1, "pm31",ChipTypes.M168,      UploadProtocols.STK500,   19200, ComProtocols.UART, "Arduino Pro or Pro Mini (3.3V, 8MHz) ATmega168"),
    ARDUINO_NG_168              (0, "ng16",ChipTypes.M168,      UploadProtocols.STK500,   19200, ComProtocols.UART, "Arduino NG or older ATmega168"),
    ARDUINO_NG_8                (0, "ng08",ChipTypes.M8,        UploadProtocols.STK500,   19200, ComProtocols.UART, "Arduino NG or older ATmega8"),

    BALANDUINO                  (1, "bala",ChipTypes.M1284P,    UploadProtocols.STK500,  115200, ComProtocols.UART, "Balanduino"),
    POCKETDUINO                 (1, "podu",ChipTypes.M328P,     UploadProtocols.STK500,   57600, ComProtocols.UART, "PocketDuino"),

    // mbed Series
//    MBED_LPC1768                (0, "mbd1",ChipTypes.MBED_LPC1768,      UploadProtocols.USBMEM,   0, ComProtocols.UART, "mbed LPC1768"),
//    MBED_LPC11U24               (0, "mbd2",ChipTypes.MBED_LPC11U24,     UploadProtocols.USBMEM,   0, ComProtocols.UART, "mbed LPC11U24"),
//    MBED_FRDM_KL25Z             (0, "mbd3",ChipTypes.MBED_FRDM_KL25Z,   UploadProtocols.USBMEM,   0, ComProtocols.UART, "mbed FRDM-KL25Z"),

    // FPGA Series
    PERIDOT                     (1, "fp01",ChipTypes.PHYSICALOID_CYCLONE,  UploadProtocols.ALTERA_FPGA_RBF, 0, ComProtocols.USYNC_FIFO, "Physicaloid FPGA PERIDOT Board"),

    NONE                        (0, "",0,0,0,0,"");

    public final int    support;
    public final String idText;
    public final int    chipType;
    public final int    uploadProtocol;
    public final int    uploadBaudrate;
    public final int    comProtocol;
    public final String text;

    private Boards(int support, String idText, int chipType, int uploadProtocol, int uploadBaudrate, int comProtocol, String text) {
        this.support        = support;
        this.idText         = idText;
        this.chipType       = chipType;
        this.uploadProtocol = uploadProtocol;
        this.uploadBaudrate = uploadBaudrate;
        this.comProtocol    = comProtocol;
        this.text           = text;
    }

    public class ChipTypes {
        // chip number distance is 30
        // AVR
        public static final int M8                      = 1;
        public static final int M168                    = 2;
        public static final int M328P                   = 3;
        public static final int M1280                   = 4;
        public static final int M2560                   = 5;
        public static final int ATMEGA32U4              = 6;
        public static final int M1284P                  = 7;

        // mbed
        public static final int MBED_LPC1768            = 31;
        public static final int MBED_LPC11U24           = 32;
        public static final int MBED_FRDM_KL25Z         = 33;

        // FPGA
        public static final int PHYSICALOID_CYCLONE    = 61;
    }

    public class UploadProtocols {
        // protocol number distance is 30
        // AVR
        public static final int STK500                      = 1;    // Arduino Uno
        public static final int STK500V2                    = 2;    // Arduino Mega
        public static final int AVR109                      = 3;    // TODO: What protocol is AVR109 on avrdude

        // mbed
        public static final int USBMEM                      = 31;    // mbed

        // FPGA
        public static final int ALTERA_FPGA_RBF             = 61;    // Physicaloid RBF Configuration
    }

    public class ComProtocols {
        public static final int UART            = 1;
        public static final int I2C             = 2;
        public static final int SPI             = 3;
        public static final int USYNC_FIFO      = 4;
        public static final int SYNC_FIFO       = 5;
    }
}
