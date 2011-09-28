@ECHO OFF

SETLOCAL

SET FMW_HOME=C:\oracle\middleware
SET WL_HOME=%FMW_HOME%\wlserver_10.3
SET JAVA_HOME=%FMW_HOME%\jdk160_24
SET OSB_HOME=%FMW_HOME%\Oracle_OSB1

SET CLASSPATH=
SET CLASSPATH=%CLASSPATH%;%WL_HOME%\server\lib\weblogic.jar
SET CLASSPATH=%CLASSPATH%;%OSB_HOME%\modules\com.bea.common.configfwk_1.5.0.0.jar
SET CLASSPATH=%CLASSPATH%;%OSB_HOME%\lib\alsb.jar

%JAVA_HOME%\bin\java -cp %CLASSPATH% weblogic.WLST %*

ENDLOCAL