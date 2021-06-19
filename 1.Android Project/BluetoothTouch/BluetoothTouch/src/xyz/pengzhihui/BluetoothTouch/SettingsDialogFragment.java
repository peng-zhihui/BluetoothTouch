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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


public class SettingsDialogFragment extends Fragment
{
    private int maxAngle, maxTurning;
    private boolean backToSpot;


    // to be fix - add for missing
    void dismiss()
    {
    }

    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        View view = getActivity().getLayoutInflater().inflate(R.layout.settings_dialog, null);

        final TextView coefficientValue = (TextView) view.findViewById(R.id.coefficientValue);
        coefficientValue.setText(MainActivity.mSensorFusion.d.format(MainActivity.mSensorFusion.filter_coefficient));
        final SeekBar mSeekBarCoefficient = (SeekBar) view.findViewById(R.id.coefficientSeekBar);
        mSeekBarCoefficient.setProgress((int) (MainActivity.mSensorFusion.filter_coefficient * mSeekBarCoefficient.getMax()));
        mSeekBarCoefficient.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch)
            {
                MainActivity.mSensorFusion.tempFilter_coefficient = ((float) progress) / mSeekBarCoefficient.getMax();
                coefficientValue.setText(MainActivity.mSensorFusion.d.format(MainActivity.mSensorFusion.tempFilter_coefficient));
            }

            public void onStartTrackingTouch(SeekBar seekBar)
            {
            }

            public void onStopTrackingTouch(SeekBar seekBar)
            {
            }
        });

        if (SensorFusion.IMUOutputSelection != 2) { // Check if a gyro is supported if not hide SeekBar and text
            view.findViewById(R.id.seekText).setVisibility(View.GONE);
            view.findViewById(R.id.coefficientLayout).setVisibility(View.GONE);
            mSeekBarCoefficient.setVisibility(View.GONE);
        }

        final TextView angleValue = (TextView) view.findViewById(R.id.angleValue);
        maxAngle = MainActivity.maxAngle;
        angleValue.setText(Integer.toString(maxAngle));
        final SeekBar mSeekBarAngle = (SeekBar) view.findViewById(R.id.angleSeekBar);
        mSeekBarAngle.setProgress(maxAngle);
        mSeekBarAngle.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch)
            {
                maxAngle = progress + 1; // The SeekBar doesn't allow to set the minimum value, so we will add 1
                angleValue.setText(Integer.toString(maxAngle));
            }

            public void onStartTrackingTouch(SeekBar seekBar)
            {
            }

            public void onStopTrackingTouch(SeekBar seekBar)
            {
            }
        });

        final TextView turningValue = (TextView) view.findViewById(R.id.turningValue);
        maxTurning = MainActivity.maxTurning;
        turningValue.setText(Integer.toString(maxTurning));
        final SeekBar mSeekBarTurning = (SeekBar) view.findViewById(R.id.turningSeekBar);
        mSeekBarTurning.setProgress(maxTurning);
        mSeekBarTurning.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch)
            {
                maxTurning = progress + 1; // The SeekBar doesn't allow to set the minimum value, so we will add 1
                turningValue.setText(Integer.toString(maxTurning));
            }

            public void onStartTrackingTouch(SeekBar seekBar)
            {
            }

            public void onStopTrackingTouch(SeekBar seekBar)
            {
            }
        });

        CheckBox mCheckBox = (CheckBox) view.findViewById(R.id.checkBox);
        backToSpot = MainActivity.backToSpot;
        mCheckBox.setChecked(backToSpot);
        mCheckBox.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                backToSpot = ((CheckBox) v).isChecked();
            }
        });


        Button mRestoreButton = (Button) view.findViewById(R.id.restoreButton);
        mRestoreButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (MainActivity.mChatService != null) {
                    if (MainActivity.mChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
                        MainActivity.mChatService.write(MainActivity.restoreDefaultValues);
                        Toast.makeText(getActivity(), "Default values have been restored", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                }
            }
        });

        Button mPairButtonWii = (Button) view.findViewById(R.id.pairButtonWii);
        mPairButtonWii.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (MainActivity.mChatService != null) {
                    if (MainActivity.mChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
                        MainActivity.mChatService.write(MainActivity.sendPairWithWii);
                        dismiss();
                    }
                }
            }
        });

        Button mPairButtonPS4 = (Button) view.findViewById(R.id.pairButtonPS4);
        mPairButtonPS4.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (MainActivity.mChatService != null) {
                    if (MainActivity.mChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
                        MainActivity.mChatService.write(MainActivity.sendPairWithPS4);
                        dismiss();
                    }
                }
            }
        });

        if (MainActivity.mChatService != null) {
            if (MainActivity.mChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
                mRestoreButton.setText(R.string.restoreButtonText);
                mPairButtonWii.setText(R.string.wiiButtonText);
                mPairButtonPS4.setText(R.string.ps4ButtonText);
            } else {
                mRestoreButton.setText(R.string.button);
                mPairButtonWii.setText(R.string.button);
                mPairButtonPS4.setText(R.string.button);
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Set title
        builder.setTitle(R.string.dialog_title)
                // Add the buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        // User clicked OK button
                        MainActivity.mSensorFusion.filter_coefficient = MainActivity.mSensorFusion.tempFilter_coefficient;
                        MainActivity.maxAngle = maxAngle;
                        MainActivity.maxTurning = maxTurning;
                        MainActivity.backToSpot = backToSpot;
                        if (MainActivity.mChatService != null) {
                            if (MainActivity.mChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
                                int val = backToSpot ? 1 : 0;
                                MainActivity.mChatService.write(MainActivity.setMaxAngle + maxAngle + ";" + MainActivity.setMaxTurning + maxTurning + ";" + MainActivity.setBackToSpot + val + ";" + MainActivity.getSettings);
                            }
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        // User cancelled the dialog
                        MainActivity.mSensorFusion.tempFilter_coefficient = MainActivity.mSensorFusion.filter_coefficient;
                    }
                })
                // Set custom view
                .setView(view);
        // Create the AlertDialog
        return builder.create();
    }


    public void onCancel(DialogInterface dialog)
    {
        // User pressed back, home or pressed outside the dialog
        MainActivity.mSensorFusion.tempFilter_coefficient = MainActivity.mSensorFusion.filter_coefficient;
    }
}