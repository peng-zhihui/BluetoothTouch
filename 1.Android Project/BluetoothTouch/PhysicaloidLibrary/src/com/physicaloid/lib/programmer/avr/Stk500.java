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
import com.physicaloid.lib.framework.SerialCommunicator;

import java.util.Arrays;

class ProgrammerType{
    
    /*
typedef struct programmer_t {
  LISTID id;
  char desc[PGM_DESCLEN];
  char type[PGM_TYPELEN];
  char port[PGM_PORTLEN];
  unsigned int pinno[N_PINS];
  exit_vcc_t exit_vcc;
  exit_reset_t exit_reset;
  exit_datahigh_t exit_datahigh;
  int ppidata;
  int ppictrl;
  int baudrate;
  int usbvid, usbpid;
  char usbdev[PGM_USBSTRINGLEN], usbsn[PGM_USBSTRINGLEN];
  char usbvendor[PGM_USBSTRINGLEN], usbproduct[PGM_USBSTRINGLEN];
  double bitclock;    // JTAG ICE clock period in microseconds
  int ispdelay;    // ISP clock delay
  union filedescriptor fd;
  int  page_size;  // page size if the programmer supports paged write/load
  int  (*rdy_led)        (struct programmer_t * pgm, int value);
  int  (*err_led)        (struct programmer_t * pgm, int value);
  int  (*pgm_led)        (struct programmer_t * pgm, int value);
  int  (*vfy_led)        (struct programmer_t * pgm, int value);
  int  (*initialize)     (struct programmer_t * pgm, AVRPART * p);
  void (*display)        (struct programmer_t * pgm, const char * p);
  void (*enable)         (struct programmer_t * pgm);
  void (*disable)        (struct programmer_t * pgm);
  void (*powerup)        (struct programmer_t * pgm);
  void (*powerdown)      (struct programmer_t * pgm);
  int  (*program_enable) (struct programmer_t * pgm, AVRPART * p);
  int  (*chip_erase)     (struct programmer_t * pgm, AVRPART * p);
  int  (*cmd)            (struct programmer_t * pgm, unsigned char cmd[4],
                          unsigned char res[4]);
  int  (*cmd_tpi)        (struct programmer_t * pgm, unsigned char cmd[],
                          int cmd_len, unsigned char res[], int res_len);
  int  (*spi)            (struct programmer_t * pgm, unsigned char cmd[],
                          unsigned char res[], int count);
  int  (*open)           (struct programmer_t * pgm, char * port);
  void (*close)          (struct programmer_t * pgm);
  int  (*paged_write)    (struct programmer_t * pgm, AVRPART * p, AVRMEM * m,
                          int page_size, int n_bytes);
  int  (*paged_load)     (struct programmer_t * pgm, AVRPART * p, AVRMEM * m,
                          int page_size, int n_bytes);
  void (*write_setup)    (struct programmer_t * pgm, AVRPART * p, AVRMEM * m);
  int  (*write_byte)     (struct programmer_t * pgm, AVRPART * p, AVRMEM * m,
                          unsigned long addr, unsigned char value);
  int  (*read_byte)      (struct programmer_t * pgm, AVRPART * p, AVRMEM * m,
                          unsigned long addr, unsigned char * value);
  int  (*read_sig_bytes) (struct programmer_t * pgm, AVRPART * p, AVRMEM * m);
  void (*print_parms)    (struct programmer_t * pgm);
  int  (*set_vtarget)    (struct programmer_t * pgm, double v);
  int  (*set_varef)      (struct programmer_t * pgm, unsigned int chan, double v);
  int  (*set_fosc)       (struct programmer_t * pgm, double v);
  int  (*set_sck_period) (struct programmer_t * pgm, double v);
  int  (*setpin)         (struct programmer_t * pgm, int pin, int value);
  int  (*getpin)         (struct programmer_t * pgm, int pin);
  int  (*highpulsepin)   (struct programmer_t * pgm, int pin);
  int  (*parseexitspecs) (struct programmer_t * pgm, char *s);
  int  (*perform_osccal) (struct programmer_t * pgm);
  int  (*parseextparams) (struct programmer_t * pgm, LISTID xparams);
  void (*setup)          (struct programmer_t * pgm);
  void (*teardown)       (struct programmer_t * pgm);
  char config_file[PATH_MAX]; // config file where defined
  int  lineno;                // config file line number
  void *cookie;               // for private use by the programmer
  char flag;                  // for private use of the programmer
} PROGRAMMER;
     */
}

class STK500Const{
 // *****************[ STK Message constants ]***************************

