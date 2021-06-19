package com.physicaloid.lib.fpga;

import java.io.IOException;
import java.io.InputStream;

import android.util.Log;

import com.physicaloid.BuildConfig;
import com.physicaloid.lib.framework.SerialCommunicator;

public class PhysicaloidFpgaConfigurator {

    private static final boolean DEBUG_SHOW = true && BuildConfig.DEBUG;
    private static final String TAG = PhysicaloidFpgaConfigurator.class.getSimpleName();

    private SerialCommunicator mSerial;
    private boolean mCanceled;

    private static final int READ_DELAY_MS          = 10;
    private static final int CONF_CHECK_RETRY       = 10;
    private static final int CONF_WRITE_PACKET_SIZE = 128;

    public PhysicaloidFpgaConfigurator(SerialCommunicator comm) {
        mSerial = comm;
        mCanceled = false;
    }

    public boolean configuration(InputStream is) {
        if(is == null) return false;
        byte[] rbuf = new byte[1];
        int retlen=0;
        boolean readStatus=true;

        //////////////////////////////////////////////////////
        // Switch user mode and check PS mode on
        //////////////////////////////////////////////////////
        if(DEBUG_SHOW){Log.d(TAG,"Configuration Step.1 : Switch user mode.");}

        for(int i=0; i<CONF_CHECK_RETRY; i++) {
            readStatus = true;
            commandSwitchUserMode();

            readDelay();

            retlen = mSerial.read(rbuf, rbuf.length);

            if(retlen == 0) {
                if(DEBUG_SHOW){Log.d(TAG,"Configuration Fail : No response on switching user mode.");}
                readStatus = false;
                continue;
            }

            if(DEBUG_SHOW){Log.d(TAG,"return value : 0x"+Integer.toHexString(rbuf[0]));}

            if(checkAsMode(rbuf[0])) {
                if(DEBUG_SHOW){Log.d(TAG,"Configuration Fail : It's not PS Mode.Please set the switch AS Mode to PS Mode");}
                readStatus = false;
                continue;
            }

            if(readStatus) break;
        }

        if(!readStatus) return false;

        //////////////////////////////////////////////////////
        // Switch config mode and check nSTATUS, CONF_DONE is Low
        //////////////////////////////////////////////////////
        if(DEBUG_SHOW){Log.d(TAG,"Configuration Step.2 : Switch config mode.");}
        for(int i=0; i<CONF_CHECK_RETRY; i++) {
            commandSwitchConfigMode();

            readDelay();

            retlen = mSerial.read(rbuf, rbuf.length);
            if(retlen == 0) {
                if(DEBUG_SHOW){Log.d(TAG,"Configuration Fail : No response on switching config mode.");}
                continue;
            }

            if(DEBUG_SHOW){Log.d(TAG,"return value : 0x"+Integer.toHexString(rbuf[0]));}

            if(!checkNstatus(rbuf[0]) && !checkConfDone(rbuf[0])) {
                break;
            }

            if(i==(CONF_CHECK_RETRY-1)) {
                if(DEBUG_SHOW){Log.d(TAG,"Configuration Fail : Check nSTATUS and CONF_DONE.Please retry.");}
                returnUserMode();
                return false;
            }
        }

        //////////////////////////////////////////////////////
        // Start config and check nSTATUS is High
        //////////////////////////////////////////////////////
        if(DEBUG_SHOW){Log.d(TAG,"Configuration Step.3 : Start config.");}
        for(int i=0; i<CONF_CHECK_RETRY; i++) {
            commandStartConfig();

            readDelay();

            retlen = mSerial.read(rbuf, rbuf.length);

            if(retlen == 0) {
                if(DEBUG_SHOW){Log.d(TAG,"Configuration Fail : No response on starting config.");}
                continue;
            }

            if(DEBUG_SHOW){Log.d(TAG,"return value : 0x"+Integer.toHexString(rbuf[0]));}

            if(checkNstatus(rbuf[0])) {
                break;
            }

            if(i==(CONF_CHECK_RETRY-1)) {
                if(DEBUG_SHOW){Log.d(TAG,"Configuration Fail : Check nSTATUS. Please retry.");}
                returnUserMode();
                return false;
            }
        }

        //////////////////////////////////////////////////////
        // Send RBF file
        //////////////////////////////////////////////////////
        if(DEBUG_SHOW){Log.d(TAG,"Configuration Step.4 : Send RBF file.");}
        int totalBytes=0;
        try {
            totalBytes = is.available();
        } catch (IOException e) {
            if(DEBUG_SHOW){Log.d(TAG,"Cannot get .rbf file's byte length.");}
            returnUserMode();
            return false;
        }

        byte[] confBuf = new byte[CONF_WRITE_PACKET_SIZE];

        int offset=0;
        int writeSize=0;
        int writtenSize=0;
        while(offset < totalBytes) {
            if(mCanceled) { return false; }
            if(DEBUG_SHOW){Log.d(TAG,"totalBytes : "+totalBytes+", writeSize : "+writeSize+", writtenSize : "+writtenSize+", offset : "+offset);}
            writeSize = totalBytes - offset;
            if(writeSize > CONF_WRITE_PACKET_SIZE) {
                writeSize = CONF_WRITE_PACKET_SIZE;
            }
            try {
                is.read(confBuf);
            } catch (IOException e) {
                if(DEBUG_SHOW){Log.d(TAG,"Cannot get .rbf data.");}
                returnUserMode();
                return false;
            }
            writtenSize = new PhysicaloidFpgaPacketFilter().writeWithEscape(mSerial, confBuf, writeSize);
            offset += writtenSize;
        }
        if(DEBUG_SHOW){Log.d(TAG,"totalBytes : "+totalBytes+", writeSize : "+writeSize+", writtenSize : "+writtenSize+", offset : "+offset);}

        drainReadBuf();

        //////////////////////////////////////////////////////
        // Check completion sending RBF file
        //////////////////////////////////////////////////////
        if(DEBUG_SHOW){Log.d(TAG,"Configuration Step.5 : Check completion sending RBF file.");}
        for(int i=0; i<CONF_CHECK_RETRY; i++) {
            commandStopConfig();

            readDelay();

            retlen = mSerial.read(rbuf, rbuf.length);

            if(retlen == 0) {
                if(DEBUG_SHOW){Log.d(TAG,"Configuration Fail : No response on configuration done.");}
                continue;
            }

            if( !(checkNstatus(rbuf[0]) && checkConfDone(rbuf[0])) ) {
                if(DEBUG_SHOW){Log.d(TAG,"Configuration Fail : Illegal response : 0x"+Integer.toHexString(rbuf[0]));}
                returnUserMode();
                return false;
            }

            break;
        }

        //////////////////////////////////////////////////////
        // Change User mode
        //////////////////////////////////////////////////////
        if(DEBUG_SHOW){Log.d(TAG,"Configuration Step.6 : Change User mode.");}
        returnUserMode();

        return true;
    }


