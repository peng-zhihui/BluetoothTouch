/*************************************************************************************
 * Copyright (C) 2012-2014 Kristian Lauszus, TKJ Electronics. All rights reserved.
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
 * e-mail   :  kristianl@tkjelectronics.com
 *
 ************************************************************************************/

package xyz.pengzhihui.BluetoothTouch;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

public class InfoFragment extends Fragment
{
    private static TextView mAppVersion, mFirmwareVersion, mEEPROMVersion, mMcu, mBatteryLevel, mRuntime;
    protected static ToggleButton mToggleButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.info, container, false);
        mAppVersion = (TextView) v.findViewById(R.id.appVersion);
        mFirmwareVersion = (TextView) v.findViewById(R.id.firmwareVersion);
        mEEPROMVersion = (TextView) v.findViewById(R.id.eepromVersion);
        mMcu = (TextView) v.findViewById(R.id.mcu);
        mBatteryLevel = (TextView) v.findViewById(R.id.batterylevel);
        mRuntime = (TextView) v.findViewById(R.id.runtime);

        mToggleButton = (ToggleButton) v.findViewById(R.id.button);
        mToggleButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (((ToggleButton) v).isChecked())
                    mToggleButton.setText("Stop");
                else
                    mToggleButton.setText("Start");

                if (MainActivity.mChatService != null) {
                    if (MainActivity.mChatService.getState() == BluetoothChatService.STATE_CONNECTED && MainActivity.checkTab(ViewPagerAdapter.INFO_FRAGMENT)) {
                        if (((ToggleButton) v).isChecked())
                            MainActivity.mChatService.write(MainActivity.statusBegin); // Request data
                        else
                            MainActivity.mChatService.write(MainActivity.statusStop); // Stop sending data
                    }
                }
            }
        });

        if (MainActivity.mChatService != null) {
            if (MainActivity.mChatService.getState() == BluetoothChatService.STATE_CONNECTED && MainActivity.checkTab(ViewPagerAdapter.INFO_FRAGMENT)) {
                if (mToggleButton.isChecked())
                    MainActivity.mChatService.write(MainActivity.statusBegin); // Request data
                else
                    MainActivity.mChatService.write(MainActivity.statusStop); // Stop sending data
            }
        }

        updateView();
        return v;
    }

    public static void updateView()
    {
        if (mAppVersion != null && MainActivity.appVersion != null)
            mAppVersion.setText(MainActivity.appVersion);
        if (mFirmwareVersion != null && MainActivity.firmwareVersion != null)
            mFirmwareVersion.setText(MainActivity.firmwareVersion);
        if (mEEPROMVersion != null && MainActivity.eepromVersion != null)
            mEEPROMVersion.setText(MainActivity.eepromVersion);
        if (mMcu != null && MainActivity.mcu != null)
            mMcu.setText(MainActivity.mcu);
        if (mBatteryLevel != null && MainActivity.batteryLevel != null)
            mBatteryLevel.setText(MainActivity.batteryLevel + 'V');
        if (mRuntime != null && MainActivity.runtime != 0) {
            String minutes = Integer.toString((int) Math.floor(MainActivity.runtime));
            String seconds = Integer.toString((int) (MainActivity.runtime % 1 / (1.0 / 60.0)));
            mRuntime.setText(minutes + " min " + seconds + " sec");
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        updateView(); // When the user resumes the view, then update the values

        if (mToggleButton.isChecked())
            mToggleButton.setText("Stop");
        else
            mToggleButton.setText("Start");

        if (MainActivity.mChatService != null) {
            if (MainActivity.mChatService.getState() == BluetoothChatService.STATE_CONNECTED && MainActivity.checkTab(ViewPagerAdapter.INFO_FRAGMENT)) {
                if (mToggleButton.isChecked())
                    MainActivity.mChatService.write(MainActivity.statusBegin); // Request data
                else
                    MainActivity.mChatService.write(MainActivity.statusStop); // Stop sending data
            }
        }
    }
}