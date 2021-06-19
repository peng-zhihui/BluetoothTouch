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

public class Stk500V2 extends UploadProtocol{
    private static final String TAG = Stk500V2.class.getSimpleName();

    private static final boolean DEBUG_NOT_SHOW             = true || !BuildConfig.DEBUG;
    private static final boolean DEBUG_SHOW_READ            = true && !DEBUG_NOT_SHOW;
    private static final boolean DEBUG_SHOW_WRITE           = true && !DEBUG_NOT_SHOW;
    private static final boolean DEBUG_SHOW_COMMAND         = true && !DEBUG_NOT_SHOW;
    private static final boolean DEBUG_SHOW_COMMAND_STATUS  = true && !DEBUG_NOT_SHOW;
    private static final boolean DEBUG_SHOW_RECV            = true && !DEBUG_NOT_SHOW;
    private static final boolean DEBUG_SHOW_GETSYNC         = true && !DEBUG_NOT_SHOW;

    private static final int RETRIES                = 5;

    // *** XPROG command constants ***
    private static final int CMD_XPROG          = 0x50;
    private static final int CMD_XPROG_SETMODE  = 0x51;

    private static final int XPRG_ERR_OK        = 0;
    private static final int XPRG_ERR_FAILED    = 1;
    private static final int XPRG_ERR_COLLISION = 2;
    private static final int XPRG_ERR_TIMEOUT   = 3;

    // *****************[ STK status constants ]***************************
    // Success
    private static final int STATUS_CMD_OK                  = 0x00;

    // Warnings
    private static final int STATUS_CMD_TOUT                = 0x80;
    private static final int STATUS_RDY_BSY_TOUT            = 0x81;
    private static final int STATUS_SET_PARAM_MISSING       = 0x82;

    // Errors
    private static final int STATUS_CMD_FAILED              = 0xC0;
    @SuppressWarnings("unused")
    private static final int STATUS_CKSUM_ERROR             = 0xC1;
    @SuppressWarnings("unused")
    private static final int STATUS_CMD_UNKNOWN             = 0xC9;
    @SuppressWarnings("unused")
    private static final int STATUS_CMD_ILLEGAL_PARAMETER   = 0xCA;

    // Status
    @SuppressWarnings("unused")
    private static final int STATUS_ISP_READY               = 0x00;
    @SuppressWarnings("unused")
    private static final int STATUS_CONN_FAIL_MOSI          = 0x01;
    @SuppressWarnings("unused")
    private static final int STATUS_CONN_FAIL_RST           = 0x02;
    @SuppressWarnings("unused")
    private static final int STATUS_CONN_FAIL_SCK           = 0x04;
    @SuppressWarnings("unused")
    private static final int STATUS_TGT_NOT_DETECTED        = 0x10;
    @SuppressWarnings("unused")
    private static final int STATUS_TGT_REVERSE_INSERTED    = 0x20;

    // hw_status
    // Bits in status variable
    // Bit 0-3: Slave MCU
    // Bit 4-7: Master MCU

    @SuppressWarnings("unused")
    private static final int STATUS_AREF_ERROR              = 0;
    // Set to '1' if AREF is short circuited

    @SuppressWarnings("unused")
    private static final int STATUS_VTG_ERROR               = 4;
    // Set to '1' if VTG is short circuited

    @SuppressWarnings("unused")
    private static final int STATUS_RC_CARD_ERROR           = 5;
    // Set to '1' if board id changes when board is powered

    @SuppressWarnings("unused")
    private static final int STATUS_PROGMODE                = 6;
    // Set to '1' if board is in programming mode

    @SuppressWarnings("unused")
    private static final int STATUS_POWER_SURGE             = 7;
    // Set to '1' if board draws excessive current


    // *****************[ STK message constants ]***************************

    private static final byte MESSAGE_START = 0x1B;     //= ESC = 27 decimal
    private static final byte TOKEN         = 0x0E;

    // *****************[ STK general command constants ]**************************

