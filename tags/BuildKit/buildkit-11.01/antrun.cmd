@ECHO OFF

SETLOCAL
CALL setenv.cmd
@REM SET FMW_HOME=C:\oracle\middleware
@REM SET WL_HOME=%FMW_HOME%\wlserver_10.3
@REM SET JAVA_HOME=%FMW_HOME%\jdk160_24
@REM SET ANT_HOME=%FMW_HOME%\modules\org.apache.ant_1.7.1

@REM Invoke the Ant build.xml to initialize the build
"%ANT_HOME%\bin\ant" %*

ENDLOCAL

