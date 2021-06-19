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

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphView.LegendAlign;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.jjoe64.graphview.GraphViewStyle;
import com.jjoe64.graphview.LineGraphView;

public class GraphFragment extends Fragment
{
    private static final String TAG = "GraphFragment";
    private static final boolean D = MainActivity.D;

    private static LineGraphView graphView;
    private static GraphViewSeries accSeries, gyroSeries, kalmanSeries;
    private static double counter = 100d;

    private static CheckBox mCheckBox1, mCheckBox2, mCheckBox3;
    private static EditText mQangle, mQbias, mRmeasure;
    public static ToggleButton mToggleButton;

    private static double[][] buffer = new double[3][101]; // Used to store the 101 last readings

    public GraphFragment()
    {
        for (int i = 0; i < 3; i++)
            for (int i2 = 0; i2 < buffer[i].length; i2++)
                buffer[i][i2] = 180d;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.graph, container, false);

        GraphViewData[] data0 = new GraphViewData[101];
        GraphViewData[] data1 = new GraphViewData[101];
        GraphViewData[] data2 = new GraphViewData[101];

        for (int i = 0; i < 101; i++) { // Restore last data
            data0[i] = new GraphViewData(counter - 100 + i, buffer[0][i]);
            data1[i] = new GraphViewData(counter - 100 + i, buffer[1][i]);
            data2[i] = new GraphViewData(counter - 100 + i, buffer[2][i]);
        }

        accSeries = new GraphViewSeries("Accelerometer", new GraphViewSeriesStyle(Color.RED, 2), data0);
        gyroSeries = new GraphViewSeries("Gyro", new GraphViewSeriesStyle(Color.GREEN, 2), data1);
        kalmanSeries = new GraphViewSeries("Kalman", new GraphViewSeriesStyle(Color.BLUE, 2), data2);

        graphView = new LineGraphView(getActivity(), "");
        if (mCheckBox1 != null) {
            if (mCheckBox1.isChecked())
                graphView.addSeries(accSeries);
        } else
            graphView.addSeries(accSeries);
        if (mCheckBox2 != null) {
            if (mCheckBox2.isChecked())
                graphView.addSeries(gyroSeries);
        } else
            graphView.addSeries(gyroSeries);
        if (mCheckBox3 != null) {
            if (mCheckBox3.isChecked())
                graphView.addSeries(kalmanSeries);
        } else
            graphView.addSeries(kalmanSeries);

        graphView.setManualYAxisBounds(360, 0);
        graphView.setViewPort(0, 100);
        graphView.setScrollable(true);
        graphView.setDisableTouch(true);

        graphView.setShowLegend(true);
        graphView.setLegendAlign(LegendAlign.BOTTOM);
        graphView.scrollToEnd();

        LinearLayout layout = (LinearLayout) v.findViewById(R.id.linegraph);

        GraphViewStyle mGraphViewStyle = new GraphViewStyle();
        mGraphViewStyle.setNumHorizontalLabels(11);
        mGraphViewStyle.setNumVerticalLabels(9);
        mGraphViewStyle.setTextSize(15);
        mGraphViewStyle.setLegendWidth(140);
        mGraphViewStyle.setLegendMarginBottom(30);

        graphView.setGraphViewStyle(mGraphViewStyle);

        layout.addView(graphView);

