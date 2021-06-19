package com.physicaloid.lib.usb.driver.uart;

import android.content.Context;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.util.Log;

import com.physicaloid.BuildConfig;
import com.physicaloid.lib.UsbVidList;
import com.physicaloid.lib.framework.SerialCommunicator;
import com.physicaloid.lib.usb.UsbCdcConnection;
import com.physicaloid.lib.usb.UsbVidPid;
import com.physicaloid.misc.RingBuffer;

import java.util.ArrayList;
import java.util.List;

public class UartCp210x extends SerialCommunicator{

    private static final String TAG = UartCp210x.class.getSimpleName();

    private static final boolean DEBUG_SHOW = false && BuildConfig.DEBUG;
    private static final int DEFAULT_BAUDRATE = 9600;

    private UsbCdcConnection mUsbConnetionManager;

    private UartConfig mUartConfig;
    private static final int RING_BUFFER_SIZE       = 1024;
    private static final int USB_READ_BUFFER_SIZE   = 256;
    private static final int USB_WRITE_BUFFER_SIZE  = 256;
    private RingBuffer mBuffer;

    private boolean mReadThreadStop = true;

    private UsbDeviceConnection mConnection;
    private UsbEndpoint mEndpointIn;
    private UsbEndpoint mEndpointOut;

    private boolean isOpened;

    /* Config request types */
    private static final byte REQTYPE_HOST_TO_INTERFACE = (byte)0x41;
    private static final byte REQTYPE_INTERFACE_TO_HOST = (byte)0xc1;
    @SuppressWarnings("unused")
    private static final byte REQTYPE_HOST_TO_DEVICE    = (byte)0x40;
    @SuppressWarnings("unused")
    private static final byte REQTYPE_DEVICE_TO_HOST    = (byte)0xc0;

    /* Config request codes */
    private static final byte CP210X_IFC_ENABLE         = 0x00;
    @SuppressWarnings("unused")
    private static final byte CP210X_SET_BAUDDIV        = 0x01;
    @SuppressWarnings("unused")
    private static final byte CP210X_GET_BAUDDIV        = 0x02;
    @SuppressWarnings("unused")
    private static final byte CP210X_SET_LINE_CTL       = 0x03;
    private static final byte CP210X_GET_LINE_CTL       = 0x04;
    @SuppressWarnings("unused")
    private static final byte CP210X_SET_BREAK          = 0x05;
    @SuppressWarnings("unused")
    private static final byte CP210X_IMM_CHAR           = 0x06;
    private static final byte CP210X_SET_MHS            = 0x07;
    @SuppressWarnings("unused")
    private static final byte CP210X_GET_MDMSTS         = 0x08;
    @SuppressWarnings("unused")
    private static final byte CP210X_SET_XON            = 0x09;
    @SuppressWarnings("unused")
    private static final byte CP210X_SET_XOFF           = 0x0A;
    @SuppressWarnings("unused")
    private static final byte CP210X_SET_EVENTMASK      = 0x0B;
    @SuppressWarnings("unused")
    private static final byte CP210X_GET_EVENTMASK      = 0x0C;
    @SuppressWarnings("unused")
    private static final byte CP210X_SET_CHAR           = 0x0D;
    @SuppressWarnings("unused")
    private static final byte CP210X_GET_CHARS          = 0x0E;
    @SuppressWarnings("unused")
    private static final byte CP210X_GET_PROPS          = 0x0F;
    @SuppressWarnings("unused")
    private static final byte CP210X_GET_COMM_STATUS    = 0x10;
    @SuppressWarnings("unused")
    private static final byte CP210X_RESET              = 0x11;
    @SuppressWarnings("unused")
    private static final byte CP210X_PURGE              = 0x12;
    @SuppressWarnings("unused")
    private static final byte CP210X_SET_FLOW           = 0x13;
    @SuppressWarnings("unused")
    private static final byte CP210X_GET_FLOW           = 0x14;
    @SuppressWarnings("unused")
    private static final byte CP210X_EMBED_EVENTS       = 0x15;
    @SuppressWarnings("unused")
    private static final byte CP210X_GET_EVENTSTATE     = 0x16;
    @SuppressWarnings("unused")
    private static final byte CP210X_SET_CHARS          = 0x19;
    @SuppressWarnings("unused")
    private static final byte CP210X_GET_BAUDRATE       = 0x1D;
    private static final byte CP210X_SET_BAUDRATE       = 0x1E;

