@ECHO OFF

@REM	This script emulates a build from Hudson

@REM Don't set the variables globally.
SETLOCAL

SET FMW_HOME=C:\oracle\middleware
SET WL_HOME=%FMW_HOME%\wlserver_10.3
SET JAVA_HOME=%FMW_HOME%\jdk160_24
SET ANT_HOME=%FMW_HOME%\modules\org.apache.ant_1.7.1


@REM Hudson sets some environment variables that are used by the Ant build.
@REM The following variables are used when performing a build:
@REM
@REM    WORKSPACE		The absolute path to the Hudson workspace
@REM    BUILD_NUMBER	The Hudson build number

SET WORKSPACE=D:\hudson\jobs\SampleOSB
SET BUILD_NUMBER=

IF NOT EXIST "%WORKSPACE%" (
	MKDIR "%WORKSPACE%"
	ECHO Created Hudson Workspace: %WORKSPACE%
) ELSE (
	ECHO Hudson Workspace exists: %WORKSPACE%
)

@REM Hudson would have performed a subversion checkout or update for us. This
@REM emulates this behaviour based on the svn-projects.properties file.
FOR /F "eol=# tokens=1,2*" %%i IN (projects.properties) DO (
	IF EXIST "%WORKSPACE%\%%j" (
		ECHO Updating dir %%j from %%i
		@REM assume we have anon access to svn repository
		svn up "%WORKSPACE%\%%j"
	) ELSE (
		ECHO Checkout %%i to dir %%j
		@REM assume we have anon access to svn repository
		svn co "%%i" "%WORKSPACE%\%%j"
	)
)

@REM Emulate the Hudson Build number environment var
IF EXIST build.number (
	FOR /F "" %%i IN (build.number) DO (
		SET /A BUILD_NUMBER=%%i+1
	)
) ELSE (
	SET BUILD_NUMBER=1
)
ECHO %BUILD_NUMBER% > build.number
ECHO Build number: %BUILD_NUMBER%

@REM Invoke the Ant build.xml to initialize the build
"%ANT_HOME%\bin\ant" -Dconfig.project=OSBSamplesConfig build,deploy


ENDLOCAL

