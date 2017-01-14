&nbsp;![CCU-Historian](https://github.com/mdzio/hm-ccu-historian/raw/master/ccu-historian-logo.png)

[![Donate](https://img.shields.io/badge/Donate-PayPal-green.svg)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=SF4BR9ZE2JUBS)
[![Code Climate](https://codeclimate.com/github/mdzio/cuxd/badges/gpa.svg)](https://codeclimate.com/github/mdzio/hm-ccu-historian)
[![Github Issues](http://githubbadges.herokuapp.com/mdzio/hm-ccu-historian/issues.svg)](https://github.com/mdzio/hm-ccu-historian/issues)

This repository hosts the CCU-Addon build environment for the CCU-Historian project (http://www.ccu-historian.de/), a long term archive for the communication history directly on RaspberryMatic or virtualized CCU2 solutions like YAHM or LXCCU.

# Supported CCU devices
* [RaspberryMatic 2.25.15.20170114](https://github.com/jens-maus/RaspberryMatic) or higher
* [YAHM](https://github.com/leonsio/YAHM)
* [LXCCU](http://www.lxccu.com/)

# Installation
1. Download installation archive (```ccu-historian-addon-X.X.X.tar.gz```) from 'releases' sub directory (https://github.com/mdzio/hm-ccu-historian/releases/latest)
2. Log into your WebUI interface
3. Upload installation archive (don't unarchive tar.gz) to the WebUI
4. Start installation

# Limitations
* Due to very limited CPU and RAM capabilities of the CCU2 hardware, this addon is NOT supported to be directly used on a CCU2 device. The Addon is, however, still released for the CCU2 platform to support virtualized solutions such as LXCCU (www.lxccu.com) or YAHM.

# Configuration
After installation use the following URL to display the configuration dialog of the addon on your CCU:

http://homematic-raspi:8082/

where you have to replace 'homematic-raspi' with the ip address or hostname of your CCU/RaspberryMatic device. Or you go to the configuration pages on your WebUI/CCU device and click on the "CCU-Historian" link to visit the CCU-Historian configuration pages.

# Authors
Copyright (c) 2011-2017 Mathias Dzionsko, Jens Maus
