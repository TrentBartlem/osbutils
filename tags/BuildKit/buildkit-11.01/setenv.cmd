@ECHO OFF

SET BUILDKIT_HOME=%~dp0

SET FMW_HOME=C:\oracle\middleware
SET WL_HOME=%FMW_HOME%\wlserver_10.3
SET OSB_HOME=%FMW_HOME%\Oracle_OSB1
SET JAVA_HOME=%FMW_HOME%\jdk160_24
SET ANT_HOME=%FMW_HOME%\modules\org.apache.ant_1.7.1
SET SVN_CLIENT=C:\Program Files\CollabNet\Subversion Client

SET PATH=%PATH%;%SVN_CLIENT%

SET CLASSPATH=
SET CLASSPATH=%CLASSPATH%;%WL_HOME%\server\lib\weblogic.jar
SET CLASSPATH=%CLASSPATH%;%OSB_HOME%\modules\com.bea.common.configfwk_1.7.0.0.jar
SET CLASSPATH=%CLASSPATH%;%OSB_HOME%\lib\alsb.jar