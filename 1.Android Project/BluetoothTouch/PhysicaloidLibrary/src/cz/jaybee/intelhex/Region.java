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

/**
 * One memory region
 * 
 * @author Jan Breuer
 * @license BSD 2-Clause
 */
public class Region implements Comparable<Region> {

    private long addressStart;
    private long addressEnd;

    public Region(long start, long length) {
        this.addressStart = start;
        this.addressEnd = start + length - 1;
    }

    /**
     * Get length of the region
     * @return 
     */
    public long getLength() {
        return addressEnd - addressStart + 1;
    }
    
    /**
     * Return last address in memory region
     * @return 
     */
    public long getAddressEnd() {
        return addressEnd;
    }

    /**
     * Set end address
     * @param addressEnd 
     */
    public void setAddressEnd(long addressEnd) {
        this.addressEnd = addressEnd;
    }
    
    /**
     * Get start address of the region
     * @return 
     */    
    public long getAddressStart() {
        return addressStart;
    }
    
    /**
     * Set start address
     * @param addressStart 
     */
    public void setAddressStart(long addressStart) {
        this.addressStart = addressStart;
    }

    /**
     * Increment length of the region by value
     * @param value 
     */
    void incLength(long value) {
        addressEnd += value;
    }

    @Override
    public String toString() {
        return String.format("0x%08x:0x%08x (%dB 0x%08X)", addressStart, addressEnd, getLength(), getLength());
    }

    /**
     * Compare, if one region is after another region
     * 
     * @param o
     * @return 
     */
    @Override
    public int compareTo(Region o) {
        if (this.addressStart == o.addressStart) {
            return Long.compare(this.addressEnd, o.addressEnd);
        } else {
            return Long.compare(this.addressStart, o.addressStart);
        }
    }
}