    private void commandSwitchUserMode() {
        byte[] cbuf = new byte[2];
        cbuf[0]  = PhysicaloidFpgaConst.COMMAND_BYTE;
        cbuf[1]  = PhysicaloidFpgaConst.CMD_BASE;
        cbuf[1] |= PhysicaloidFpgaConst.CMD_NCONFIG;
        cbuf[1] |= PhysicaloidFpgaConst.CMD_USERMODE;

        mSerial.write(cbuf, 2);
    }

    private void commandSwitchConfigMode() {
        byte[] cbuf = new byte[2];
        cbuf[0]  = PhysicaloidFpgaConst.COMMAND_BYTE;
        cbuf[1]  = PhysicaloidFpgaConst.CMD_BASE;

        mSerial.write(cbuf, 2);
    }

    private void commandStartConfig() {
        byte[] cbuf = new byte[2];
        cbuf[0]  = PhysicaloidFpgaConst.COMMAND_BYTE;
        cbuf[1]  = PhysicaloidFpgaConst.CMD_BASE;
        cbuf[1] |= PhysicaloidFpgaConst.CMD_NCONFIG;

        mSerial.write(cbuf, 2);
    }

    private void commandStopConfig() {
        byte[] cbuf = new byte[2];
        cbuf[0]  = PhysicaloidFpgaConst.COMMAND_BYTE;
        cbuf[1]  = PhysicaloidFpgaConst.CMD_BASE;
        cbuf[1] |= PhysicaloidFpgaConst.CMD_NCONFIG;

        mSerial.write(cbuf, 2);
    }

    private void returnUserMode() {
        byte[] rbuf = new byte[1];
        commandSwitchUserMode();
        readDelay();
        mSerial.read(rbuf, rbuf.length); // throw a byte away
    }

    private boolean checkAsMode(byte ret) {
        if( (ret & PhysicaloidFpgaConst.CMD_RET_MSEL_AS) == PhysicaloidFpgaConst.CMD_RET_MSEL_AS) return true;
        return false;
    }

    private boolean checkNstatus(byte ret) {
        if( (ret & PhysicaloidFpgaConst.CMD_RET_NSTATUS) == PhysicaloidFpgaConst.CMD_RET_NSTATUS) return true;
        return false;
    }

    private boolean checkConfDone(byte ret) {
        if( (ret & PhysicaloidFpgaConst.CMD_RET_CONF_DONE) == PhysicaloidFpgaConst.CMD_RET_CONF_DONE) return true;
        return false;
    }

    @SuppressWarnings("unused")
    private boolean checkTimeout(byte ret) {
        if( (ret & PhysicaloidFpgaConst.CMD_RET_TIMEOUT) == PhysicaloidFpgaConst.CMD_RET_TIMEOUT) return true;
        return false;
    }

    private void drainReadBuf() {
        int retlen;
        byte[] tmpbuf = new byte[128];
        while(true) {
            readDelay();
            retlen = mSerial.read(tmpbuf, tmpbuf.length);
            if(retlen == 0) {
                break;
            }

            if(DEBUG_SHOW) {
                Log.d(TAG, "return value : "+toHexStr(tmpbuf, retlen));
            }

        }
    }

    private void readDelay() {
        try {
            Thread.sleep(READ_DELAY_MS);
        } catch (InterruptedException e) {
        }
    }

    private String toHexStr(byte[] b, int length) {
        String str="";
        for(int i=0; i<length; i++) {
            str += String.format("%02x ", b[i]);
        }
        return str;
    }

}
