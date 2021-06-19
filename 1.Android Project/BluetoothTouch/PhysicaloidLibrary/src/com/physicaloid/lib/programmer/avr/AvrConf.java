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

import com.physicaloid.lib.Boards;

class AVRConfMemEEPROM {
    String desc;
    boolean paged;
    int page_size;
    int size;
    int min_write_delay;
    int max_write_delay;
    int readback_p1;
    int readback_p2;
    String[] read;
    String[] write;
    String[] loadpage_lo;
    String[] writepage;
    int mode;
    int delay;
    int blocksize;
    int readsize;

    public AVRConfMemEEPROM(
            boolean memoryPaged,
            int memoryPage_size,
            int memorySize,
            int memoryMin_write_delay,
            int memoryMax_write_delay,
            int memoryReadback_p1,
            int memoryReadback_p2,
            String[] memoryRead,
            String[] memoryWrite,
            String[] memoryLoadpage_lo,
            String[] memoryWritepage,
            int memoryMode,
            int memoryDelay,
            int memoryBlocksize,
            int memoryReadsize
            ) {
        desc            = "eeprom";
        paged           = memoryPaged;
        page_size       = memoryPage_size;
        size            = memorySize;
        min_write_delay = memoryMin_write_delay;
        max_write_delay = memoryMax_write_delay;
        readback_p1     = memoryReadback_p1;
        readback_p2     = memoryReadback_p2;
        read            = memoryRead;
        write           = memoryWrite;
        loadpage_lo     = memoryLoadpage_lo;
        writepage       = memoryWritepage;
        mode            = memoryMode;
        delay           = memoryDelay;
        blocksize       = memoryBlocksize;
        readsize        = memoryReadsize;
    }
}

/*
memory "eeprom"
paged           = no;
page_size       = 4;
size            = 1024;
min_write_delay = 3600;
max_write_delay = 3600;
readback_p1     = 0xff;
readback_p2     = 0xff;
read = " 1 0 1 0 0 0 0 0",
       " 0 0 0 x x x a9 a8",
       " a7 a6 a5 a4 a3 a2 a1 a0",
       " o o o o o o o o";

write = " 1 1 0 0 0 0 0 0",
        " 0 0 0 x x x a9 a8",
        " a7 a6 a5 a4 a3 a2 a1 a0",
        " i i i i i i i i";

loadpage_lo = " 1 1 0 0 0 0 0 1",
              " 0 0 0 0 0 0 0 0",
              " 0 0 0 0 0 0 a1 a0",
              " i i i i i i i i";

writepage = " 1 1 0 0 0 0 1 0",
            " 0 0 x x x x a9 a8",
            " a7 a6 a5 a4 a3 a2 0 0",
            " x x x x x x x x";

mode            = 0x41;
delay           = 20;
blocksize       = 4;
readsize        = 256;
;
*/

class AVRConfMemFlash {

    String desc;
    boolean paged;
    int size;
    int page_size;
    int num_pages;
    int min_write_delay;
    int max_write_delay;
    int readback_p1;
    int readback_p2;
    String[] read_lo;
    String[] read_hi;
    String[] loadpage_lo;
    String[] loadpage_hi;
    String[] load_ext_addr;
    String[] writepage;

    int mode;
    int delay;
    int blocksize;
    int readsize;

    public AVRConfMemFlash(
            boolean memoryPaged,
            int memorySize,
            int memoryPage_size,
            int memoryNum_pages,
            int memoryMin_write_delay,
            int memoryMax_write_delay,
            int memoryReadback_p1,
            int memoryReadback_p2,
            String[] memoryRead_lo,
            String[] memoryRead_hi,
            String[] memoryLoadpage_lo,
            String[] memoryLoadpage_hi,
            String[] memoryWritepage,
            String[] memoryLoad_ext_addr,

            int memoryMode,
            int memoryDelay,
            int memoryBlocksize,
            int memoryReadsize) {

        desc            = "flash";
        paged           = memoryPaged;
        size            = memorySize;
        page_size       = memoryPage_size;
        num_pages       = memoryNum_pages;
        min_write_delay = memoryMin_write_delay;
        max_write_delay = memoryMax_write_delay;
        readback_p1     = memoryReadback_p1;
        readback_p2     = memoryReadback_p2;
        read_lo         = memoryRead_lo;
        read_hi         = memoryRead_hi;
        loadpage_lo     = memoryLoadpage_lo;
        loadpage_hi     = memoryLoadpage_hi;
        writepage       = memoryWritepage;

        mode            = memoryMode;
        delay           = memoryDelay;
        blocksize       = memoryBlocksize;
        readsize        = memoryReadsize;
    }
}
/*
memory "flash"
paged           = yes;
size            = 32768;
page_size       = 128;
num_pages       = 256;
min_write_delay = 4500;
max_write_delay = 4500;
readback_p1     = 0xff;
readback_p2     = 0xff;
read_lo = " 0 0 1 0 0 0 0 0",
          " 0 0 a13 a12 a11 a10 a9 a8",
          " a7 a6 a5 a4 a3 a2 a1 a0",
          " o o o o o o o o";

read_hi = " 0 0 1 0 1 0 0 0",
          " 0 0 a13 a12 a11 a10 a9 a8",
          " a7 a6 a5 a4 a3 a2 a1 a0",
          " o o o o o o o o";

loadpage_lo = " 0 1 0 0 0 0 0 0",
              " 0 0 0 x x x x x",
              " x x a5 a4 a3 a2 a1 a0",
              " i i i i i i i i";

loadpage_hi = " 0 1 0 0 1 0 0 0",
              " 0 0 0 x x x x x",
              " x x a5 a4 a3 a2 a1 a0",
              " i i i i i i i i";

writepage = " 0 1 0 0 1 1 0 0",
            " 0 0 a13 a12 a11 a10 a9 a8",
            " a7 a6 x x x x x x",
            " x x x x x x x x";

mode            = 0x41;
delay           = 6;
blocksize       = 128;
readsize        = 256;

;
*/

