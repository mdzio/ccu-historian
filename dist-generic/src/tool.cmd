@echo off
rem Executes groovy scripts.
set "SCRIPT_DIR=%~dp0"
java -cp "%SCRIPT_DIR%ccu-historian.jar;%SCRIPT_DIR%lib\*.jar" groovy.ui.GroovyMain %*
