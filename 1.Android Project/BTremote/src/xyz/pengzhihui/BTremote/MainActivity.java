package xyz.pengzhihui.BTremote;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import xyz.pzh.BTremote.Upload;

import com.viewpagerindicator.UnderlinePageIndicator;

public class MainActivity extends SherlockFragmentActivity implements ActionBar.TabListener
{
    private static final String TAG = "MainActivity";
    public static final boolean D = true; // This is automatically set when building

    public static Activity activity;
    public static Context context;
    private static Toast mToast;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_DEVICE_NAME = 3;
    public static final int MESSAGE_DISCONNECTED = 4;
    public static final int MESSAGE_RETRY = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothHandler mBluetoothHandler = null;
    // Member object for the chat services
    public static BluetoothChatService mChatService = null;
    public static SensorFusion mSensorFusion = null;

    BluetoothDevice btDevice; // The BluetoothDevice object
    boolean btSecure; // If it's a new device we will pair with the device
    public static boolean stopRetrying;

    private UnderlinePageIndicator mUnderlinePageIndicator;
    public static int currentTabSelected;

    public static String accValue = "";
    public static String gyroValue = "";
    public static String kalmanValue = "";
    public static boolean newIMUValues;

    public static String Qangle = "";
    public static String Qbias = "";
    public static String Rmeasure = "";
    public static boolean newKalmanValues;

    public static String pValue = "";
    public static String iValue = "";
    public static String dValue = "";
    public static String targetAngleValue = "";
    public static boolean newPIDValues;

    public static boolean backToSpot;
    public static int maxAngle = 8; // Eight is the default value
    public static int maxTurning = 20; // Twenty is the default value

    public static String appVersion;
    public static String firmwareVersion;
    public static String eepromVersion;
    public static String mcu;
    public static boolean newInfo;

    public static String batteryLevel;
    public static double runtime;
    public static boolean newStatus;

    public static boolean pairingWithDevice;

    public static boolean buttonState;
    public static boolean joystickReleased;

    public final static String getPIDValues = "GP;";
    public final static String getSettings = "GS;";
    public final static String getInfo = "GI;";
    public final static String getKalman = "GK;";

    public final static String setPValue = "SP,";
    public final static String setIValue = "SI,";
    public final static String setDValue = "SD,";
    public final static String setKalman = "SK,";
    public final static String setTargetAngle = "ST,";
    public final static String setMaxAngle = "SA,";
    public final static String setMaxTurning = "SU,";
    public final static String setBackToSpot = "SB,";

    public final static String imuBegin = "IB;";
    public final static String imuStop = "IS;";

    public final static String statusBegin = "RB;";
    public final static String statusStop = "RS;";

    public final static String sendStop = "CS;";
    public final static String sendIMUValues = "CM,";
    public final static String sendJoystickValues = "CJ,";
    public final static String sendPairWithWii = "CPW;";
    public final static String sendPairWithPS4 = "CPP;";

    public final static String restoreDefaultValues = "CR;";

    public final static String responsePIDValues = "P";
    public final static String responseKalmanValues = "K";
    public final static String responseSettings = "S";
    public final static String responseInfo = "I";
    public final static String responseIMU = "V";
    public final static String responseStatus = "R";
    public final static String responsePairConfirmation = "PC";

    public final static int responsePIDValuesLength = 5;
    public final static int responseKalmanValuesLength = 4;
    public final static int responseSettingsLength = 4;
    public final static int responseInfoLength = 4;
    public final static int responseIMULength = 4;
    public final static int responseStatusLength = 3;
    public final static int responsePairConfirmationLength = 1;


    public static int mode;

    @Override
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        activity = this;
        context = getApplicationContext();

