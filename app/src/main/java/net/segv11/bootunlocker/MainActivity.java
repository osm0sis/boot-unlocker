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

import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends ActionBarActivity {

    /**
     * For logging
     */
    private static final String TAG = "net.segv11.mainActivity";
    private static final Boolean dontCare = false;
    private bootLoader theBootLoader = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    /**
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
    public void doUnlockBootloader(View v) {
        Boolean setState = true;
        Boolean desiredState = false;
        Boolean setTamperFlag = false;
        Boolean desiredTamperFlag = dontCare;
        new AsyncBootLoader().execute(setState, desiredState, setTamperFlag, desiredTamperFlag);
    }

    /**
     * Called from UI to lock the bootloader
     */
    public void doLockBootloader(View v) {
        Boolean setState = true;
        Boolean desiredState = true;
        Boolean setTamperFlag = false;
        Boolean desiredTamperFlag = dontCare;
        new AsyncBootLoader().execute(setState, desiredState, setTamperFlag, desiredTamperFlag);
    }

    /**
     * Called from UI to clear tamper flag
     */
    public void doClearTamper(View v) {
        Boolean setState = false;
        Boolean desiredState = dontCare;
        Boolean setTamperFlag = true;
        Boolean desiredTamperFlag = false;
        new AsyncBootLoader().execute(setState, desiredState, setTamperFlag, desiredTamperFlag);
    }

    /**
     * Called from UI to set tamper flag
     */
    public void doSetTamper(View v) {
        Boolean setState = false;
        Boolean desiredState = dontCare;
        Boolean setTamperFlag = true;
        Boolean desiredTamperFlag = true;
        new AsyncBootLoader().execute(setState, desiredState, setTamperFlag, desiredTamperFlag);
    }

    private class AsyncBootLoader extends AsyncTask<Boolean, Void, Integer> {
        /**
         * Ideas for the future:
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

        /**
         *  We don't override onProgresUpdate, because we're not
         *  taking that long
         */
        @Override
        protected void onPostExecute(Integer resultObj) {
            int result = resultObj.intValue();

            TextView bootLoaderStatusText = (TextView) findViewById(R.id.bootLoaderStatusText);
            TextView bootLoaderTamperFlagText = (TextView) findViewById(R.id.tamperFlagText);
            Button lockButton = (Button) findViewById(R.id.lockButton);
            Button unlockButton = (Button) findViewById(R.id.unlockButton);
            Button setButton = (Button) findViewById(R.id.setButton);
            Button clearButton = (Button) findViewById(R.id.clearButton);

            TextView extendedStatus = (TextView) findViewById(R.id.extendedStatus);
            LinearLayout tamperLL = (LinearLayout) findViewById(R.id.tamperLayout);

            if (result == bootLoader.BL_UNLOCKED || result == bootLoader.BL_TAMPERED_UNLOCKED) {
                bootLoaderStatusText.setText(R.string.stat_unlocked);
                lockButton.setEnabled(true);
                unlockButton.setEnabled(true);
                extendedStatus.setText("");
            } else if (result == bootLoader.BL_LOCKED || result == bootLoader.BL_TAMPERED_LOCKED) {
                bootLoaderStatusText.setText(R.string.stat_locked);
                lockButton.setEnabled(true);
                unlockButton.setEnabled(true);
                extendedStatus.setText("");
            } else if (result == bootLoader.BL_UNSUPPORTED_DEVICE) {
                bootLoaderStatusText.setText(R.string.stat_unknown_device);
                lockButton.setEnabled(false);
                unlockButton.setEnabled(false);
                Resources res = getResources();
                extendedStatus.setText(String.format(
                        res.getString(R.string.extra_unknown_device),
                        android.os.Build.DEVICE));
            } else {
                bootLoaderStatusText.setText(R.string.stat_no_root);
                lockButton.setEnabled(false);
                unlockButton.setEnabled(false);
                extendedStatus.setText("");
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
            } else {
                tamperLL.setVisibility(View.GONE);
            }

        } // onPostExecute
    } // AsyncBootLoader
} // MainActivity