    public static final String STK_SIGN_ON_MESSAGE      = "AVR STK";   // Sign on string for Cmnd_STK_GET_SIGN_ON

    // *****************[ STK Response constants ]***************************

    public static final byte Resp_STK_OK                = (byte)0x10;  // ' '
    public static final byte Resp_STK_FAILED            = (byte)0x11;  // ' '
    public static final byte Resp_STK_UNKNOWN           = (byte)0x12;  // ' '
    public static final byte Resp_STK_NODEVICE          = (byte)0x13;  // ' '
    public static final byte Resp_STK_INSYNC            = (byte)0x14;  // ' '
    public static final byte Resp_STK_NOSYNC            = (byte)0x15;  // ' '

    public static final byte Resp_ADC_CHANNEL_ERROR     = (byte)0x16;  // ' '
    public static final byte Resp_ADC_MEASURE_OK        = (byte)0x17;  // ' '
    public static final byte Resp_PWM_CHANNEL_ERROR     = (byte)0x18;  // ' '
    public static final byte Resp_PWM_ADJUST_OK         = (byte)0x19;  // ' '

    // *****************[ STK Special constants ]***************************

    public static final byte Sync_CRC_EOP               = (byte)0x20;  // 'SPACE'

    // *****************[ STK Command constants ]***************************

    public static final byte Cmnd_STK_GET_SYNC          = (byte)0x30;  // ' '
    public static final byte Cmnd_STK_GET_SIGN_ON       = (byte)0x31;  // ' '

    public static final byte Cmnd_STK_SET_PARAMETER     = (byte)0x40;  // ' '
    public static final byte Cmnd_STK_GET_PARAMETER     = (byte)0x41;  // ' '
    public static final byte Cmnd_STK_SET_DEVICE        = (byte)0x42;  // ' '
    public static final byte Cmnd_STK_SET_DEVICE_EXT    = (byte)0x45;  // ' '                 

    public static final byte Cmnd_STK_ENTER_PROGMODE    = (byte)0x50;  // ' '
    public static final byte Cmnd_STK_LEAVE_PROGMODE    = (byte)0x51;  // ' '
    public static final byte Cmnd_STK_CHIP_ERASE        = (byte)0x52;  // ' '
    public static final byte Cmnd_STK_CHECK_AUTOINC     = (byte)0x53;  // ' '
    public static final byte Cmnd_STK_LOAD_ADDRESS      = (byte)0x55;  // ' '
    public static final byte Cmnd_STK_UNIVERSAL         = (byte)0x56;  // ' '
    public static final byte Cmnd_STK_UNIVERSAL_MULTI   = (byte)0x57;  // ' '

    public static final byte Cmnd_STK_PROG_FLASH        = (byte)0x60;  // ' '
    public static final byte Cmnd_STK_PROG_DATA         = (byte)0x61;  // ' '
    public static final byte Cmnd_STK_PROG_FUSE         = (byte)0x62;  // ' '
    public static final byte Cmnd_STK_PROG_LOCK         = (byte)0x63;  // ' '
    public static final byte Cmnd_STK_PROG_PAGE         = (byte)0x64;  // ' '
    public static final byte Cmnd_STK_PROG_FUSE_EXT     = (byte)0x65;  // ' '

    public static final byte Cmnd_STK_READ_FLASH        = (byte)0x70;  // ' '
    public static final byte Cmnd_STK_READ_DATA         = (byte)0x71;  // ' '
    public static final byte Cmnd_STK_READ_FUSE         = (byte)0x72;  // ' '
    public static final byte Cmnd_STK_READ_LOCK         = (byte)0x73;  // ' '
    public static final byte Cmnd_STK_READ_PAGE         = (byte)0x74;  // ' '
    public static final byte Cmnd_STK_READ_SIGN         = (byte)0x75;  // ' '
    public static final byte Cmnd_STK_READ_OSCCAL       = (byte)0x76;  // ' '
    public static final byte Cmnd_STK_READ_FUSE_EXT     = (byte)0x77;  // ' '
    public static final byte Cmnd_STK_READ_OSCCAL_EXT   = (byte)0x78;  //

    // *****************[ STK Parameter constants ]***************************

