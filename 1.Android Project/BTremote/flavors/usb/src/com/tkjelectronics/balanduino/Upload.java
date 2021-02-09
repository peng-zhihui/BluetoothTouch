/*************************************************************************************
 * Copyright (C) 2012 Kristian Lauszus, TKJ Electronics. All rights reserved.
 *
 * This software may be distributed and modified under the terms of the GNU
 * General Public License version 2 (GPL2) as published by the Free Software
 * Foundation and appearing in the file GPL2.TXT included in the packaging of
 * this file. Please note that GPL2 Section 2[b] requires that all works based
 * on this software must also be made publicly available under the terms of
 * the GPL2 ("Copyleft").
 *
 * Contact information
 * -------------------
 *
 * Kristian Lauszus, TKJ Electronics
 * Web      :  http://www.tkjelectronics.com
 * e-mail   :  kristianl@pzh.com
 *
 ************************************************************************************/

package com.pzh.BTremote;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.physicaloid.lib.Boards;
import com.physicaloid.lib.Physicaloid;
import com.physicaloid.lib.Physicaloid.UploadCallBack;
import com.physicaloid.lib.programmer.avr.UploadErrors;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.Locale;
import java.util.Map;

@TargetApi(12)
public class Upload {
    private static final String TAG = "Upload";
    private static final boolean D = BTremote.D;
    private static final String ACTION_USB_PERMISSION = "com.pzh.BTremote.USB_PERMISSION";

    private static Physicaloid mPhysicaloid;
    private static boolean uploading = false, cancelled = false;
    private static ProgressDialog mProgressDialog;
    private final static String fileUrl_rev12 = "https://raw.githubusercontent.com/TKJElectronics/Balanduino/master/Firmware/Balanduino/Balanduino_rev12.hex";
    private final static String fileUrlMd5_rev12 = "https://raw.githubusercontent.com/TKJElectronics/Balanduino/master/Firmware/Balanduino/Balanduino_rev12.md5";
    private final static String fileUrl_rev13 = "https://raw.githubusercontent.com/TKJElectronics/Balanduino/master/Firmware/Balanduino/Balanduino_rev13.hex";
    private final static String fileUrlMd5_rev13 = "https://raw.githubusercontent.com/TKJElectronics/Balanduino/master/Firmware/Balanduino/Balanduino_rev13.md5";
    private static String fileName;

    /**
     * Closes the serial communication.
     */
    public static void close() {
        if (mPhysicaloid != null) {
            if (uploading) {
                uploading = false;
                mPhysicaloid.cancelUpload();
                BTremote.showToast("Upload canceled", Toast.LENGTH_SHORT);
            }
            try {
                if (mPhysicaloid.isOpened())
                    mPhysicaloid.close();
            } catch (RuntimeException e) {
                if (D)
                    Log.e(TAG, e.toString());
            }
        }
    }

    /**
     * Start the firmware upload.
     * First it check if the Balanduino is actually connected and then check the permission.
     * If permission is not granted it will ask for permission.
     * After this it will download the firmware and then upload it to the Balanduino via the USB Host port.
     *
     * @return Returns true if a new upload has started.
     */
    public static boolean uploadFirmware() {
        if (uploading) {
            BTremote.showToast("Upload is already in progress", Toast.LENGTH_SHORT);
            return false;
        }

        // Check permission before trying to do anything else
        UsbManager mUsbManager = (UsbManager) BTremote.activity.getSystemService(BTremote.USB_SERVICE);
        if (mUsbManager == null)
            return false;
        Map<String, UsbDevice> map = mUsbManager.getDeviceList();
        if (map == null)
            return false;
        if (D)
            Log.i(TAG, "UsbDevices: " + map);

        boolean deviceFound = false;
        for (UsbDevice mUsbDevice : map.values()) {
            if (mUsbDevice.getVendorId() == 0x0403 && mUsbDevice.getProductId() == 0x6015) { // Check if the robot is connected
                deviceFound = true;
                if (mUsbManager.hasPermission(mUsbDevice)) {
                    if (D)
                        Log.i(TAG, "Already has permission");
                    showDialog();
                } else {
                    if (D)
                        Log.i(TAG, "Requesting permission");
                    PendingIntent mPermissionIntent = PendingIntent.getBroadcast(BTremote.context, 0, new Intent(ACTION_USB_PERMISSION), 0);
                    BTremote.activity.registerReceiver(mUsbReceiver, new IntentFilter(ACTION_USB_PERMISSION));
                    mUsbManager.requestPermission(mUsbDevice, mPermissionIntent);
                }
                break;
            }
        }
        if (!deviceFound) {
            BTremote.showToast("Please connect the Balanduino to the USB Host port", Toast.LENGTH_SHORT);
            return false;
        }

        return true;
    }

