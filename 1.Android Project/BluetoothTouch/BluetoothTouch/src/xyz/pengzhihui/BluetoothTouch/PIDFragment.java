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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;


public class PIDFragment extends Fragment
{
    private static final String TAG = "PIDFragment";
    private static final boolean D = MainActivity.D;

    private static Button mButton;
    private static TextView mKpView, mKiView, mKdView, mTargetAngleView;
    private static SeekBar mKpSeekBar, mKiSeekBar, mKdSeekBar, mTargetAngleSeekBar;
    private static TextView mKpSeekBarValue, mKiSeekBarValue, mKdSeekBarValue, mTargetAngleSeekBarValue;

    private float newKpValue, newKiValue, newKdValue, newTargetAngleValue;
    private float oldKpValue, oldKiValue, oldKdValue, oldTargetAngleValue;

    private Handler mHandler = new Handler();
    private int counter = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.pid, container, false);

        mKpView = (TextView) v.findViewById(R.id.textView1);
        mKiView = (TextView) v.findViewById(R.id.textView2);
        mKdView = (TextView) v.findViewById(R.id.textView3);
        mTargetAngleView = (TextView) v.findViewById(R.id.textView4);

        mKpSeekBar = (SeekBar) v.findViewById(R.id.KpSeekBar);
        mKpSeekBar.setMax(2000); // 0-20
        mKpSeekBarValue = (TextView) v.findViewById(R.id.KpValue);
        mKpSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch)
            {
                newKpValue = (float) progress / 100.0f; // Since the SeekBar can only handle integers, so this is needed
                mKpSeekBarValue.setText(String.format("%.2f", newKpValue)); // Two decimal places
            }

            public void onStartTrackingTouch(SeekBar seekBar)
            {
            }

            public void onStopTrackingTouch(SeekBar seekBar)
            {
            }
        });
        mKpSeekBar.setProgress(mKpSeekBar.getMax() / 2);

        mKiSeekBar = (SeekBar) v.findViewById(R.id.KiSeekBar);
        mKiSeekBar.setMax(2000); // 0-20
        mKiSeekBarValue = (TextView) v.findViewById(R.id.KiValue);
        mKiSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch)
            {
                newKiValue = (float) progress / 100.0f; // Since the SeekBar can only handle integers, so this is needed
                mKiSeekBarValue.setText(String.format("%.2f", newKiValue)); // Two decimal places
            }

            public void onStartTrackingTouch(SeekBar seekBar)
            {
            }

            public void onStopTrackingTouch(SeekBar seekBar)
            {
            }
        });
        mKiSeekBar.setProgress(mKiSeekBar.getMax() / 2); // Call this after the OnSeekBarChangeListener is created

        mKdSeekBar = (SeekBar) v.findViewById(R.id.KdSeekBar);
        mKdSeekBar.setMax(2000); // 0-20
        mKdSeekBarValue = (TextView) v.findViewById(R.id.KdValue);
        mKdSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch)
            {
                newKdValue = (float) progress / 100.0f; // Since the SeekBar can only handle integers, so this is needed
                mKdSeekBarValue.setText(String.format("%.2f", newKdValue)); // Two decimal places
            }

            public void onStartTrackingTouch(SeekBar seekBar)
            {
            }

            public void onStopTrackingTouch(SeekBar seekBar)
            {
            }
        });
        mKdSeekBar.setProgress(mKdSeekBar.getMax() / 2);

        mTargetAngleSeekBar = (SeekBar) v.findViewById(R.id.TargetAngleSeekBar);
        mTargetAngleSeekBar.setMax(600); // 150-210
        mTargetAngleSeekBarValue = (TextView) v.findViewById(R.id.TargetAngleValue);
        mTargetAngleSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch)
            {
                newTargetAngleValue = (float) progress / 10.0f + 150.0f; // It's not possible to set the minimum value either, so we will add a offset
                mTargetAngleSeekBarValue.setText(String.format("%.2f", newTargetAngleValue)); // Two decimal places
            }

            public void onStartTrackingTouch(SeekBar seekBar)
            {
            }

            public void onStopTrackingTouch(SeekBar seekBar)
            {
            }
        });
        mTargetAngleSeekBar.setProgress(mTargetAngleSeekBar.getMax() / 2);

        mButton = (Button) v.findViewById(R.id.button);
        mButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (MainActivity.mChatService == null) {
                    if (D)
                        Log.e(TAG, "mChatService == null");
                    return;
                }
                if (MainActivity.mChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
                    if (newKpValue != oldKpValue) {
                        oldKpValue = newKpValue;
                        mHandler.post(new Runnable()
                        {
                            public void run()
                            {
                                MainActivity.mChatService.write(MainActivity.setPValue + newKpValue + ";");
                            }
                        });
                        counter = 25;
                    }
                    if (newKiValue != oldKiValue) {
                        oldKiValue = newKiValue;
                        mHandler.postDelayed(new Runnable()
                        {
                            public void run()
                            {
                                MainActivity.mChatService.write(MainActivity.setIValue + newKiValue + ";");
                            }
                        }, counter); // Wait before sending the message
                        counter += 25;
                    }
                    if (newKdValue != oldKdValue) {
                        oldKdValue = newKdValue;
                        mHandler.postDelayed(new Runnable()
                        {
                            public void run()
                            {
                                MainActivity.mChatService.write(MainActivity.setDValue + newKdValue + ";");
                            }
                        }, counter); // Wait before sending the message
                        counter += 25;
                    }
                    if (newTargetAngleValue != oldTargetAngleValue) {
                        oldTargetAngleValue = newTargetAngleValue;
                        mHandler.postDelayed(new Runnable()
                        {
                            public void run()
                            {
                                MainActivity.mChatService.write(MainActivity.setTargetAngle + newTargetAngleValue + ";");
                            }
                        }, counter); // Wait before sending the message
                        counter += 25;
                    }
                    if (counter != 0) {
                        mHandler.postDelayed(new Runnable()
                        {
                            public void run()
                            {
                                MainActivity.mChatService.write(MainActivity.getPIDValues);
                            }
                        }, counter); // Wait before sending the message
                        if (D)
                            Log.i(TAG, newKpValue + "," + newKiValue + "," + newKdValue + "," + newTargetAngleValue);
                    }
                    counter = 0; // Reset counter
                }
            }
        });
        updateView();
        updateButton();
        return v;
    }

    public static void updateView()
    {
        if (mKpView != null && mKpSeekBar != null && mKpSeekBarValue != null && !MainActivity.pValue.isEmpty()) {
            mKpView.setText(MainActivity.pValue);
            mKpSeekBarValue.setText(String.format("%.2f", Float.parseFloat(MainActivity.pValue))); // Two decimal places
            mKpSeekBar.setProgress((int) (Float.parseFloat(MainActivity.pValue) * 100.0f));
        }
        if (mKiView != null && mKiSeekBar != null && mKiSeekBarValue != null && !MainActivity.iValue.isEmpty()) {
            mKiView.setText(MainActivity.iValue);
            mKiSeekBarValue.setText(String.format("%.2f", Float.parseFloat(MainActivity.iValue))); // Two decimal places
            mKiSeekBar.setProgress((int) (Float.parseFloat(MainActivity.iValue) * 100.0f));
        }
        if (mKdView != null && mKdSeekBar != null && mKdSeekBarValue != null && !MainActivity.dValue.isEmpty()) {
            mKdView.setText(MainActivity.dValue);
            mKdSeekBarValue.setText(String.format("%.2f", Float.parseFloat(MainActivity.dValue))); // Two decimal places
            mKdSeekBar.setProgress((int) (Float.parseFloat(MainActivity.dValue) * 100.0f));
        }
        if (mTargetAngleView != null && mTargetAngleSeekBar != null && mTargetAngleSeekBarValue != null && !MainActivity.targetAngleValue.isEmpty()) {
            mTargetAngleView.setText(MainActivity.targetAngleValue);
            mTargetAngleSeekBarValue.setText(String.format("%.2f", Float.parseFloat(MainActivity.targetAngleValue))); // Two decimal places
            mTargetAngleSeekBar.setProgress((int) ((Float.parseFloat(MainActivity.targetAngleValue) - 150.0f) * 10.0f));
        }
    }

    public static void updateButton()
    {
        if (MainActivity.mChatService != null && mButton != null) {
            if (MainActivity.mChatService.getState() == BluetoothChatService.STATE_CONNECTED)
                mButton.setText(R.string.updateValues);
            else
                mButton.setText(R.string.button);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        // When the user resumes the view, then set the values again
        if (MainActivity.mChatService != null) {
            if (MainActivity.mChatService.getState() == BluetoothChatService.STATE_CONNECTED)
                updateView();
            updateButton();
        }
    }
}