    public static final byte Parm_STK_HW_VER                = (byte)0x80;  // ' ' - R
    public static final byte Parm_STK_SW_MAJOR              = (byte)0x81;  // ' ' - R
    public static final byte Parm_STK_SW_MINOR              = (byte)0x82;  // ' ' - R
    public static final byte Parm_STK_LEDS                  = (byte)0x83;  // ' ' - R/W
    public static final byte Parm_STK_VTARGET               = (byte)0x84;  // ' ' - R/W
    public static final byte Parm_STK_VADJUST               = (byte)0x85;  // ' ' - R/W
    public static final byte Parm_STK_OSC_PSCALE            = (byte)0x86;  // ' ' - R/W
    public static final byte Parm_STK_OSC_CMATCH            = (byte)0x87;  // ' ' - R/W
    public static final byte Parm_STK_RESET_DURATION        = (byte)0x88;  // ' ' - R/W
    public static final byte Parm_STK_SCK_DURATION          = (byte)0x89;  // ' ' - R/W

    public static final byte Parm_STK_BUFSIZEL              = (byte)0x90;  // ' ' - R/W, Range {0..255}
    public static final byte Parm_STK_BUFSIZEH              = (byte)0x91;  // ' ' - R/W, Range {0..255}
    public static final byte Parm_STK_DEVICE                = (byte)0x92;  // ' ' - R/W, Range {0..255}
    public static final byte Parm_STK_PROGMODE              = (byte)0x93;  // ' ' - 'P' or 'S'
    public static final byte Parm_STK_PARAMODE              = (byte)0x94;  // ' ' - TRUE or FALSE
    public static final byte Parm_STK_POLLING               = (byte)0x95;  // ' ' - TRUE or FALSE
    public static final byte Parm_STK_SELFTIMED             = (byte)0x96;  // ' ' - TRUE or FALSE
    public static final byte Param_STK500_TOPCARD_DETECT    = (byte)0x98;  // ' ' - Detect top-card attached

    // *****************[ STK status bit definitions ]***************************

    public static final byte Stat_STK_INSYNC            = (byte)0x01;  // INSYNC status bit, '1' - INSYNC
    public static final byte Stat_STK_PROGMODE          = (byte)0x02;  // Programming mode,  '1' - PROGMODE
    public static final byte Stat_STK_STANDALONE        = (byte)0x04;  // Standalone mode,   '1' - SM mode
    public static final byte Stat_STK_RESET             = (byte)0x08;  // RESET button,      '1' - Pushed
    public static final byte Stat_STK_PROGRAM           = (byte)0x10;  // Program button, '   1' - Pushed
    public static final byte Stat_STK_LEDG              = (byte)0x20;  // Green LED status,  '1' - Lit
    public static final byte Stat_STK_LEDR              = (byte)0x40;  // Red LED status,    '1' - Lit
    public static final byte Stat_STK_LEDBLINK          = (byte)0x80;  // LED blink ON/OFF,  '1' - Blink

    // *****************************[ End Of COMMAND.H ]**************************

}

public class Stk500 extends UploadProtocol{

    private static final String TAG = Stk500.class.getSimpleName();

    private static final boolean DEBUG_NOT_SHOW         = true || !BuildConfig.DEBUG;
    private static final boolean DEBUG_SHOW_SEND        = true && !DEBUG_NOT_SHOW;
    private static final boolean DEBUG_SHOW_RECV        = true && !DEBUG_NOT_SHOW;
    private static final boolean DEBUG_SHOW_DRAIN       = true && !DEBUG_NOT_SHOW;
    private static final boolean DEBUG_SHOW_DUMP_LOGE   = true && !DEBUG_NOT_SHOW;

    SerialCommunicator mComm;
    AvrConf mAVRConf;
    AVRMem mAVRMem;

    boolean mCanceled;

    public Stk500() {
        mCanceled = false;
    }

    public void setSerial(SerialCommunicator comm) {
        mComm = comm;
    }

    public void setConfig(AvrConf avrConf, AVRMem avrMem) {
        mAVRConf = avrConf;
        mAVRMem  = avrMem;
    }

    private int send(byte[] buf, int length) {
        int retval = mComm.write(buf, length);
        if(DEBUG_SHOW_SEND) {
            if(retval > 0) {
                String str = "";
                for(int i=0; i<retval; i++) {
                    str += toHexStr(buf[i]) + " ";
                }
                Log.d(TAG, "send("+retval+") : " +str);
            }
        }
        return retval;
    }

    // タイムアウト型にしないとデータがうまくひろえない場合がある
    private int recv(byte[] buf, int length) {
        int retval=0;
        int totalRetval=0;
        long endTime;
        long startTime = System.currentTimeMillis();
        byte[] tmpbuf = new byte[length];

        while(true) {
            retval = mComm.read(tmpbuf,length);
//            retval = mSerial.read(tmpbuf);
            if(retval > 0) {
                System.arraycopy(tmpbuf, 0, buf, totalRetval, retval);
                totalRetval += retval;
                startTime = System.currentTimeMillis();
                if(DEBUG_SHOW_RECV) {
                    Log.d(TAG, "recv("+retval+") : " +toHexStr(buf, totalRetval));
                }
            }
            if(totalRetval >= length){break;}

            endTime = System.currentTimeMillis();
            if((endTime - startTime) > 250) {
                Log.e(TAG,"recv timeout.");
                break;
            }
        }
        return retval;
    }