    /**
     * Check if USB Host is available on the device.
     * I am not sure if this is actually needed or not.
     *
     * @return Return true if USB Host is available.
     */
    public static boolean isUsbHostAvailable() {
        UsbManager mUsbManager = (UsbManager) BTremote.activity.getSystemService(BTremote.USB_SERVICE);
        if (mUsbManager == null)
            return false;
        Map<String, UsbDevice> map = mUsbManager.getDeviceList();
        return map != null;
    }

    private static final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (ACTION_USB_PERMISSION.equals(intent.getAction())) {
                synchronized (this) {
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (D)
                            Log.i(TAG, "Permission allowed");
                        showDialog();
                    } else {
                        if (D)
                            Log.e(TAG, "Permission denied");
                    }
                }
            }
        }
    };

    private static void showDialog() {
        if (!BTremote.activity.isFinishing()) {
            LayoutInflater inflater = LayoutInflater.from(BTremote.context);
            View view = inflater.inflate(com.pzh.BTremote.R.layout.upload_dialog, null);
            final RadioButton rev12 = (RadioButton) view.findViewById(com.pzh.BTremote.R.id.radio_rev12);

            new AlertDialog.Builder(BTremote.activity)
                    .setIcon(com.pzh.BTremote.R.drawable.ic_dialog_usb)
                    .setTitle("Download firmware")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (rev12.isChecked())
                                downloadFile(fileUrl_rev12, fileUrlMd5_rev12);
                            else
                                downloadFile(fileUrl_rev13, fileUrlMd5_rev13);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .setView(view)
                    .create().show();
        }
    }

    private static void downloadFile(String fileUrl, String fileUrlMd5) {
        // Execute this when the downloader must be fired
        final DownloadTask downloadTask = new DownloadTask(BTremote.activity);
        downloadTask.execute(fileUrl, fileUrlMd5);

        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                downloadTask.cancel(true);
            }
        });
    }

    private static void upload() {
        if (fileName == null)
            return;
        if (mPhysicaloid == null)
            mPhysicaloid = new Physicaloid(BTremote.activity);

        try {
            mPhysicaloid.upload(Boards.BALANDUINO, BTremote.context.openFileInput(fileName), mUploadCallback);
        } catch (RuntimeException e) {
            if (D)
                Log.e(TAG, e.toString());
            BTremote.showToast("Error opening USB host communication", Toast.LENGTH_SHORT);
        } catch (FileNotFoundException e) {
            if (D)
                Log.e(TAG, e.toString());
            BTremote.showToast("Error opening hex file", Toast.LENGTH_SHORT);
        }
    }

    private static UploadCallBack mUploadCallback = new UploadCallBack() {
        @Override
        public void onUploading(int value) {
            if (D)
                Log.i(TAG, "Uploading: " + value);
            mProgressDialog.setProgress(value);
        }

        @Override
        public void onPreUpload() {
            if (D)
                Log.i(TAG, "Upload start");
            uploading = true;
            BTremote.activity.runOnUiThread(new Runnable() {
                public void run() {
                    mProgressDialog = new ProgressDialog(BTremote.activity);
                    mProgressDialog.setMessage("Uploading...");
                    mProgressDialog.setIndeterminate(false);
                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    mProgressDialog.setCancelable(true);
                    mProgressDialog.setMax(100);
                    mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            mPhysicaloid.cancelUpload();
                            try {
                                if (mPhysicaloid.isOpened())
                                    mPhysicaloid.close();
                            } catch (RuntimeException e) {
                                if (D)
                                    Log.e(TAG, e.toString());
                            }
                        }
                    });
                    mProgressDialog.show();
                }
            });
        }

        @Override
        public void onPostUpload(boolean success) {
            if (D)
                Log.i(TAG, "Upload finished");
            mProgressDialog.dismiss();
            uploading = false;
            if (cancelled) {
                cancelled = false;
                return;
            }

            if (success) {
                BTremote.activity.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(BTremote.context, "Uploading was successful", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                BTremote.activity.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(BTremote.context, "Uploading failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        @Override
        public void onCancel() {
            cancelled = true;
        }

        @Override
        public void onError(UploadErrors err) {
            if (D)
                Log.e(TAG, "Error: " + err.toString());
            mProgressDialog.dismiss();
            uploading = false;
            BTremote.activity.runOnUiThread(new Runnable() {
                public void run() {
                    BTremote.showToast("Uploading error", Toast.LENGTH_SHORT);
                }
            });
        }
    };

    // Usually, subclasses of AsyncTask are declared inside the activity class.
    // That way, you can easily modify the UI thread from here
    // Source: http://stackoverflow.com/questions/3028306/download-a-file-with-android-and-showing-the-progress-in-a-progressdialog
    private static class DownloadTask extends AsyncTask<String, Integer, String> {
        private static final String TAG = "DownloadTask";
        private static final boolean D = BTremote.D;

        private Context context;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            String networkStatus = checkNetwork();
            if (networkStatus != null)
                return networkStatus;

            // Take CPU lock to prevent CPU from going off if the user presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
            wl.acquire();

            InputStream input = null;
            DigestInputStream disInput = null;
            FileOutputStream output = null;
            HttpURLConnection connection = null;

            try {
                fileName = sUrl[0].substring(sUrl[0].lastIndexOf('/') + 1, sUrl[0].length());
                if (D)
                    Log.i(TAG, "FileName: " + fileName);

                // Download hex file
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // Expect HTTP 200 OK, so we do not mistakenly save error report instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
                    return "Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage();

                input = connection.getInputStream(); // Get input stream
                output = context.openFileOutput(fileName, Context.MODE_PRIVATE); // Open output stream

                // Used to calculate MD5 checksum
                MessageDigest mMessageDigest = MessageDigest.getInstance("MD5");
                disInput = new DigestInputStream(input, mMessageDigest);

                byte data[] = new byte[4096];
                int count;
                while ((count = disInput.read(data)) != -1) {
                    if (isCancelled()) // Allow canceling with back button
                        return null;
                    output.write(data, 0, count);
                }
                String checksum = bytesToHex(mMessageDigest.digest()); // Calculated MD5 checksum of file

                // Download MD5 file
                url = new URL(sUrl[1]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // Expect HTTP 200 OK, so we do not mistakenly save error report instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
                    return "Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage();

                input = connection.getInputStream(); // Get input stream

                String md5 = "";
                while (input.read(data) != -1) {
                    if (isCancelled()) // Allow canceling with back button
                        return null;
                    md5 += new String(data);
                }
                md5 = md5.substring(0, md5.indexOf(" *")).toUpperCase(Locale.ENGLISH); // MD5 file is in the coreutils format

                if (D)
                    Log.i(TAG, "Checksum: " + checksum + " " + md5 + " " + checksum.equals(md5));

                if (!checksum.equals(md5))
                    return "Error in MD5 checksum";

            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                    if (disInput != null)
                        disInput.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();

                wl.release();
            }

            return null;
        }

        final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
        private static String bytesToHex(byte[] bytes) {
            char[] hexChars = new char[bytes.length * 2];
            int v;
            for (int j = 0; j < bytes.length; j++) {
                v = bytes[j] & 0xFF;
                hexChars[j * 2] = hexArray[v >>> 4];
                hexChars[j * 2 + 1] = hexArray[v & 0x0F];
            }
            return new String(hexChars);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!BTremote.activity.isFinishing()) {
                mProgressDialog = new ProgressDialog(BTremote.activity);
                mProgressDialog.setMessage("Downloading file...");
                mProgressDialog.setIndeterminate(true);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // STYLE_HORIZONTAL
                mProgressDialog.setCancelable(true);
                mProgressDialog.show();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            mProgressDialog.dismiss();
            if (result != null)
                BTremote.showToast("Download error: " + result, Toast.LENGTH_LONG);
            else {
                BTremote.showToast("File downloaded", Toast.LENGTH_SHORT);
                upload(); // Upload downloaded file
            }
        }

        private String checkNetwork() {
            ConnectivityManager mConnectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = mConnectivityManager.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnected())
                return null;
            return "No internet connection available";
        }
    }
}