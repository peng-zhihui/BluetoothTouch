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

import android.content.Context;
import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one
 * of the sections/tabs/pages.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter
{
    public static final int IMU_FRAGMENT = 0;
    public static final int JOYSTICK_FRAGMENT = 1;
    public static final int GRAPH_FRAGMENT = 2;
    public static final int PID_FRAGMENT = 3;
    public static final int INFO_FRAGMENT = 4;

    private Context context;

    public ViewPagerAdapter(Context context, FragmentManager fm)
    {
        super(fm);
        this.context = context;
    }

    @Override
    public float getPageWidth(int position)
    {
        if (context.getResources().getBoolean(R.bool.isTablet) && context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            return 0.5f; // On tablets in landscape mode two fragments are shown side by side
        else
            return 1.0f;
    }

    @Override
    public Fragment getItem(int position)
    {
        switch (position) {
            case 0:
                return new ImuFragment();
            case 1:
                return new JoystickFragment();
            case 2:
                return new GraphFragment();
            case 3:
                return new PIDFragment();
            case 4:
                return new InfoFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount()
    {
        // Return number of tabs
        return 5;
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        switch (position) {
            case 0:
                return "IMU";
            case 1:
                return "Joystick";
            case 2:
                return "Graph";
            case 3:
                return "PID";
            case 4:
                return "Info";
        }
        return null;
    }
}