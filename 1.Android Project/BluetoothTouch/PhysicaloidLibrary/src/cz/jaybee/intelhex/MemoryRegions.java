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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Class to hold all memory address regions
 * 
 * @author Jan Breuer
 * @author riilabs
 * @license BSD 2-Clause
 */
public class MemoryRegions {

    private final List<Region> regions = new ArrayList<>();

    public void add(long start, long length) {
        Region prevRegion;
        if (regions.size() > 0) {
            prevRegion = regions.get(regions.size() - 1);
            long nextAddress = prevRegion.getAddressStart() + prevRegion.getLength();
            if (nextAddress == start) {
                prevRegion.incLength(length);
                return;
            }
        }
        regions.add(new Region(start, length));
    }

    public void compact() {
        Collections.sort(regions);

        Iterator<Region> iter = regions.iterator();
        Region prev = null;
        while (iter.hasNext()) {
            Region curr = iter.next();
            if (prev == null) {
                prev = curr;
            } else {
                // check for chaining
                if (curr.getAddressStart() == (prev.getAddressStart() + prev.getLength())) {
                    prev.incLength(curr.getLength());
                    iter.remove();
                } else {
                    prev = curr;
                }
            }
        }
    }
    
    public void clear() {
        regions.clear();
    }

    public int size() {
        return regions.size();
    }
    
    public Region get(int index) {
        return regions.get(index);
    }
    
    public Region getFullRangeRegion() {
        long start = 0;
        long length = 0;
        if (!regions.isEmpty()) {
            start = regions.get(0).getAddressStart();
            Region last = regions.get(regions.size() - 1);
            length = last.getAddressStart() + last.getLength() - start;
        }

        return new Region(start, length);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Region r : regions) {
            sb.append(r).append("\r\n");
        }

        return sb.toString();
    }
}