class AVRConfMemFuse {
    String name;
    int size;
    int min_write_delay;
    int max_write_delay;
    String[] read;
    String[] write;

    public AVRConfMemFuse(
            String memoryName,
            int memorySize,
            int memoryMinWriteDelay,
            int memoryMaxWriteDelay,
            String[] memoryRead,
            String[] memoryWrite) {
        name            = memoryName;
        size            = memorySize;
        min_write_delay = memoryMinWriteDelay;
        max_write_delay = memoryMaxWriteDelay;
        read            = memoryRead;
        write           = memoryWrite;
    }
}
/*
memory "lfuse"
    size = 1;
    min_write_delay = 4500;
    max_write_delay = 4500;
    read = "0 1 0 1 0 0 0 0 0 0 0 0 0 0 0 0",
           "x x x x x x x x o o o o o o o o";

    write = "1 0 1 0 1 1 0 0 1 0 1 0 0 0 0 0",
            "x x x x x x x x i i i i i i i i";
;
*/

class AVRMem {
    public static final int AVR_OP_READ             = 0;
    public static final int AVR_OP_WRITE            = 1;
    public static final int AVR_OP_READ_LO          = 2;
    public static final int AVR_OP_READ_HI          = 3;
    public static final int AVR_OP_WRITE_LO         = 4;
    public static final int AVR_OP_WRITE_HI         = 5;
    public static final int AVR_OP_LOADPAGE_LO      = 6;
    public static final int AVR_OP_LOADPAGE_HI      = 7;
    public static final int AVR_OP_LOAD_EXT_ADDR    = 8;
    public static final int AVR_OP_WRITEPAGE        = 9;
    public static final int AVR_OP_CHIP_ERASE       = 10;
    public static final int AVR_OP_PGM_ENABLE       = 11;
    public static final int AVR_OP_MAX              = 12;

    public static final int AVR_CMDBIT_IGNORE   = 0;    /* bit is ignored on input and output */
    public static final int AVR_CMDBIT_VALUE    = 1;    /* bit is set to 0 or 1 for input or output */
    public static final int AVR_CMDBIT_ADDRESS  = 2;    /* this bit represents an input address bit */
    public static final int AVR_CMDBIT_INPUT    = 3;    /* this bit is an input bit */
    public static final int AVR_CMDBIT_OUTPUT   = 4;    /* this bit is an output bit */

    String desc;                // memory description ("flash", "eeprom", etc)
    boolean paged;                  // page addressed (e.g. ATmega flash)
    int size;                   // total memory size in bytes
    int page_size;              // size of memory page (if page addressed)
    int num_pages;              // number of pages (if page addressed)
    long offset;                // offset in IO memory (ATxmega)
    int min_write_delay;        // microseconds
    int max_write_delay;        // microseconds
    int pwroff_after_write;     // after this memory type is written to,
                                // the device must be powered off and
                                // back on, see errata
                                // http://www.atmel.com/atmel/acrobat/doc1280.pdf
    byte[] readback;            // polled read-back values

    int mode;                   // stk500 v2 xml file parameter
    int delay;                  // stk500 v2 xml file parameter
    int blocksize;              // stk500 v2 xml file parameter
    int readsize;               // stk500 v2 xml file parameter
    int pollindex;              // stk500 v2 xml file parameter

    byte[]      buf;            // pointer to memory buffer
    OPCODE[]    op;             // opcodes

