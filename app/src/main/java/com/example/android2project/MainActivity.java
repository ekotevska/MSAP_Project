package com.example.android2project;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.android2project.ReminderTasks.ACTION_INCREMENT_WATER_COUNT;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    IntentFilter mChargingIntentFilter;
    ChargingBroadcastReceiver mChargingReceiver;
    private TextView mWaterCountDisplay;
    private TextView mChargingCountDisplay;
    private ImageView mChargingImageView;
    private Toast mToast;
    private Button onOffSwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWaterCountDisplay = (TextView)findViewById(R.id.tv_water_count);
        mChargingCountDisplay = (TextView) findViewById(R.id.tv_charging_reminder_count);
        mChargingImageView = (ImageView) findViewById(R.id.iv_power_increment);
        onOffSwitch = (Button) findViewById(R.id.onOffSwitch);
        updateWaterCount();

        scheduleReminderService();

        PreferenceUtils.setDefaultSharedPreferenceReminderTime(this);

        onOffSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder =
                        new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setMessage(getString(R.string.stopRemiderDialogMessage));
                alertDialogBuilder.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                //ReminderUtils

                                Intent intent = new Intent(Intent.ACTION_MAIN);
                                intent.addCategory(Intent.CATEGORY_HOME);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        /** Setup the shared preference listener **/
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);

        /*
         * Setup and register the dynamic broadcast receiver
         */
        mChargingIntentFilter = new IntentFilter();
        mChargingReceiver = new ChargingBroadcastReceiver();
        mChargingIntentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        mChargingIntentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
    }
    @Override
    protected void onResume() {
        super.onResume();

        scheduleReminderService();
        /** Determine the current charging state **/
        //  Check if you are on Android M or later, if so...
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Get a BatteryManager instance using getSystemService()
            BatteryManager batteryManager = (BatteryManager) getSystemService(BATTERY_SERVICE);
            // Call isCharging on the battery manager and pass the result on to your show
            // charging method
            showCharging(batteryManager.isCharging());
        } else {
            // If your user is not on M+, then...

            // Create a new intent filter with the action ACTION_BATTERY_CHANGED. This is a
            // sticky broadcast that contains a lot of information about the battery state.
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            // Set a new Intent object equal to what is returned by registerReceiver, passing in null
            // for the receiver. Pass in your intent filter as well. Passing in null means that you're
            // getting the current state of a sticky broadcast - the intent returned will contain the
            // battery information you need.
            Intent currentBatteryStatusIntent = registerReceiver(null, ifilter);
            // Get the integer extra BatteryManager.EXTRA_STATUS. Check if it matches
            // BatteryManager.BATTERY_STATUS_CHARGING or BatteryManager.BATTERY_STATUS_FULL. This means
            // the battery is currently charging.
            int batteryStatus = currentBatteryStatusIntent.getIntExtra(
                    BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = batteryStatus == BatteryManager.BATTERY_STATUS_CHARGING ||
                    batteryStatus == BatteryManager.BATTERY_STATUS_FULL;
            // Update the UI using your showCharging method
            showCharging(isCharging);
        }

        /** Register the receiver for future state changes **/
        registerReceiver(mChargingReceiver, mChargingIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mChargingReceiver);
    }

    /**
     * Updates the TextView to display the new water count from SharedPreferences
     */
    private void updateWaterCount() {
        int waterCount = PreferenceUtils.getWaterCount(this);
        mWaterCountDisplay.setText(waterCount + "");
    }

    /**
     * Adds one to the water count and shows a toast
     */
    public void incrementWater(View view) {
        if (mToast != null) mToast.cancel();
        mToast = Toast.makeText(this, "Excellent!", Toast.LENGTH_SHORT);
        mToast.show();

        Intent intent = new Intent(this, ReminderIntentService.class);
        intent.setAction(ACTION_INCREMENT_WATER_COUNT);
        startService(intent);
    }

    private void showCharging(boolean isCharging) {
        if (isCharging) {
            mChargingImageView.setImageResource(R.drawable.ic_power_pink_80px);
        } else {
            mChargingImageView.setImageResource(R.drawable.ic_power_grey_80px);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /** Cleanup the shared preference listener **/
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * This is a listener that will update the UI when the water count or charging reminder counts
     * change
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (PreferenceUtils.KEY_WATER_COUNT.equals(key)) {
            updateWaterCount();
        } else if (PreferenceUtils.KEY_CHARGING_REMINDER_COUNT.equals(key)) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        if (id == R.id.action_reset) {
            ReminderTasks.resetWaterCount(this);
            ReminderTasks.resetChargingReminderCount(this);
            Toast.makeText(this,
                    R.string.resetHydrationCounterToastMessage, Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);

    }

    public void scheduleReminderService() {
        Intent intent = new Intent(this, ReminderUtils.class);
        startActivity(intent);

    }
/*
    public void scheduleReminderService() {

    }*/


    private class ChargingBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            boolean isCharging = TextUtils.equals(action, Intent.ACTION_POWER_CONNECTED);
            showCharging(isCharging);
        }
    }
}