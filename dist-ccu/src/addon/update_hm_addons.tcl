#!/usr/bin/env tclsh
# adds or removes the addon configuration

# addon configuration
set id ccu-historian
set name "CCU-Historian"
set descr(de) {<li>Betriebsdatenerfassung f&uuml;r die CCU</li>}
set descr(en) {<li>Operating data acquisition for the CCU</li>}
set configUrl /addons/ccu-historian/config.cgi

# config file
set cfgFileName /usr/local/etc/config/hm_addons.cfg

# check arguments
if {[llength $argv] < 1} {
  puts stderr "usage: update_hm_addons add|remove"
  exit 1
}
if {[lindex $argv 0] == "add"} {
  set add true
} elseif {[lindex $argv 0] == "remove"} {
  set add false
} else {
  puts stderr "error: invalid argument"
  exit 1
}

# read config file
set file [open $cfgFileName r]
array set config [read -nonewline $file] 
close $file

# remove addon config, if present
unset -nocomplain config($id)

# add addon config, if requested
if {$add} {
  # build structure
  set cfg(ID) $id
  set cfg(CONFIG_NAME) $name
  set cfg(CONFIG_URL) $configUrl
  set cfg(CONFIG_DESCRIPTION) [array get descr]
	# add
  set config($id) [array get cfg]
}

# write config file
set file [open $cfgFileName w]
puts -nonewline $file [array get config]
close $file
