#!/usr/bin/env tclsh
catch {
  set fileName /usr/local/addons/ccu-historian/ccu-historian.config
  set file [open $fileName r]
  set config [read -nonewline $file]
  close $file
}
if {[info exists config]} {
  set lines [split $config "\n"]
  foreach line $lines {
    regexp {^webServer.historianAddress='(.*)'} $line -> ip
    regexp {^webServer.port=(.*)} $line -> port
  }
}
if {[info exists ip] && [info exists port]} {
  puts "<html><head><meta http-equiv='refresh' content='0; url=http://$ip:$port/' /></head></html>"
} else {
  puts "<html><body>Error reading file ccu-historian.config!</body></html>"
}