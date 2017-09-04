&nbsp;![CCU-Historian](https://github.com/mdzio/ccu-historian/raw/master/doc/ccu-historian-logo.png)

[![Donate](https://img.shields.io/badge/Donate-PayPal-green.svg)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=SF4BR9ZE2JUBS)
[![Code Climate](https://codeclimate.com/github/mdzio/cuxd/badges/gpa.svg)](https://codeclimate.com/github/mdzio/ccu-historian)
[![Github Issues](http://githubbadges.herokuapp.com/mdzio/ccu-historian/issues.svg)](https://github.com/mdzio/ccu-historian/issues)

Der CCU-Historian ist ein Langzeitarchiv für die Zentrale (CCU) des [Hausautomations-Systems](http://de.wikipedia.org/wiki/Hausautomation) HomeMatic der Firma [eQ-3](http://www.eq-3.de/).

Die Kommunikationsvorgänge der CCU-Schnittstellen (BidCos-RF, BidCos-Wired und System) werden aufgezeichnet. Darunter befinden sich z.B. die Messwerte aller Sensoren und alle ausgeführten Schaltvorgänge. Aus der Logikschicht der CCU werden zusätzlich die Systemvariablen archiviert. Die gesammelten Daten werden strukturiert in einer Datenbank abgelegt und stehen daraufhin für Visualisierungen oder Analysen zur Verfügung. Für einen ersten Überblick werden Web-Seiten mit Trend-Diagrammen durch einen eingebetteten Web-Server generiert.

Der CCU-Historian erfreut sich bei den HomeMatic-Anwendern einer großen Beliebtheit und verfügt über eine breite Installationsbasis.

# Unterstützte Platformen
* Ubuntu Linux (x86 / ARM)
* Debain Linux (x86 / ARM)
* als CCU-Addon:
  * [RaspberryMatic](https://github.com/jens-maus/RaspberryMatic)
  * [YAHM](https://github.com/leonsio/YAHM)
  * [LXCCU](http://www.lxccu.com/)
* Windows

# Installation
1. TBD

# Limitationen
* Durch die geringen CPU und RAM Ressourcen einer CCU2 wird die Installation als CCU-Addon auf dieser Platform nicht unterstützt. Es wird daher geraten CCU-Historian auf einem weiteren System zu installieren und so zu konfigurieren das die CCU2 über das Netzwerk entsprechend überwacht wird.

# Konfiguration
* TBD

# Authoren
Copyright (c) 2011-2017 Mathias Dzionsko, Jens Maus
