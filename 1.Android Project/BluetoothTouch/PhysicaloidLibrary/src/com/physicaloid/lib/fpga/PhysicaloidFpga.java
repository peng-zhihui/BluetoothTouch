package com.physicaloid.lib.fpga;

import android.content.Context;

import com.physicaloid.lib.Physicaloid;
import com.physicaloid.BuildConfig;

public class PhysicaloidFpga extends Physicaloid {
    @SuppressWarnings("unused")
    private static final boolean DEBUG_SHOW = false && BuildConfig.DEBUG;
    @SuppressWarnings("unused")
    private static final String TAG = PhysicaloidFpga.class.getSimpleName();

    PhysicaloidFpgaPacketFilter mFilter;

    public PhysicaloidFpga(Context context) {
        super(context);
        mFilter = new PhysicaloidFpgaPacketFilter();
    }

    @Override
    public int write(byte[] buf) throws RuntimeException {
        return this.write(buf, buf.length);
    }

    @Override
    public int write(byte[] buf, int size) throws RuntimeException {
        return this.write(buf, 0, size);
    }

    public int write(byte[] buf, int offset, int size) throws RuntimeException {
        synchronized (LOCK_WRITE){
            if(mSerial == null) return 0;
            if(mFilter == null) return 0;
            return mFilter.writeWithEscape(mSerial, buf, offset, size);
        }
    }

    public int writeWithoutEscape(byte[] buf, int size) throws RuntimeException {
//        if(DEBUG_SHOW) { Log.d(TAG, "write wo E ("+size+") : "+toHexStr(buf, buf.length)); }
        return super.write(buf, size);
    }

}
