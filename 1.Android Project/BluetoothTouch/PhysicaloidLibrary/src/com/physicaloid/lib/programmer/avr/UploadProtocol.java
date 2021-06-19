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

import com.physicaloid.lib.Physicaloid.UploadCallBack;
import com.physicaloid.lib.framework.SerialCommunicator;

public abstract class UploadProtocol {
    @SuppressWarnings("unused")
    private static final String TAG = UploadProtocol.class.getSimpleName();

    UploadCallBack callback;
    public UploadProtocol(){};

    public abstract void setSerial(SerialCommunicator comm);
    public abstract void setConfig(AvrConf conf, AVRMem mem);
    public abstract int  open();
    public abstract void enable();
    public abstract int  initialize();
    public abstract int  check_sig_bytes();
    public abstract int  paged_write();
    public abstract void disable();

    public void setCallback(UploadCallBack callback) {
        this.callback = callback;
    }

    protected void report_progress(int prog) {
        if(prog > 100) { prog = 100; }
        if(callback == null) return;
        callback.onUploading(prog);
    }

    protected void report_cancel() {
        if(callback == null) return;
        callback.onCancel();
    }
}
