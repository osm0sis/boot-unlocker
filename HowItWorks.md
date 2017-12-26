---


*BootUnlocker for Nexus Devices* avoids using "`fastboot oem
unlock`", with its associated "userdata" wipe. When fastboot unlocks
it updates a lock status flag, stored on a partition of your device's
internal storage. Device partitions, positions and state values
(locked/unlocked) are as follows:


* On the Galaxy Nexus, the bootloader uses position
  `0x000007C` of the "param" partition, stored as `01` / `00`.

* On the Nexus 10, the bootloader uses position
  `0x0000224` of the "param" partition, stored as `00` / `01`.

* On the Nexus 4 and Nexus 5, the bootloaders use position
  `0x0004010` of the "misc" partition, stored as `00` / `01`.
  The Nexus 4 and Nexus 5 have a "Tamper" flag, at position
  `0x0004014` of the "misc" partition, stored as `00` / `01` (untampered/tampered).

* On the Nexus 7 (2013), the bootloader uses position
  `0x04FFC00` of the "aboot" partition, stored as `00` / `02`.

* On the OnePlus One, OnePlus X, YU Yuphoria, YU Yureka, InFocus M810, and InFocus M812, the bootloaders use position
  `0x000FFE10` of the "aboot" partition, stored as `00` / `01`.
  The OnePlus One, OnePlus X, YU Yuphoria, YU Yureka, InFocus M810, and InFocus M812 have a "Tamper" flag, at position
  `0x000FFE14` of the "aboot" partition, stored as `00` / `01` (untampered/tampered).

* On the OnePlus 2, the bootloader uses position
  `0x00000010` of the "devinfo" partition, stored as `00` / `01`.
  The OnePlus 2 has a "Tamper" flag, at position
  `0x00000014` of the "devinfo" partition, stored as `00` / `01` (untampered/tampered).

* On the Lenovo Zuk Z1, the bootloader uses position
  `0x001FFE10` of the "aboot" partition, stored as `00` / `01`.
  The Lenovo Zuk Z1 has a "Tamper" flag, at position
  `0x001FFE14` of the "aboot" partition, stored as `00` / `01` (untampered/tampered).

* On the Yota Phone 2, the bootloader uses position
  `0x004FFE10` of the "aboot" partition, stored as `00` / `01`.

* On the Infinix HOT 3 LTE, the bootloader uses position
  '0x00073BC0' of the "aboot" partition, stored as '00' / '01'.
  The Infinix HOT 3 LTE has a "Tamper" flag, at position
  '0x00073BC4' of the "aboot" partition, stored as '00' / '01' (untampered/tampered).

On devices with Tamper flag locations listed above,
*BootUnlocker for Nexus Devices* can also set and clear this flag.
You can also view this flag using "`fastboot oem device-info`".

*BootUnlocker* uses root privileges to write to to the appropriate
location directly, bypassing fastboot. This allows you to lock and
unlock your bootloader from within Android, without wiping your
"userdata" partition.

The technique used was discovered through the efforts of several
contributors on <http://forum.xda-developers.com/showthread.php?t=1650830&page=13>

Special thanks go to those who posted raw images of their device
partitions, helped with/conducted the analysis, or put their devices in
harm's way to beta test: efrant, osm0sis, iuss, Archpope, AdamOutler,
NCguy, Raftysworld, Mach3.2, Meep70, Polarfuchs, thedropdead, Titokhan,
Crazyphil, heineken78, and others. This application could not have been
written without their contributions.

To learn more about how this app works, and plans for future
functionality, follow this project on GitHub, or subscribe the
application's XDA thread: <http://bit.ly/BootUnlocker>

Please note that the Nexus 7 (2012 version) cannot be supported
in *BootUnlocker*. See this XDA thread for an alternative:
<http://forum.xda-developers.com/showthread.php?t=2068207>
