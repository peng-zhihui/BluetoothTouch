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

package com.physicaloid.lib.programmer.avr;

import cz.jaybee.intelhex.IntelHexParser;
import cz.jaybee.intelhex.IntelHexParserRun;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class IntelHexFileToBuf {

    @SuppressWarnings("unused")
    private static final String TAG = IntelHexFileToBuf.class.getSimpleName();

    IntelHexParser ihp;
    IntelHexParserRun ihpd;

    public IntelHexFileToBuf() {
    }

    public long getByteLength() {
        if(ihpd != null) {
            return ihpd.getTotalBufLength();
        } else {
            return 0;
        }
    }

    public void getHexData(byte[] buf) {
        if(ihpd != null) {
            ihpd.getBufData(buf);
        }
    }

    public void parse(String filePath) throws FileNotFoundException, IOException, Exception {
        InputStream is = null;
        is = new FileInputStream(filePath);
        parse(is);
    }

    public void parse(InputStream is) throws FileNotFoundException, IOException, Exception {

        ihp = new IntelHexParser(is);
        ihpd = new IntelHexParserRun(0, 0xFFFFFF);
        ihp.setDataListener(ihpd);

        ihp.parse();

        is.close();

    }

}
