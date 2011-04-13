/*
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.oracle.uk.ocs.osb.xqtest;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.junit.Assert;

import com.oracle.uk.ocs.osb.xqtest.util.XQXmlTestUtil;


/**
 * Provides a base context for testing XQuery.
 * 
 * <p>This abstract class provides a base context for writing tests for
 * XQuery transformations. It provides the following advantages:</p>
 * 
 * <ul>
 * 		<li>Namespace declarations are re-used, allowing easier use of XPath
 * 		for validation of results.</li>
 * 		<li>Utilty methods to be able to read XQuery from a file</li>
 * </ul>
 * 
 * <p><b>Reminder: A new instance of the class will be created by JUnit for
 * each test method!</b></p>
 * 
 * <p><b>Reminder:</b> You should use the {@link #setBaseDir(File) setBaseDir}
 * method to identify the base directory for your tests. This can then be
 * overriden with a system property in automated environments.</p>
 * 
 * <p>A full description of how to use this test can be found in the
 * description for the <a href="package-summary.html">Package Summary</a>.</p>
 * 
 * @author		James Nash (james.nash@oracle.com)
 * @version		0.1
 *
 */

public abstract class XQAbstractTest {
	
	/** The system property to set to override the XQuery basedir **/
	public static String XQ_BASEDIR_PROPKEY = "xqtests.basedir";
	
	private static Logger logger = Logger.getLogger(XQAbstractTest.class.getName());

	///////////////////////////////////////////////////////////////////////////
	// Properties
	///////////////////////////////////////////////////////////////////////////	
		
	private Map <String, String> namespaceMap;
	private Map <String, Object> parameterMap;
	private XmlObject[] result;
	
	private File basedir;
		
	///////////////////////////////////////////////////////////////////////////
	// Constructor
	///////////////////////////////////////////////////////////////////////////	
	
	/**
	 * Initiates the namespace map, parameter map and base directory
	 */
	public XQAbstractTest() {
		
		namespaceMap = createNamespaceMapInstance();
		parameterMap = createParameterMapInstance();
		basedir = new File(System.getProperty("user.dir"));
		
	}
	

	
	///////////////////////////////////////////////////////////////////////////
	// Namespaces
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Declare a namespace to be used in subsequent XPath definitions
	 * 
	 * Allows a namespace to be readily re-used within the context of a test.
	 */
	public void declareNamespace(String prefix, String url) {
		namespaceMap.put(prefix, url);
	}
	
	/**
	 * Used internally to create the declaration statements. Should then be
	 * appended to the XPath expression that assumes these namespaces are
	 * declared.
	 *  
	 * @return	The namespaces as declaration statements
	 */
	public String getNamespaceDeclarations() {
		StringBuffer declarations = new StringBuffer();
		for (String prefix: namespaceMap.keySet()) {
			declarations.append("declare namespace ");
			declarations.append(prefix);
			declarations.append("='");
			declarations.append(namespaceMap.get(prefix));
			declarations.append("';");
		}
		declarations.append("\n");
		return declarations.toString();
	}	
	
	///////////////////////////////////////////////////////////////////////////
	// Parameters
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Set a String as a parameter that will is bound to a named variable in
	 * any XQuery function.
	 * 
	 * @param name			The parameter name
	 * @param value			The parameter value
	 */
	public void setParameter(String name, String value) {
		parameterMap.put(name, value);
	}
	
	/**
	 * Sets a named parameter to some XML
	 * @param name
	 * @param value
	 */
	public void setParameter(String name, XmlObject value) {
		parameterMap.put(name, value);
	}
	
	/**
	 * Set a named parameter to an XmlCursor
	 * @param name
	 * @param value
	 */
	public void setParameter(String name, XmlCursor value) {
		parameterMap.put(name, value);
	}
	
	/**
	 * Set a named parameter to be an XPath in some XML
	 * @param name
	 * @param xmlObject
	 * @param xpath
	 */
	public void setParameter(String name, XmlObject xmlObject, String xpath) {
		XmlObject[] r = xmlObject.selectPath(getNamespaceDeclarations() + xpath);
		if (r.length == 1)
			setParameter(name, r[0]);
	}
	
	/**
	 * Set a parameter as a <code>Node</code> 
	 * @param name
	 * @param file
	 * @param xpath
	 * @throws IOException
	 * @throws XmlException
	 */
	public void setParameter(String name, File file, String xpath) throws IOException, XmlException  {
		XmlObject xmlObject = XmlObject.Factory.parse(file);
		setParameter(name, xmlObject, xpath);
	}
	