    /* CP210X_IFC_ENABLE */
    private static final int UART_ENABLE                = 0x0001;
    private static final int UART_DISABLE               = 0x0000;

    /* CP210X_(SET|GET)_BAUDDIV */
    @SuppressWarnings("unused")
    private static final int BAUD_RATE_GEN_FREQ         = 0x384000;

    /* CP210X_(SET|GET)_LINE_CTL */
    private static final int BITS_DATA_MASK             = 0x0f00;
    @SuppressWarnings("unused")
    private static final int BITS_DATA_5                = 0x0500;
    @SuppressWarnings("unused")
    private static final int BITS_DATA_6                = 0x0600;;
    private static final int BITS_DATA_7                = 0x0700;
    private static final int BITS_DATA_8                = 0x0800;
    @SuppressWarnings("unused")
    private static final int BITS_DATA_9                = 0x0900;

    private static final int BITS_PARITY_MASK           = 0x00f0;
    private static final int BITS_PARITY_NONE           = 0x0000;
    private static final int BITS_PARITY_ODD            = 0x0010;
    private static final int BITS_PARITY_EVEN           = 0x0020;
    private static final int BITS_PARITY_MARK           = 0x0030;
    private static final int BITS_PARITY_SPACE          = 0x0040;

    private static final int BITS_STOP_MASK             = 0x000f;
    private static final int BITS_STOP_1                = 0x0000;
    private static final int BITS_STOP_1_5              = 0x0001;
    private static final int BITS_STOP_2                = 0x0002;

    /* CP210X_SET_BREAK */
    @SuppressWarnings("unused")
    private static final int BREAK_ON                   = 0x0001;
    @SuppressWarnings("unused")
    private static final int BREAK_OFF                  = 0x0000;

    /* CP210X_(SET_MHS|GET_MDMSTS) */
    private static final int CONTROL_DTR                = 0x0001;
    private static final int CONTROL_RTS                = 0x0002;
    @SuppressWarnings("unused")
    private static final int CONTROL_CTS                = 0x0010;
    @SuppressWarnings("unused")
    private static final int CONTROL_DSR                = 0x0020;
    @SuppressWarnings("unused")
    private static final int CONTROL_RING               = 0x0040;
    @SuppressWarnings("unused")
    private static final int CONTROL_DCD                = 0x0080;
    private static final int CONTROL_WRITE_DTR          = 0x0100;
    private static final int CONTROL_WRITE_RTS          = 0x0200;

    public UartCp210x(Context context) {
        super(context);
        mUsbConnetionManager = new UsbCdcConnection(context);
        mUartConfig = new UartConfig();
        mBuffer = new RingBuffer(RING_BUFFER_SIZE);
        isOpened = false;
    }

    @Override
    public boolean open() {
        for(UsbVidList id : UsbVidList.values()) {
            if(open(new UsbVidPid(id.getVid(), 0))){
                return true;
            }
        }
        return false;
    }

