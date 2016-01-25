# HomeMatic CCU-Historian Addon
A HomeMatic/CCU Addon package to run CCU-Historian (http://www.ccu-historian.de/) as a long term archive for the communication history directly on a CCU2 / RaspberryMatic device.

# Supported CCU devices
* HomeMatic CCU2 (http://www.eq-3.de/produkt-detail-zentralen-und-gateways/items/homematic-zentrale-ccu-2.html)
* RaspberryMatic (http://homematic-forum.de/forum/viewtopic.php?f=56&t=26917)

# Installation
1. Download installation archive (```ccu-historian-addon-X.X.X.tar.gz```) from 'releases' sub directory (https://github.com/jens-maus/hm-ccu-historian/releases)
2. Log into your WebUI interface
3. Upload installation archive (don't unarchive tar.gz) to the WebUI
4. Start installation

# Configuration
After installation use the following URL to display the configuration dialog of the email addon on your CCU:

http://homematic-ccu2:8083/

where you have to replace 'homematic-ccu2' with the ip address or hostname of your CCU/RaspberryMatic device. Or you go to the configuration pages on your WebUI/CCU device and click on the "CCU-Historian" link to visit the CCU-Historian configuration pages.

# NOTE
Due to very limited CPU power capabilities of the CCU2 hardware, this addon might run considerably slow if used directly on a CCU2 device. The Addon is, however, still released for the CCU2 platform to support virtualized solutions such as LXCCU (www.lxccu.com).

# Authors
Copyright (c) 2013-2016 Mathias Dzionsko, Jens Maus <mail@jens-maus.de>
