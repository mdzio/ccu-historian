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

# evaluate argument
switch $argv {
  add { set add true }
  remove { set add false }
  default { 
    puts stderr "usage: update_hm_addons.tcl add|remove"
    exit 1
  }
}

# read config file
if {[file exists $cfgFileName]} {
  set file [open $cfgFileName r]
  array set config [read -nonewline $file] 
  close $file
} else {
  array set config {} 
}

# remove addon config, if present
if {[info exists config($id)]} { 
  unset config($id)
}

# add addon config, if requested
if {$add} {
  # build structure
  set cfg(ID) $id
  set cfg(CONFIG_NAME) $name
  set cfg(CONFIG_URL) $configUrl
  set cfg(CONFIG_DESCRIPTION) [array get descr]
  # add structure
  set config($id) [array get cfg]
}

# write config file
set file [open $cfgFileName w]
puts -nonewline $file [array get config]
close $file