    // リードバッファをカラにする
    private int drain() {
        byte[] buf = new byte[1];
        int retval = 0;
        long endTime;
        long startTime = System.currentTimeMillis();
        while(true) {
            retval = mComm.read(buf,1);
            if(retval > 0) {
//                startTime = System.currentTimeMillis();
                if(DEBUG_SHOW_DRAIN) {
                    Log.d(TAG, "drain("+retval+") : " +toHexStr(buf[0]));
                }
            }
            endTime = System.currentTimeMillis();
            if((endTime - startTime) > 1000) {break;}
        }
        return retval;
    }
    
    private int getsync() {
        byte[] buf  = new byte[32];
        byte[] resp = new byte[32];

        /* get in sync */
        buf[0] = STK500Const.Cmnd_STK_GET_SYNC;
        buf[1] = STK500Const.Sync_CRC_EOP;

        /*
         * First send and drain a few times to get rid of line noise 
         */

        send(buf, 2);
        drain();
        send(buf, 2);
        drain();

        send(buf, 2);
        if (recv(resp, 1) < 0)
          return -1;
        if (resp[0] != STK500Const.Resp_STK_INSYNC) {
          Log.e(TAG,"STK500.getsync(): not in sync: resp="+toHexStr(resp[0]));
          drain();
          return -1;
        }

        if (recv(resp, 1) < 0)
          return -1;
        if (resp[0] != STK500Const.Resp_STK_OK) {
          Log.e(TAG,"STK500.getsync(): can't communicate with device: resp="+toHexStr(resp[0]));
          return -1;
        }

        return 0;

    }

    private void dumpLogE(byte[] buf) {
        if(DEBUG_SHOW_DUMP_LOGE) {
            Log.e(TAG, "buffer("+buf.length+") : "+toHexStr(buf, buf.length));
        }
    }
    public int check_sig_bytes() {
        byte[] buf = new byte[32];

        buf[0] = STK500Const.Cmnd_STK_READ_SIGN;
        buf[1] = STK500Const.Sync_CRC_EOP;

        send(buf, 2);

        if (recv(buf, 5) < 0) { return -1; }
        if (buf[0] == STK500Const.Resp_STK_NOSYNC) {
            Log.e(TAG, "STK500.cmd(): programmer is out of sync");
            dumpLogE(buf);
            return -1;
        } else if (buf[0] != STK500Const.Resp_STK_INSYNC) {
            Log.e(TAG,"STK500.read_sig_bytes(): (a) protocol error, expect="
                    +toHexStr(STK500Const.Resp_STK_INSYNC)+", resp="+toHexStr(buf[0]));
            dumpLogE(buf);
            return -2;
        }

        if (buf[4] != STK500Const.Resp_STK_OK) {
            Log.e(TAG,"STK500.read_sig_bytes(): (a) protocol error, expect="
                    +toHexStr(STK500Const.Resp_STK_OK)+", resp="+toHexStr(buf[4]));
            dumpLogE(buf);
            return -3;
        }

        if(mAVRConf.signature[0] == buf[1] && mAVRConf.signature[1] == buf[2] && mAVRConf.signature[2] == buf[3]) {
            return 0;
        }

        return -4;

    }

    public int open() {
        setDtrRts(false);
        try { Thread.sleep(50); } catch (InterruptedException e) {}
        setDtrRts(true);
        try { Thread.sleep(50); } catch (InterruptedException e) {}

        drain();
        if(getsync()<0) { return -1; }
        return 0;
    }

    public void enable() {
        
    }

