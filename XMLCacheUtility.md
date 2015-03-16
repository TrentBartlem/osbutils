# Introduction #

Add your content here.

# Downloads #

There are currently no releases of this sub-project. It must be built from the source code.

# Source Code #

 SVN

# Configuration #

The XML cache utility is configured via a properties file. By default the a properties file named 'xmlcache.properties' from the domain directory.

A custom location can be specified by setting the system property:

  * com.oracle.ukps.osbutil.xmlcache.CONFIGFILE

| **Property** | **Description** |
|:-------------|:----------------|
| cache.expiry | The time to live (TTL) for cache entries before they expire and get reloaded from the cache source |
| cache.statistics | Flag to control whether or not cache statistics are recorded. For performance statistics should be turned off. |
| source._key_.class | Define an XML source that the cache will consult when attempting to find some XML |
| source._key_._sourceconf_ | Any configuration properties required by the XML source |

# Cache Sources #

The XML cache utility will attempt to load the XMLThe XML cache utility comes with a default implementation of CacheSource

# Logging #

The XML cache utility uses the JDK Logging.

## Example ##

```
# -----------------------------------------------------------------------------
# Root Logger Configuration

# Handlers for the root logger
handlers = weblogic.logging.ServerLoggingHandler

# Logging level for the root logger
.level = ALL

# Logging level for new ConsoleHandler instances
java.util.logging.ConsoleHandler.level = INFO
 
# Logging level for WebLogicServer Handler
weblogic.logging.ServerLoggingHandler.level = ALL

# -----------------------------------------------------------------------------
# OSB Utils Configuration

# Register handlers for the com.oracle.ukps.osbutils and its child loggers
# to log to console and WebLogic Server 
com.oracle.uk.ocs.osbutils.handlers = java.util.logging.ConsoleHandler, weblogic.logging.ServerLoggingHandler
 
# Do not send the com.oracle.ukps.osbutils log messages to the root handler
com.oracle.uk.ocs.osbutils.useParentHandlers = false

# Configure the XML cache utility
# WARNING in production
com.oracle.uk.ocs.osbutils.XMLCacheUtility.level = INFO

# All XML cache utility statistics are logged at INFO level.
# Set level to OFF (WARNING, SEVERE are not recommended)
com.oracle.uk.ocs.osbutils.XMLCacheUtility.STATS.level = INFO
```

# Cache Statistics #

Cache statistics are logged to the JDK logger

  * com.oracle.uk.ocs.osbutil.xmlcache.XMLCacheUtility.STATS

Therefore cache statistics can be