    AVRMem(AvrConf avrConf){
        // Only memory type is "flash"
        desc                = avrConf.flash.desc;
        paged               = avrConf.flash.paged;
        size                = avrConf.flash.size;
        page_size           = avrConf.flash.page_size;
        num_pages           = avrConf.flash.num_pages;
        offset              = 0;
        min_write_delay     = avrConf.flash.min_write_delay;
        max_write_delay     = avrConf.flash.max_write_delay;
        pwroff_after_write  = 0;
        readback            = new byte[2];
        readback[0]         = (byte)avrConf.flash.readback_p1;
        readback[1]         = (byte)avrConf.flash.readback_p2;
        mode                = avrConf.flash.mode;
        delay               = avrConf.flash.delay;
        blocksize           = avrConf.flash.blocksize;
        readsize            = avrConf.flash.readsize;
        pollindex           = 0;
        buf                 = null;
        op                  = new OPCODE[AVR_OP_MAX];
        for(int i=0; i<AVR_OP_MAX; i++) {
            op[i] = new OPCODE();
        }
        parseOpcode(op[AVR_OP_READ_LO], avrConf.flash.read_lo);
        parseOpcode(op[AVR_OP_READ_HI], avrConf.flash.read_hi);
        parseOpcode(op[AVR_OP_LOADPAGE_LO], avrConf.flash.loadpage_lo);
        parseOpcode(op[AVR_OP_LOADPAGE_HI], avrConf.flash.loadpage_hi);
        parseOpcode(op[AVR_OP_LOAD_EXT_ADDR], avrConf.flash.load_ext_addr);
        parseOpcode(op[AVR_OP_WRITEPAGE], avrConf.flash.writepage);
    }

    void setBuf(byte[] inBuf, int length) {
        buf = new byte[length];
        System.arraycopy(inBuf, 0, buf, 0, length);
    }

    void parseOpcode(OPCODE op, String[] mem) {
        String tmpstr="";
        String[] str;
        int no = 31;

        if(mem == null) {
            return;
        }

        for(int i=0; i<mem.length; i++) {
            tmpstr += mem[i] + " ";
        }

        // 最初の空白を取り除く
        while(tmpstr.charAt(0) == ' ') {
            tmpstr = tmpstr.substring(1);
        }
        str = tmpstr.split("[\\s]+");//空白がいくつあっても1区切りとする

        for(int i=0; i<32; i++) {
            if(str[i].charAt(0) == '0') {
                op.bit[no].type  = AVR_CMDBIT_VALUE;
                op.bit[no].bitno = no%8;
                op.bit[no].value = 0;
            } else if(str[i].charAt(0) == '1') {
                op.bit[no].type  = AVR_CMDBIT_VALUE;
                op.bit[no].bitno = no%8;
                op.bit[no].value = 1;
            } else if(str[i].charAt(0) == 'i') {
                op.bit[no].type  = AVR_CMDBIT_INPUT;
                op.bit[no].bitno = no%8;
                op.bit[no].value = 0;
            } else if(str[i].charAt(0) == 'o') {
                op.bit[no].type  = AVR_CMDBIT_OUTPUT;
                op.bit[no].bitno = no%8;
                op.bit[no].value = 0;
            } else if(str[i].charAt(0) == 'a') {
                op.bit[no].type  = AVR_CMDBIT_ADDRESS;
                op.bit[no].bitno = Integer.valueOf(str[i].substring(1));
                op.bit[no].value = 0;
            } else if(str[i].charAt(0) == 'x') {
                op.bit[no].type  = AVR_CMDBIT_IGNORE;
                op.bit[no].bitno = no%8;
                op.bit[no].value = 0;
            }
            no--;
        }
    }

    class OPCODE {
        CMDBIT[] bit = new CMDBIT[32];
        OPCODE() {
            for(int i=0; i<32; i++) {
                bit[i] = new CMDBIT();
            }
        }
        class CMDBIT {
            public int type;
            public int bitno;
            public int value;
        }
    }
}

/*
typedef struct avrmem {
  char desc[AVR_MEMDESCLEN];  // memory description ("flash", "eeprom", etc)
  int paged;                  // page addressed (e.g. ATmega flash)
  int size;                   // total memory size in bytes
  int page_size;              // size of memory page (if page addressed)
  int num_pages;              // number of pages (if page addressed)
  unsigned int offset;        // offset in IO memory (ATxmega)
  int min_write_delay;        // microseconds
  int max_write_delay;        // microseconds
  int pwroff_after_write;     // after this memory type is written to,
                              // the device must be powered off and
                              // back on, see errata
                              // http://www.atmel.com/atmel/acrobat/doc1280.pdf
  unsigned char readback[2];  // polled read-back values

  int mode;                   // stk500 v2 xml file parameter
  int delay;                  // stk500 v2 xml file parameter
  int blocksize;              // stk500 v2 xml file parameter
  int readsize;               // stk500 v2 xml file parameter
  int pollindex;              // stk500 v2 xml file parameter

  unsigned char * buf;        // pointer to memory buffer
  OPCODE * op[AVR_OP_MAX];    // opcodes
} AVRMEM;

*/

