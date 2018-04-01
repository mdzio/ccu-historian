package mdz.ccuhistorian.webapp

import groovy.util.logging.Log
import java.util.prefs.Preferences
import mdz.Exceptions
import mdz.hc.itf.hm.HmSysVarInterface

@Log
public class PageRenderer {

    // the "user" of this class
    def servlet
    
    // closures with page content, delegate is set to servlet.html
    def head
    def content
    def end

    // render page content
    public render() {
        servlet.utils.catchToLog(log) {
            setup()
            handleUserLogInOut()
            writeDocument()
        }
    }
    
	private def setup() {
        if (!servlet) 
            throw new IllegalStateException('Field servlet is not set')
		
        // setup session
        if (!servlet.session) {
            servlet.session=servlet.request.session
            servlet.session.maxInactiveInterval=1800
        }
        
        // setup request context
        servlet.ctx=servlet.session.getAttribute('ctx')
        if (!servlet.ctx) {
            servlet.ctx=[
                prefs: Preferences.userRoot().node('mdz/ccuhistorian/webpages'),
                user: [loggedIn:false, logInFailed:false]
            ]
            servlet.session.setAttribute('ctx', servlet.ctx)
        }
	}
	
	private def runSafe(cl) {
		if (cl!=null) {
			def e=servlet.utils.catchToLog(log) {
				cl.delegate=servlet.html
				cl()
			}
			if (e) {
				// show error description
				servlet.html.div(class:'alert alert-danger') {
					h4 {
						strong 'Fehler: '
						mkp.yield e.message?:e.class.name
					}
					button class:'btn btn-default', type:'button', 'data-toggle':'collapse', 
						'data-target':'#errdescr', 'Details'
					div(class:'collapse', id:'errdescr') {
						pre Exceptions.getStackTrace(e)
					}
				}
			}
		}
	}
	
	private def handleUserLogInOut() {
        // already logged in?
        if (!servlet.ctx.user.loggedIn) {
            def password_admin=servlet.ctx.prefs.get('password_admin', '')
            if (password_admin=='') {
                // auto login, if no password is set
                servlet.ctx.user.loggedIn=true
            } else if (servlet.params.login) {
                // check credentials
                if (password_admin==servlet.utils.secureHash(servlet.params.login_password)) {
                    servlet.ctx.user.loggedIn=true
                    servlet.ctx.user.logInFailed=false
                } else {
                    servlet.ctx.user.loggedIn=false
                    servlet.ctx.user.logInFailed=true
                }
            }
        } else {
            // logout?
            if (servlet.params.logout) {
                servlet.ctx.user.loggedIn=false
                servlet.ctx.user.logInFailed=false
            }
        }
	}
	
	private def writeDocument() {
		// start of HTML document
		servlet.println '<!doctype html>'
		servlet.html.html(lang:'de') {
			writeHead()
			writeBody()
		}
	}
	
	private def writeHead() {
		servlet.html.head {
			// standard headers
			meta charset:'utf-8'
			meta 'http-equiv':'X-UA-Compatible', content:'IE=edge'
			meta name:'viewport', content:'width=device-width, initial-scale=1'
			
			// bootstrap CSS
			link href:'../external/bootstrap/css/bootstrap.css', rel:'stylesheet'

			// own CSS
			// TODO: new theme
			//link href:'ccu-historian-bootstrap.css', rel:'stylesheet'
			
			// icon
			// TODO: new icon with size 196 x 196 pixels
			link href:'ccu-historian.ico', rel:'icon'
			
			// execute the head closure
			runSafe head
		}
	}
	
	private def writeBody() {
		servlet.html.body {
			div(class:'container-fluid', style:'margin-top: 0.5em') {
				if (!servlet.ctx.user.loggedIn) {
					writeLogIn()
				} else {
					writeNavigation()
					writeContent()
				}
			}
			
			// jquery JS
			script src:'../external/jquery/jquery.js'
			// bootstrap JS
			script src:'../external/bootstrap/js/bootstrap.js'
			// underscore JS
			script src:'../external/underscore/underscore.js'

			// execute the page end closure
			runSafe end
		}
	}
	
