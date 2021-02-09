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

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.actionbarsherlock.app.SherlockFragment;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphView.LegendAlign;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.jjoe64.graphview.GraphViewStyle;
import com.jjoe64.graphview.LineGraphView;
import com.pengzhihui.BTremote.R;

public class GraphFragment extends SherlockFragment implements TextWatcher
{
    private static final String TAG = "GraphFragment";
    private static final boolean D = MainActivity.D;

    private static LineGraphView graphView;
    private static GraphViewSeries CH1, CH2, CH3;
    private static double counter = 100d;

    private static CheckBox mCheckBox1, mCheckBox2, mCheckBox3;
    private static EditText edt1, edt2;
    public static ToggleButton mToggleButton;

    private static double[][] buffer = new double[3][101]; // Used to store the 101 last readings

    public GraphFragment()
    {
        for (int i = 0; i < 3; i++)
            for (int i2 = 0; i2 < buffer[i].length; i2++)
                buffer[i][i2] = 0d;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.graph, container, false);

        GraphViewData[] data0 = new GraphViewData[101];
        GraphViewData[] data1 = new GraphViewData[101];
        GraphViewData[] data2 = new GraphViewData[101];

        for (int i = 0; i < 101; i++)
        { // Restore last data
            data0[i] = new GraphViewData(counter - 100 + i, buffer[0][i]);
            data1[i] = new GraphViewData(counter - 100 + i, buffer[1][i]);
            data2[i] = new GraphViewData(counter - 100 + i, buffer[2][i]);
        }

        CH1 = new GraphViewSeries("通道1", new GraphViewSeriesStyle(Color.RED, 2), data0);
        CH2 = new GraphViewSeries("通道2", new GraphViewSeriesStyle(Color.GREEN, 2), data1);
        CH3 = new GraphViewSeries("通道3", new GraphViewSeriesStyle(Color.YELLOW, 2), data2);

        graphView = new LineGraphView(getActivity(), "");
        if (mCheckBox1 != null)
        {
            if (mCheckBox1.isChecked())
                graphView.addSeries(CH1);
        } else
            graphView.addSeries(CH1);
        if (mCheckBox2 != null)
        {
            if (mCheckBox2.isChecked())
                graphView.addSeries(CH2);
        } else
            graphView.addSeries(CH2);
        if (mCheckBox3 != null)
        {
            if (mCheckBox3.isChecked())
                graphView.addSeries(CH3);
        } else
            graphView.addSeries(CH3);

        graphView.setManualYAxisBounds(200, -200);
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
                    graphView.addSeries(CH1);
                else
                    graphView.removeSeries(CH1);
            }
        });
        mCheckBox2 = (CheckBox) v.findViewById(R.id.checkBox2);
        mCheckBox2.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (((CheckBox) v).isChecked())
                    graphView.addSeries(CH2);
                else
                    graphView.removeSeries(CH2);
            }
        });
        mCheckBox3 = (CheckBox) v.findViewById(R.id.checkBox3);
        mCheckBox3.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (((CheckBox) v).isChecked())
                    graphView.addSeries(CH3);
                else
                    graphView.removeSeries(CH3);
            }
        });


        edt1 = (EditText) v.findViewById(R.id.editText1);
        edt2 = (EditText) v.findViewById(R.id.editText2);

        edt1.addTextChangedListener(this);
        edt2.addTextChangedListener(this);

        mToggleButton = (ToggleButton) v.findViewById(R.id.on_off);
        mToggleButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (MainActivity.mChatService != null)
                {
                    if (MainActivity.mChatService.getState() == BluetoothChatService.STATE_CONNECTED
                            && MainActivity.checkTab(ViewPagerAdapter.GRAPH_FRAGMENT))
                    {

                    }
                }
            }
        });


        if (MainActivity.mChatService != null)
        {
            if (MainActivity.mChatService.getState() == BluetoothChatService.STATE_CONNECTED && MainActivity.checkTab(ViewPagerAdapter.GRAPH_FRAGMENT))
            {
//                    MainActivity.mChatService.write(MainActivity.imuBegin); // Request data
            }
        }

        return v;
    }


    public static void updateIMUValues()
    {
        if (mToggleButton == null)
            return;
        if (!(mToggleButton.isChecked()))
            return;

        for (int i = 0; i < 3; i++)
            System.arraycopy(buffer[i], 1, buffer[i], 0, 100);

        try
        { // In some rare occasions the values can be corrupted
            buffer[0][100] = Double.parseDouble(MainActivity.accValue);
            buffer[1][100] = Double.parseDouble(MainActivity.gyroValue);
            buffer[2][100] = Double.parseDouble(MainActivity.kalmanValue);
        } catch (NumberFormatException e)
        {
            if (D)
                Log.e(TAG, "error in input", e);
            return;
        }

        boolean scroll = mCheckBox1.isChecked() || mCheckBox2.isChecked() || mCheckBox3.isChecked();

        counter++;
        CH1.appendData(new GraphViewData(counter, buffer[0][100]), scroll, 101);
        CH2.appendData(new GraphViewData(counter, buffer[1][100]), scroll, 101);
        CH3.appendData(new GraphViewData(counter, buffer[2][100]), scroll, 101);

        if (!scroll)
            graphView.redrawAll();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (MainActivity.mChatService != null)
        {
            if (MainActivity.mChatService.getState() == BluetoothChatService.STATE_CONNECTED && MainActivity.checkTab(ViewPagerAdapter.GRAPH_FRAGMENT))
            {
//                    MainActivity.mChatService.write(MainActivity.imuBegin); // Request data
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
    {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
    {

    }

    @Override
    public void afterTextChanged(Editable editable)
    {
        int b1, b2;
        try
        {
            b1 = Integer.parseInt(String.valueOf(edt1.getText()));
            b2 = Integer.parseInt(String.valueOf(edt2.getText()));

            graphView.setManualYAxisBounds(b2, b1);
            graphView.redrawAll();
        } catch (NumberFormatException e)
        {
            MainActivity.showToast("数字格式不正确!", Toast.LENGTH_SHORT);
        }
    }
}