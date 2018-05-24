/******************************************************************************
 * Copyright 2015 James Mason                                                 *
 *                                                                            *
 *   Licensed under the Apache License, Version 2.0 (the "License");          *
 *   you may not use this file except in compliance with the License.         *
 *   You may obtain a copy of the License at                                  *
 *                                                                            *
 *       http://www.apache.org/licenses/LICENSE-2.0                           *
 *                                                                            *
 *   Unless required by applicable law or agreed to in writing, software      *
 *   distributed under the License is distributed on an "AS IS" BASIS,        *
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. *
 *   See the License for the specific language governing permissions and      *
 *   limitations under the License.                                           *
 ******************************************************************************/

package net.segv11.bootunlocker;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * For logging
     */
    private static final String TAG = "net.segv11.mainActivity";
    private static final Boolean dontCare = false;
    private bootLoader theBootLoader = null;
    protected Button lockButton;
    protected Button unlockButton;
    protected Button setButton;
    protected Button clearButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sf = getSharedPreferences(getString(R.string.preferences_settings),MODE_PRIVATE);
        Boolean useLightTheme = sf.getBoolean(getString(R.string.preferences_key_light_theme),false);
        if(useLightTheme)
            setTheme(R.style.AppThemeLight);
        else setTheme(R.style.AppThemeDark);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lockButton = (Button) findViewById(R.id.lockButton);
        unlockButton = (Button) findViewById(R.id.unlockButton);
        setButton = (Button) findViewById(R.id.setButton);
        clearButton = (Button) findViewById(R.id.clearButton);

        lockButton.setOnClickListener(this);
        unlockButton.setOnClickListener(this);
        setButton.setOnClickListener(this);
        unlockButton.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        SharedPreferences sf;
        switch (id){
            case R.id.light_theme:
                sf = getSharedPreferences("settings",MODE_PRIVATE);
                sf.edit().putBoolean("light_theme",true).apply();
                recreate();
                break;
            case R.id.dark_theme:
                sf = getSharedPreferences("settings",MODE_PRIVATE);
                sf.edit().putBoolean("light_theme",false).apply();
                recreate();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     *
     * Called at the start of the visible lifetime
     */
    @Override
    public void onStart() {
        super.onStart();
        Log.v(TAG, "handling onStart ");
        Boolean setState = false;
        Boolean desiredState = dontCare;
        Boolean setTamperFlag = false;
        Boolean desiredTamperFlag = dontCare;
        theBootLoader = bootLoader.makeBootLoader();


        TextView versionID = (TextView) findViewById(R.id.versionID);
        TextView modelID = (TextView) findViewById(R.id.modelID);
        TextView deviceID = (TextView) findViewById(R.id.deviceID);
        TextView bootloaderID = (TextView) findViewById(R.id.bootloaderID);

        try {
            versionID.setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
            modelID.setText(android.os.Build.MODEL);
            deviceID.setText(android.os.Build.DEVICE);
            bootloaderID.setText(android.os.Build.BOOTLOADER);
        } catch (PackageManager.NameNotFoundException e) {
            // Auto-generated catch block
            e.printStackTrace();
        }


        new AsyncBootLoader().execute(setState, desiredState, setTamperFlag, desiredTamperFlag);
    }

    /**
     * Called from UI to unlock the bootloader
     */
    public void doUnlockBootloader() {
        Boolean setState = true;
        Boolean desiredState = false;
        Boolean setTamperFlag = false;
        Boolean desiredTamperFlag = dontCare;
        new AsyncBootLoader().execute(setState, desiredState, setTamperFlag, desiredTamperFlag);
    }

    /**
     * Called from UI to lock the bootloader
     */
    public void doLockBootloader() {
        Boolean setState = true;
        Boolean desiredState = true;
        Boolean setTamperFlag = false;
        Boolean desiredTamperFlag = dontCare;
        new AsyncBootLoader().execute(setState, desiredState, setTamperFlag, desiredTamperFlag);
    }

    /**
     * Called from UI to clear tamper flag
     */
    public void doClearTamper() {
        Boolean setState = false;
        Boolean desiredState = dontCare;
        Boolean setTamperFlag = true;
        Boolean desiredTamperFlag = false;
        new AsyncBootLoader().execute(setState, desiredState, setTamperFlag, desiredTamperFlag);
    }

    /**
     * Called from UI to set tamper flag
     */
    public void doSetTamper() {
        Boolean setState = false;
        Boolean desiredState = dontCare;
        Boolean setTamperFlag = true;
        Boolean desiredTamperFlag = true;
        new AsyncBootLoader().execute(setState, desiredState, setTamperFlag, desiredTamperFlag);
    }


    /***
     * Handle clicks
     */

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.lockButton:
                doLockBootloader();
                break;
            case R.id.unlockButton:
                doUnlockBootloader();
                break;
            case R.id.setButton:
                doSetTamper();
                break;
            case R.id.clearButton:
                doClearTamper();
                break;
        }
    }
    private class AsyncBootLoader extends AsyncTask<Boolean, Void, Integer> {
        /* Ideas for the future:
    	 * 		Can we receive a broadcast intent when someone ELSE tweaks the param partition?
    	 * 		Do we want a progress indicator in case su takes a long time?
    	 *		Set text colors for lock status?
    	 */

        @Override
        protected Integer doInBackground(Boolean... booleans) {
            Boolean setState = booleans[0];
            Boolean desiredState = booleans[1];
            Boolean setTamperFlag = booleans[2];
            Boolean desiredTamperFlag = booleans[3];

            // If this device is incompatible, return immediately.
            if (theBootLoader == null) {
                return Integer.valueOf(bootLoader.BL_UNSUPPORTED_DEVICE);
            }

            // If we need to change the bootloader state, then do so.
            if (setState) {
                try {
                    theBootLoader.setLockState(desiredState);
                } catch (IOException e) {
                    if (desiredState) {
                        Log.e(TAG, "Caught IOException locking: " + e);
                    } else {
                        Log.e(TAG, "Caught IOException unlocking: " + e);
                    }
                }

                // Since we changed the bootloader lock state, we will pause for
                // it to take effect.  This is OK because we are on a background
                // thread.
                try {
                    Thread.sleep(bootLoader.delayAfterChange);
                } catch (InterruptedException e) {
                    // Should not happen; if it does, we just keep going.
                    e.printStackTrace();
                }
            }

            if (theBootLoader.hasTamperFlag() && setTamperFlag) {
                try {
                    theBootLoader.setTamperFlag(desiredTamperFlag);
                } catch (IOException e) {
                    if (desiredTamperFlag) {
                        Log.e(TAG, "Caught IOException setting tamper flag: " + e);
                    } else {
                        Log.e(TAG, "Caught IOException clearing tamper flag: " + e);
                    }
                }

                // Since we changed the bootloader tamper flag, we will pause for
                // it to take effect.  This is OK because we are on a background
                // thread.
                try {
                    Thread.sleep(bootLoader.delayAfterChange);
                } catch (InterruptedException e) {
                    // Should not happen; if it does, we just keep going.
                    e.printStackTrace();
                }
            }


            // Now query the bootloader lock state and tamper flag.
            return Integer.valueOf(theBootLoader.getBootLoaderState());
        } // doInBackground


        /*
         *  We don't override onProgresUpdate, because we're not
         *  taking that long
         */
        @Override
        protected void onPostExecute(Integer resultObj) {
            int result = resultObj.intValue();

            TextView bootLoaderStatusText = (TextView) findViewById(R.id.bootLoaderStatusText);
            TextView bootLoaderTamperFlagText = (TextView) findViewById(R.id.tamperFlagText);

            CardView tamperLL = findViewById(R.id.tamperLayout);

            ImageView bootLoaderStatusIcon = findViewById(R.id.boot_loader_status_iv);
            if (result == bootLoader.BL_UNLOCKED || result == bootLoader.BL_TAMPERED_UNLOCKED) {
                bootLoaderStatusText.setText(R.string.stat_unlocked);
                bootLoaderStatusIcon.setImageResource(R.drawable.ic_unlock);
                lockButton.setEnabled(true);
                unlockButton.setEnabled(true);

            } else if (result == bootLoader.BL_LOCKED || result == bootLoader.BL_TAMPERED_LOCKED) {
                bootLoaderStatusText.setText(R.string.stat_locked);
                bootLoaderStatusIcon.setImageResource(R.drawable.ic_lock);
                lockButton.setEnabled(true);
                unlockButton.setEnabled(true);

            } else if (result == bootLoader.BL_UNSUPPORTED_DEVICE) {
                bootLoaderStatusText.setText(R.string.stat_unknown_device);
                lockButton.setEnabled(false);
                lockButton.setTextColor(getColor(R.color.gray));
                unlockButton.setEnabled(false);
                unlockButton.setTextColor(getColor(R.color.gray));
                Resources res = getResources();
                Snackbar.make(tamperLL.getRootView(),String.format(
                        res.getString(R.string.extra_unknown_device),
                        android.os.Build.DEVICE),Snackbar.LENGTH_LONG)
                        .show();
            } else {
                bootLoaderStatusText.setText(R.string.stat_no_root);
                bootLoaderStatusIcon.setImageResource(R.drawable.ic_unsupported_device);
                lockButton.setEnabled(false);
                unlockButton.setEnabled(false);
            }

            if (theBootLoader != null && theBootLoader.hasTamperFlag()) {
                if (result == bootLoader.BL_LOCKED || result == bootLoader.BL_UNLOCKED) {
                    bootLoaderTamperFlagText.setText(R.string.stat_not_tampered);
                    setButton.setEnabled(true);
                    clearButton.setEnabled(true);
                } else if (result == bootLoader.BL_TAMPERED_LOCKED || result == bootLoader.BL_TAMPERED_UNLOCKED) {
                    bootLoaderTamperFlagText.setText(R.string.stat_tampered);
                    setButton.setEnabled(true);
                    clearButton.setEnabled(true);
                } else {
                    setButton.setEnabled(false);
                    clearButton.setEnabled(false);
                }
                tamperLL.setVisibility(View.VISIBLE);
                setButton.setVisibility(View.VISIBLE);
                clearButton.setVisibility(View.VISIBLE);
            } else {
                tamperLL.setVisibility(View.GONE);
                setButton.setVisibility(View.GONE);
                clearButton.setVisibility(View.GONE);
            }

        } // onPostExecute
    } // AsyncBootLoader
} // MainActivity