public class AvrConf {
    @SuppressWarnings("unused")
    private static final String TAG = AvrConf.class.getSimpleName();

    public String   desc;
    public byte     stk500_devcode;
    public byte     pagel;
    public byte     bs2;
    public byte[]   signature;
    public boolean  has_jtag;

    public byte     timeout;
    public byte     stabdelay;
    public byte     cmdexedelay;
    public byte     synchloops;
    public byte     bytedelay;
    public byte     pollindex;
    public byte     pollvalue;
    public byte     predelay;
    public byte     postdelay;
    public byte     pollmethod;

//    public int reset_disposition;

    public AVRConfMemFuse   fuse;
    public AVRConfMemFuse   lfuse;
    public AVRConfMemFuse   hfuse;
    public AVRConfMemFuse   efuse;
    public AVRConfMemFuse   lock;
    public AVRConfMemFlash  flash;
    public AVRConfMemEEPROM eeprom;

    public AvrConf(Boards board) throws InterruptedException{
        if(board.chipType == Boards.ChipTypes.M168) {
            setATmega168();
        } else if(board.chipType == Boards.ChipTypes.M328P) {
            setATmega328P();
        } else if(board.chipType == Boards.ChipTypes.M1284P) {
            setATmega1284P();
        } else if(board.chipType == Boards.ChipTypes.M2560) {
            setATmega2560();
        } else {
            throw new IllegalArgumentException("not support AVR type.");
        }
    }

    private byte[] createSignature(int sig1, int sig2, int sig3) {
        return new byte[]{(byte)sig1, (byte)sig2, (byte) sig3};
    }

    private void setATmega2560(){
        desc            = "ATMEGA2560";
        signature       = createSignature(0x1e, 0x98, 0x01);
        has_jtag        = true;
        pagel           = (byte)0xD7;
        bs2             = (byte)0xA0;

        timeout         = (byte)200;
        stabdelay       = (byte)100;
        cmdexedelay     = (byte)25;
        synchloops      = (byte)32;
        bytedelay       = (byte)0;
        pollindex       = (byte)3;
        pollvalue       = (byte)0x53;
        predelay        = (byte)1;
        postdelay       = (byte)1;
        pollmethod      = (byte)1;

        flash = new AVRConfMemFlash(
                true,
                262144,
                256,
                1024,
                4500,
                4500,
                0x00,
                0x00,
                new String[] {  "   0   0   1   0     0   0   0   0",
                                " a15 a14 a13 a12   a11 a10  a9  a8",
                                "  a7  a6  a5  a4    a3  a2  a1  a0",
                                "   o   o   o   o     o   o   o   o"},

                new String[] {  "   0   0   1   0     1   0   0   0",
                                " a15 a14 a13 a12   a11 a10  a9  a8",
                                "  a7  a6  a5  a4    a3  a2  a1  a0",
                                "   o   o   o   o     o   o   o   o"},

                new String[] {  "   0   1   0   0     0   0   0   0",
                                "   x   x   x   x     x   x   x   x",
                                "   x  a6  a5  a4    a3  a2  a1  a0",
                                "   i   i   i   i     i   i   i   i"},

                new String[] {  "   0   1   0   0     1   0   0   0",
                                "   x   x   x   x     x   x   x   x",
                                "   x  a6  a5  a4    a3  a2  a1  a0",
                                "   i   i   i   i     i   i   i   i"},

                new String[] {  "   0   1   0   0     1   1   0   0",
                                " a15 a14 a13 a12   a11 a10  a9  a8",
                                "  a7   x   x   x     x   x   x   x",
                                "   x   x   x   x     x   x   x   x"},

                new String[] {  "   0   1   0   0     1   1   0   1",
                                "   0   0   0   0     0   0   0   0",
                                "   0   0   0   0     0   0   0 a16",
                                "   0   0   0   0     0   0   0   0"},
                0x41,
                10,
                256,
                256); 
    }

