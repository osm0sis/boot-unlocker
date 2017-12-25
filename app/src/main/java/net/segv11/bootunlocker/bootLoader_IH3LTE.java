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

import android.util.Log;

import java.io.IOException;

/**
 * @description device-specific bootloader code for Infinix HOT 3 LTE phones
 */
public class bootLoader_IH3LTE extends bootLoader {
    /**
     * For logging
     */
    private static final String TAG = "net.segv11.bootLoader_IH3LTE";

    /**
     * The bit for unlocked bootloader is at ? in the aboot partition.
     * The tamper flag should be at ?.
     *
     * 01 for unlocked
     * 01 for tampered
     */

    /**
     * Private constants for working with the lock state in the aboot partition
     */
    private static final String queryCommand =
            "";
    private static final String writeCommand =
            "";

    private static final String queryTamperCommand =
            "";
    private static final String writeTamperCommand =
            "";

    /**
     * Locks or unlocks the bootloader
     */
    @Override
    public void setLockState(boolean newState) throws IOException {
        int outByte;
        if (newState) {
            outByte = 0;
            Log.i(TAG, "Locking bootloader by sending " + outByte + " to " + writeCommand);
        } else {
            outByte = 1;
            Log.i(TAG, "Unlocking bootloader by sending " + outByte + " to " + writeCommand);
        }

        superUserCommandWithDataByte(writeCommand, outByte);
    }

    /**
     * Does this bootloader support a tamper flag?
     */
    @Override
    public boolean hasTamperFlag() {
        return true;
    }

    /**
     * Sets or clears the tamper flag
     */
    @Override
    public void setTamperFlag(boolean newState) throws IOException {
        int outByte;
        if (newState) {
            outByte = 1;
            Log.i(TAG, "Setting tamper flag by sending " + outByte + " to " + writeTamperCommand);
        } else {
            outByte = 0;
            Log.i(TAG, "Clearing tamper flag by sending " + outByte + " to " + writeTamperCommand);
        }

        superUserCommandWithDataByte(writeTamperCommand, outByte);
    }

    /**
     * Finds out if the bootloader is unlocked and if the tamper flag is set
     */
    @Override
    public int getBootLoaderState() {
        try {
            Log.v(TAG, "Getting bootloader lock state with " + queryCommand);
            int lockResult = superUserCommandWithByteResult(queryCommand);
            Log.v(TAG, "Got lock value " + lockResult);

            Log.v(TAG, "Getting bootloader tamper flag with " + queryTamperCommand);
            int tamperResult = superUserCommandWithByteResult(queryTamperCommand);
            Log.v(TAG, "Got tamper flag " + tamperResult);

            if (lockResult == 0) {
                if (tamperResult == 0) {
                    return BL_LOCKED;
                } else {
                    return BL_TAMPERED_LOCKED;
                }
            } else if (lockResult == 1) {
                if (tamperResult == 0) {
                    return BL_UNLOCKED;
                } else {
                    return BL_TAMPERED_UNLOCKED;
                }
            } else {
                return BL_UNKNOWN;
            }
        } catch (IOException e) {
            Log.v(TAG, "Caught IOException while querying: " + e);
            return BL_UNKNOWN;
        }
    }
}