    private static final byte CMD_SIGN_ON                   = 0x01;
    @SuppressWarnings("unused")
    private static final byte CMD_SET_PARAMETER             = 0x02;
    @SuppressWarnings("unused")
    private static final byte CMD_GET_PARAMETER             = 0x03;
    @SuppressWarnings("unused")
    private static final byte CMD_SET_DEVICE_PARAMETERS     = 0x04;
    @SuppressWarnings("unused")
    private static final byte CMD_OSCCAL                    = 0x05;
    private static final byte CMD_LOAD_ADDRESS              = 0x06;
    @SuppressWarnings("unused")
    private static final byte CMD_FIRMWARE_UPGRADE          = 0x07;
    @SuppressWarnings("unused")
    private static final byte CMD_CHECK_TARGET_CONNECTION   = 0x0D;
    @SuppressWarnings("unused")
    private static final byte CMD_LOAD_RC_ID_TABLE          = 0x0E;
    @SuppressWarnings("unused")
    private static final byte CMD_LOAD_EC_ID_TABLE          = 0x0F;

    // *****************[ STK ISP command constants ]******************************

    private static final byte CMD_ENTER_PROGMODE_ISP    = 0x10;
    private static final byte CMD_LEAVE_PROGMODE_ISP    = 0x11;
    @SuppressWarnings("unused")
    private static final byte CMD_CHIP_ERASE_ISP        = 0x12;
    private static final byte CMD_PROGRAM_FLASH_ISP     = 0x13;
    @SuppressWarnings("unused")
    private static final byte CMD_READ_FLASH_ISP        = 0x14;
    private static final byte CMD_PROGRAM_EEPROM_ISP    = 0x15;
    @SuppressWarnings("unused")
    private static final byte CMD_READ_EEPROM_ISP       = 0x16;
    @SuppressWarnings("unused")
    private static final byte CMD_PROGRAM_FUSE_ISP      = 0x17;
    @SuppressWarnings("unused")
    private static final byte CMD_READ_FUSE_ISP         = 0x18;
    @SuppressWarnings("unused")
    private static final byte CMD_PROGRAM_LOCK_ISP      = 0x19;
    @SuppressWarnings("unused")
    private static final byte CMD_READ_LOCK_ISP         = 0x1A;
    @SuppressWarnings("unused")
    private static final byte CMD_READ_SIGNATURE_ISP    = 0x1B;
    @SuppressWarnings("unused")
    private static final byte CMD_READ_OSCCAL_ISP       = 0x1C;
    @SuppressWarnings("unused")
    private static final byte CMD_SPI_MULTI             = 0x1D;

    // *****************[ STK answer constants ]***************************
    private static final int ANSWER_CKSUM_ERROR = 0xB0;

    private static final int PGMTYPE_UNKNOWN = 0;
    private static final int PGMTYPE_STK500 = 1;
    private static final int PGMTYPE_AVRISP = 2;
    private static final int PGMTYPE_AVRISP_MKII = 3;
    @SuppressWarnings("unused")
    private static final int PGMTYPE_JTAGICE_MKII = 4;
    private static final int PGMTYPE_STK600 = 5;

    private int mCommandSeqNum=1;
    private int mProgrammerType = PGMTYPE_UNKNOWN;
    private SerialCommunicator mComm;
    private AvrConf mAVRConf;
    private AVRMem mAVRMem;

    public Stk500V2() {
    }

    public void setSerial(SerialCommunicator comm) {
        mComm = comm;
    }

    public void setConfig(AvrConf avrConf, AVRMem avrMem) {
        mAVRConf = avrConf;
        mAVRMem  = avrMem;
    }

    @SuppressWarnings("unused")
    private void init() {
        mCommandSeqNum = 1;
    }

    private static final boolean DEBUG_SHOW_DRAIN = true;
    // リードバッファをカラにする
    private int drain() {
        byte[] buf = new byte[1];
        int retval = 0;
        long endTime;
        long startTime = System.currentTimeMillis();
        while(true) {
            retval = mComm.read(buf,1);
            if(retval > 0) {
                startTime = System.currentTimeMillis();
                if(DEBUG_SHOW_DRAIN) {
                    Log.d(TAG, "drain("+retval+") : " +toHexStr(buf[0]));
                }
            }
            endTime = System.currentTimeMillis();
            if((endTime - startTime) > 250) {break;}
        }
        return retval;
    }