    private void setATmega1284P(){
        desc            = "ATMEGA1284P";
        has_jtag        = true;
        stk500_devcode  = (byte) 0x82;
        signature       = createSignature(0x1e, 0x97, 0x05);
        pagel           = (byte) 0xd7;
        bs2             = (byte) 0xa0;

        timeout         = (byte)200;
        stabdelay       = (byte)100;
        cmdexedelay     = (byte)25;
        synchloops      = (byte)32;
        bytedelay       = (byte)0;
        pollindex       = (byte)3;
        pollvalue       = (byte)0x53;
        predelay        = (byte)1;
        postdelay       = (byte)1;
        pollmethod      = (byte)1;

        eeprom = new AVRConfMemEEPROM(
                false,
                8,
                4096,
                9000,
                9000,
                0xff,
                0xff,
                new String[] {  " 1 0 1 0 0 0 0 0",
                        " 0 0 x x a11 a10 a9 a8",
                        " a7 a6 a5 a4 a3 a2 a1 a0",
                        " o o o o o o o o"},
                new String[] {  " 1 1 0 0 0 0 0 0",
                        " 0 0 x x a11 a10 a9 a8",
                        " a7 a6 a5 a4 a3 a2 a1 a0",
                        " i i i i i i i i"},
                new String[] {  " 1 1 0 0 0 0 0 1",
                        " 0 0 0 0 0 0 0 0",
                        " 0 0 0 0 0 a2 a1 a0",
                        " i i i i i i i i"},
                new String[] {  " 1 1 0 0 0 0 1 0",
                        " 0 0 x x a11 a10 a9 a8",
                        " a7 a6 a5 a4 a3 0 0 0",
                        " x x x x x x x x"},
                0x41,
                10,
                128,
                256);

        flash = new AVRConfMemFlash(
                true,
                131072,
                256,
                512,
                4500,
                4500,
                0xff,
                0xff,
                new String[] {  " 0 0 1 0 0 0 0 0",
                        " a15 a14 a13 a12 a11 a10 a9 a8",
                        " a7 a6 a5 a4 a3 a2 a1 a0",
                        " o o o o o o o o"},
                new String[] {  " 0 0 1 0 1 0 0 0",
                        " a15 a14 a13 a12 a11 a10 a9 a8",
                        " a7 a6 a5 a4 a3 a2 a1 a0",
                        " o o o o o o o o"},
                new String[] {  " 0 1 0 0 0 0 0 0",
                        " 0 0 x x x x x x",
                        " x a6 a5 a4 a3 a2 a1 a0",
                        " i i i i i i i i"},
                new String[] {  " 0 1 0 0 1 0 0 0",
                        " 0 0 x x x x x x",
                        " x a6 a5 a4 a3 a2 a1 a0",
                        " i i i i i i i i"},

                new String[] { " 0 1 0 0 1 1 0 0",
                        " a15 a14 a13 a12 a11 a10 a9 a8",
                        " a7 x x x x x x x",
                        " x x x x x x x x"},
                null,
                0x41,
                10,
                256,
                256);

        fuse = new AVRConfMemFuse(
                "",
                0,
                0,
                0,
                new String[]{   "",
                        ""},
                new String[]{   "",
                        ""});

        lfuse = new AVRConfMemFuse(
                "lfuse",
                1,
                9000,
                9000,
                new String[]{   "0 1 0 1 0 0 0 0 0 0 0 0 0 0 0 0",
                        "x x x x x x x x o o o o o o o o"},
                new String[]{   "1 0 1 0 1 1 0 0 1 0 1 0 0 0 0 0",
                        "x x x x x x x x i i i i i i i i"});

        hfuse = new AVRConfMemFuse(
                "hfuse",
                1,
                9000,
                9000,
                new String[]{   "0 1 0 1 1 0 0 0 0 0 0 0 1 0 0 0",
                        "x x x x x x x x o o o o o o o o"},
                new String[]{   "1 0 1 0 1 1 0 0 1 0 1 0 1 0 0 0",
                        "x x x x x x x x i i i i i i i i"});

        efuse = new AVRConfMemFuse(
                "efuse",
                1,
                9000,
                9000,
                new String[]{   "0 1 0 1 0 0 0 0 0 0 0 0 1 0 0 0",
                        "x x x x x x x x o o o o o o o o"},
                new String[]{   "1 0 1 0 1 1 0 0 1 0 1 0 0 1 0 0",
                        "x x x x x x x x 1 1 1 1 1 i i i"});

        lock = new AVRConfMemFuse(
                "lock",
                1,
                9000,
                9000,
                new String[]{   "0 1 0 1 1 0 0 0 0 0 0 0 0 0 0 0",
                        "x x x x x x x x x x o o  o o o o"},
                new String[]{   "1 0 1 0 1 1 0 0 1 1 1 x x x x x",
                        "x x x x x x x x 1 1 i i i i i i"});
    }

