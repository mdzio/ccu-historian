#!/bin/sh

SYNO_IP=$(ifconfig | grep -A 1 'eth0' | awk '/inet addr/{print substr($2,6)}')
CCU_IP=$(ifconfig | grep -A 1 'eth0' | awk '/inet addr/{print substr($2,6)}' | sed 's/\.[0-9]*$/.100/')
tee $SYNOPKG_TEMP_LOGFILE <<EOF

[
  {
    "step_title": "Create basic CCU-Historian configuration",
    "items": [
      {
        "type": "singleselect",
        "desc": "Do you want to you want to create an initial configuration?",
        "subitems": [
          {
            "key": "pkgwizard_create_initial_configuration_NO",
            "desc": "No, I am a pro and want do do it myself (the following entries will be ignored)",
            "defaultValue": false
          },
          {
            "key": "pkgwizard_create_initial_configuration_YES",
            "desc": "Yes, please",
            "defaultValue": true
          }
        ]
      }
    ]
  },
  {
    "step_title": "CCU type",
    "items": [
      {
        "type": "singleselect",
        "desc": "What type of CCU are you using?",
        "subitems": [
          {
            "key": "pkgwizard_type_ccu1",
            "desc": "CCU1",
            "defaultValue": false
          },
          {
            "key": "pkgwizard_type_ccu2",
            "desc": "CCU2",
            "defaultValue": true
          }
        ]
      }
    ]
  },
  {
    "step_title": "Plugins and connectivity",
    "items": [
      {
        "type": "multiselect",
        "desc": "Choose plugins to enable",
        "subitems": [
          {
            "key": "pkgwizard_enable_cuxd",
            "desc": "CUxD",
			"defaultValue": false
          },
          {
            "key": "pkgwizard_enable_hmlgw",
            "desc": "Homematic LAN Gateway",
            "defaultValue": false
            }
		]
      },
      {
        "type": "textfield",
        "desc": "CCU IP address / FQDN",
        "subitems": [
          {
            "key": "pkgwizard_ccu_ip",
            "desc": "IP / FQDN",
            "defaultValue": "$CCU_IP"
          }
        ]
      },
	  {
		"type": "textfield",
		"desc": "Synology IP address",
		"subitems": [
		  {
			"key": "pkgwizard_synology_ip",
			"desc": "IP",
			"defaultValue": "$SYNO_IP"
		  }
		]
	  },
	  {
		"type": "textfield",
		"desc": "Webserver Port",
		"subitems": [
		  {
			"key": "pkgwizard_ccu_webport",
			"desc": "Port",
			"defaultValue": "8080"
		  }
		]
	  }
    ]
  }
];
EOF

exit 0