    private int send(byte[] data, int len) {
        byte[] buf = new byte[275 + 6]; // max MESSAGE_BODY of 275 bytes, 6 bytes overhead
        int i;

        /*
         * if (PDATA(pgm)->pgmtype == PGMTYPE_AVRISP_MKII || PDATA(pgm)->pgmtype
         * == PGMTYPE_STK600) return stk500v2_send_mk2(pgm, data, len); else if
         * (PDATA(pgm)->pgmtype == PGMTYPE_JTAGICE_MKII) return
         * stk500v2_jtagmkII_send(pgm, data, len);
         */
        buf[0] = MESSAGE_START;
        buf[1] = (byte) mCommandSeqNum;
        buf[2] = (byte) (len / 256);
        buf[3] = (byte) (len % 256);
        buf[4] = TOKEN;
        System.arraycopy(data, 0, buf, 5, len);

        // calculate the XOR checksum
        buf[5 + len] = 0;
        for (i = 0; i < 5 + len; i++)
            buf[5 + len] ^= buf[i];

        return write(buf, len + 6);

    }

    private int command(byte[] buf, int len, int maxlen) {
        int i;
        int tries = 0;
        int status;
        boolean bRetry=true;

        if(DEBUG_SHOW_COMMAND) { Log.d(TAG, "STK500V2.command("+toHexStr(buf, len)+", "+len+")"); }

        while (bRetry) {
            bRetry = false;
            tries++;

            // send the command to the programmer
            send(buf, len);
            // attempt to read the status back
            status = recv(buf, maxlen);

            if (DEBUG_SHOW_COMMAND_STATUS) {
                Log.d(TAG, "STK500V2.command(): status:"+status+",buf{"+toHexStr(buf, buf.length)+"}");
            }

            // if we got a successful readback, return
            if (status > 0) {
                if (DEBUG_SHOW_COMMAND_STATUS) {
                    Log.d(TAG, "status = " + status);
                }
                if (status < 2) {
                    Log.e(TAG, "STK500V2.command(): short reply\n");
                    return -1;
                }
                if (buf[0] == CMD_XPROG_SETMODE || buf[0] == CMD_XPROG) {
                    /*
                     * Decode XPROG wrapper errors.
                     */
                    String msg;

                    /*
                     * For CMD_XPROG_SETMODE, the status is returned in buf[1].
                     * For CMD_XPROG, buf[1] contains the XPRG_CMD_* command,
                     * and buf[2] contains the status.
                     */
                    i = (buf[0] == CMD_XPROG_SETMODE) ? 1 : 2;

                    if (buf[i] != XPRG_ERR_OK) {
                        switch (buf[i]) {
                            case XPRG_ERR_FAILED:
                                msg = "Failed";
                                break;
                            case XPRG_ERR_COLLISION:
                                msg = "Collision";
                                break;
                            case XPRG_ERR_TIMEOUT:
                                msg = "Timeout";
                                break;
                            default:
                                msg = "Unknown";
                                break;
                        }
                        Log.e(TAG, "STK500V2.command(): error in " +
                                (buf[0] == CMD_XPROG_SETMODE ? "CMD_XPROG_SETMODE" : "CMD_XPROG")
                                + ": " + msg);
                        return -1;
                    }
                    return 0;
                } else {
                    /*
                     * Decode STK500v2 errors.
                     */
                    if (buf[1] >= STATUS_CMD_TOUT && buf[1] < 0xa0) {
                        String msg;
//                        byte[] msgbuf = new byte[30];
                        switch (buf[1]) {
                            case (byte) STATUS_CMD_TOUT:
                                msg = "Command timed out";
                                break;

                            case (byte) STATUS_RDY_BSY_TOUT:
                                msg = "Sampling of the RDY/nBSY pin timed out";
                                break;

                            case (byte) STATUS_SET_PARAM_MISSING:
                                msg = "The `Set Device Parameters' have not been "
                                        + "executed in advance of this command";

                            default:
                                msg = "unknown, code " + Integer.toHexString((int) buf[1]);
                                break;
                        }
                        if (DEBUG_SHOW_COMMAND_STATUS) {
                            Log.v(TAG, "STK500V2.command(): warning: " + msg);
                        }
                    } else if (buf[1] == (byte) STATUS_CMD_OK) {
                        return status;
                    } else if (buf[1] == (byte) STATUS_CMD_FAILED) {
                        Log.e(TAG, "STK500V2.command(): command failed");
                    } else {
                        Log.e(TAG,
                                "STK500V2.command(): unknown status "
                                        + Integer.toHexString((int) buf[1]));
                    }
                    return -1;
                }
            } // end of if (status > 0)

            // otherwise try to sync up again
            status = getsync();
            if (status != 0) {
                if (tries > RETRIES) {
                    Log.e(TAG,
                            "STK500V2.command(): failed miserably to execute command "
                                    + Integer.toHexString((int) buf[0]));
                    return -1;
                } else {
                    bRetry = true;
                }
            }
        } // end of while(bRetry)

        if (DEBUG_SHOW_COMMAND) {
            Log.d(TAG, " = 0");
        }
        return 0;
    } // end of private int command()


