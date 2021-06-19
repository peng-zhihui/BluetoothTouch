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

import android.content.Context;
import android.util.Log;

import com.physicaloid.BuildConfig;
import com.physicaloid.lib.framework.AutoCommunicator;
import com.physicaloid.lib.framework.SerialCommunicator;
import com.physicaloid.lib.framework.Uploader;
import com.physicaloid.lib.programmer.avr.UploadErrors;
import com.physicaloid.lib.usb.driver.uart.ReadLisener;
import com.physicaloid.lib.usb.driver.uart.UartConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class Physicaloid {
    private static final boolean DEBUG_SHOW = true && BuildConfig.DEBUG;
    private static final String TAG = Physicaloid.class.getSimpleName();

    private Context mContext;
    private Boards mBoard;

    protected SerialCommunicator mSerial;
    private Uploader mUploader;
    private Thread mUploadThread;

    private UploadCallBack mCallBack;
    private InputStream mFileStream;

    private static final Object LOCK = new Object();
    protected static final Object LOCK_WRITE = new Object();
    protected static final Object LOCK_READ = new Object();

    public Physicaloid(Context context) {
        this.mContext = context;
    }

    /**
     * Opens a device and communicate USB UART by default settings
     * @return true : successful , false : fail
     * @throws RuntimeException
     */
    public boolean open() throws RuntimeException {
        return open(new UartConfig());
    }

    /**
     * Opens a device and communicate USB UART
     * @param uart UART configuration
     * @return true : successful , false : fail
     * @throws RuntimeException
     */
    public boolean open(UartConfig uart) throws RuntimeException {
        synchronized (LOCK) {
            if(mSerial == null) {
                mSerial = new AutoCommunicator().getSerialCommunicator(mContext);
                if(mSerial == null) return false;
            }
            if(mSerial.open()) {
                mSerial.setUartConfig(uart);
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Closes a device.
     * @return true : successful , false : fail
     * @throws RuntimeException
     */
    public boolean close() throws RuntimeException {
        synchronized (LOCK) {
            if(mSerial == null) return true;
            if(mSerial.close()) {
                mSerial = null;
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Reads from a device
     * @param buf
     * @return read byte size
     * @throws RuntimeException
     */
    public int read(byte[] buf) throws RuntimeException {
        if(mSerial == null) return 0;
        return read(buf, buf.length);
    }

    /**
     * Reads from a device
     * @param buf
     * @param size
     * @return read byte size
     * @throws RuntimeException
     */
    public int read(byte[] buf, int size) throws RuntimeException {
        synchronized (LOCK_READ) {
            if(mSerial == null) return 0;
            return mSerial.read(buf, size);
        }
    }

    /**
     * Adds read listener
     * @param listener ReadListener
     * @return true : successful , false : fail
     * @throws RuntimeException
     */
    public boolean addReadListener(ReadLisener listener) throws RuntimeException {
        synchronized (LOCK_READ) {
            if(mSerial == null) return false;
            if(listener == null) return false;
            mSerial.addReadListener(listener);
            return true;
        }
    }

    /**
     * Clears read listener
     * @throws RuntimeException
     */
    public void clearReadListener() throws RuntimeException {
        synchronized (LOCK_READ) {
            if(mSerial == null) return;
            mSerial.clearReadListener();
        }
    }

    /**
     * Writes to a device.
     * @param buf
     * @return written byte size
     * @throws RuntimeException
     */
    public int write(byte[] buf) throws RuntimeException {
        if(mSerial == null) return 0;
        return write(buf, buf.length);
    }

    /**
     * Writes to a device.
     * @param buf
     * @param size
     * @return written byte size
     * @throws RuntimeException
     */
    public int write(byte[] buf, int size) throws RuntimeException {
        synchronized (LOCK_WRITE){
            if(mSerial == null) return 0;
            return mSerial.write(buf, size);
        }
    }

    /**
     * Uploads a binary file to a device on background process. No need to open().
     * @param board board profile e.g. Boards.ARDUINO_UNO
     * @param filePath a binary file path e.g. /sdcard/arduino/Blink.hex
     * @throws RuntimeException
     */
    public void upload(Boards board, String filePath) throws RuntimeException {
        upload(board, filePath, null);
    }

    /**
     * Uploads a binary file to a device on background process. No need to open().
     * @param board board profile e.g. Boards.ARDUINO_UNO
     * @param filePath a binary file path e.g. /sdcard/arduino/Blink.uno.hex
     * @param callback
     * @throws RuntimeException
     */
    public void upload(Boards board, String filePath, UploadCallBack callback) throws RuntimeException {
        if(filePath == null) {
            if(callback != null){ callback.onError(UploadErrors.FILE_OPEN); }
            return;
        }

        File file = new File(filePath);
        if(!file.exists() || !file.isFile() || !file.canRead()) {
            if(callback != null){ callback.onError(UploadErrors.FILE_OPEN); }
            return;
        }

        InputStream is;
        try {
            is = new FileInputStream(filePath);
        } catch(Exception e) {
            if(callback != null){ callback.onError(UploadErrors.FILE_OPEN); }
            return;
        }
        upload(board, is, callback);
    }

    /**
     * Uploads a binary file to a device on background process. No need to open().
     * @param board board profile e.g. Boards.ARDUINO_UNO
     * @param fileStream a binary stream e.g. getResources().getAssets().open("Blink.uno.hex")
     * @throws RuntimeException
     */
    public void upload(Boards board, InputStream fileStream) throws RuntimeException {
        upload(board, fileStream, null);
    }

    boolean serialIsNull = false;
    /**
     * Uploads a binary file to a device on background process. No need to open().
     * @param board board profile e.g. Boards.ARDUINO_UNO
     * @param fileStream a binary stream e.g. getResources().getAssets().open("Blink.uno.hex")
     * @param callback
     * @throws RuntimeException
     */
    public void upload(Boards board, InputStream fileStream, UploadCallBack callback) throws RuntimeException {
        mUploader   = new Uploader();
        mCallBack   = callback;
        mFileStream = fileStream;
        mBoard      = board;

        if (mSerial == null) { // if not open
            if(DEBUG_SHOW) { Log.d(TAG, "upload : mSerial is null"); }
            mSerial = new AutoCommunicator()
            .getSerialCommunicator(mContext);   // need to run on non-thread
            serialIsNull = true;
        }

        mUploadThread = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (LOCK) {
                synchronized (LOCK_WRITE) {
                synchronized (LOCK_READ) {
                    UartConfig tmpUartConfig = new UartConfig();


                    if (mSerial == null) { // fail
                        if(DEBUG_SHOW) { Log.d(TAG, "upload : mSerial is null"); }
                        if (mCallBack != null) {
                            mCallBack.onError(UploadErrors.OPEN_DEVICE);
                        }
                        mBoard = null;
                        mFileStream = null;
                        mCallBack = null;
                        mUploader = null;
                        mSerial = null;
                        return;
                    }

                    if(!mSerial.isOpened()){
                        if(!mSerial.open()) {
                            if(DEBUG_SHOW) { Log.d(TAG, "upload : cannot mSerial.open"); }
                            if (mCallBack != null) { mCallBack.onError(UploadErrors.OPEN_DEVICE); }
                            mBoard = null;
                            mFileStream = null;
                            mCallBack = null;
                            mUploader = null;
                            mSerial = null;
                            return;
                        }
                        if(DEBUG_SHOW) { Log.d(TAG, "upload : open successful"); }
                    } else { // if already open
                        UartConfig origUartConfig = mSerial.getUartConfig();
                        tmpUartConfig.baudrate = origUartConfig.baudrate;
                        tmpUartConfig.dataBits = origUartConfig.dataBits;
                        tmpUartConfig.stopBits = origUartConfig.stopBits;
                        tmpUartConfig.parity = origUartConfig.parity;
                        tmpUartConfig.dtrOn = origUartConfig.dtrOn;
                        tmpUartConfig.rtsOn = origUartConfig.rtsOn;
                        if(DEBUG_SHOW) { Log.d(TAG, "upload : already open"); }
                    }

                    mSerial.stopReadListener();
                    mSerial.clearBuffer();

                    mUploader.upload(mFileStream, mBoard, mSerial, mCallBack);

                    mSerial.setUartConfig(tmpUartConfig); // recover if already
                                                          // open
                    mSerial.clearBuffer();
                    mSerial.startReadListener();
                    if (serialIsNull) {
                        mSerial.close();
                    }

                    mBoard = null;
                    mFileStream = null;
                    mCallBack = null;
                    mUploader = null;
                }}}
            }
        });

        mUploadThread.start();
    }

    public void cancelUpload() {
        if(mUploadThread == null){ return; }
        mUploadThread.interrupt();
    }

    /**
     * Callbacks of program process<br>
     * normal process:<br>
     *  onPreUpload() -> onUploading -> onPostUpload<br>
     * cancel:<br>
     *  onPreUpload() -> onUploading -> onCancel -> onPostUpload<br>
     * error:<br>
     *  onPreUpload   |<br>
     *  onUploading   | -> onError<br>
     *  onPostUpload  |<br>
     * @author keisuke
     *
     */
    public interface UploadCallBack{
        /*
         * Callback methods
         */
        void onPreUpload();
        void onUploading(int value);
        void onPostUpload(boolean success);
        void onCancel();
        void onError(UploadErrors err);
    }

    /**
     * Gets opened or closed status
     * @return true : opened, false : closed
     * @throws RuntimeException
     */
    public boolean isOpened() throws RuntimeException {
        synchronized (LOCK) {
            if(mSerial == null) return false;
            return mSerial.isOpened();
        }
    }

    /**
     * Sets Serial Configuration
     * @param settings
     */
    public void setConfig(UartConfig settings) throws RuntimeException{
        synchronized (LOCK) {
            if(mSerial == null) return;
            mSerial.setUartConfig(settings);
        }
    }

    /**
     * Sets Baud Rate
     * @param baudrate any baud-rate e.g. 9600
     * @return true : successful, false : fail
     */
    public boolean setBaudrate(int baudrate) throws RuntimeException{
        synchronized (LOCK) {
            if(mSerial == null) return false;
            return mSerial.setBaudrate(baudrate);
        }
    }

    /**
     * Sets Data Bits
     * @param dataBits data bits e.g. UartConfig.DATA_BITS8
     * @return true : successful, false : fail
     */
    public boolean setDataBits(int dataBits) throws RuntimeException{
        synchronized (LOCK) {
            if(mSerial == null) return false;
            return mSerial.setDataBits(dataBits);
        }
    }

    /**
     * Sets Parity Bits
     * @param parity parity bits e.g. UartConfig.PARITY_NONE
     * @return true : successful, false : fail
     */
    public boolean setParity(int parity) throws RuntimeException{
        synchronized (LOCK) {
            if(mSerial == null) return false;
            return mSerial.setParity(parity);
        }
    }

    /**
     * Sets Stop bits
     * @param stopBits stop bits e.g. UartConfig.STOP_BITS1
     * @return true : successful, false : fail
     */
    public boolean setStopBits(int stopBits) throws RuntimeException{
        synchronized (LOCK) {
            if(mSerial == null) return false;
            return mSerial.setStopBits(stopBits);
        }
    }

    /**
     * Sets flow control DTR/RTS
     * @param dtrOn true then DTR on
     * @param rtsOn true then RTS on
     * @return true : successful, false : fail
     */
    public boolean setDtrRts(boolean dtrOn, boolean rtsOn) throws RuntimeException{
        synchronized (LOCK) {
            if(mSerial == null) return false;
            return mSerial.setDtrRts(dtrOn, rtsOn);
        }
    }
}
