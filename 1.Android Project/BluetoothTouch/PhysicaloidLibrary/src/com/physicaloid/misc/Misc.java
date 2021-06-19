package com.physicaloid.misc;

public class Misc {
    static public String toHexStr(byte[] b, int length) {
        String str="";
        for(int i=0; i<length; i++) {
            str += String.format("%02x ", b[i]);
        }
        return str;
    }
}
