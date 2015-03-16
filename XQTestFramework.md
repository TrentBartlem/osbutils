# Introduction #

The XQTest Framework provides a framework for creating JUnit tests for XQuery transformations **out of container**.

# Downloads #

  * [Latest binary JAR](http://code.google.com/p/osbutils/downloads/detail?name=xqtestfmk-0.1-2011-04-13-bin.jar&can=2&q=)

# Source Code #

The source code is available from Google code. The code can either be
[browsed online](http://code.google.com/p/osbutils/source/browse/#svn%2Ftrunk%2FXQTestFramework) or, if you would like to contribute, can be accessed with a Subversion client.

# Contents #
  * [Writing Tests](XQTestFramework#Writing_Tests.md)
  * [Examples](XQTestFramework#Examples.md)
  * [ClassPath](XQTestFramework#Classpath.md)
  * [Releases](XQTestFramework#Releases.md)

# Writing Tests #

The framework extends JUnit by providing a base class that can be readily extended in order to write tests. The base class provides:

  * A Context in which namespaces can be declared and re-used.
  * Convenience methods for dealing with XML and XQuery from files.
  * Assertion methods for validating and checking XML

Therefore writing a test is as easy as writing a JUnit test case class that extends the base class. The examples provide some sample code for how to achieve this.

It is worth remembering that JUnit will create a new instance of the test case class for each test method that is defined. Therefore common setup should be performed in a method that is marked with the @Before annotation.

# Examples #

## Testing with XML Files ##

This example demonstrates how to construct a test that reads XML from files for both the parameters to the XQuery transformation and also the expected result.

```
...
import java.io.File;
import org.junit.Before;
import org.junit.Test;
...
public class XQExampleTest extends XQAbstractTest {
	...
	@Before
	public void setup() {
		declareNamespace("test", "http://www.example.org/test");
	}
	...
	@Test
	public void test1 {
		executeQuery(new File("MyTest.xq"));
		setParameter("param1", new File("testdata/MyTest/test1/param1.xml"));
		setParameter("param2", new File("testdata/MyTest/test1/param2.xml"));
		assertEquals(new File("testdata/MyTest/test1/expected-result.xml"));
	}	
}
```

## Testing With XPaths ##

The following example demonstrates how to construct a test that evaluates an XPath in the result against a string value.

```
...
import java.io.File;
import org.junit.Before;
import org.junit.Test;
...
public class XQExampleTest extends XQAbstractTest {
	...
	@Before
	public void setup() {
		declareNamespace("test", "http://www.example.org/test");
		setParameter("test", new File("test.xml");
	}
	...
	@Test
	public void test1 {
		executeQuery(new File("test.xq"));
		assertEquals(0, "./test:Test/@testattr", "attrval");
	}	
}
```

# Classpath #

This framework uses both Apache XML Beans and JUnit frameworks. It also uses the WebLogic XQuery implementation. As such in order to build or run an XQuery test based on this framework the following must be available on the classpath:

  * The OSB utils library containing this framework
  * JUnit 4 Library
  * Apache XML Beans library
  * WebLogic XQuery implementation library
  * WebLogic XQuery and Apache XML Beans interoperation library
  * Antlr library (required by the XQuery library)

Based on an OSB 11g installation this resulted in the following classpath:

  * xqtestfmk-0.1-2011-04-13-bin.jar
  * _${eclipse.home}_/plugins/org.junit4\_4.5.0.v20090824.jar
  * _${wl.home}_/modules/com.bea.core.xml.xmlbeans\_2.2.0.0.jar
  * _${wl.home}_/modules/com.bea.core.xquery.xmlbeans-interop\_1.3.0.0.jar
  * _${wl.home}_/modules/com.bea.core.xquery\_1.3.0.0.jar
  * _${wl.home}_/modules/com.bea.core.antlr.runtime\_2.7.7.jar

# Releases #

| **Version** | **Changes** |
|:------------|:------------|
| xqtestfmk-0.1-2011-04-13-bin.jar | assertEquals now writes out the XML if it does not match to STDOUT so that the difference can be observed |