    public int initialize() {
        byte[] buf = new byte[32];
        int tries;
        int[] majArr={0};
        int[] minArr={0};
        int maj=0;
        int min=0;
        int rc;
        int n_extparms;

        getparm(STK500Const.Parm_STK_SW_MAJOR, majArr);
        getparm(STK500Const.Parm_STK_SW_MINOR, minArr);

        maj = majArr[0];
        min = minArr[0];
        // MIB510 does not need extparams
//        if (strcmp(ldata(lfirst(pgm->id)), "mib510") == 0)
//          n_extparms = 0;
//        else if ((maj > 1) || ((maj == 1) && (min > 10)))
        if ((maj > 1) || ((maj == 1) && (min > 10))) {
          n_extparms = 4;
        } else {
          n_extparms = 3;
        }

        tries = 0;

        boolean bRetry = true;
        while(bRetry) {
            bRetry = false;
        tries++;

        Arrays.fill(buf, (byte)0);

        /*
         * set device programming parameters
         */
        buf[0] = STK500Const.Cmnd_STK_SET_DEVICE;

        buf[1] = mAVRConf.stk500_devcode;//p->stk500_devcode;
        buf[2] = 0; /* device revision */

//        if ((p->flags & AVRPART_SERIALOK) && (p->flags & AVRPART_PARALLELOK))
          buf[3] = 0; /* device supports parallel and serial programming */
//        else
//          buf[3] = 1; /* device supports parallel only */

//        if (p->flags & AVRPART_PARALLELOK) {
//          if (p->flags & AVRPART_PSEUDOPARALLEL) {
//            buf[4] = 0; /* pseudo parallel interface */
//            n_extparms = 0;
//          }
//          else {
            buf[4] = 1; /* full parallel interface */
//          }
//        }

        buf[5] = 1; /* polling supported - need this in config file */
        buf[6] = 1; /* programming is self-timed - need in config file */

        buf[7] = (byte) mAVRConf.lock.size;

        /*
         * number of fuse bytes
         */
        buf[8] = 0;
        buf[8] += (byte)mAVRConf.fuse.size;
        buf[8] += (byte)mAVRConf.lfuse.size;
        buf[8] += (byte)mAVRConf.hfuse.size;
        buf[8] += (byte)mAVRConf.efuse.size;

        buf[9] = (byte)mAVRConf.flash.readback_p1;
        buf[10] = (byte)mAVRConf.flash.readback_p2;
        if(mAVRConf.flash.paged) {
            buf[13] = (byte)((mAVRConf.flash.page_size >> 8) & 0x00FF);
            buf[14] = (byte)( mAVRConf.flash.page_size       & 0x00FF);
        }
        buf[17] = (byte)((mAVRConf.flash.size >> 24) & 0xFF);
        buf[18] = (byte)((mAVRConf.flash.size >> 16) & 0xFF);
        buf[19] = (byte)((mAVRConf.flash.size >> 8 ) & 0xFF);
        buf[20] = (byte)( mAVRConf.flash.size        & 0xFF);

        buf[11] = (byte)mAVRConf.eeprom.readback_p1;
        buf[12] = (byte)mAVRConf.eeprom.readback_p2;
        buf[15] = (byte)((mAVRConf.eeprom.size >> 8) & 0x00FF);
        buf[16] = (byte)( mAVRConf.eeprom.size       & 0x00FF);

        buf[21] = STK500Const.Sync_CRC_EOP;

        send(buf, 22);
        if (recv(buf, 1) < 0) {
            return -1;
        }
        if (buf[0] == STK500Const.Resp_STK_NOSYNC) {
            Log.e(TAG,"STK500.initialize(): programmer not in sync, resp="+toHexStr(buf[0]));
            if (tries > 33) { return -1; }
            if (getsync() < 0) { return -1; }
            bRetry = true;
            continue;
        } else if (buf[0] != STK500Const.Resp_STK_INSYNC) {
            Log.e(TAG,"STK500.initialize(): (a) protocol error, expect="+toHexStr(STK500Const.Resp_STK_INSYNC)+", resp="+toHexStr(buf[0]));
            return -1;
        }

        if (recv(buf, 1) < 0) { return -1; }
        if (buf[0] != STK500Const.Resp_STK_OK) {
            Log.e(TAG,"STK500.initialize(): (b) protocol error, expect="+toHexStr(STK500Const.Resp_STK_OK)+", resp="+toHexStr(buf[0]));
            return -1;
        }

        if (n_extparms!=0) {
            if ((mAVRConf.pagel == 0) || (mAVRConf.bs2 == 0)) {
              Log.e(TAG,"please define PAGEL and BS2 signals in the configuration file for part "+mAVRConf.desc);
            } else {
              buf[0] = (byte)(n_extparms+1);

              /*
               * m is currently pointing to eeprom memory if the part has it
               */
              buf[1] = (byte)mAVRConf.eeprom.page_size;

              buf[2] = mAVRConf.pagel;
              buf[3] = mAVRConf.bs2;

              if (n_extparms == 4) {
//                if (mAVRConf.reset_disposition == RESET_DEDICATED)
                  buf[4] = 0;
//                else
//                  buf[4] = 1;
              }

              rc = set_extended_parms(n_extparms+1, buf);
              if (rc!=0) {
                Log.e(TAG,"STK500.initialize(): failed");
                return -1;
              }
            }
          }
        } // end of while(bRetry)

          return program_enable();
    }

