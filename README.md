[![Release](https://img.shields.io/github/release/mdzio/ccu-historian.svg)](https://github.com/mdzio/ccu-historian/releases/latest)
[![Downloads](https://img.shields.io/github/downloads/mdzio/ccu-historian/total.svg)](https://github.com/mdzio/ccu-historian/releases)
[![Donate](https://img.shields.io/badge/Donate-PayPal-green.svg)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=SF4BR9ZE2JUBS)

# CCU-Historian

Langzeitarchiv für die Zentrale (CCU) des Hausautomations-Systems HomeMatic

![](doc/cover.png)

Der CCU-Historian ist ein Langzeitarchiv für die Zentrale (CCU) des [Hausautomations-Systems](http://de.wikipedia.org/wiki/Hausautomation) HomeMatic der Firma [eQ-3](http://www.eq-3.de/).

Die Kommunikationsvorgänge der CCU-Schnittstellen (BidCos-RF, BidCos-Wired und System) werden aufgezeichnet. Darunter befinden sich z.B. die Messwerte aller Sensoren und alle ausgeführten Schaltvorgänge. Aus der Logikschicht der CCU werden zusätzlich die Systemvariablen archiviert. Die gesammelten Daten werden strukturiert in einer Datenbank abgelegt und stehen daraufhin für Visualisierungen oder Analysen zur Verfügung. Für einen ersten Überblick werden Web-Seiten mit Trend-Diagrammen durch einen eingebetteten Web-Server generiert.

Der CCU-Historian erfreut sich bei den HomeMatic-Anwendern einer großen Beliebtheit und verfügt über eine breite Installationsbasis.

Viele weitere Informationen sind auf der [offiziellen Web-Seite des CCU-Historians](http://www.ccu-historian.de/) und im [umfangreichen Handbuch](https://github.com/mdzio/ccu-historian/wiki) zu finden.

# Übersicht

Das folgende Diagramm zeigt die einzelnen Komponenten des CCU-Historians:

![CCU-Historian Übersicht](doc/ccu-historian-overview.svg)

# Unterstützte Plattformen

Da der CCU-Historian nur eine [Java-Laufzeitumgebung](https://java.com/) zum Betrieb voraus setzt, ist er auf etlichen Hard- und Software-Plattformen lauffähig: 

* Windows / Linux / MacOS 
* x86 / x64 / ARM

Für bestimmte Systeme existieren fertige Installationspakete:

* CCU-Addon für folgende HomeMatic-Zentralen:
  * CCU3
  * [RaspberryMatic](https://github.com/jens-maus/RaspberryMatic)
* Synology-Paket
* Docker-Images (alphabetisch sortiert)
  * [sacomedv/ccu-historian](https://hub.docker.com/r/sacomedv/ccu-historian)
  * [xjokay/ccu-historian](https://hub.docker.com/r/xjokay/ccu-historian)

**Hinweis:** Für den Betrieb als Addon auf einer CCU3 oder RaspberryMatic muss an der Zentrale ein USB-Stick angeschlossen sein!

# Installation und Konfiguration

Die Installation und Konfiguration ist im [Handbuch](https://github.com/mdzio/ccu-historian/wiki) ausführlich beschrieben. 

# Entwicklungsumgebung

Der CCU-Historian ist größtenteils in der [Programmiersprache Apache Groovy](http://groovy-lang.org) geschrieben. Weitere Programmiersprachen sind Java und JavaScript. Als Entwicklungsumgebung wird [Eclipse Photon](http://www.eclipse.org) in der Variante für Java-Entwickler verwendet. Desweiteren wird das [Groovy-Eclipse-Plugin](https://github.com/groovy/groovy-eclipse/wiki) benötigt. Dafür unter *Help* → *Install New Software* die Update-Site [http://dist.springsource.org/snapshot/GRECLIPSE/e4.8](http://dist.springsource.org/snapshot/GRECLIPSE/e4.8) eintragen. Über *File* → *Import* → *Projects from Git* kann direkt das Repository geklont werden. Die Zielplattform ist Java Version 8.

Alle Beiträge zum Projekt müssen unter die [GNU General Public License V3](LICENSE.txt) gestellt werden. Die Lizenzen von verwendeten Bibliotheken müssen mit dieser kompatibel sein.

# Lizenz und Haftungsausschluss

Dieses Projekt steht unter der [GNU General Public License V3](LICENSE.txt) mit folgenden Ausnahmen:
* Highstock/Highcharts JS: [Creative Commons (CC) Attribution-NonCommercial licence](http://creativecommons.org/licenses/by-nc/3.0/)

Bei einer kommerziellen Verwendung des CCU-Historians muss also auf die Erweiterung H2-Highcharts verzichtet werden, oder eine entsprechende Lizenz erworben werden.

# Autoren

Copyright (c) 2011-2020

* Mathias Dzionsko
* Jens Maus (CCU/RaspberryMatic-Distribution)
* Yannick Rocks, Thomas Zahari (Synology-Distribution)
* wak (Erweiterung H2-Highcharts)
