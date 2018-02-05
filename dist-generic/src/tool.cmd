@echo off
rem Executes groovy scripts.
java -cp ccu-historian.jar;lib\*.jar groovy.ui.GroovyMain %*
