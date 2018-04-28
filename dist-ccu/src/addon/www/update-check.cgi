#!/bin/tclsh
set infoUrl https://api.github.com/repos/mdzio/ccu-historian/releases/latest
set infoError [catch {
  set info [exec wget -q -O- --no-check-certificate $infoUrl]
  set found [regexp {\"tag_name\"\s*:\s*\"([^\"]*)\"} $info -> version]
  if {!$found} error
  set found [regexp {\"browser_download_url\"\s*:\s*\"([^\"]*/ccu-historian-addon-[^\"]+\.tar\.gz)\"} $info -> downloadUrl]
  if {!$found} error
}]
set downloadCmd [regexp {\mcmd=download\M} $env(QUERY_STRING)]
if {$downloadCmd} {
  if {$infoError} {
    puts "<html><body>Fehler: Download-Link kann nicht ermittelt werden!</body></html>"
  } else {
    puts "<html><head><meta http-equiv='refresh' content='0; url=$downloadUrl' /></head></html>"
  }
} else {
  if {$infoError} {
    puts "N/A"
  } else {
    puts $version
  }
}
