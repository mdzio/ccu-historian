#!/bin/sh

SYNO_IP=$(ifconfig | grep -A 1 'eth0' | awk '/inet addr/{print substr($2,6)}')
CCU_IP=$(ifconfig | grep -A 1 'eth0' | awk '/inet addr/{print substr($2,6)}' | sed 's/\.[0-9]*$/.100/')
tee $SYNOPKG_TEMP_LOGFILE <<EOF

[
  {
    "step_title": "Erstelle Basis-Konfiguration für CCU-Historian",
    "items": [
      {
        "type": "singleselect",
        "desc": "Soll eine Initialkonfiguration erstellt werden?",
        "subitems": [
          {
            "key": "pkgwizard_create_initial_configuration_NO",
            "desc": "Nein, ich bin ein Profi und will alles selber machen (die folgenden Einträge werden ignoriert)",
            "defaultValue": false
          },
          {
            "key": "pkgwizard_create_initial_configuration_YES",
            "desc": "Ja, gerne",
            "defaultValue": true
          }
        ]
      }
    ]
  },
  {
    "step_title": "CCU Typ",
    "items": [
      {
        "type": "singleselect",
        "desc": "Welcher CCU Typ wird eingesetzt?",
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
    "step_title": "Plugins und Verbindung",
    "items": [
      {
        "type": "multiselect",
        "desc": "Zu aktivierende Plugins",
        "subitems": [
          {
            "key": "pkgwizard_enable_cuxd",
            "desc": "CUxD",
			"defaultValue": false
          },
          {
            "key": "pkgwizard_enable_hmlgw",
            "desc": "Homematic Wired RS485 LAN Gateway (HMW-LGW)",
            "defaultValue": false
            }
		]
      },
      {
        "type": "textfield",
        "desc": "CCU IP Adresse / FQDN",
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
        "desc": "Synology IP Adresse",
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
