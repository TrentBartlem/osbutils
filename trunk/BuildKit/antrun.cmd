@ECHO OFF

@REM	This script emulates a build from Hudson

@REM Don't set the variables globally.
SETLOCAL

SET FMW_HOME=C:\oracle\middleware
SET WL_HOME=%FMW_HOME%\wlserver_10.3
SET JAVA_HOME=%FMW_HOME%\jdk160_24
SET ANT_HOME=%FMW_HOME%\modules\org.apache.ant_1.7.1

@REM Invoke the Ant build.xml to initialize the build
"%ANT_HOME%\bin\ant" %*


ENDLOCAL

