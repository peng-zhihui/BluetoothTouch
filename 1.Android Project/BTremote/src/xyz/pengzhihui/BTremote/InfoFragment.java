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
 * e-mail   :  kristianl@pzh.com
 *
 ************************************************************************************/

package xyz.pengzhihui.BTremote;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.pengzhihui.BTremote.R;

public class InfoFragment extends SherlockFragment
{
    static TextView more2, more3, mAppVersion, mFirmwareVersion, mEEPROMVersion, mMcu, mBatteryLevel, mRuntime;
    static Button mToggleButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.info, container, false);
        mAppVersion = (TextView) v.findViewById(R.id.appVersion);

        mToggleButton = (Button) v.findViewById(R.id.button_beta);
        mToggleButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if (MainActivity.mChatService != null)
                {
                    if (MainActivity.mChatService.getState() == BluetoothChatService.STATE_CONNECTED && MainActivity.checkTab(ViewPagerAdapter.INFO_FRAGMENT))
                    {
                    }
                }
            }
        });

        if (MainActivity.mChatService != null)
        {
            if (MainActivity.mChatService.getState() == BluetoothChatService.STATE_CONNECTED && MainActivity.checkTab(ViewPagerAdapter.INFO_FRAGMENT))
            {

            }
        }

        more2 = (TextView) v.findViewById(R.id.textView5);
        more2.setMovementMethod(LinkMovementMethod.getInstance());
        more3 = (TextView) v.findViewById(R.id.textView7);
        more3.setMovementMethod(LinkMovementMethod.getInstance());

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
        if (mRuntime != null && MainActivity.runtime != 0)
        {
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


        if (MainActivity.mChatService != null)
        {
            if (MainActivity.mChatService.getState() == BluetoothChatService.STATE_CONNECTED && MainActivity.checkTab(ViewPagerAdapter.INFO_FRAGMENT))
            {
            }
        }
    }
}