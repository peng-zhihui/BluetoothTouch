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

/*
 * This code has built in knowledge of avrdude.
 * Thanks to avrdude coders
 *  Brian S. Dean, Joerg Wunsch, Eric Weddington, Jan-Hinnerk Reichert,
 *  Alex Shepherd, Martin Thomas, Theodore A. Roth, Michael Holzt
 *  Colin O'Flynn, Thomas Fischl, David Hoerl, Michal Ludvig,
 *  Darell Tan, Wolfgang Moser, Ville Voipio, Hannes Weisbach,
 *  Doug Springer, Brett Hagman
 *  and all contributers.
 */

package com.physicaloid.lib.programmer.avr;

import android.util.Log;

import com.physicaloid.BuildConfig;
import com.physicaloid.lib.Boards;
import com.physicaloid.lib.Physicaloid.UploadCallBack;
import com.physicaloid.lib.framework.SerialCommunicator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class AvrUploader {
    private static final String TAG = AvrUploader.class.getSimpleName();

    private static final boolean DEBUG_SHOW_HEXDUMP = false && BuildConfig.DEBUG;

    private UploadProtocol      mProg;
    private SerialCommunicator  mComm;
    private IntelHexFileToBuf   mIntelHex;
    private AvrConf             mAVRConf;
    private AVRMem              mAVRMem;

    public AvrUploader(SerialCommunicator serial) {
        mComm = serial;
    }

    public void setSerial(SerialCommunicator serial) {
        mComm = serial;
    }

    public boolean run(String filePath, Boards board, UploadCallBack callback) {
        if(filePath == null) {
            if(callback != null){ callback.onError(UploadErrors.FILE_OPEN); }
            return false;
        }

        File file = new File(filePath);
        if(!file.exists() || !file.isFile() || !file.canRead()) {
            if(callback != null){ callback.onError(UploadErrors.FILE_OPEN); }
            return false;
        }

        InputStream is;
        try {
            is = new FileInputStream(filePath);
        } catch(Exception e) {
            if(callback != null){ callback.onError(UploadErrors.FILE_OPEN); }
            return false;
        }
        return run(is, board, callback);
    }

    public boolean run(InputStream hexFile, Boards board, UploadCallBack callback) {
        if(board == null) {
            if(callback != null){ callback.onError(UploadErrors.AVR_CHIPTYPE); }
            return false;
        }

        if (board.uploadProtocol == Boards.UploadProtocols.STK500) {
            mProg = new Stk500();
        } else if(board.uploadProtocol == Boards.UploadProtocols.STK500V2) {
            mProg = new Stk500V2();
        } else {
            if(callback != null){ callback.onError(UploadErrors.AVR_CHIPTYPE); }
            return false;
        }

        mProg.setSerial(mComm);
        mProg.setCallback(callback);

        /////////////////////////////////////////////////////////////////
        // AVRタイプの定数セット
        /////////////////////////////////////////////////////////////////
        try {
            setConfig(board); // .hexを読む前に実行すること(AVRMemがnewされない)
        } catch(Exception e) {
            if(callback != null){ callback.onError(UploadErrors.AVR_CHIPTYPE); }
            return false;
        }

        /////////////////////////////////////////////////////////////////
        // ファイル読み込み
        /////////////////////////////////////////////////////////////////
        try {
            getFileToBuf(hexFile);
        } catch(Exception e) {
            mIntelHex = null;
            if(callback != null){ callback.onError(UploadErrors.HEX_FILE_OPEN); }
            return false;
        }

        /////////////////////////////////////////////////////////////////
        // 書込みスタート
        /////////////////////////////////////////////////////////////////
        mProg.setConfig(mAVRConf, mAVRMem);
        mProg.open();
        mProg.enable();
        int initOK = mProg.initialize();
        if(initOK < 0) {
            Log.e(TAG,"initialization failed ("+initOK+")");
            if(callback != null){ callback.onError(UploadErrors.CHIP_INIT); }
            return false;
        }
//        Log.v(TAG,"AVR device initialized and ready to accept instructions");

        int sigOK = mProg.check_sig_bytes();
        if( sigOK != 0) {
            Log.e(TAG,"check signature failed ("+sigOK+")");
            if(callback != null){ callback.onError(UploadErrors.SIGNATURE); }
            return false;
        }

        int writeOK = mProg.paged_write();
        if(writeOK == 0) { return false; } // canceled
        if(writeOK < 0) {
            Log.e(TAG,"paged write failed ("+initOK+")");
            if(callback != null){ callback.onError(UploadErrors.PAGE_WRITE); }
            return false;
        }
        mProg.disable();
        return true;
    }

    /**
     * Sets AVR configs
     * @param board
     * @throws InterruptedException
     */
    private void setConfig(Boards board) throws InterruptedException {
        mAVRConf = new AvrConf(board);
        mAVRMem = new AVRMem(mAVRConf);
    }

    /**
     * Converts a hex file to byte arrays
     * @param hexFile
     * @throws FileNotFoundException
     * @throws IOException
     * @throws Exception
     */
    private void getFileToBuf(InputStream hexFile) throws FileNotFoundException, IOException, Exception {
        mIntelHex = new IntelHexFileToBuf();
        mIntelHex.parse(hexFile);
        long byteLength = mIntelHex.getByteLength();
        mAVRMem.buf = new byte[(int)byteLength];
        mIntelHex.getHexData(mAVRMem.buf);
        mIntelHex = null;

        if(DEBUG_SHOW_HEXDUMP) {
            String str = "";
            for(int i=0; i<16; i++) {
                str += String.format("%02x ", mAVRMem.buf[i]);
            }
            Log.d(TAG, "Hex Dump [0:16]: "+str);

            str = "";
            for(int i=(int) (byteLength-16); i<byteLength; i++) {
                str += String.format("%02x ", mAVRMem.buf[i]);
            }
            Log.d(TAG, "Hex Dump ["+(byteLength-16)+":"+byteLength+"]: "+str);
        }
    }

}
