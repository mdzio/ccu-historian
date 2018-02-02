@echo off
rem Executes groovy scripts. H2 Database driver is included.
java -cp lib\groovy-all-2.4.13.jar;lib\commons-cli-1.2.jar;lib\h2-1.4.196.jar groovy.ui.GroovyMain %*