	private def writeLogIn() {
        // show a login dialog
		servlet.html.div(class:'row') {
			div(class:'col-md-4 col-md-offset-4') {
				div(class:'panel panel-default') {
					div(class:'panel-heading') {
						h3 class:'panel-title', 'CCU-Historian Anmeldung'
					}
					div(class:'panel-body') {
						if (servlet.ctx.user.logInFailed) {
							p class:'alert alert-danger', 'Die Anmeldung ist fehlgeschlagen!'
						}
						form(class:'form-horizontal', method:'post') {
							div(class:'form-group') {
								label class:'col-md-4 control-label', for:'input_password', 'Passwort:'
								div(class:'col-md-8') {
									input class:'form-control', type:'password', id:'input_password', name:'login_password', placeholder:'Passwort'
								}
							}
							div(class:'form-group') {
								div(class:'col-md-offset-4 col-md-8') {
									button class:'btn btn-default', type:'submit', name:'login', value:1, 'Anmelden'
								}
							}
						}
					}
				}
			}
		}
	}
	
	private def writeNavigation() {
		// navigation bar
		servlet.html.nav(class:'navbar navbar-default') {
			div(class:'container-fluid') {
				// header for mobile display
				div(class:'navbar-header') {
					button (type:'button', class:'navbar-toggle collapsed', 'data-toggle':'collapse', 'data-target':'#navbar-collapse-id') {
						span class:'icon-bar'
						span class:'icon-bar'
						span class:'icon-bar'
					}
					p class:'navbar-brand', 'CCU-Historian'
				}
				
				// navbar content
				div(class:'collapse navbar-collapse', id:'navbar-collapse-id') {
					ul(class:'nav navbar-nav') {
						// datapoint list
						li { a href:'index.gy', 'Datenpunktliste' }
						
						// tools
						li(class:'dropdown') {
							a(href:'#', class:'dropdown-toggle', 'data-toggle':'dropdown', role:'button') {
								mkp.yield 'Werkzeuge'
								span class:'caret'
							}
							ul(class:'dropdown-menu') {
								li { a href:'dpconfig.gy', 'Datenpunktkonfiguration' }
								// TODO
								/*
								li { a href:'config.html', 'CCU-Historian Konfiguration' }
								li { a href:'diagnosis.html', 'Diagnose' }
								*/
								// database web access
								def port; servlet.utils.catchToLog(log) { port=servlet.database.config.webPort }
								if (port)
									li { a href:"http://$servlet.webServer.historianAddress:$port", target:'_blank', 'Datenbank' }
							}
						}
						
						// CCU web UI
						// (each CCU has one HmSysVarInterface)
						def sysVarItfs=servlet.interfaceManager.interfaces.findAll { it.value instanceof HmSysVarInterface }
						if (sysVarItfs) {
							li(class:'dropdown') {
								a(href:'#', class:'dropdown-toggle', 'data-toggle':'dropdown', role:'button') {
									mkp.yield 'Zentralen'
									span class:'caret'
								}
								ul(class:'dropdown-menu') {
									def ccuNo=1
									sysVarItfs.each { itfName, itf ->
										li { a href:"http://$itf.scriptClient.address", target:'_blank', 'CCU '+(ccuNo>1?ccuNo:'') }
										ccuNo++
									}
								}
							}
						}
						
						// configured menu entries
						if (servlet.webServer.config.menuLinks) {
							li(class:'dropdown') {
								a(href:'#', class:'dropdown-toggle', 'data-toggle':'dropdown', role:'button') {
									mkp.yield 'Extras'
									span class:'caret'
								}
								ul(class:'dropdown-menu') {
									servlet.webServer.config.menuLinks.each { k, v ->
										li { a href:v.address, v.text }
									}
								}
							}
						}
					}
					
					// logout button
					if (servlet.ctx.prefs.get('password_admin', '')!='') {
						form(class:'navbar-form navbar-right') {
							div(class:'form-group') {
								button class:'btn btn-default', type:'submit', name:'logout', value:1, 'Abmelden'
							}
						}
					}
					
					// version
					p class:'navbar-text navbar-right', "V$servlet.utils.historianVersion"
				}
			}
		}
	}
	
	private def writeContent() {
        // execute the content closure
		runSafe content
	}
}
