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

/*
 * This is a custom ViewPager that allows me to enable and disable the ViewPager
 * When the button is pressed
 * Source: http://blog.svpino.com/2011/08/disabling-pagingswiping-on-android.html
 */

package xyz.pengzhihui.BluetoothTouch;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CustomViewPager extends ViewPager
{
    private static boolean pagerEnabled;

    public CustomViewPager(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        pagerEnabled = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return pagerEnabled && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event)
    {
        return pagerEnabled && super.onInterceptTouchEvent(event);
    }

    public static void setPagingEnabled(boolean e)
    {
        pagerEnabled = e;
    }
}