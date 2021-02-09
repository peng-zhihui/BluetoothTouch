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
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.pengzhihui.BTremote.R;

public class TerminalFragment extends SherlockFragment
{
    private static final String TAG = "TerminalFragment";
    private static final boolean D = MainActivity.D;

    public static Handler mHandler = new Handler();

    public static TextView data_tv;
    public static StringBuffer data;
    public static boolean hasNew = false;
    private ScrollView sc;

    private Button bt;
    private EditText senBox;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.terminal, container, false);

        data_tv = (TextView) v.findViewById(R.id.data_tv);
        data = new StringBuffer();
        sc = (ScrollView) v.findViewById(R.id.sc);

        senBox = (EditText) v.findViewById(R.id.send_box);
        bt = (Button) v.findViewById(R.id.send_button);
        bt.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (MainActivity.mChatService != null)
                    MainActivity.mChatService.write(String.valueOf(senBox.getText()));
            }
        });

        mHandler = new Handler(new Handler.Callback()
        {
            @Override
            public boolean handleMessage(Message message)
            {
                data_tv.setText(data);
                sc.fullScroll(ScrollView.FOCUS_DOWN);
                return false;
            }
        });

        return v;
    }


    @Override
    public void onResume()
    {
        super.onResume();
        // When the user resumes the view, then set the values again
        if (MainActivity.mChatService != null)
        {
            //if (MainActivity.mChatService.getState() == BluetoothChatService.STATE_CONNECTED)
        }
    }
}