	public void setParameter(String name, File file) throws IOException, XmlException {
		String xpath = "./*[1]";
		setParameter(name, file, xpath);
	}
	

	
	///////////////////////////////////////////////////////////////////////////
	// Execute
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Execute an XQuery defined in a string
	 */
	public void executeQuery(String xq) {
		XmlObject tempXml = XmlObject.Factory.newInstance();
		XmlOptions opts = new XmlOptions();
		opts.setXqueryVariables(parameterMap);
		result = tempXml.execQuery(xq, opts);
	}
	
	/**
	 * Execute and XQuery defined in a file
	 * @param file			The file containing the XQuery expression
	 * @throws IOException	If the file cannot be read
	 */
	public void executeQuery(File file) throws IOException {
		File xqfile = findXQueryFile(file);
		executeQuery(readFileAsString(xqfile));
	}
	
	///////////////////////////////////////////////////////////////////////////
	// Result
	///////////////////////////////////////////////////////////////////////////


	/**
	 * Returns the result of executing an XQuery expression
	 * 
	 * Should only be called after executeQuery as been called.
	 * 
	 * @return	The result of executing the XQuery expression
	 */
	public XmlObject[] getResult() {
		return result;
	}

	///////////////////////////////////////////////////////////////////////////
	// Assertions
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Assert that an XML document returned by the XQuery contains an XML node
	 * whose value is equal to the specified value.
	 * 
	 * This can be used test XQueries that will return a simple String type as
	 * Apache XML Beans wraps this in a <code>&lt;xml-fragment&gt;</code> tag.
	 * 
	 * @param index			The index of the result to assert
	 * @param value			The value to assert as equal
	 */
	public void assertEquals(int index, String value) {
		logger.fine("index: " + index);
		logger.fine("value: " + value);
		Assert.assertEquals ("Expected string value for result index " + index, value, result[index].getDomNode().getFirstChild().getNodeValue());
	}
	
	
	//TODO: Overload the above functions for other types
	
	/**
	 * Assert that an element or attribute specified by an XPath
	 * @param index
	 * @param xpath
	 * @param value
	 */
	public void assertEquals(int index, String xpath, String value) {
		logger.fine("index: " + index);
		logger.fine("xpath: " + xpath);
		logger.fine("value: " + value);
		XmlObject xmlObject = result[index];
		XmlObject pathValue = xmlObject.selectPath(getNamespaceDeclarations() + xpath)[0];
		Assert.assertEquals("Expected string value for index " + index + " at path " + xpath, value, pathValue.getDomNode().getFirstChild().getNodeValue());
	}
	
	/**
	 * Asserts that the result is deep equal to a known value.
	 * @param index
	 * @param value
	 */
	public void assertDeepEquals(int index, XmlObject value) throws XmlException, IOException {
		XmlObject xmlObject = result[index];
		Assert.assertTrue("XML objects not equal: ", XQXmlTestUtil.equals(xmlObject, value));
	}
	
	/**
	 * Asserts that a node returned by an XQuery expression is equal to a node
	 * within some XML.
	 * 
	 * The XPath expressions are prepended with any namespace declarations
	 * that have been declared for the test case.
	 * 
	 * @param index		An index into the result from the XQuery expression 
	 * @param xpath1	A XPath within the indexed result to evaluate as equal
	 * @param value		Some XML to evaluate against
	 * @param xpath2	A XPath within the provided XML to evaluate as equal
	 * @throws XmlException
	 */
	public void assertEquals(int index, String xpath1, XmlObject value, String xpath2) throws XmlException, IOException  {
		String fullXPath1 = getNamespaceDeclarations() + xpath1;
		String fullXPath2 = getNamespaceDeclarations() + xpath2;
		XmlObject a = getResult()[index].selectPath(fullXPath1)[0];
		XmlObject b = value.selectPath(fullXPath1)[0];
		boolean equal = XQXmlTestUtil.equals(a, b);
		if (!equal) {
			
			XmlOptions xmlOpts = new XmlOptions();
			xmlOpts.setSavePrettyPrint();
			xmlOpts.setSavePrettyPrintIndent(4);

			System.out.println();
			System.out.println();
			System.out.println("---- A -----------------------------------------------------");
			a.save(System.out, xmlOpts);
			System.out.println();
			System.out.println();
			System.out.println("---- B -----------------------------------------------------");
			b.save(System.out, xmlOpts);
			System.out.println();
			System.out.println();
			
			Assert.fail("XML not equal. Please see STDOUT for the XML values to compare.");
		}
	}
	
