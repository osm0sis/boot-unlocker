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
 * @description device-specific bootloader code for InFocusM810 phones
 */
public class bootLoader_InFocusM810 extends bootLoader {
    /**
     * For logging
     */
    private static final String TAG = "net.segv11.bootLoader_InFocusM810";

    /*
     * The bit for unlocked bootloader is at 0x000FFE10 in the aboot partition.
     * The tamper flag is at 0x000FFE14.
     *
     * 01 for unlocked; 01 for tampered
     */

    /**
     * Private constants for working with the lock state in the aboot partition
     */
    private static final String queryCommand =
            "dd ibs=1 count=1 skip=1048080 if=/dev/block/platform/msm_sdcc.1/by-name/aboot  # query ";
    private static final String writeCommand =
            "dd obs=1 count=1 seek=1048080 of=/dev/block/platform/msm_sdcc.1/by-name/aboot  # write ";

    private static final String queryTamperCommand =
            "dd ibs=1 count=1 skip=1048084 if=/dev/block/platform/msm_sdcc.1/by-name/aboot  # query ";
    private static final String writeTamperCommand =
            "dd obs=1 count=1 seek=1048084 of=/dev/block/platform/msm_sdcc.1/by-name/aboot  # write ";

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
     * Finds out  if the bootloader is unlocked and if the tamper flag is set
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