    private void setATmega328P(){
        desc            = "ATMEGA328P";
        stk500_devcode  = (byte) 0x86;
        pagel           = (byte) 0xd7;
        bs2             = (byte) 0xc2;
        signature       = createSignature(0x1e, 0x95, 0x0f);

        eeprom = new AVRConfMemEEPROM(
                false,
                4,
                1024,
                3600,
                3600,
                0xff,
                0xff,
                new String[] {  " 1 0 1 0 0 0 0 0",
                                " 0 0 0 x x x a9 a8",
                                " a7 a6 a5 a4 a3 a2 a1 a0",
                                " o o o o o o o o"},
                new String[] {  " 1 1 0 0 0 0 0 0",
                                " 0 0 0 x x x a9 a8",
                                " a7 a6 a5 a4 a3 a2 a1 a0",
                                " i i i i i i i i"},
                new String[] {  " 1 1 0 0 0 0 0 1",
                                " 0 0 0 0 0 0 0 0",
                                " 0 0 0 0 0 0 a1 a0",
                                " i i i i i i i i"},
                new String[] {  " 1 1 0 0 0 0 1 0",
                                " 0 0 x x x x a9 a8",
                                " a7 a6 a5 a4 a3 a2 0 0",
                                " x x x x x x x x"},
                0x41,
                20,
                4,
                256);

        flash = new AVRConfMemFlash(
                true,
                32768,
                128,
                256,
                4500,
                4500,
                0xff,
                0xff,
                new String[] {  " 0 0 1 0 0 0 0 0",
                                " 0 0 a13 a12 a11 a10 a9 a8",
                                " a7 a6 a5 a4 a3 a2 a1 a0",
                                " o o o o o o o o"},
                new String[] {  " 0 0 1 0 1 0 0 0",
                                " 0 0 a13 a12 a11 a10 a9 a8",
                                " a7 a6 a5 a4 a3 a2 a1 a0",
                                " o o o o o o o o"},
                new String[] {  " 0 1 0 0 0 0 0 0",
                                " 0 0 0 x x x x x",
                                " x x a5 a4 a3 a2 a1 a0",
                                " i i i i i i i i"},
                new String[] {  " 0 1 0 0 1 0 0 0",
                                " 0 0 0 x x x x x",
                                " x x a5 a4 a3 a2 a1 a0",
                                " i i i i i i i i"},
                new String[] {  " 0 1 0 0 1 1 0 0",
                                " 0 0 a13 a12 a11 a10 a9 a8",
                                " a7 a6 x x x x x x",
                                " x x x x x x x x"},
                null,
                0x41,
                6,
                128,
                256); 

        fuse = new AVRConfMemFuse(
                "",
                0,
                0,
                0,
                new String[]{   "",
                                ""},
                new String[]{   "",
                                ""});

        lfuse = new AVRConfMemFuse(
                "lfuse",
                1,
                4500,
                4500,
                new String[]{   "0 1 0 1 0 0 0 0 0 0 0 0 0 0 0 0",
                                "x x x x x x x x o o o o o o o o"},
                new String[]{   "1 0 1 0 1 1 0 0 1 0 1 0 0 0 0 0",
                                "x x x x x x x x i i i i i i i i"});

        hfuse = new AVRConfMemFuse(
                "hfuse",
                1,
                4500,
                4500,
                new String[]{   "0 1 0 1 1 0 0 0 0 0 0 0 1 0 0 0",
                                "x x x x x x x x o o o o o o o o"},
                new String[]{   "1 0 1 0 1 1 0 0 1 0 1 0 1 0 0 0",
                                "x x x x x x x x i i i i i i i i"});

        efuse = new AVRConfMemFuse(
                "efuse",
                1,
                4500,
                4500,
                new String[]{   "0 1 0 1 0 0 0 0 0 0 0 0 1 0 0 0",
                                "x x x x x x x x x x x x x o o o"},
                new String[]{   "1 0 1 0 1 1 0 0 1 0 1 0 0 1 0 0",
                                "x x x x x x x x x x x x x i i i"});

        lock = new AVRConfMemFuse(
                "lock",
                1,
                4500,
                4500,
                new String[]{   "0 1 0 1 1 0 0 0 0 0 0 0 0 0 0 0",
                                "x x x x x x x x x x o o o o o o"},
                new String[]{   "1 0 1 0 1 1 0 0 1 1 1 x x x x x",
                                "x x x x x x x x 1 1 i i i i i i"});
  }