	/**
	 * Asserts that a node returned by an XQuery expression is equal to a node
	 * within some XML within a file.
	 * 
	 * <p>This is a convenience method that reads some XML from the specified
	 * file and then invokes
	 * @{link {@link #assertEquals(int, String, XmlObject, String)}</p>
	 * 
	 * @param index			An index into the result from the XQuery expression
	 * @param xpath1		A XPath within the indexed result to evaluate as equal
	 * @param xmlFile		The file that contains the XML to evaluate against 
	 * @param xpath2		A XPath within the provided XML to evaluate as equal
	 * @see assertEquals(int, String, XmlObject, String)
	 * 
	 * @throws XmlException		If bad XML argument
	 * @throws IOException		reading the XML file
	 */
	public void assertEquals(int index, String xpath1, File xmlFile, String xpath2) throws XmlException, IOException {
		XmlObject valueXmlObject = XmlObject.Factory.parse(xmlFile);
		assertEquals(index, xpath1, valueXmlObject, xpath2);
	}
	
	/**
	 * Assert that the result of an XQuery is equal to XML from a file
	 * @param xmlFile		The XML file containing the value to assert
	 * @see assertEquals(int, String, XmlObject, String
	 * @throws XmlException
	 * @throws IOException
	 */
	public void assertEquals(File xmlFile) throws XmlException, IOException {
		assertEquals(0, ".", xmlFile, ".");
	}
	
	/**
	 * Asserts that a result is valid XML
	 * @param index		The index of the result to validate
	 */
	public void assertValid(int index) {
		ArrayList validationErrors = new ArrayList();
		XmlOptions xmlOpts = new XmlOptions();
		xmlOpts.setErrorListener(validationErrors);
		boolean valid = result[index].validate(xmlOpts);
		Assert.assertTrue("XML document is not valid: ", valid);
		if (!valid) {
			for (Object o: validationErrors) {
				System.err.println(o);
			}
		}
	}
	
	/**
	 * Asserts the result is valid XML
	 */
	public void assertValid() {
		assertValid(0);
	}
	
	///////////////////////////////////////////////////////////////////////////
	// XQuery file basedir
	///////////////////////////////////////////////////////////////////////////	

	/**
	 * Sets the base directory which is used for locating the XQuery to be
	 * tested.
	 * 
	 * @param basedir	The directory
	 */
	public void setBaseDir(File basedir) {
		if (basedir.exists() && basedir.isDirectory())
			this.basedir = basedir;
		else
			throw new IllegalArgumentException("The specified base is not a directory");
	}	
	
	public File findXQueryFile(File file) {
		
		// If the file that has been specified is an absolute path then we
		// should trust that the path is correct.
		if (file.isAbsolute())
			return file;
		
		// If the system property has been set then we should use that to
		// find the file in preference to that set within the test.
		String sysbasedir = System.getProperty(XQAbstractTest.XQ_BASEDIR_PROPKEY);
		if (sysbasedir != null) {
			return new File(sysbasedir + File.separator + file.getPath());
		}
		
		return new File(basedir.getPath() + File.separator + file.getPath());
	}	

	///////////////////////////////////////////////////////////////////////////
	// Template Pattern for Maps
	///////////////////////////////////////////////////////////////////////////

	/**
	 * Sub-classes can override this method to swap out the map implementation
	 * @return	A new instance of Map
	 */
	protected Map <String, String> createNamespaceMapInstance() {
		return new HashMap <String, String> ();
	}
	
	/**
	 * Sub-classes can override this method to swap out the map implementation
	 * @return	A new instance of Map
	 */	
	protected Map <String, Object> createParameterMapInstance() {
		return new HashMap <String, Object> ();
	}
			
	///////////////////////////////////////////////////////////////////////////
	// Private Utils
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Reads a file into a String
	 * @param file	The file to read
	 * @return		A string of the contents of the file
	 * @throws IOException
	 */
	
	private static String readFileAsString(File file) throws IOException {		
		FileReader fr = new FileReader(file);
		StringWriter sw = new StringWriter();
		char[] cbuf = new char[2048];
		int len = 0;
		while ( (len = fr.read(cbuf)) != -1 ) {
			sw.write(cbuf, 0, len);
		}
		return sw.toString();
	}
	

}
