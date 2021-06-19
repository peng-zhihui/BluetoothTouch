package com.physicaloid.lib.fpga;

public class PhysicaloidFpgaConst {
    public static final byte COMMAND_BYTE  = 0x3A;
    public static final byte ESCAPE_BYTE   = 0x3D;

    public static final byte CMD_BASE      = 0x30;
    public static final byte CMD_NCONFIG   = 0x01;
    public static final byte CMD_USERMODE  = 0x08;

    public static final byte CMD_RET_MSEL_AS   = 0x01;
    public static final byte CMD_RET_NSTATUS   = 0x02;
    public static final byte CMD_RET_CONF_DONE = 0x04;
    public static final byte CMD_RET_TIMEOUT   = 0x08;
}