    private boolean compareByteArrayWithString(byte[] buf,int bufPos, String str) {
        byte[] tmpbuf = new byte[str.length()];
        System.arraycopy(buf, bufPos, tmpbuf, 0, str.length());
        if(Arrays.equals(tmpbuf, str.getBytes())) {
            return true;
        }
        return false;
    }

    private static final String[] PROGRAMMER_NAME =
    {
      "unknown",
      "STK500",
      "AVRISP",
      "AVRISP mkII",
      "JTAG ICE mkII",
      "STK600",
    };

    @SuppressWarnings("unused")
    private static final int sINIT      = 0;
    private static final int sSTART     = 1;
    private static final int sSEQNUM    = 2;
    private static final int sSIZE1     = 3;
    private static final int sSIZE2     = 4;
    private static final int sTOKEN     = 5;
    private static final int sDATA      = 6;
    private static final int sCSUM      = 7;
    private static final int sDONE      = 8;

    private static final int SERIAL_TIMEOUT = 2;

    int recv(byte[] buf, int length) {
        int state = sSTART;
        int msglen = 0;
        int curlen = 0;
        byte[] c = new byte[1];
        c[0] = 0;
        byte checksum = 0;

        long timeoutval = SERIAL_TIMEOUT; // seconds

        /*
         * if (mProgrammerType == PGMTYPE_AVRISP_MKII || mProgrammerType ==
         * PGMTYPE_STK600) return stk500v2_recv_mk2(pgm, msg, maxsize); else if
         * (mProgrammerType == PGMTYPE_JTAGICE_MKII) return
         * stk500v2_jtagmkII_recv(pgm, msg, maxsize);
         */
        if (DEBUG_SHOW_RECV) {
            Log.v(TAG, "STK500V2.recv(): ");
        }

        long tstart = java.lang.System.currentTimeMillis();

        while ((state != sDONE)) {
            if (read(c, 1) <= 0) {
                long tnow = java.lang.System.currentTimeMillis();
                if ((tnow - tstart) / 1000 > timeoutval) { // wuff -
                                                           // signed/unsigned/overflow
                    Log.e(TAG, "STK500V2.recv(): timeout");
                    return -1;
                }
                continue;
            }
            if (DEBUG_SHOW_RECV) { Log.d(TAG, "recv : "+toHexStr(c[0])); }
            checksum ^= c[0];

            switch (state) {
                case sSTART:
                    if (DEBUG_SHOW_RECV) { Log.d(TAG, "hoping for start token..."); }

                    if (c[0] == MESSAGE_START) {
                        if (DEBUG_SHOW_RECV) { Log.d(TAG, "got it\n"); }
                        checksum = MESSAGE_START;
                        state = sSEQNUM;
                    } else {
                        if (DEBUG_SHOW_RECV) { Log.d(TAG, "sorry\n"); }
                    }
                    break;

                case sSEQNUM:
                    if (DEBUG_SHOW_RECV) { Log.d(TAG, "hoping for sequence...\n"); }

                    if (c[0] == mCommandSeqNum) {
                        if (DEBUG_SHOW_RECV) { Log.d(TAG, "got it, incrementing\n"); }
                        state = sSIZE1;
                        mCommandSeqNum++;
                    } else {
                        if (DEBUG_SHOW_RECV) { Log.d(TAG, "sorry\n"); }
                        state = sSTART;
                    }
                    break;

                case sSIZE1:
                    if (DEBUG_SHOW_RECV) { Log.d(TAG, "hoping for size LSB\n"); }
                    msglen = ((int) c[0]) * 256;
                    state = sSIZE2;
                    break;

                case sSIZE2:
                    if (DEBUG_SHOW_RECV) { Log.d(TAG, "hoping for size MSB..."); }
                    msglen += (int) c[0];
                    if (DEBUG_SHOW_RECV) { Log.d(TAG, " msg is " + msglen + " bytes"); }
                    state = sTOKEN;
                    break;

                case sTOKEN:
                    if (c[0] == TOKEN) {
                        if (DEBUG_SHOW_RECV) { Log.d(TAG, "recv : sTOKEN : sDATA"); }
                        state = sDATA;
                    } else {
                        if (DEBUG_SHOW_RECV) { Log.d(TAG, "recv : sTOKEN : sSTART"); }
                        state = sSTART;
                    }
                    break;

                case sDATA:
                    if (DEBUG_SHOW_RECV) { Log.d(TAG, "recv | sDATA | msglen:"+msglen+", curlen:"+curlen+", length:"+length+", c[0]:"+toHexStr(c[0])); }
                    if (curlen < length) {
                        buf[curlen] = c[0];
                    } else {
                        Log.e(TAG, "STK500V2.recv(): buffer too small, received " + curlen
                                + " byte into " + length + " byte buffer");
                        return -2;
                    }
                    if ((curlen == 0) && (buf[0] == ANSWER_CKSUM_ERROR)) {
                        Log.e(TAG, "STK500V2.recv(): previous packet sent with wrong checksum");
                        return -3;
                    }
                    curlen++;
                    if (curlen == msglen) {
                        state = sCSUM;
                    }
                    break;

                case sCSUM:
                    if (DEBUG_SHOW_RECV) { Log.d(TAG, "recv | sCSUM"); }
                    if (checksum == 0) {
                        state = sDONE;
                    } else {
                        state = sSTART;
                        Log.e(TAG, "STK500V2.recv(): checksum error");
                        return -4;
                    }
                    break;
                default:
                    Log.e(TAG, "STK500V2.recv(): unknown state");
                    return -5;
            } /* switch */
        } /* while */

        return (int) (msglen + 6);
    }

