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
import org.junit.Before;


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
 * @author		James Nash (james.nash@oracle.com)
 * @version		0.1
 *
 */

public abstract class XQAbstractTest {
	
	public Logger logger;

	///////////////////////////////////////////////////////////////////////////
	// Constructor
	///////////////////////////////////////////////////////////////////////////	
		
	private Map <String, String> namespaceMap;
	private Map <String, Object> parameterMap;
	private XmlObject[] result;
	
	///////////////////////////////////////////////////////////////////////////
	// Constructor
	///////////////////////////////////////////////////////////////////////////	
	
	/**
	 * Initiates the namespace and parameter maps
	 */
	public XQAbstractTest() {
		namespaceMap = createNamespaceMapInstance();
		parameterMap = createParameterMapInstance();
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
	 * 
	 */
	public void executeQuery(String xq) {
		XmlObject tempXml = XmlObject.Factory.newInstance();
		XmlOptions opts = new XmlOptions();
		opts.setXqueryVariables(parameterMap);
		result = tempXml.execQuery(xq, opts);
	}
	
	public void executeQuery(File file) throws IOException {
		executeQuery(readFileAsString(file));
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
		Assert.assertEquals (value, result[index].getDomNode().getFirstChild().getNodeValue());
	}
	
	
	//TODO: Overload the above functions for other types
	
	/**
	 * Assert that an element or attribute specified by an XPath
	 * @param index
	 * @param xpath
	 * @param value
	 */
	public void assertEquals(int index, String xpath, String value) {
		XmlObject xmlObject = result[index];
		XmlObject pathValue = xmlObject.selectPath(getNamespaceDeclarations() + xpath)[0];
		Assert.assertEquals(value, pathValue.getDomNode().getFirstChild().getNodeValue());
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
	 * Assert
	 * @param index
	 * @param xpath1
	 * @param value
	 * @param xpath2
	 * @throws XmlException
	 */
	public void assertEquals(int index, String xpath1, XmlObject value, String xpath2) throws XmlException {
		String fullXPath1 = getNamespaceDeclarations() + xpath1;
		String fullXPath2 = getNamespaceDeclarations() + xpath2;
		Assert.assertTrue(XQXmlTestUtil.equals(getResult()[index], fullXPath1, value, fullXPath2));
	}
	
	/**
	 * Assert that an XPath expression evaluated against a specific index fromresult from an XQuery expression is equal to
	 * an XPath 
	 * 
	 * @param index			The index of the XQ expression result
	 * @param xpath1		The XPath within the selected result index
	 * @param xmlFile		The XML file that contains the value to assert 
	 * @param xpath2		The XPath to assert within the XML file
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
	 * 
	 * @throws XmlException
	 * @throws IOException
	 */
	public void assertEquals(File xmlFile) throws XmlException, IOException {
		assertEquals(0, ".", xmlFile, ".");
	}
	
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
