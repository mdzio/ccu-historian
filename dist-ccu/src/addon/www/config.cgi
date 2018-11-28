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
  puts -nonewline "Content-Type: text/html; charset=utf-8\r\n\r\n"
  puts "<html><head><meta http-equiv='refresh' content='0; url=http://$ip:$port/' /></head></html>"
} else {
  puts -nonewline "Content-Type: text/html; charset=utf-8\r\n\r\n"
  puts "<html><body>Error reading the CCU-Historian configuration file!<br>"
  puts "Have you restarted the CCU after installing the CCU-Historian?</body></html>"
}