    public boolean open(UsbVidPid ids) {
        if(mUsbConnetionManager.open(ids)) {
            mConnection     = mUsbConnetionManager.getConnection();
            mEndpointIn     = mUsbConnetionManager.getEndpointIn();
            mEndpointOut    = mUsbConnetionManager.getEndpointOut();
            if(!init()) { return false; }
            if(!setBaudrate(DEFAULT_BAUDRATE)) {return false;}
            mBuffer.clear();
            startRead();
            isOpened = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean close() {
        stopRead();
        isOpened = false;
        cp210xUsbDisable();
        return mUsbConnetionManager.close();
    }

    @Override
    public int read(byte[] buf, int size) {
        return mBuffer.get(buf, size);
    }

    @Override
    public int write(byte[] buf, int size) {
        if(buf == null) { return 0; }
        int offset = 0;
        int write_size;
        int written_size;
        byte[] wbuf = new byte[USB_WRITE_BUFFER_SIZE];

        while (offset < size) {
            write_size = USB_WRITE_BUFFER_SIZE;

            if (offset + write_size > size) {
                write_size = size - offset;
            }
            System.arraycopy(buf, offset, wbuf, 0, write_size);

            written_size = mConnection.bulkTransfer(mEndpointOut, wbuf, write_size, 100);

            if (written_size < 0) {
                return -1;
            }
            offset += written_size;
        }

        return offset;
    }

    private void stopRead() {
        mReadThreadStop = true;
    }

    private void startRead() {
        if(mReadThreadStop) {
            mReadThreadStop = false;
            new Thread(mLoop).start();
        }
    }

    private Runnable mLoop = new Runnable() {
        @Override
        public void run() {
            int len=0;
            byte[] rbuf = new byte[USB_READ_BUFFER_SIZE];
            for (;;) {// this is the main loop for transferring

                try {
                    len = mConnection.bulkTransfer(mEndpointIn,
                            rbuf, rbuf.length, 50);
                } catch(Exception e) {
                    Log.e(TAG, e.toString());
                }

                if (len > 0) {
                    mBuffer.add(rbuf, len);
                    onRead(len);
                }

                if (mReadThreadStop) {
                    return;
                }

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                }

            }
        } // end of run()
    }; // end of runnable

    @Override
    public boolean setUartConfig(UartConfig config) {
        boolean res = true;
        boolean ret = true;
        if(mUartConfig.baudrate != config.baudrate) {
            res = setBaudrate(config.baudrate);
            ret = ret && res;
        }

        if(mUartConfig.dataBits != config.dataBits) {
            res = setDataBits(config.dataBits);
            ret = ret && res;
        }

        if(mUartConfig.parity != config.parity) {
            res = setParity(config.parity);
            ret = ret && res;
        }

        if(mUartConfig.stopBits != config.stopBits) {
            res = setStopBits(config.stopBits);
            ret = ret && res;
        }

        if(mUartConfig.dtrOn != config.dtrOn ||
           mUartConfig.rtsOn != config.rtsOn) {
            res = setDtrRts(config.dtrOn, config.rtsOn);
            ret = ret && res;
        }

        return ret;
    }

    /**
     * Initializes CP210x communication
     * @return true : successful, false : fail
     */
    private boolean init() {
        int ret = cp210xUsbEnable();
        if(ret < 0) { return false; }
        return true;
    }

    @Override
    public boolean isOpened() {
        return isOpened;
    }


    /**
     * Enables CP210x
     * @return positive value : successful, negative value : fail
     */
    private int cp210xUsbEnable() {
        if(mConnection == null) return -1;
        return mConnection.controlTransfer(
                REQTYPE_HOST_TO_INTERFACE,
                CP210X_IFC_ENABLE,
                UART_ENABLE,
                0,
                null,
                0,
                100);
    }

    /**
     * Disables CP210x
     * @return positive value : successful, negative value : fail
     */
    private int cp210xUsbDisable() {
        if(mConnection == null) return -1;
        return mConnection.controlTransfer(
                REQTYPE_HOST_TO_INTERFACE,
                CP210X_IFC_ENABLE,
                UART_DISABLE,
                0,
                null,
                0,
                100);
    }

    /**
     * Gets configurations from CP210x
     * @param request request id
     * @param buf gotten buffer
     * @param size size of getting buffer
     * @return positive value : successful, negative value : fail
     */
    private int cp210xGetConfig(int request, byte[] buf, int size) {
        if(mConnection == null) return -1;
        int ret = mConnection.controlTransfer(
                REQTYPE_INTERFACE_TO_HOST,
                request,
                0x0000,
                0,
                buf,
                size,
                100);
        return ret;
    }

    /**
     * Sets configurations from CP210x
     * @param request request id
     * @param buf set buffer
     * @param size size of sending buffer
     * @return
     */
    private int cp210xSetConfig(int request, byte[] buf, int size) {
        if(mConnection == null) return -1;
        int ret = mConnection.controlTransfer(
                REQTYPE_HOST_TO_INTERFACE,
                request,
                0x0000,
                0,
                buf,
                size,
                100);
        return ret;
    }

    @Override
    public boolean setBaudrate(int baudrate) {
        if (baudrate <= 300)            baudrate = 300;
        else if (baudrate <= 600)       baudrate = 600;
        else if (baudrate <= 1200)      baudrate = 1200;
        else if (baudrate <= 1800)      baudrate = 1800;
        else if (baudrate <= 2400)      baudrate = 2400;
        else if (baudrate <= 4000)      baudrate = 4000;
        else if (baudrate <= 4803)      baudrate = 4800;
        else if (baudrate <= 7207)      baudrate = 7200;
        else if (baudrate <= 9612)      baudrate = 9600;
        else if (baudrate <= 14428)     baudrate = 14400;
        else if (baudrate <= 16062)     baudrate = 16000;
        else if (baudrate <= 19250)     baudrate = 19200;
        else if (baudrate <= 28912)     baudrate = 28800;
        else if (baudrate <= 38601)     baudrate = 38400;
        else if (baudrate <= 51558)     baudrate = 51200;
        else if (baudrate <= 56280)     baudrate = 56000;
        else if (baudrate <= 58053)     baudrate = 57600;
        else if (baudrate <= 64111)     baudrate = 64000;
        else if (baudrate <= 77608)     baudrate = 76800;
        else if (baudrate <= 117028)    baudrate = 115200;
        else if (baudrate <= 129347)    baudrate = 128000;
        else if (baudrate <= 156868)    baudrate = 153600;
        else if (baudrate <= 237832)    baudrate = 230400;
        else if (baudrate <= 254234)    baudrate = 250000;
        else if (baudrate <= 273066)    baudrate = 256000;
        else if (baudrate <= 491520)    baudrate = 460800;
        else if (baudrate <= 567138)    baudrate = 500000;
        else if (baudrate <= 670254)    baudrate = 576000;
        else if (baudrate < 1000000)    baudrate = 921600;
        else if (baudrate > 2000000)    baudrate = 2000000;

        byte[] baudBytes = new byte[4];
        intToLittleEndianBytes(baudrate, baudBytes);
        int ret = cp210xSetConfig(CP210X_SET_BAUDRATE, baudBytes, 4);
        if(ret < 0) { 
            if(DEBUG_SHOW) { Log.d(TAG, "Fail to setBaudrate"); }
            return false;
        }
        mUartConfig.baudrate = baudrate;
        return true;
    }

    @Override
    public boolean setDataBits(int dataBits) {
        int bits;
        byte[] buf = new byte[2];

        cp210xGetConfig(CP210X_GET_LINE_CTL, buf, buf.length);
        bits = littleEndianBytesToInt(buf);
        bits &= ~BITS_DATA_MASK;

        switch (dataBits) {
        case UartConfig.DATA_BITS7:
            bits |= BITS_DATA_7;
            break;

        case UartConfig.DATA_BITS8:
            bits |= BITS_DATA_8;
            break;

        default:
            bits |= BITS_DATA_8;
            break;
        }

        intToLittleEndianBytes(bits, buf);
        int ret = cp210xSetConfig(CP210X_GET_LINE_CTL, buf, buf.length);

        if(ret < 0) {
            if(DEBUG_SHOW) { Log.e(TAG, "Fail to setDataBits"); }
            return false;
        }

        mUartConfig.dataBits = dataBits;
        return true;
    }


    @Override
    public boolean setParity(int parity) {
        int bits;
        byte[] buf = new byte[2];

        cp210xGetConfig(CP210X_GET_LINE_CTL, buf, buf.length);
        bits = littleEndianBytesToInt(buf);
        bits &= ~BITS_PARITY_MASK;

        switch (parity) {

        case UartConfig.PARITY_NONE:
            bits |= BITS_PARITY_NONE;
            break;

        case UartConfig.PARITY_ODD:
            bits |= BITS_PARITY_ODD;
            break;

        case UartConfig.PARITY_EVEN:
            bits |= BITS_PARITY_EVEN;
            break;

        case UartConfig.PARITY_MARK:
            bits |= BITS_PARITY_MARK;
            break;

        case UartConfig.PARITY_SPACE:
            bits |= BITS_PARITY_SPACE;
            break;

        default:
            bits |= BITS_PARITY_NONE;
            break;
        }

        intToLittleEndianBytes(bits, buf);
        int ret = cp210xSetConfig(CP210X_GET_LINE_CTL, buf, buf.length);

        if(ret < 0) {
            if(DEBUG_SHOW) { Log.d(TAG, "Fail to setParity"); }
            return false;
        }

        mUartConfig.parity = parity;
        return true;
    }


    @Override
    public boolean setStopBits(int stopBits) {
        int bits;
        byte[] buf = new byte[2];

        cp210xGetConfig(CP210X_GET_LINE_CTL, buf, buf.length);
        bits = littleEndianBytesToInt(buf);
        bits &= ~BITS_STOP_MASK;

        switch (stopBits) {
        case UartConfig.STOP_BITS1:
            bits |= BITS_STOP_1;
            break;

        case UartConfig.STOP_BITS1_5:
            bits |= BITS_STOP_1_5;
            break;

        case UartConfig.STOP_BITS2:
            bits |= BITS_STOP_2;
            break;

        default:
            bits |= BITS_STOP_1;
            break;
        }

        intToLittleEndianBytes(bits, buf);
        int ret = cp210xSetConfig(CP210X_GET_LINE_CTL, buf, buf.length);

        if(ret < 0) {
            if(DEBUG_SHOW) { Log.d(TAG, "Fail to setStopBits"); }
            return false;
        }

        mUartConfig.stopBits = stopBits;
        return true;
    }

    @Override
    public boolean setDtrRts(boolean dtrOn, boolean rtsOn) {
        int ctrlValue = 0x0000;
        byte[] buf = new byte[4];

        if(dtrOn) {
            ctrlValue |= CONTROL_DTR;
            ctrlValue |= CONTROL_WRITE_DTR;
        } else {
            ctrlValue &= ~CONTROL_DTR;
            ctrlValue |= CONTROL_WRITE_DTR;
        }

        if(rtsOn) {
            ctrlValue |= CONTROL_RTS;
            ctrlValue |= CONTROL_WRITE_RTS;
        } else {
            ctrlValue &= ~CONTROL_RTS;
            ctrlValue |= CONTROL_WRITE_RTS;
        }

        intToLittleEndianBytes(ctrlValue, buf);
        int ret = cp210xSetConfig(CP210X_SET_MHS, buf, buf.length);

        if(ret < 0) { 
            if(DEBUG_SHOW) { Log.d(TAG, "Fail to setDtrRts"); }
            return false;
        }
        mUartConfig.dtrOn = dtrOn;
        mUartConfig.rtsOn = rtsOn;
        return true;
    }

    @Override
    public UartConfig getUartConfig() {
        return mUartConfig;
    }

    @Override
    public int getBaudrate() {
        return mUartConfig.baudrate;
    }

    @Override
    public int getDataBits() {
        return mUartConfig.dataBits;
    }

    @Override
    public int getParity() {
        return mUartConfig.parity;
    }

    @Override
    public int getStopBits() {
        return mUartConfig.stopBits;
    }

    @Override
    public boolean getDtr() {
        return mUartConfig.dtrOn;
    }

    @Override
    public boolean getRts() {
        return mUartConfig.rtsOn;
    }

    @Override
    public void clearBuffer() {
        mBuffer.clear();
    }

    //////////////////////////////////////////////////////////
    // Listener for reading uart
    //////////////////////////////////////////////////////////
    private List<ReadLisener> uartReadListenerList
        = new ArrayList<ReadLisener>();
    private boolean mStopReadListener = false;

    @Override
    public void addReadListener(ReadLisener listener) {
        uartReadListenerList.add(listener);
    }

    @Override
    public void clearReadListener() {
        uartReadListenerList.clear();
    }

    @Override
    public void startReadListener() {
        mStopReadListener = false;
    }

    @Override
    public void stopReadListener() {
        mStopReadListener = true;
    }

    private void onRead(int size) {
        if(mStopReadListener) return;
        for (ReadLisener listener: uartReadListenerList) {
            listener.onRead(size);
        }
    }
    //////////////////////////////////////////////////////////


    /**
     * Transfers int to little endian byte array
     * @param in integer value
     * @param out 4 or less length byte array
     */
    private void intToLittleEndianBytes(int in, byte[] out) {
        if(out == null) return;
        if(out.length > 4) return;
        for(int i=0; i<out.length; i++) {
            out[i] = (byte)((in >> (i*8)) & 0x000000FF);
        }
    }

    /**
     * Transfers little endian byte array to int
     * @param in 4 or less length byte array
     * @return integer value
     */
    private int littleEndianBytesToInt(byte[] in) {
        if(in == null) return 0;
        if(in.length > 4) return 0;
        int ret=0;
        for(int i=0; i<in.length; i++) {
            ret |= (((int)in[i]) & 0x000000FF) << (i*8);
        }
        return ret;
    }
}
