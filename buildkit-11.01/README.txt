===============================================================================

                                  OSB BuildKit

Version:		11.1.1
Author:			james.nash@oracle.com
Project Home:	http://code.google.com/p/osbutils/

===============================================================================

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE. 

===============================================================================

-------------------------------------------------------------------------------
1. About OSB BuildKit


The OSB BuildKit provides the ability to easily script builds and deployments
of OSB configurations.

Ant Targets

 * build		Builds an OSB configuration JAR from an OEPE workspace
 * deploy		Deploys an OSB configuration JAR and optionally applies
 
This release of the BuildKit has been tested against OSB 11gR1 PS7 (11.1.1.7)
on Windows 7. Due to the fact that the underlying technologies, namely Ant and
WLST, are cross-platform there should not be any issue with running this on a
different platform but this has not been confirmed.

-------------------------------------------------------------------------------
Integration with Hudson/Jenkins

The OSB BuildKit has been specifically designed to be easy to integrate with
Hudson/Jenkins. To that end the OSB BuildKit by default will use the
environment variables BUILD_NUMBER and WORKSPACE as parameters. These override
the equivalent build properties build.number and workspace.dir respectively.

Any additional configuration that may be required for a particular build may be
supplied via -D arguments from Hudson/Jenkins.

-------------------------------------------------------------------------------
Configuration

The aim of the BuildKit is to minimize the configuration necessary to build and
deploy OSB configurations.

Some configuration is required, in particular the name of the project in the
OEPE workspace that is the OSB configuration (container) project must be
specified.

-------------------------------------------------------------------------------
Files

This section describes the files that are delivered in the BuildKit.

build.xml					The Ant build file. DO NOT MODIFY THIS FILE!

build.properties			The SHARED build properties. Modify per environment

export.py					The WLST script to export a OSB configuration JAR
							from an OSB server/cluster to local filesystem.

import.py					The WLST script to import an OSB configuration JAR
							to an OSB server/cluster. Optionally will also
							apply a customization file at the same time.


antrun.cmd					Command to launch Ant.
							
build.cmd


-------------------------------------------------------------------------------
Examples


To check the environment is configured BEFORE running a build or deployment:
ant checkenv

To build an OSB configuration JAR
ant -Dconfig.project=MyOSBConfig build

To deploy an OSB configuration JAR
> ant
>     -Dimport.jar=<config.jar>
>     -Dimport.custfile=c:/projects/os/genie

To build and deploy an OSB configuration JAR
ant -Dconfig.project=MyOSBConfig build,deploy

To build a specific tagged workspace
First use your VCS tools to "checkout" the tagged version into a temporary
location.

svn -co "http://svn/osbconfig/trunk/ExampleOSBConfig"
    "c:/temp/osb/build/ExampleOSBConfig"
	
ant -Dconfig.project=MyOSBConfig -Dworkspace.dir=c:/temp/osb

-------------------------------------------------------------------------------
Contributions

===============================================================================
Change Log

28/01/2014	v11.1.1

* Improved the Ant script so that the config framework JAR and eclipse launcher
  JAR files do not have to be explicitly configured in the build.properties.
  Instead the Ant script uses wildcard matches to path structures to find the
  relevant JARs. This should make the script run across versions of OSB without
  this manual configuration.
  
* The build.number has been fixed to provide the correct behaviour in terms of
  defaulting.

* This README.txt has been added to assist in delivering newer versions of the
  OSB BuildKit.



===============================================================================

