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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class to demonstrate usage of Intel HEX parser
 *
 * @author Jan Breuer
 * @license BSD 2-Clause
 */
public class IntelHexParserDemo {

    /**
     * Convert Intel HEX to bin
     *
     * usage:
     *
     * IntelHexParserDemo {source} {target}
     *
     * IntelHexParserDemo {source} {target} {address_from} {address_to}
     * 
     * {source} is source Intel HEX file name
     *
     * {target} is target BIN file name
     *
     * {address_from} is start address e.g. 0x1D000000 or min
     *
     * {address_to} is end address e.g. 0x1D07FFFF or max
     *
     * if no address_from and address_to is specified, maximum range is used
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String fileIn = "Application.hex";
        String fileOut = "Application.bin";
        String dataFrom = "min";
        String dataTo = "max";
        boolean minimize = false;

        if (args.length == 0) {
            System.out.println("usage:");
            System.out.println("    hex2bin <hex> <bin> <start address> <end address> [minimize]");            
            System.out.println();
            System.out.println("    full address range of app.hex");
            System.out.println("        hex2bin app.hex app.bin");
            System.out.println();
            System.out.println("    limited exact address range of app.hex, undefined data are 0xff");
            System.out.println("        hex2bin app.hex app.bin 0x0000 0x1fff");
            System.out.println();
            System.out.println("    limited minimal address range of app.hex, start at 0x0000,");
            System.out.println("    max address is 0x1fff, but can be lower");
            System.out.println("        hex2bin app.hex app.bin 0x0000 0x1fff minimize");
            return;
        }
        
        if (args.length >= 1) {
            fileIn = args[0];
        }

        if (args.length >= 2) {
            fileOut = args[1];
        }

        if (args.length >= 3) {
            dataFrom = args[2];
        }

        if (args.length >= 4) {
            dataTo = args[3];
        }
        
        if (args.length >=5 ) {
            if (args[4].equals("minimize")) {
                minimize = true;
            }
        }

        try (FileInputStream is = new FileInputStream(fileIn)) {
            OutputStream os = new FileOutputStream(fileOut);
            // init parser
            IntelHexParser parser = new IntelHexParser(is);

            // 1st iteration - calculate maximum output range            
            RangeDetector rangeDetector = new RangeDetector();
            parser.setDataListener(rangeDetector);
            parser.parse();
            is.getChannel().position(0);
            Region outputRegion = rangeDetector.getFullRangeRegion();
          
            // if address parameter is "max", calculate maximum memory region
            if (!("min".equals(dataFrom))) {
                outputRegion.setAddressStart(Long.parseLong(dataFrom.substring(2), 16));
            } 
            if (!("max".equals(dataTo))) {
                outputRegion.setAddressEnd(Long.parseLong(dataTo.substring(2), 16));
            }

            // 2nd iteration - actual write of the output
            BinWriter writer = new BinWriter(outputRegion, os, minimize);
            parser.setDataListener(writer);
            parser.parse();

            // print statistics
            System.out.printf("Program start address 0x%08X\r\n", parser.getStartAddress());
            System.out.println("Memory regions: ");
            System.out.println(writer.getMemoryRegions());

            System.out.print("Written output: ");
            System.out.println(outputRegion);
            

        } catch (IntelHexException | IOException ex) {
            Logger.getLogger(IntelHexParserDemo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