    private int program_enable() {
        byte[] buf = new byte[16];
        int tries = 0;
        boolean bRetry = true;

        while (bRetry) {
            bRetry = false;
            tries++;

            buf[0] = STK500Const.Cmnd_STK_ENTER_PROGMODE;
            buf[1] = STK500Const.Sync_CRC_EOP;

            send(buf, 2);
            if (recv(buf, 1) < 0) {
                return -1;
            }
            if (buf[0] == STK500Const.Resp_STK_NOSYNC) {
                if (tries > 33) {
                    Log.e(TAG, "STK500.program_enable(): can't get into sync");
                    return -1;
                }
                if (getsync() < 0) {
                    return -1;
                }
                bRetry = true;
                continue;
            } else if (buf[0] != STK500Const.Resp_STK_INSYNC) {
                Log.e(TAG, "STK500.program_enable(): protocol error, expect="
                        + toHexStr(STK500Const.Resp_STK_INSYNC) + ", resp=" + toHexStr(buf[0]));
                return -1;
            }

            if (recv(buf, 1) < 0) {
                return -1;
            }
            if (buf[0] == STK500Const.Resp_STK_OK) {
                return 0;
            } else if (buf[0] == STK500Const.Resp_STK_NODEVICE) {
                Log.e(TAG, "STK500.program_enable(): no device");
                return -1;
            }

            if (buf[0] == STK500Const.Resp_STK_FAILED) {
                Log.e(TAG,
                        "STK500.program_enable(): failed to enter programming mode");
                return -1;
            }

            Log.e(TAG, "STK500.program_enable(): unknown response=" + toHexStr(buf[0]));
        }
        return -1;
    }

    private int getparm(byte parm, int[] value) {
        byte[] buf = new byte[16];
        int v;
        int tries = 0;
        boolean bRetry = true;

        while (bRetry) {
            bRetry = false;
            tries++;
            buf[0] = STK500Const.Cmnd_STK_GET_PARAMETER;
            buf[1] = parm;
            buf[2] = STK500Const.Sync_CRC_EOP;

            send(buf, 3);

            if (recv(buf, 1) < 0) {
                return -1;
            }
            if (buf[0] == STK500Const.Resp_STK_NOSYNC) {
                if (tries > 33) {
                    Log.e(TAG, "STK500.getparm(): can't get into sync\n");
                    return -1;
                }
                if (getsync() < 0) {
                    return -1;
                }
                bRetry = true;
                continue;
            } else if (buf[0] != STK500Const.Resp_STK_INSYNC) {
                Log.e(TAG, "STK500.getparm(): (a) protocol error, expect="
                        + toHexStr(STK500Const.Resp_STK_INSYNC) + ", resp=" + toHexStr(buf[0]));
                return -2;
            }

            if (recv(buf, 1) < 0) {
                return -1;
            }
            v = buf[0];

            if (recv(buf, 1) < 0) {
                return -1;
            }
            if (buf[0] == STK500Const.Resp_STK_FAILED) {
                Log.e(TAG, "STK500.getparm(): parameter " + toHexStr((byte) v) + " failed");
                return -3;
            } else if (buf[0] != STK500Const.Resp_STK_OK) {
                Log.e(TAG, "STK500.getparm(): (a) protocol error, expect="
                        + toHexStr(STK500Const.Resp_STK_INSYNC) + ", resp=" + toHexStr(buf[0]));
                return -3;
            }

            value[0] = v;
        }
        return 0;
    }

