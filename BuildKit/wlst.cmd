@ECHO OFF

SETLOCAL
CALL setenv.cmd
%JAVA_HOME%\bin\java -cp %CLASSPATH% weblogic.WLST %*
ENDLOCAL