        mCheckBox1 = (CheckBox) v.findViewById(R.id.checkBox1);
        mCheckBox1.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (((CheckBox) v).isChecked())
                    graphView.addSeries(accSeries);
                else
                    graphView.removeSeries(accSeries);
            }
        });
        mCheckBox2 = (CheckBox) v.findViewById(R.id.checkBox2);
        mCheckBox2.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (((CheckBox) v).isChecked())
                    graphView.addSeries(gyroSeries);
                else
                    graphView.removeSeries(gyroSeries);
            }
        });
        mCheckBox3 = (CheckBox) v.findViewById(R.id.checkBox3);
        mCheckBox3.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (((CheckBox) v).isChecked())
                    graphView.addSeries(kalmanSeries);
                else
                    graphView.removeSeries(kalmanSeries);
            }
        });

        mToggleButton = (ToggleButton) v.findViewById(R.id.toggleButton);
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
                    if (MainActivity.mChatService.getState() == BluetoothChatService.STATE_CONNECTED && MainActivity.checkTab(ViewPagerAdapter.GRAPH_FRAGMENT)) {
                        if (((ToggleButton) v).isChecked())
                            MainActivity.mChatService.write(MainActivity.imuBegin); // Request data
                        else
                            MainActivity.mChatService.write(MainActivity.imuStop); // Stop sending data
                    }
                }
            }
        });

        mQangle = (EditText) v.findViewById(R.id.editText1);
        mQbias = (EditText) v.findViewById(R.id.editText2);
        mRmeasure = (EditText) v.findViewById(R.id.editText3);
        Button mButton = (Button) v.findViewById(R.id.updateButton);
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
                if (mQangle.getText() != null)
                    MainActivity.Qangle = mQangle.getText().toString();
                if (mQbias.getText() != null)
                    MainActivity.Qbias = mQbias.getText().toString();
                if (mRmeasure.getText() != null)
                    MainActivity.Rmeasure = mRmeasure.getText().toString();
                MainActivity.mChatService.write(MainActivity.setKalman + MainActivity.Qangle + "," + MainActivity.Qbias + "," + MainActivity.Rmeasure + ";");
            }
        });

        if (MainActivity.mChatService != null) {
            if (MainActivity.mChatService.getState() == BluetoothChatService.STATE_CONNECTED && MainActivity.checkTab(ViewPagerAdapter.GRAPH_FRAGMENT)) {
                if (mToggleButton.isChecked())
                    MainActivity.mChatService.write(MainActivity.imuBegin); // Request data
                else
                    MainActivity.mChatService.write(MainActivity.imuStop); // Stop sending data
            }
        }

        return v;
    }

    public static void updateKalmanValues()
    {
        if (mQangle != null && mQangle.getText() != null) {
            if (!(mQangle.getText().toString().equals(MainActivity.Qangle)))
                mQangle.setText(MainActivity.Qangle);
        }
        if (mQbias != null && mQbias.getText() != null) {
            if (!(mQbias.getText().toString().equals(MainActivity.Qbias)))
                mQbias.setText(MainActivity.Qbias);
        }
        if (mRmeasure != null && mRmeasure.getText() != null) {
            if (!(mRmeasure.getText().toString().equals(MainActivity.Rmeasure)))
                mRmeasure.setText(MainActivity.Rmeasure);
        }
    }

    public static void updateIMUValues()
    {
        if (mToggleButton == null)
            return;
        if (!(mToggleButton.isChecked()))
            return;

        for (int i = 0; i < 3; i++)
            System.arraycopy(buffer[i], 1, buffer[i], 0, 100);

        try { // In some rare occasions the values can be corrupted
            buffer[0][100] = Double.parseDouble(MainActivity.accValue);
            buffer[1][100] = Double.parseDouble(MainActivity.gyroValue);
            buffer[2][100] = Double.parseDouble(MainActivity.kalmanValue);
        } catch (NumberFormatException e) {
            if (D)
                Log.e(TAG, "error in input", e);
            return;
        }

        boolean scroll = mCheckBox1.isChecked() || mCheckBox2.isChecked() || mCheckBox3.isChecked();

        counter++;
        accSeries.appendData(new GraphViewData(counter, buffer[0][100]), scroll, 101);
        if (buffer[1][100] <= 360 && buffer[1][100] >= 0) // Don't draw it if it would be larger than y-axis boundaries
            gyroSeries.appendData(new GraphViewData(counter, buffer[1][100]), scroll, 101);
        kalmanSeries.appendData(new GraphViewData(counter, buffer[2][100]), scroll, 101);

        if (!scroll)
            graphView.redrawAll();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (mToggleButton.isChecked())
            mToggleButton.setText("Stop");
        else
            mToggleButton.setText("Start");

        if (MainActivity.mChatService != null) {
            if (MainActivity.mChatService.getState() == BluetoothChatService.STATE_CONNECTED && MainActivity.checkTab(ViewPagerAdapter.GRAPH_FRAGMENT)) {
                if (mToggleButton.isChecked())
                    MainActivity.mChatService.write(MainActivity.imuBegin); // Request data
                else
                    MainActivity.mChatService.write(MainActivity.imuStop); // Stop sending data
            }
        }
    }
}