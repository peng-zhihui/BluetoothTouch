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
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class JoystickFragment extends Fragment implements JoystickView.OnJoystickChangeListener
{
    private DecimalFormat d = (DecimalFormat) NumberFormat.getNumberInstance(Locale.ENGLISH);
    private JoystickView mJoystick;
    private TextView mText1;
    private Handler mHandler = new Handler();
    private Runnable mRunnable;

    private double xValue, yValue;
    private boolean joystickReleased;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.joystick, container, false);

        mJoystick = (JoystickView) v.findViewById(R.id.joystick);
        mJoystick.setOnJoystickChangeListener(this);

        mText1 = (TextView) v.findViewById(R.id.textView1);
        mText1.setText(R.string.defaultJoystickValue);

        MainActivity.joystickReleased = true;

        return v;
    }

    private void newData(double xValue, double yValue, boolean joystickReleased)
    {
        if (xValue == 0 && yValue == 0)
            joystickReleased = true;

        CustomViewPager.setPagingEnabled(joystickReleased);
        MainActivity.joystickReleased = joystickReleased;
        this.joystickReleased = joystickReleased;
        this.xValue = xValue;
        this.yValue = yValue;
        mText1.setText("x: " + d.format(xValue) + " y: " + d.format(yValue));
    }

    @Override
    public void setOnTouchListener(double xValue, double yValue)
    {
        newData(xValue, yValue, false);
    }

    @Override
    public void setOnMovedListener(double xValue, double yValue)
    {
        newData(xValue, yValue, false);
    }

    @Override
    public void setOnReleaseListener(double xValue, double yValue)
    {
        newData(xValue, yValue, true);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        mJoystick.invalidate();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mJoystick.invalidate();
        MainActivity.joystickReleased = true;

        mRunnable = new Runnable()
        {
            @Override
            public void run()
            {
                mHandler.postDelayed(this, 150); // Send data every 150ms
                if (MainActivity.mChatService == null)
                    return;

                if (MainActivity.mChatService.getState() == BluetoothChatService.STATE_CONNECTED){// && BalanduinoActivity.checkTab(ViewPagerAdapter.JOYSTICK_FRAGMENT)) {
                    if (!getResources().getBoolean(R.bool.isTablet) || !MainActivity.buttonState) { // Don't send stop if the button in the IMU fragment is pressed
                        if (joystickReleased || (xValue == 0 && yValue == 0))
                            MainActivity.mChatService.write(MainActivity.sendStop);
                        else {
                            String message = MainActivity.sendJoystickValues + d.format(xValue) + ',' + d.format(yValue) + ";";
                            MainActivity.mChatService.write(message);
                            Log.d("pzhh", "run: "+message);
                        }
                    }
                }
            }
        };
        mHandler.postDelayed(mRunnable, 150); // Send data every 150ms
    }

    @Override
    public void onPause()
    {
        super.onPause();
        mJoystick.invalidate();
        MainActivity.joystickReleased = true;
        CustomViewPager.setPagingEnabled(true);
        mHandler.removeCallbacks(mRunnable);
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        mJoystick.invalidate();
        MainActivity.joystickReleased = true;
    }

    @Override
    public void onStop()
    {
        super.onStop();
        mJoystick.invalidate();
        MainActivity.joystickReleased = true;
        CustomViewPager.setPagingEnabled(true);
    }
}