    private int set_extended_parms(int n, byte[] cmd) {
        byte[] buf = new byte[16];
        int tries = 0;
        int i;
        boolean bRetry = true;

        while (bRetry) {
            bRetry = false;
            tries++;

            buf[0] = STK500Const.Cmnd_STK_SET_DEVICE_EXT;
            System.arraycopy(cmd, 0, buf, 1, n);
            i = n + 1;
            buf[i] = STK500Const.Sync_CRC_EOP;

            send(buf, i + 1);
            if (recv(buf, 1) < 0) {
                return -1;
            }
            if (buf[0] == STK500Const.Resp_STK_NOSYNC) {
                if (tries > 33) {
                    Log.e(TAG, "STK500.set_extended_parms(): can't get into sync");
                    return -1;
                }
                if (getsync() < 0) {
                    return -1;
                }
                bRetry = true;
                continue;
            } else if (buf[0] != STK500Const.Resp_STK_INSYNC) {
                Log.e(TAG, "STK500.set_extended_parms(): protocol error, expect="
                        + toHexStr(STK500Const.Resp_STK_INSYNC) + ", resp=" + toHexStr(buf[0]));
                return -1;
            }

            if (recv(buf, 1) < 0) {
                return -1;
            }
            if (buf[0] == STK500Const.Resp_STK_OK) {
                return 0;
            } else if (buf[0] == STK500Const.Resp_STK_NODEVICE) {
                Log.e(TAG, "STK500_set_extended_parms(): no device");
                return -1;
            }

            if (buf[0] == STK500Const.Resp_STK_FAILED) {
                Log.e(TAG,
                        "STK500.set_extended_parms(): failed to set extended device programming parameters");
                return -1;
            }

            Log.e(TAG, "STK500.set_extended_parms(): unknown response=" + toHexStr(buf[0]));

        }

        return -1;
    }

    public int paged_write() {
        int page_size = mAVRMem.page_size;
        int n_bytes = mAVRMem.buf.length;

        byte[] buf = new byte[page_size + 16];
        int memtype;
        int addr;
        int a_div;
        int block_size=0;
        int tries;
        long n; 
        int i;
        boolean flash;
        boolean bRetry = true;

        if (page_size == 0) {
            // MIB510 uses page size of 256 bytes
//            if (strcmp(ldata(lfirst(pgm->id)), "mib510") == 0) {
//                page_size = 256;
//            }
//            else {
                page_size = 128;
//            }
        }

        if ( mAVRMem.desc.compareTo("flash")==0) {
            memtype = 'F';
            flash = true;
        } else if (mAVRMem.desc.compareTo("eeprom")==0) {
            memtype = 'E';
            flash = false;
        } else {
            return -2;
        }

//        if ((m->op[AVR_OP_LOADPAGE_LO]) || (m->op[AVR_OP_READ_LO])) {
            a_div = 2;
//        } else {
//            a_div = 1;
//        }

        if (n_bytes > mAVRMem.size) {
            n_bytes = mAVRMem.size;
            n = mAVRMem.size;
        } else {
            if ((n_bytes % page_size) != 0) {
                n = n_bytes + page_size - (n_bytes % page_size);
            } else {
                n = n_bytes;
            }
        }

/*
        Log.d(TAG,
                "page_size="+page_size+
                ", flash="+flash+
                ", n_bytes="+n_bytes+
                ", n="+n+
                ", a_div="+a_div+
                ", page_size"+page_size);
*/

        for (addr = 0; addr < n; addr += page_size) {
            if(Thread.interrupted()) {
                report_cancel();
                return 0;
            }

            report_progress((int)(addr*100/n));

            // MIB510 uses fixed blocks size of 256 bytes
//            if ((strcmp(ldata(lfirst(pgm->id)), "mib510") != 0) &&
//                    (addr + page_size > n_bytes)) {
//                block_size = n_bytes % page_size;
//            }
//            else {
                block_size = page_size;
//            }

            // Only skip on empty page if programming flash.
            if (flash) {
                if (is_page_empty(addr, block_size, mAVRMem.buf)) {
                    continue;
                }
            }
            tries = 0;
            bRetry = true;
            while(bRetry) {
                bRetry = false;
                tries++;
                loadaddr(addr/a_div);
                /*  build command block and avoid multiple send commands as it leads to a crash
                    of the silabs usb serial driver on mac os x */
                i = 0;

                int send_size = block_size;
                if((addr+send_size) > n_bytes) {
                    send_size -= (addr+send_size) - n_bytes;
                }
//                Log.d(TAG,"addr="+addr+", send_size="+send_size+", n_bytes="+n_bytes);
                buf[i++] = STK500Const.Cmnd_STK_PROG_PAGE;
                buf[i++] = (byte)((send_size >> 8) & 0xff);
                buf[i++] = (byte)( send_size       & 0xff);
                buf[i++] = (byte)memtype;
                System.arraycopy(mAVRMem.buf, addr, buf, i, send_size);
                i += send_size;
                buf[i++] = STK500Const.Sync_CRC_EOP;
                send(buf, i);

                if (recv(buf, 1) < 0){ return -1; }
                if (buf[0] == STK500Const.Resp_STK_NOSYNC) {
                    if (tries > 33) {
                        Log.e(TAG, "STK500.paged_write(): can't get into sync");
                        return -3;
                    }
                    if (getsync() < 0){ return -1; }
                    bRetry=true;
                    continue;
                }
                else if (buf[0] != STK500Const.Resp_STK_INSYNC) {
                    Log.e(TAG,"STK500.paged_write(): (a) protocol error, expect="+toHexStr(STK500Const.Resp_STK_INSYNC)+", resp="+toHexStr(buf[0]));
                    return -4;
                }
            } // end of while(bRetry)

            if (recv(buf, 1) < 0){ return -1; }
            if (buf[0] != STK500Const.Resp_STK_OK) {
                Log.e(TAG,"STK500.paged_write(): (a) protocol error, expect="+toHexStr(STK500Const.Resp_STK_INSYNC)+", resp="+toHexStr(buf[0]));
                return -5;
            }
        }
        report_progress((int)(addr*100/n));

        return n_bytes;
    }

