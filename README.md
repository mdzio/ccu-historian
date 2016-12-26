&nbsp;![CCU-Historian](https://github.com/jens-maus/hm-ccu-historian/raw/master/ccu-historian-logo.png)

[![Donate](https://img.shields.io/badge/Donate-PayPal-green.svg)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=SF4BR9ZE2JUBS)
[![Code Climate](https://codeclimate.com/github/jens-maus/cuxd/badges/gpa.svg)](https://codeclimate.com/github/jens-maus/hm-ccu-historian)
[![Github Issues](http://githubbadges.herokuapp.com/jens-maus/hm-ccu-historian/issues.svg)](https://github.com/jens-maus/hm-ccu-historian/issues)

This repository hosts the CCU-Addon build environment for the CCU-Historian project (http://www.ccu-historian.de/), a long term archive for the communication history directly on a CCU2 / RaspberryMatic device.

# Supported CCU devices
* [HomeMatic CCU2](http://www.eq-3.de/produkt-detail-zentralen-und-gateways/items/homematic-zentrale-ccu-2.html) / [YAHM](https://github.com/leonsio/YAHM) / [LXCCU](http://www.lxccu.com/)
* [RaspberryMatic](https://github.com/jens-maus/RaspberryMatic)

# Installation
1. Download installation archive (```ccu-historian-addon-X.X.X.tar.gz```) from 'releases' sub directory (https://github.com/jens-maus/hm-ccu-historian/releases)
2. Log into your WebUI interface
3. Upload installation archive (don't unarchive tar.gz) to the WebUI
4. Start installation

## NOTES
* Due to very limited CPU power capabilities of the CCU2 hardware, this addon might run considerably slower if used directly on a CCU2 device. The Addon is, however, still released for the CCU2 platform to support virtualized solutions such as LXCCU (www.lxccu.com) or YAHM.
* If using this Addon with a CCU2 device, an inserted microSD card is required to actually store the amount of data that is constantly written during the use of CCU-Historian.

# Configuration
After installation use the following URL to display the configuration dialog of the addon on your CCU:

http://homematic-raspi:8082/

where you have to replace 'homematic-raspi' with the ip address or hostname of your CCU/RaspberryMatic device. Or you go to the configuration pages on your WebUI/CCU device and click on the "CCU-Historian" link to visit the CCU-Historian configuration pages.

# Authors
Copyright (c) 2013-2016 Mathias Dzionsko, Jens Maus
