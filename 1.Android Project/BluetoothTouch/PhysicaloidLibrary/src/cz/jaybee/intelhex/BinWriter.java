/**
 * @license Copyright (c) 2015, Jan Breuer All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
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

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Binary file writer
 *
 * @author Jan Breuer
 * @license BSD 2-Clause
 */
public class BinWriter implements IntelHexDataListener {

    private final Region outputRegion;
    private final OutputStream destination;
    private final byte[] buffer;
    private final MemoryRegions regions;
    private long maxAddress;
    private final boolean minimize;

    public BinWriter(Region outputRegion, OutputStream destination, boolean minimize) {
        this.outputRegion = outputRegion;
        this.destination = destination;
        this.minimize = minimize;
        this.buffer = new byte[(int) (outputRegion.getLength())];
        Arrays.fill(buffer, (byte) 0xFF);
        regions = new MemoryRegions();
        maxAddress = outputRegion.getAddressStart();
    }

    @Override
    public void data(long address, byte[] data) {
        regions.add(address, data.length);

        if ((address >= outputRegion.getAddressStart()) && (address <= outputRegion.getAddressEnd())) {
            int length = data.length;
            if ((address + length) > outputRegion.getAddressEnd()) {
                length = (int) (outputRegion.getAddressEnd() - address + 1);
            }
            System.arraycopy(data, 0, buffer, (int) (address - outputRegion.getAddressStart()), length);
            
            if (maxAddress < (address + data.length -1)) {
                maxAddress = address + data.length - 1;
            }
        }
    }

    @Override
    public void eof() {       
        try {
            if (!minimize) {
                maxAddress = outputRegion.getAddressEnd();
            }
            destination.write(buffer, 0, (int)(maxAddress - outputRegion.getAddressStart() + 1));
        } catch (IOException ex) {
            Logger.getLogger(BinWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public MemoryRegions getMemoryRegions() {
        return regions;
    }    
}
