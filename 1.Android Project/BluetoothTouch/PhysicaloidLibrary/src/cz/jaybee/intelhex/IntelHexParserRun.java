/**
 * @license
 * Copyright (c) 2012, Jan Breuer
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this 
 * list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation 
 * and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */
package cz.jaybee.intelhex;

import java.util.Arrays;

/**
 *
 * @author Jan Breuer
 * @license BSD 2-Clause 
 */
public class IntelHexParserRun implements IntelHexDataListener {

    private long addressStart;
    private long addressStop;
    private long length;
    private long totalLength;
    private byte[] buffer;
    private boolean eofDone = false;

    public IntelHexParserRun(long addressStart, long addressStop) {
        this.addressStart = addressStart;
        this.addressStop = addressStop;
        this.length = (addressStop - addressStart + 1);
        this.totalLength = 0;
        this.buffer = new byte[(int) length];
        Arrays.fill(buffer, (byte) 0xFF);
        eofDone = false;
    }

    public void getBufData(byte[] buf){
        int copyLen = (int)length;
        if(copyLen > buf.length) {
            copyLen = buf.length;
        }
        System.arraycopy(buffer, 0, buf, 0, copyLen);
    }

    public long getTotalBufLength() {
        return totalLength;
    }

    public boolean isEOF() {
        return eofDone;
    }

    @Override
    public void data(long address, byte[] data) {
        if ((address >= addressStart) && (address <= addressStop)) {
            int length = data.length;
            if ((address + length) > addressStop) {
                length = (int) (addressStop - address + 1);
            }
            System.arraycopy(data, 0, buffer, (int) (address - addressStart), length);
            totalLength += length;
        }
    }

    @Override
    public void eof() {
        eofDone = true;
    }
}
