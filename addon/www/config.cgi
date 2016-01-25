#!/usr/bin/env tclsh
set fileName "/usr/local/addons/ccu-historian/ccu-historian.config"
catch {set fptr [open $fileName r]}
set contents [read -nonewline $fptr]
close $fptr
set splitCont [split $contents "\n"]
foreach ele $splitCont {
  if {[regexp {^webServer.port=(.*)} $ele -> port]} {
    set hostName [info hostname]
    puts "<meta http-equiv='refresh' content='0; url=http://$hostName:$port/' />"
    break
  }
}