    private void setATmega168() {
        desc = "ATMEGA168";
        stk500_devcode = (byte) 0x86;
        pagel = (byte) 0xd7;
        bs2 = (byte) 0xc2;
        signature = createSignature(0x1e, 0x94, 0x06);

        eeprom = new AVRConfMemEEPROM(false, 4, 512, 3600, 3600, 0xff, 0xff,
                new String[] { " 1 0 1 0 0 0 0 0", " 0 0 0 x x x x a8",
                        " a7 a6 a5 a4 a3 a2 a1 a0", " o o o o o o o o" },
                new String[] { " 1 1 0 0 0 0 0 0", " 0 0 0 x x x x a8",
                        " a7 a6 a5 a4 a3 a2 a1 a0", " i i i i i i i i" },
                new String[] { " 1 1 0 0 0 0 0 1", " 0 0 0 0 0 0 0 0",
                        " 0 0 0 0 0 0 a1 a0", " i i i i i i i i" },
                new String[] { " 1 1 0 0 0 0 1 0", " 0 0 x x x x x a8",
                        " a7 a6 a5 a4 a3 a2 0 0", " x x x x x x x x" }, 0x41,
                20, 4, 256);

        flash = new AVRConfMemFlash(true, 16384, 128, 256, 4500, 4500, 0xff,
                0xff,
                new String[] { " 0  0  1  0  0  0  0  0", "  0  0  0 a12 a11 a10 a9 a8",
                               " a7 a6 a5 a4 a3 a2 a1 a0", "  o  o  o  o  o  o  o  o" },
                new String[] { " 0 0 1 0 1 0 0 0", " 0 0 0 a12 a11 a10 a9 a8",
                                " a7 a6 a5 a4 a3 a2 a1 a0", " o o o o o o o o" },
                new String[] { " 0 1 0 0 0 0 0 0", " 0 0 0 x x x x x",
                                " x x a5 a4 a3 a2 a1 a0", " i i i i i i i i" },
                new String[] { " 0 1 0 0 1 0 0 0", " 0 0 0 x x x x x",
                                " x x a5 a4 a3 a2 a1 a0", " i i i i i i i i" },
                new String[] { " 0 1 0 0 1 1 0 0", " 0 0 0 a12 a11 a10 a9 a8",
                                " a7 a6 x x x x x x", " x x x x x x x x" }, null, 0x41, 6, 128, 256);

        fuse = new AVRConfMemFuse("", 0, 0, 0, new String[] { "", "" },
                new String[] { "", "" });

        lfuse = new AVRConfMemFuse("lfuse", 1, 4500, 4500, new String[] {
                "0 1 0 1 0 0 0 0 0 0 0 0 0 0 0 0",
                "x x x x x x x x o o o o o o o o" }, new String[] {
                "1 0 1 0 1 1 0 0 1 0 1 0 0 0 0 0",
                "x x x x x x x x i i i i i i i i" });

        hfuse = new AVRConfMemFuse("hfuse", 1, 4500, 4500, new String[] {
                "0 1 0 1 1 0 0 0 0 0 0 0 1 0 0 0",
                "x x x x x x x x o o o o o o o o" }, new String[] {
                "1 0 1 0 1 1 0 0 1 0 1 0 1 0 0 0",
                "x x x x x x x x i i i i i i i i" });

        efuse = new AVRConfMemFuse("efuse", 1, 4500, 4500, new String[] {
                "0 1 0 1 0 0 0 0 0 0 0 0 1 0 0 0",
                "x x x x x x x x x x x x x o o o" }, new String[] {
                "1 0 1 0 1 1 0 0 1 0 1 0 0 1 0 0",
                "x x x x x x x x x x x x x i i i" });

        lock = new AVRConfMemFuse("lock", 1, 4500, 4500, new String[] {
                "0 1 0 1 1 0 0 0 0 0 0 0 0 0 0 0",
                "x x x x x x x x x x o o o o o o" }, new String[] {
                "1 0 1 0 1 1 0 0 1 1 1 x x x x x",
                "x x x x x x x x 1 1 i i i i i i" });
    }
}
/*
#------------------------------------------------------------
# ATmega328P
#------------------------------------------------------------

part
    id                  = "m328p";
    desc                = "ATMEGA328P";
    has_debugwire       = yes;
    flash_instr         = 0xB6, 0x01, 0x11;
    eeprom_instr        = 0xBD, 0xF2, 0xBD, 0xE1, 0xBB, 0xCF, 0xB4, 0x00,
                          0xBE, 0x01, 0xB6, 0x01, 0xBC, 0x00, 0xBB, 0xBF,
                          0x99, 0xF9, 0xBB, 0xAF;
    stk500_devcode      = 0x86;
    # avr910_devcode    = 0x;
    signature           = 0x1e 0x95 0x0F;
    pagel               = 0xd7;
    bs2                 = 0xc2;
    chip_erase_delay    = 9000;
    pgm_enable = "1 0 1 0 1 1 0 0 0 1 0 1 0 0 1 1",
                 "x x x x x x x x x x x x x x x x";
                          
    chip_erase = "1 0 1 0 1 1 0 0 1 0 0 x x x x x",
                 "x x x x x x x x x x x x x x x x";
                          
    timeout     = 200;
    stabdelay   = 100;
    cmdexedelay = 25;
    synchloops  = 32;   
    bytedelay   = 0;
    pollindex   = 3;
    pollvalue   = 0x53; 
    predelay    = 1;      
    postdelay   = 1;
    pollmethod  = 1;      
                          
    pp_controlstack =
        0x0E, 0x1E, 0x0F, 0x1F, 0x2E, 0x3E, 0x2F, 0x3F,
        0x4E, 0x5E, 0x4F, 0x5F, 0x6E, 0x7E, 0x6F, 0x7F,
        0x66, 0x76, 0x67, 0x77, 0x6A, 0x7A, 0x6B, 0x7B,
        0xBE, 0xFD, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00;
    hventerstabdelay    = 100;
    progmodedelay       = 0;
    latchcycles         = 5;
    togglevtg           = 1;
    poweroffdelay       = 15;
    resetdelayms        = 1;
    resetdelayus        = 0;
    hvleavestabdelay    = 15;
    resetdelay          = 15;
    chiperasepulsewidth = 0;
    chiperasepolltimeout = 10;
    programfusepulsewidth = 0;
    programfusepolltimeout = 5;
    programlockpulsewidth = 0;
    programlockpolltimeout = 5;
    
    memory "eeprom"     
        paged           = no;
        page_size       = 4;
        size            = 1024; 
        min_write_delay = 3600; 
        max_write_delay = 3600; 
        readback_p1     = 0xff;
        readback_p2     = 0xff;
        read = " 1 0 1 0 0 0 0 0",
               " 0 0 0 x x x a9 a8",
               " a7 a6 a5 a4 a3 a2 a1 a0",
               " o o o o o o o o";
       
        write = " 1 1 0 0 0 0 0 0",
                " 0 0 0 x x x a9 a8",
                " a7 a6 a5 a4 a3 a2 a1 a0",
                " i i i i i i i i";

        loadpage_lo = " 1 1 0 0 0 0 0 1",
                      " 0 0 0 0 0 0 0 0",
                      " 0 0 0 0 0 0 a1 a0",
                      " i i i i i i i i";
    
        writepage = " 1 1 0 0 0 0 1 0",
                    " 0 0 x x x x a9 a8",
                    " a7 a6 a5 a4 a3 a2 0 0",
                    " x x x x x x x x";
       
        mode            = 0x41;
        delay           = 20;
        blocksize       = 4;
        readsize        = 256;
    ;

    memory "flash"
        paged           = yes;
        size            = 32768;
        page_size       = 128;
        num_pages       = 256;
        min_write_delay = 4500;
        max_write_delay = 4500;
        readback_p1     = 0xff;
        readback_p2     = 0xff;
        read_lo = " 0 0 1 0 0 0 0 0",
                  " 0 0 a13 a12 a11 a10 a9 a8",
                  " a7 a6 a5 a4 a3 a2 a1 a0",
                  " o o o o o o o o";

        read_hi = " 0 0 1 0 1 0 0 0",
                  " 0 0 a13 a12 a11 a10 a9 a8",
                  " a7 a6 a5 a4 a3 a2 a1 a0",
                  " o o o o o o o o";

        loadpage_lo = " 0 1 0 0 0 0 0 0",
                      " 0 0 0 x x x x x",
                      " x x a5 a4 a3 a2 a1 a0",
                      " i i i i i i i i";

        loadpage_hi = " 0 1 0 0 1 0 0 0",
                      " 0 0 0 x x x x x",
                      " x x a5 a4 a3 a2 a1 a0",
                      " i i i i i i i i";

        writepage = " 0 1 0 0 1 1 0 0",
                    " 0 0 a13 a12 a11 a10 a9 a8",
                    " a7 a6 x x x x x x",
                    " x x x x x x x x";

        mode            = 0x41;
        delay           = 6;
        blocksize       = 128;
        readsize        = 256;

    ;

    memory "lfuse"
        size = 1;
        min_write_delay = 4500;
        max_write_delay = 4500;
        read = "0 1 0 1 0 0 0 0 0 0 0 0 0 0 0 0",
               "x x x x x x x x o o o o o o o o";

        write = "1 0 1 0 1 1 0 0 1 0 1 0 0 0 0 0",
                "x x x x x x x x i i i i i i i i";
    ;

    memory "hfuse"
        size = 1;
        min_write_delay = 4500;
        max_write_delay = 4500;
        read = "0 1 0 1 1 0 0 0 0 0 0 0 1 0 0 0",
               "x x x x x x x x o o o o o o o o";

        write = "1 0 1 0 1 1 0 0 1 0 1 0 1 0 0 0",
                "x x x x x x x x i i i i i i i i";
    ;

    memory "efuse"
        size = 1;
        min_write_delay = 4500;
        max_write_delay = 4500;
        read = "0 1 0 1 0 0 0 0 0 0 0 0 1 0 0 0",
               "x x x x x x x x x x x x x o o o";

        write = "1 0 1 0 1 1 0 0 1 0 1 0 0 1 0 0",
                "x x x x x x x x x x x x x i i i";
    ;

    memory "lock"
        size = 1;
        min_write_delay = 4500;
        max_write_delay = 4500;
        read = "0 1 0 1 1 0 0 0 0 0 0 0 0 0 0 0",
               "x x x x x x x x x x o o o o o o";

        write = "1 0 1 0 1 1 0 0 1 1 1 x x x x x",
                "x x x x x x x x 1 1 i i i i i i";
    ;

    memory "calibration"
        size = 1;
        read = "0 0 1 1 1 0 0 0 0 0 0 x x x x x",
               "0 0 0 0 0 0 0 0 o o o o o o o o";
    ;
    memory "signature"
        size = 3;
        read = "0 0 1 1 0 0 0 0 0 0 0 x x x x x",
               "x x x x x x a1 a0 o o o o o o o o";
    ;
;


*/