    int getsync() {
        int tries = 0;
        byte[] buf = new byte[1];
        byte[] resp = new byte[32];
        int status;
        boolean bRetry = true;

        if (DEBUG_SHOW_GETSYNC) {
            Log.d(TAG, "STK500V2.getsync()");
        }

        while (bRetry) {
            bRetry = false;
            tries++;

            // send the sync command and see if we can get there
            buf[0] = CMD_SIGN_ON;
            send(buf, 1);

            // try to get the response back and see where we got
            status = recv(resp, resp.length);

            // if we got bytes returned, check to see what came back
            if (status > 0) {
                if ((resp[0] == CMD_SIGN_ON) && (resp[1] == STATUS_CMD_OK) &&
                        (status > 3)) {
                    // success!
                    int siglen = resp[2];
                    if (siglen >= "STK500_2".length() &&
                            compareByteArrayWithString(resp, 3, "STK500_2")) {
                        mProgrammerType = PGMTYPE_STK500;
                    } else if (siglen >= "AVRISP_2".length() &&
                            compareByteArrayWithString(resp, 3, "AVRISP_2")) {
                        mProgrammerType = PGMTYPE_AVRISP;
                    } else if (siglen >= "AVRISP_MK2".length() &&
                            compareByteArrayWithString(resp, 3, "AVRISP_MK2")) {
                        mProgrammerType = PGMTYPE_AVRISP_MKII;
                    } else if (siglen >= "STK600".length() &&
                            compareByteArrayWithString(resp, 3, "STK600")) {
                            mProgrammerType = PGMTYPE_STK600;
                    } else {
                        resp[siglen + 3] = 0;
                        byte[] tmpbuf = new byte[siglen];
                        System.arraycopy(buf, 3, tmpbuf, 0, siglen);
                        mProgrammerType = PGMTYPE_STK500;
                        if (DEBUG_SHOW_GETSYNC) {
                            Log.e(TAG,
                                    "STK500V2.getsync(): got response from unknown "
                                            + "programmer " + PROGRAMMER_NAME[mProgrammerType]
                                            + ", assuming STK500");
                        }
                    }

                    if (DEBUG_SHOW_GETSYNC) {
                        Log.e(TAG,
                                "STK500V2.getsync(): found " + PROGRAMMER_NAME[mProgrammerType]
                                        + " programmer");
                        return 0;
                    } else {
                        if (tries > RETRIES) {
                            Log.e(TAG,
                                    "STK500V2.getsync(): can't communicate with device: resp="
                                            + Integer.toHexString((int) resp[0]));
                            return -6;
                        } else {
                            bRetry = true;
                        }
                    }

                    // or if we got a timeout
                } else if (status == -1) {
                    if (tries > RETRIES) {
                        Log.e(TAG, "STK500V2.getsync(): timeout communicating with programmer");
                        return -1;
                    } else {
                        bRetry = true;
                    }

                    // or any other error
                } else {
                    if (tries > RETRIES) {
                        Log.e(TAG, "STK500V2.getsync(): error communicating with programmer: ("
                                + status + ")");
                    } else {
                        bRetry = true;
                    }
                }
            } // end of if (status > 0)
        } // end of while(bRetry)
            return 0;
    } // end of int getsync()

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
/*        if ((PDATA(pgm)->pgmtype == PGMTYPE_STK600 ||
                PDATA(pgm)->pgmtype == PGMTYPE_AVRISP_MKII ||
                PDATA(pgm)->pgmtype == PGMTYPE_JTAGICE_MKII) != 0
               && (p->flags & (AVRPART_HAS_PDI | AVRPART_HAS_TPI)) != 0) {
             /*
              * This is an ATxmega device, must use XPROG protocol for the
              * remaining actions.
              *
             stk600_setup_xprog(pgm);
           } else {

        stk600_setup_isp(pgm);
//           }

           if (p->flags & AVRPART_IS_AT90S1200) {
             /*
              * AT90S1200 needs a positive reset pulse after a chip erase.
              *
             pgm->disable(pgm);
             usleep(10000);
           }
           
    */
        return program_enable();

    }

    public int program_enable() {
        byte[] buf = new byte[16];
//        String msg;             /* see remarks above about size needed */
        int rv;

//        PDATA(pgm)->lastpart = p;

/*        if (p->op[AVR_OP_PGM_ENABLE] == NULL) {
          fprintf(stderr, "%s: stk500v2_program_enable(): program enable instruction not defined for part \"%s\"\n",
                  progname, p->desc);
          return -1; 
        }
*/
/*        if (PDATA(pgm)->pgmtype == PGMTYPE_STK500 ||
            PDATA(pgm)->pgmtype == PGMTYPE_STK600)
            /* Activate AVR-style (low active) RESET *
            stk500v2_setparm_real(pgm, PARAM_RESET_POLARITY, 0x01);
*/

        buf[0] = CMD_ENTER_PROGMODE_ISP;
        buf[1] = mAVRConf.timeout;
        buf[2] = mAVRConf.stabdelay;
        buf[3] = mAVRConf.cmdexedelay;
        buf[4] = mAVRConf.synchloops;
        buf[5] = mAVRConf.bytedelay;
        buf[6] = mAVRConf.pollvalue;
        buf[7] = mAVRConf.pollindex;
/*        byte[] tmpbuf = new byte[32];
        avr_set_bits(mAVRMem.op[AVRMem.AVR_OP_PGM_ENABLE], tmpbuf);
        System.arraycopy(tmpbuf, 0, buf, 8, 2);
*/
        buf[8] = (byte)0xac;
        buf[9] = 0x53;
        buf[10] = 0;
        buf[11] = 0;

        rv = command(buf, 12, buf.length);

/*        if (rv < 0) {
          switch (PDATA(pgm)->pgmtype)
          {
          case PGMTYPE_STK600:
          case PGMTYPE_AVRISP_MKII:
              if (stk500v2_getparm(pgm, PARAM_STATUS_TGT_CONN, &buf[0]) != 0) {
                  fprintf(stderr,
                          "%s: stk500v2_program_enable(): cannot get connection status\n",
                          progname);
              } else {
                  stk500v2_translate_conn_status(buf[0], msg);
                  fprintf(stderr, "%s: stk500v2_program_enable():"
                          " bad AVRISPmkII connection status: %s\n",
                          progname, msg);
              }
              break;
        
          default:
              /* cannot report anything for other pgmtypes *
              break;
          }
        }
*/

        return rv;
    }

    private static final int UINT_MAX = 65535;
    /*
     * avr_set_bits() 
     *
     * Set instruction bits in the specified command based on the opcode.
     */
    int avr_set_bits(AVRMem.OPCODE op , byte[] cmd) {
        int i, j, bit; 
        byte mask;

        for (i=0; i<32; i++) {
            if (op.bit[i].type == AVRMem.AVR_CMDBIT_VALUE) {
                j = 3 - i / 8;
                bit = i % 8;
                mask = (byte) (1 << bit);
                if (op.bit[i].value!=0) {
                    cmd[j] = (byte) (cmd[j] | mask);
                } else {
                    cmd[j] = (byte) (cmd[j] & ~mask);
                }
            }
        }
        return 0;
    }

    public int paged_write() {
        int addr;
        int block_size;
        int last_addr;
//        int hiaddr;
        int addrshift;
        int use_ext_addr;
        byte[] commandbuf = new byte[10];
        byte[] buf = new byte[266];
        byte[] cmds = new byte[4];
        int result;
        AVRMem.OPCODE rop, wop;

        int page_size = mAVRMem.page_size;
        int n_bytes = mAVRMem.buf.length;

//        Log.d(TAG,"STK500V2: STK500V2.paged_write(..,"+mAVRMem.desc+","+page_size+","+n_bytes+")");

        if (page_size == 0) { page_size = 256; }
//        hiaddr = UINT_MAX;
        addrshift = 0;
        use_ext_addr = 0;

        // determine which command is to be used
        if (mAVRMem.desc.compareTo("flash")==0) {
            addrshift = 1;
            commandbuf[0] = CMD_PROGRAM_FLASH_ISP;
            /*
             * If bit 31 is set, this indicates that the following read/write
             * operation will be performed on a memory that is larger than
             * 64KBytes. This is an indication to STK500 that a load extended
             * address must be executed.
             */ 
            if (mAVRMem.op[AVRMem.AVR_OP_LOAD_EXT_ADDR] != null) {
                use_ext_addr = (1 << 31);
            }
        } else if (mAVRMem.desc.compareTo("eeprom") == 0) {
            commandbuf[0] = CMD_PROGRAM_EEPROM_ISP;
        }
        commandbuf[4] = (byte) mAVRMem.delay;

        if (addrshift == 0) {
            wop = mAVRMem.op[AVRMem.AVR_OP_WRITE];
            rop = mAVRMem.op[AVRMem.AVR_OP_READ];
        } else {
            wop = mAVRMem.op[AVRMem.AVR_OP_WRITE_LO];
            rop = mAVRMem.op[AVRMem.AVR_OP_READ_LO];
        }

        // if the memory is paged, load the appropriate commands into the buffer
        if ((mAVRMem.mode & 0x01) == 0x01) {
            commandbuf[3] = (byte) (mAVRMem.mode | 0x80);             // yes, write the page to flash

            if (mAVRMem.op[AVRMem.AVR_OP_LOADPAGE_LO] == null) {
                Log.e(TAG, "STK500V2.paged_write: loadpage instruction not defined for part \""+mAVRMem.desc+"\"");
                return -1;
            }
            avr_set_bits(mAVRMem.op[AVRMem.AVR_OP_LOADPAGE_LO], cmds);
            commandbuf[5] = cmds[0];

            if (mAVRMem.op[AVRMem.AVR_OP_WRITEPAGE] == null) {
                Log.e(TAG, "STK500V2.paged_write: write page instruction not defined for part \""+mAVRMem.desc+"\"");
                return -1;
            }
            avr_set_bits(mAVRMem.op[AVRMem.AVR_OP_WRITEPAGE], cmds);
            commandbuf[6] = cmds[0];

            // otherwise, we need to load different commands in
        } else {
            commandbuf[3] = (byte) (mAVRMem.mode | 0x80);             // yes, write the words to flash

            if (wop == null) {
                Log.e(TAG, "STK500V2.paged_write: write instruction not defined for part \""+mAVRMem.desc+"\"");
                return -1;
            }
            avr_set_bits(wop, cmds);
            commandbuf[5] = cmds[0];
            commandbuf[6] = 0;
        }

        // the read command is common to both methods
        if (rop == null) {
            Log.e(TAG, "STK500V2.paged_write: read instruction not defined for part \""+mAVRMem.desc+"\"");
            return -1;
        }
        avr_set_bits(rop, cmds);
        commandbuf[7] = cmds[0];

        commandbuf[8] = mAVRMem.readback[0];
        commandbuf[9] = mAVRMem.readback[1];

        last_addr=UINT_MAX;           /* impossible address */

        for (addr=0; addr < n_bytes; addr += page_size) {
            if(Thread.interrupted()) {
                report_cancel();
                return 0;
            }

            report_progress((int)(addr*100/n_bytes));

            if ((n_bytes-addr) < page_size) {
                block_size = n_bytes - addr;
            } else {
                block_size = page_size;
            }

//            Log.d(TAG,"n_bytes "+n_bytes);
//            Log.d(TAG,"block_size at addr "+addr+" is "+block_size);

            if(commandbuf[0] == CMD_PROGRAM_FLASH_ISP){
                if (is_page_empty(addr, block_size, mAVRMem.buf)) {
                    continue;
                }
            }

            System.arraycopy(commandbuf, 0, buf, 0, commandbuf.length);

            buf[1] = (byte) (block_size >> 8);
            buf[2] = (byte) (block_size & 0xff);

            if((last_addr==UINT_MAX)||(last_addr+block_size != addr)){
                if (loadaddr(use_ext_addr | (addr >> addrshift)) < 0)
                    return -1;
            }
            last_addr=addr;

            System.arraycopy(mAVRMem.buf, addr, buf, 10, block_size);

            result = command(buf,block_size+10, buf.length);
            if (result < 0) {
                Log.e(TAG, "STK500V2.paged_write: write command failed");
                return -1;
            }
        }

        report_progress((int)(addr*100/n_bytes));

        return n_bytes;
    }

    boolean is_page_empty(int address, int page_size, byte[] buf) {
        int i;

        for(i = 0; i < page_size; i++) {
            if(buf[address + i] != 0xFF) {
                /* Page is not empty. */
                return false;
            }
        }
        /* Page is empty. */
        return true;
    }

    int loadaddr(int addr) {
        byte[] buf = new byte[16];
        int result;

//        Log.d(TAG, "STK500V2.loadaddr("+addr+")");

        buf[0] = CMD_LOAD_ADDRESS;
        buf[1] = (byte) ((addr >> 24)   & 0xff);
        buf[2] = (byte) ((addr >> 16)   & 0xff);
        buf[3] = (byte) ((addr >> 8)    & 0xff);
        buf[4] = (byte) ( addr          & 0xff);

        result = command(buf, 5, buf.length);

        if (result < 0) {
            Log.e(TAG,"STK500V2.loadaddr(): failed to set load address");
            return -1;
        }
        return 0;
    }

    private int read(byte[] buf, int length) {
        int retval;
        retval = mComm.read(buf,length);
        if(DEBUG_SHOW_READ){
            if(retval>0){
                String str = "";
                for(int i=0; i<retval; i++) {
                    str += Integer.toHexString((int)buf[i])+ " ";
                }
                Log.d(TAG, "read("+retval+") : "+str);
            }
        }
        return retval;
    }

    private int write(byte[] buf, int length) {
        int retval;
        retval = mComm.write(buf, length);
        if(DEBUG_SHOW_WRITE){
            if(retval>0){
                Log.d(TAG, "write("+retval+") : "+toHexStr(buf, retval));
            }
        }
        return retval;
    }

    private void setDtrRts(boolean on) {
        if(on) {
            mComm.setDtrRts(true, true);
        } else {
            mComm.setDtrRts(false, false);
        }
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

    @Override
    public int check_sig_bytes() {
        // TODO : implement
        return 0;
    }

    @Override
    public void disable() {
        byte[] buf = new byte[16];
        int result;
        buf[0] = CMD_LEAVE_PROGMODE_ISP;
        buf[1] = 1; // preDelay;
        buf[2] = 1; // postDelay;
        result = command(buf, 3, buf.length);
        if (result < 0) {
          Log.e(TAG, "STK500V2.disable(): failed to leave programming mode");
        }
        return;
    }

}