    public void disable() {
      byte[] buf = new byte[16];
      int tries=0;
      boolean bRetry = true;

      while(bRetry) {
          bRetry = false;
          tries++;

          buf[0] = STK500Const.Cmnd_STK_LEAVE_PROGMODE;
          buf[1] = STK500Const.Sync_CRC_EOP;

          send(buf, 2);
          if (recv(buf, 1) < 0) { return; }
          if (buf[0] == STK500Const.Resp_STK_NOSYNC) {
              if (tries > 33) {
                  Log.e(TAG, "STK500.disable(): can't get into sync");
                  return;
              }
              if (getsync() < 0) { return; }
              bRetry = true;
              continue;
          }
          else if (buf[0] != STK500Const.Resp_STK_INSYNC) {
              Log.e(TAG,
                "STK500.disable(): protocol error, expect="+toHexStr(STK500Const.Resp_STK_INSYNC)+", resp="+toHexStr((byte)buf[0]));
              return; 
          } 
      }
      if (recv(buf, 1) < 0) { return; }
      if (buf[0] == STK500Const.Resp_STK_OK) { Log.d(TAG,"disable OK"); return; }
      else if (buf[0] == STK500Const.Resp_STK_NODEVICE) {
          Log.e(TAG, "STK500.disable(): no device");
          return;
      }

      Log.e(TAG, "STK500.disable(): unknown response="+toHexStr(buf[0]));

      return;
    }

    public void close() {
        mComm = null;
    }

    private boolean is_page_empty(int address, int page_size, byte[] buf) {
        int i;
        for(i = 0; i < page_size; i++) {
            if(buf[address + i] != 0xFF) {
                // Page is not empty.
                //Log.d(TAG,"is_page_empty is false");
                return false;
            }
        }
        // Page is empty.
        //Log.d(TAG,"is_page_empty is true");
        return true;
    }

    private int loadaddr(int addr) {
        byte[] buf = new byte[16];
        int tries;
        boolean bRetry = true;

        tries = 0;
        while(bRetry) {
            bRetry = false;
            tries++;
            buf[0] = STK500Const.Cmnd_STK_LOAD_ADDRESS;
            buf[1] = (byte)( addr       & 0xff);
            buf[2] = (byte)((addr >> 8) & 0xff);
            buf[3] = STK500Const.Sync_CRC_EOP;

            send(buf, 4);

            if (recv(buf, 1) < 0) { return -1; }
            if (buf[0] == STK500Const.Resp_STK_NOSYNC) {
                if (tries > 33) {
                    Log.e(TAG, "STK500.loadaddr(): can't get into sync");
                    return -1;
                }
                if (getsync() < 0) { return -1; }
                bRetry=true;
                continue;
            }
            else if (buf[0] != STK500Const.Resp_STK_INSYNC) {
                Log.e(TAG,"STK500.loadaddr(): (a) protocol error, expect="+toHexStr(STK500Const.Resp_STK_INSYNC)+", resp="+toHexStr(buf[0]));
                return -1;
            }
        }
        if (recv(buf, 1) < 0) { return -1; }
        if (buf[0] == STK500Const.Resp_STK_OK) {
            return 0;
        }

        Log.e(TAG,"STK500.loadaddr(): (b) protocol error, expect="+toHexStr(STK500Const.Resp_STK_INSYNC)+", resp="+toHexStr(buf[0]));

        return -1;
    }

    private String toHexStr(byte b) {
        return String.format("0x%02x", b);
    }

    private String toHexStr(byte[] b, int length) {
        String str="";
        for(int i=0; i<length; i++) {
            str += String.format("0x%02x ", b[i]);
        }
        return str;
    }

    private void setDtrRts(boolean on) {
        if(on) {
            mComm.setDtrRts(true, true);
        } else {
            mComm.setDtrRts(false, false);
        }
    }
}
