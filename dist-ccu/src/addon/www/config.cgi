#!/usr/bin/env tclsh

# read content of file
proc readLines {path} {
  if {[catch {open $path} file]} {
    error "Datei konnte nicht geöffnet werden (Pfad: $path)!"
  }
  if {[catch {read -nonewline $file} content]} {
    close $file
    error "Datei konnte nicht eingelesen werden (Pfad: $path)!"
  }
  close $file
  return [split $content "\n"]
}

# write HTTP header
puts -nonewline "Content-Type: text/html; charset=utf-8\r\n\r\n"

# read CCU-Historian configuration
set configPath /usr/local/addons/ccu-historian/ccu-historian.config
if {[catch {readLines $configPath} configLines]} {
  puts "<html><body>$config<br>"
  puts "Hinweis: Die CCU muss nach der Installation des CCU-Historians neu gestartet werden.</body></html>"
  exit 0
}

# parse CCU-Historian configuration
foreach line $configLines {
  regexp {^webServer.historianAddress='(.*)'} $line -> optionIP
  regexp {^webServer.port=(.*)} $line -> optionPort
  regexp {^database.dir='(.*)'} $line -> optionDatabaseDir
}
if {![info exists optionIP] || ![info exists optionPort] || ![info exists optionDatabaseDir]} {
  puts "<html><body>Die Konfigurationsdatei vom CCU-Historian ist ungültig (Pfad: $configPath)!</body></html>"
  exit 0
}

# read firewall configuration
set firewallPath /usr/local/etc/config/firewall.conf
if {[catch {readLines $firewallPath} firewallLines]} {
  puts "<html><body>$firewall</body></html>"
  exit 0
}

# parse firewall configuration
foreach line $firewallLines {
  regexp {^USERPORTS\s*=\s*(.*)$} $line -> firewallPorts
}

# check firewall configuration
if {![info exists firewallPorts] || ![regexp "(^|\\s)${optionPort}($|\\s)" $firewallPorts]} {
  puts "<html><body>Der Port $optionPort muss in der CCU-Firewall zu den Port-Freigaben hinzugefügt werden!</body></html>"
  exit 0
}

# check filesystem of database
if {[exec stat -f -c%T $optionDatabaseDir] == "tmpfs"} {
  puts "<html><body>Das Datenbankverzeichnis $optionDatabaseDir darf nicht auf einem temporären Dateisystem liegen!<br>"
  puts "Hinweis: Der CCU-Historian benötigt einen an der CCU angeschlossenen USB-Stick.</body></html>"
  exit 0
}

# redirect to web UI of the CCU-Historian
puts "<html><head><meta http-equiv='refresh' content='0; url=http://$optionIP:$optionPort/' /></head></html>"
