[![Donate](https://img.shields.io/badge/Donate-PayPal-green.svg)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=SF4BR9ZE2JUBS)

# CCU-Historian

Dies ist das offizielle Docker-Image für den [CCU-Historian](https://github.com/mdzio/ccu-historian). Der CCU-Historian erfasst die Betriebsdaten der Zentrale CCU3 des Hausautomations-Systems HomeMatic. 

Dieses Docker-Image ist kompatibel mit dem Vorgänger-Image [xjokay/ccu-historian](https://hub.docker.com/r/xjokay/ccu-historian), welches nicht mehr weiter gepflegt wird. Die Umgebungsvariablen und verwendeten Volumes können ohne Änderung weiter verwendet werden.

### Umgebungsvariablen

Die folgenden Umgebungsvariablen müssen für den ersten Start des Containers gesetzt werden. Aus diesen wird die Konfigurationsdatei `ccu-historian.config` generiert, **wenn diese nicht bereits existiert**. Die Konfigurationsdatei kann auch auf dem Volume `/opt/ccu-historian/config` (siehe auch weiter unten) direkt editiert werden.

| Variablenname              | Erforderlich / Optional | Beschreibung                                    |
|----------------------------|-------------------------|-------------------------------------------------|
| CONFIG\_CCU\_TYPE          | Erforderlich            | Typ der CCU, z. B. `CCU1`, `CCU2` oder `CCU3`   |
| CONFIG\_CCU\_IP            | Erforderlich            | IP-Adresse der CCU                              |
| CONFIG\_HOST\_IP           | Erforderlich            | IP-Adresse des Docker-Rechners                  |
| CONFIG\_HOST\_XMLRPCPORT   | Optional                | XML-RPC-Port, z. B. `2098`                      |
| CONFIG\_HOST\_BINRPCPORT   | Optional                | BIN-RPC-Port, z. B. `2099`                      |
| CONFIG\_CCU\_PLUGIN1\_TYPE | Optional                | Zusätzliche Plugins, z. B. `CUXD` oder `HMWLGW` |
| CONFIG\_CCU\_PLUGIN2\_TYPE | Optional                | Zusätzliche Plugins, z. B. `CUXD` oder `HMWLGW` |
| CONFIG\_CCU\_USERNAME      | Optional                | Benutzername für die Authentifizierung          |
| CONFIG\_CCU\_PASSWORD      | Optional                | Passwort für die Authentifizierung              |

Weitere Informationen sind im [Handbuch des CCU-Historian im Abschnitt Konfiguration](https://github.com/mdzio/ccu-historian/wiki#konfiguration) zu finden.

Folgende Umgebungsvariablen für Wartungszwecke sind optional und werden bei **jedem** Start des Docker-Containers erneut ausgewertet:

| Variablenname        | Beschreibung                                                                                                                                                                                                                                    |
|----------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| CONFIG\_KEEP\_MONTHS | Es werden Messwerte gelöscht, die älter als `x` Monate sind. Die Wartung erfolgt, bevor der CCU-Historian tatsächlich gestartet wird. Dabei wird der CCU-Historian nacheinander mit den Optionen `-clean`, `-recalc` und `-compact` aufgerufen. |
| CONFIG\_MAINTENANCE  | Mögliche Werte sind `true` (Ja) oder `false` (Nein). Die Wartung erfolgt, bevor der CCU-Historian tatsächlich gestartet wird. Dabei wird der CCU-Historian nacheinander mit den Optionen `-recalc` und `-compact` aufgerufen.                   |
| CONFIG\_JAVA\_OPTS   | Ermöglicht die Angabe von Java-spezifischen Einstellungen, z. B. `-Xmx100m` zum Festlegen des maximalen Arbeitsspeichers auf 100 MB.                                                                                                            |

Weitere Informationen sind im [Handbuch des CCU-Historian im Abschnitt Startparameter](https://github.com/mdzio/ccu-historian/wiki#startparameter) zu finden.

### Freizugegebene Netzwerk-Ports

| Port | Protokoll | Beschreibung                                                            |
|------|-----------|-------------------------------------------------------------------------|
| 80   | TCP       | Port für den eingebetteten Web-Server                                   |
| 2098 | TCP       | Netzwerk-Port für den XMLRPC-Server des CCU-Historians                  |
| 2099 | TCP       | Netzwerk-Port für den BINRPC-Server des CCU-Historians                  |
| 8082 | TCP       | Netzwerk-Port für die Web-Bedienschnittstelle der Datenbank             |
| 9092 | TCP       | (Optional) Netzwerk-Port für die TCP-Schnittstelle der Datenbank        |
| 5435 | TCP       | (Optional) Netzwerk-Port für die PostgreSQL-Schnittstelle der Datenbank |

Weitere Informationen sind im [Handbuch des CCU-Historian im Abschnitt Firewall-Einstellungen](https://github.com/mdzio/ccu-historian/wiki#firewall-einstellungen) zu finden.

### Einzurichtende Volumes

| Verzeichnis im Container  | Beschreibung                                             |
|---------------------------|----------------------------------------------------------|
| /database                 | Datenbankverzeichnis                                     |
| /opt/ccu-historian/config | Verzeichnis der Konfigurationsdatei ccu-historian.config |

Im Ordner `/opt/ccu-historian/config` wird die Konfigurationsdatei `ccu-historian.config` auf Basis der Umgebungsvariablen erstellt, wenn sie nicht vorhanden ist.

Im Ordner `/database` wird die Datenbankdatei `history.mv.db` beim ersten Start erstellt.

### Automatisches Backup einrichten

Ein automatisches Backup der Datenbank kann folgendermaßen eingerichtet werden:
1. Verzeichnis `backup` im Volume `/database` erstellen.
2. Folgende Zeile zur Konfigurationsdatei `ccu-historian.config` hinzufügen. Dadurch wird wöchentlich ein Backup erstellt.

```
database.backup='/database/backup/db_%Y-w%W.zip'
```

### Log-Ausgaben aufzeichnen

Die Log-Ausgabe des CCU-Historians können für die Fehlersuche in Textdateien geschrieben werden:
1. Verzeichnis `log` im Volume `/database` erstellen.
2. Folgende Zeilen zur Konfigurationsdatei `ccu-historian.config` hinzufügen. Dadurch werden bis zu 10 Log-Dateien erstellt.

```
logSystem.fileLevel=Level.INFO
logSystem.fileName='/database/log/ccu-historian-%g.log'
logSystem.fileLimit=10000000
logSystem.fileCount=10
```

## Beispiele

### Start mit Docker-Compose

```yml
services:
  app:
    image: mdzio/ccu-historian
    volumes:
      - ./data/database:/database
      - ./data/config:/opt/ccu-historian/config
    ports:
      - 80:80
      - 2098:2098
      - 2099:2099
      - 8082:8082
    environment:
      - CONFIG_CCU_TYPE=CCU3
      - CONFIG_CCU_IP=192.168.1.10
      - CONFIG_HOST_IP=192.168.1.100
      - CONFIG_HOST_BINRPCPORT=2099
      - CONFIG_HOST_XMLRPCPORT=2098
      - CONFIG_CCU_PLUGIN1_TYPE=CUXD
      - CONFIG_KEEP_MONTHS=12
```

### Start mit Docker Run

```sh
docker run -d \
  -v ./data/database:/database \
  -v ./data/config:/opt/ccu-historian/config \
  -p 80:80 \
  -p 2098:2098 \
  -p 2099:2099 \
  -p 8082:8082 \
  -e CONFIG_CCU_TYPE=CCU3 \
  -e CONFIG_CCU_IP=192.168.1.10  \
  -e CONFIG_HOST_IP=192.168.1.100 \
  -e CONFIG_HOST_BINRPCPORT=2099 \
  -e CONFIG_HOST_XMLRPCPORT=2098 \
  -e CONFIG_CCU_PLUGIN1_TYPE=CUXD \
  -e CONFIG_KEEP_MONTHS=12 \
  mdzio/ccu-historian
```
