package com.physicaloid.lib.fpga;

import android.util.Log;

import com.physicaloid.BuildConfig;
import com.physicaloid.lib.framework.SerialCommunicator;

public class PhysicaloidFpgaPacketFilter {
    @SuppressWarnings("unused")
    private static final boolean DEBUG_SHOW = false && BuildConfig.DEBUG;
    private static final String TAG = PhysicaloidFpgaPacketFilter.class.getSimpleName();

    public int writeWithEscape(SerialCommunicator comm, byte[] buf, int size) {
        return writeWithEscape(comm, buf, 0, size);
    }

    public int writeWithEscape(SerialCommunicator comm, byte[] buf, int offset, int size) {
        if(comm == null) return 0;
        if(buf == null) return 0;

        int totalWrittenSize=0;

        byte[] packet = createEscapedPacket(buf, offset, size);
        if(DEBUG_SHOW) { Log.d(TAG, "write("+packet.length+") : "+toHexStr(packet, packet.length)); }

        totalWrittenSize = comm.write(packet, packet.length);

        while(totalWrittenSize<packet.length) {
            int remainingSize = packet.length - totalWrittenSize;
            byte[] tmpBuf = new byte[remainingSize];
            System.arraycopy(packet, totalWrittenSize, tmpBuf, 0, remainingSize);
            totalWrittenSize += comm.write(tmpBuf, tmpBuf.length);
        }

        return size;
    }

    public byte[] createEscapedPacket(byte[] buf, int offset, int size) {
        if(buf == null) return null;
        int bufPointer=0;
        int escapedBufPointer=0;
        byte[] escapedPacket = new byte[buf.length*2];

        for(int i=0; i<size; i++) {
            bufPointer = i+offset;
            escapedPacket[escapedBufPointer] = buf[bufPointer];
            escapedBufPointer++;
            if((buf[bufPointer] == PhysicaloidFpgaConst.COMMAND_BYTE) || buf[bufPointer] == PhysicaloidFpgaConst.ESCAPE_BYTE) {
                escapedPacket[escapedBufPointer-1]  = PhysicaloidFpgaConst.ESCAPE_BYTE;
                escapedPacket[escapedBufPointer]    = (byte)(buf[bufPointer] ^ (byte)0x20);
//                if(DEBUG_SHOW){Log.d(TAG, "Escape Char : 0x"+Integer.toHexString(buf[bufPointer])+", 0x"+Integer.toHexString(escapedPacket[escapedBufPointer]));}
                escapedBufPointer++;
            }
        }

        byte[] packet = new byte[escapedBufPointer];
        System.arraycopy(escapedPacket, 0, packet, 0, escapedBufPointer);
        return packet;
    }


    private String toHexStr(byte[] b, int length) {
        String str="";
        for(int i=0; i<length; i++) {
            str += String.format("%02x ", b[i]);
        }
        return str;
    }
}