        if (!getResources().getBoolean(R.bool.isTablet))
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // Set portrait mode only - for small screens like phones
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_USER); // Full screen rotation
            else
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR); // Full screen rotation
            new Handler().postDelayed(new Runnable()
            { // Hack to hide keyboard when the layout it rotated
                @Override
                public void run()
                {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); // Hide the keyboard - this is needed when the device is rotated
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getApplicationWindowToken(), 0);
                }
            }, 1000);
        }

        setContentView(R.layout.activity_main);

        // Get local Bluetooth adapter
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
            mBluetoothAdapter = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        else
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            showToast("Bluetooth is not available", Toast.LENGTH_LONG);
            finish();
            return;
        }

        // get sensorManager and initialize sensor listeners
        SensorManager mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorFusion = new SensorFusion(getApplicationContext(), mSensorManager);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Create the adapter that will return a fragment for each of the primary sections of the app.
        ViewPagerAdapter mViewPagerAdapter = new ViewPagerAdapter(getApplicationContext(), getSupportFragmentManager());

        // Set up the ViewPager with the adapter.
        CustomViewPager mViewPager = (CustomViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mViewPagerAdapter);

        if (getResources().getBoolean(R.bool.isTablet))
            mViewPager.setOffscreenPageLimit(2); // Since two fragments is selected in landscape mode, this is used to smooth things out

        // Bind the underline indicator to the adapter
        mUnderlinePageIndicator = (UnderlinePageIndicator) findViewById(R.id.indicator);
        mUnderlinePageIndicator.setViewPager(mViewPager);
        mUnderlinePageIndicator.setFades(false);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mUnderlinePageIndicator
                .setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
                {
                    @Override
                    public void onPageSelected(int position)
                    {
                        if (D)
                            Log.d(TAG, "ViewPager position: " + position);
                        if (position < actionBar.getTabCount()) // Needed for when in landscape mode
                            actionBar.setSelectedNavigationItem(position);
                        else
                            mUnderlinePageIndicator.setCurrentItem(position - 1);
                    }
                });

        int count = mViewPagerAdapter.getCount();
        Resources mResources = getResources();
        boolean landscape = false;
        if (mResources.getBoolean(R.bool.isTablet) && mResources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            landscape = true;
            count -= 1; // There is one less tab when in landscape mode
        }

        for (int i = 0; i < count; i++) { // For each of the sections in the app, add a tab to the action bar
            String text;
            if (landscape && i == count - 1)
                text = mViewPagerAdapter.getPageTitle(i) + " & " + mViewPagerAdapter.getPageTitle(i + 1); // Last tab in landscape mode have two titles in one tab
            else
                text = mViewPagerAdapter.getPageTitle(i).toString();

            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(actionBar.newTab()
                    .setText(text)
                    .setTabListener(this));
        }
        try {
            PackageManager mPackageManager = getPackageManager();
            if (mPackageManager != null)
                MainActivity.appVersion = mPackageManager.getPackageInfo(getPackageName(), 0).versionName; // Read the app version name
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        setTitle("移动摇杆来遥控");
    }

    @Override
    public void onStart()
    {
        super.onStart();
        if (D)
            Log.d(TAG, "++ ON START ++");
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            if (D)
                Log.d(TAG, "Request enable BT");
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else
            setupBTService(); // Otherwise, setup the chat session

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this); // Create SharedPreferences instance
        String filterCoefficient = preferences.getString("filterCoefficient", null); // Read the stored value for filter coefficient
        if (filterCoefficient != null) {
            mSensorFusion.filter_coefficient = Float.parseFloat(filterCoefficient);
            mSensorFusion.tempFilter_coefficient = mSensorFusion.filter_coefficient;
        }
        // Read the previous back to spot value
        backToSpot = preferences.getBoolean("backToSpot", true); // Back to spot is true by default
        // Read the previous max angle
        maxAngle = preferences.getInt("maxAngle", 8); // Eight is the default value
        // Read the previous max turning value
        maxTurning = preferences.getInt("maxTurning", 20); // Twenty is the default value
    }

    @Override
    public void onStop()
    {
        super.onStop();
        if (D)
            Log.d(TAG, "-- ON STOP --");
        // unregister sensor listeners to prevent the activity from draining the
        // device's battery.
        mSensorFusion.unregisterListeners();

        // Store the value for FILTER_COEFFICIENT and max angle at shutdown
        Editor edit = PreferenceManager.getDefaultSharedPreferences(this).edit();
        edit.putString("filterCoefficient", Float.toString(mSensorFusion.filter_coefficient));
        edit.putBoolean("backToSpot", backToSpot);
        edit.putInt("maxAngle", maxAngle);
        edit.putInt("maxTurning", maxTurning);
        edit.apply();
    }

    @Override
    public void onBackPressed()
    {
        Upload.close(); // Close serial communication
        if (mChatService != null) {
            new Handler().postDelayed(new Runnable()
            {
                public void run()
                {
                    mChatService.stop(); // Stop the Bluetooth chat services if the user exits the app
                }
            }, 1000); // Wait 1 second before closing the connection, this is needed as onPause() will send stop messages before closing
        }
        finish(); // Exits the app
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (D)
            Log.d(TAG, "--- ON DESTROY ---");
        mSensorFusion.unregisterListeners();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (D)
            Log.d(TAG, "- ON PAUSE -");
        // Unregister sensor listeners to prevent the activity from draining the device's battery.
        mSensorFusion.unregisterListeners();
        if (mChatService != null) { // Send stop command and stop sending graph data command
            if (mChatService.getState() == BluetoothChatService.STATE_CONNECTED)
                mChatService.write(sendStop + imuStop + statusStop);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (D)
            Log.d(TAG, "+ ON RESUME +");
        // Restore the sensor listeners when user resumes the application.
        mSensorFusion.initListeners();
    }

    private void setupBTService()
    {
        if (mChatService != null)
            return;

        if (D)
            Log.d(TAG, "setupBTService()");
        if (mBluetoothHandler == null)
            mBluetoothHandler = new BluetoothHandler(this);
        mChatService = new BluetoothChatService(mBluetoothHandler, mBluetoothAdapter); // Initialize the BluetoothChatService to perform Bluetooth connections
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
    {
        if (D)
            Log.d(TAG, "onTabSelected: " + tab.getPosition());
        currentTabSelected = tab.getPosition();

        Resources mResources = getResources();
        if (mResources.getBoolean(R.bool.isTablet) && mResources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && currentTabSelected == ViewPagerAdapter.INFO_FRAGMENT) { // Check if the last tab is selected in landscape mode
            currentTabSelected -= 1; // If so don't go any further
            ActionBar bar = getSupportActionBar();
            bar.selectTab(bar.getTabAt(currentTabSelected));
        }

        mUnderlinePageIndicator.setCurrentItem(currentTabSelected); // When the given tab is selected, switch to the corresponding page in the ViewPager
        CustomViewPager.setPagingEnabled(true);
        if (checkTab(ViewPagerAdapter.GRAPH_FRAGMENT) && mChatService != null) {
            if (mChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
                mChatService.write(getKalman);
                if (GraphFragment.mToggleButton != null) {
                    if (GraphFragment.mToggleButton.isChecked())
                        mChatService.write(imuBegin); // Request data
                    else
                        mChatService.write(imuStop); // Stop sending data
                }
            }
        } else if (checkTab(ViewPagerAdapter.INFO_FRAGMENT) && mChatService != null) {
            if (mChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
                mChatService.write(getInfo); // Update info
                if (InfoFragment.mToggleButton != null) {
                }
            }
        }

        if (checkTab(ViewPagerAdapter.JOYSTICK_FRAGMENT)) {
            mode = ViewPagerAdapter.JOYSTICK_FRAGMENT;
            setTitle("移动摇杆来遥控");
        } else if (checkTab(ViewPagerAdapter.IMU_FRAGMENT)) {
            mode = ViewPagerAdapter.IMU_FRAGMENT;
            setTitle("倾斜手机来遥控");
        } else if (checkTab(ViewPagerAdapter.TERMINAL_FRAGMENT)) {
            mode = ViewPagerAdapter.TERMINAL_FRAGMENT;
            setTitle("数据透传模式");
        } else if (checkTab(ViewPagerAdapter.GRAPH_FRAGMENT)) {
            mode = ViewPagerAdapter.GRAPH_FRAGMENT;
            setTitle("注意显示范围");
        } else if (checkTab(ViewPagerAdapter.INFO_FRAGMENT)) {
            mode = ViewPagerAdapter.INFO_FRAGMENT;
            setTitle("关于本APP");
        }


        if (checkTab(ViewPagerAdapter.GRAPH_FRAGMENT)) { // Needed when the user rotates the screen
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); // Hide the keyboard
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getApplicationWindowToken(), 0);
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
    {
        if (D)
            Log.d(TAG, "onTabUnselected: " + tab.getPosition() + " " + currentTabSelected);
        if ((checkTab(ViewPagerAdapter.IMU_FRAGMENT) || checkTab(ViewPagerAdapter.JOYSTICK_FRAGMENT)) && mChatService != null) { // Send stop command if the user selects another tab
            if (mChatService.getState() == BluetoothChatService.STATE_CONNECTED)
                mChatService.write(sendStop);
        } else if (checkTab(ViewPagerAdapter.GRAPH_FRAGMENT) && mChatService != null) {
            if (mChatService.getState() == BluetoothChatService.STATE_CONNECTED)
                mChatService.write(imuStop);
        } else if (checkTab(ViewPagerAdapter.INFO_FRAGMENT) && mChatService != null) {
            if (mChatService.getState() == BluetoothChatService.STATE_CONNECTED)
                mChatService.write(statusStop);
        }
        if (checkTab(ViewPagerAdapter.GRAPH_FRAGMENT)) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); // Hide the keyboard
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getApplicationWindowToken(), 0);
        }
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab,
                                FragmentTransaction fragmentTransaction)
    {
    }

    public static boolean checkTab(int tab)
    {
        return (currentTabSelected == tab || (context.getResources().getBoolean(R.bool.isTablet) && context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && currentTabSelected == tab - 1)); // Check the tab to the left as well in landscape mode
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        if (D)
            Log.d(TAG, "onPrepareOptionsMenu");
        MenuItem menuItem = menu.findItem(R.id.menu_connect); // Find item
        if (mChatService != null && mChatService.getState() == BluetoothChatService.STATE_CONNECTED)
            menuItem.setIcon(R.drawable.device_access_bluetooth_connected);
        else
            menuItem.setIcon(R.drawable.device_access_bluetooth);

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        if (D)
            Log.d(TAG, "onCreateOptionsMenu");
        getSupportMenuInflater().inflate(R.menu.menu, menu); // Inflate the menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.menu_connect:
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                return true;
            case R.id.menu_settings:
                // Open up the settings dialog
                //SettingsDialogFragment dialogFragment = new SettingsDialogFragment();
                //dialogFragment.show(getSupportFragmentManager(), null);
                return true;
            case android.R.id.home:
                //Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.baidu.com/"));
                //startActivity(browserIntent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static int getRotation()
    {
        return activity.getWindowManager().getDefaultDisplay().getRotation();
    }

    public static void showToast(String message, int duration)
    {
        if (duration != Toast.LENGTH_SHORT && duration != Toast.LENGTH_LONG)
            throw new IllegalArgumentException();
        if (mToast != null)
            mToast.cancel(); // Close the toast if it's already open
        mToast = Toast.makeText(context, message, duration);
        mToast.show();
    }

    // The Handler class that gets information back from the BluetoothChatService
    static class BluetoothHandler extends Handler
    {
        private final MainActivity mMainActivity;
        private String mConnectedDeviceName; // Name of the connected device

        BluetoothHandler(MainActivity mMainActivity)
        {
            this.mMainActivity = mMainActivity;
        }

        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    mMainActivity.supportInvalidateOptionsMenu();
                    if (D)
                        Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            MainActivity.showToast(mMainActivity.getString(R.string.connected_to) + " " + mConnectedDeviceName, Toast.LENGTH_SHORT);
                            if (mChatService == null)
                                return;
                            Handler mHandler = new Handler();
                            mHandler.postDelayed(new Runnable()
                            {
                                public void run()
                                {
                                    //mChatService.write(getPIDValues + getSettings + getInfo + getKalman);
                                }
                            }, 1000); // Wait 1 second before sending the message
                            if (GraphFragment.mToggleButton != null) {
                                if (GraphFragment.mToggleButton.isChecked() && checkTab(ViewPagerAdapter.GRAPH_FRAGMENT)) {
                                    mHandler.postDelayed(new Runnable()
                                    {
                                        public void run()
                                        {
                                            //mChatService.write(imuBegin); // Request data
                                        }
                                    }, 1000); // Wait 1 second before sending the message
                                } else {
                                    mHandler.postDelayed(new Runnable()
                                    {
                                        public void run()
                                        {
                                            //mChatService.write(imuStop); // Stop sending data
                                        }
                                    }, 1000); // Wait 1 second before sending the message
                                }
                            }
                            if (checkTab(ViewPagerAdapter.INFO_FRAGMENT)) {
                                mHandler.postDelayed(new Runnable()
                                {
                                    public void run()
                                    {
                                        //mChatService.write(statusBegin); // Request data
                                    }
                                }, 1000); // Wait 1 second before sending the message
                            }
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            break;
                    }
                    break;
                case MESSAGE_READ:
                    if (newPIDValues) {
                        newPIDValues = false;
                    }
                    if (newInfo || newStatus) {
                        newInfo = false;
                        newStatus = false;
                        InfoFragment.updateView();
                    }
                    if (newIMUValues) {
                        newIMUValues = false;
                        GraphFragment.updateIMUValues();
                    }
                    if (newKalmanValues) {
                        newKalmanValues = false;
                    }
                    if (pairingWithDevice) {
                        pairingWithDevice = false;
                        MainActivity.showToast("Now enable discovery of your device", Toast.LENGTH_LONG);
                    }
                    break;
                case MESSAGE_DEVICE_NAME:
                    // Save the connected device's name
                    if (msg.getData() != null)
                        mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    break;
                case MESSAGE_DISCONNECTED:
                    mMainActivity.supportInvalidateOptionsMenu();
                    if (msg.getData() != null)
                        MainActivity.showToast(msg.getData().getString(TOAST), Toast.LENGTH_SHORT);
                    break;
                case MESSAGE_RETRY:
                    if (D)
                        Log.d(TAG, "MESSAGE_RETRY");
                    mMainActivity.connectDevice(null, true);
                    break;
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (D)
            Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect to
                if (resultCode == Activity.RESULT_OK)
                    connectDevice(data, false);
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK)
                    setupBTService(); // Bluetooth is now enabled, so set up a chat session
                else {
                    // User did not enable Bluetooth or an error occured
                    if (D)
                        Log.d(TAG, "BT not enabled");
                    showToast(getString(R.string.bt_not_enabled_leaving), Toast.LENGTH_SHORT);
                    finish();
                }
        }
    }

    private void connectDevice(Intent data, boolean retry)
    {
        if (retry) {
            if (btDevice != null && !stopRetrying) {
                mChatService.start(); // This will stop all the running threads
                mChatService.connect(btDevice, btSecure); // Attempt to connect to the device
            }
        } else { // It's a new connection
            stopRetrying = false;
            mChatService.newConnection = true;
            mChatService.start(); // This will stop all the running threads
            if (data.getExtras() == null)
                return;
            String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS); // Get the device Bluetooth address
            btSecure = data.getExtras().getBoolean(DeviceListActivity.EXTRA_NEW_DEVICE); // If it's a new device we will pair with the device
            btDevice = mBluetoothAdapter.getRemoteDevice(address); // Get the BluetoothDevice object
            mChatService.nRetries = 0; // Reset retry counter
            mChatService.connect(btDevice, btSecure); // Attempt to connect to the device
            showToast(getString(R.string.connecting), Toast.LENGTH_SHORT);
        }
    }
}