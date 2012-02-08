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
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;

import weblogic.xml.query.exceptions.XQueryException;
import weblogic.xml.query.types.XQueryType;
import weblogic.xml.query.xdbc.Connection;
import weblogic.xml.query.xdbc.DriverManager;
import weblogic.xml.query.xdbc.PreparedStatement;
import weblogic.xml.query.xdbc.XQType;
import weblogic.xml.query.xdbc.iterators.TokenIterator;
import weblogic.xml.query.xdbc.util.Serializer;


public class WebLogicXQueryTestFramework {
	
	private static Logger logger = Logger.getLogger(WebLogicXQueryTestFramework.class.getName());
	
	private static QName StringQName	= new QName("http://www.w3.org/2001/XMLSchema", "string");
	private static QName IntegerQName	= new QName("http://www.w3.org/2001/XMLSchema", "integer");
	private static QName IntQName		= new QName("http://www.w3.org/2001/XMLSchema", "int");
	private static QName BoolQName		= new QName("http://www.w3.org/2001/XMLSchema", "boolean");
	
	private Connection conn;
	private PreparedStatement pStmt;
	
	private List <String> namespaces = new ArrayList <String> ();
	private Map <String, Object> parameters = new HashMap <String, Object> ();
	private Object result;
	
	public WebLogicXQueryTestFramework() throws XQueryException {
		conn = DriverManager.getConnection();
	}

	public void executeQuery(String xq) throws XQueryException, IOException, XmlException {
		
		pStmt = conn.prepareStatement(xq);
		processParameters();
		TokenIterator tokenIterator = pStmt.execute();
		
		System.out.println(pStmt.getReturnType());
		System.out.println(pStmt.getReturnType().schemaType());
		System.out.println(pStmt.getReturnType().schemaType().asQName());
		
		StringWriter writer;
		Serializer serializer;
		
		switch (pStmt.getReturnType().kind()) {
			case XQType.SIMPLE:
				tokenIterator.open();
				writer = new StringWriter();
				serializer = new Serializer(writer);
				serializer.process(tokenIterator);
				String resultStr = writer.toString();
				// This seems like a LOT of work. Can we use XMLBeans to do this??
				QName type = pStmt.getReturnType().schemaType().asQName();
				if (StringQName.equals(type)) {
					result = resultStr;
				} else if (IntegerQName.equals(type) || IntQName.equals(type)) {
					result = Integer.parseInt(resultStr);
				} else if (BoolQName.equals(type)) {
					result = Boolean.parseBoolean(resultStr);
				}
				//simpleTypeQNameToken.
				break;
			default:
				throw new IllegalArgumentException("Unable to determine the return type for: " + pStmt.getReturnType().kind());
		}
	}
	
	public void setParameter(String name, String value) throws XQueryException {
		parameters.put(name, value);
	}
	
	//public String getResult
	
	
	private void processParameters() throws XQueryException {
		for (String name: parameters.keySet()) {
			Object valueObject = parameters.get(name);
			if (valueObject instanceof String) {
				pStmt.setAtomic(
					new QName(name),
					XQueryType.STRING.schemaName().asQName(),
					(String) valueObject
				);
			} else if (valueObject instanceof XmlObject) {
				
			} else {
				
			}
		}
	}
	
	public Object getResult() {
		return result;
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
	
	///////////////////////////////////////////////////////////////////////////
	// Main (rough testing only)
	///////////////////////////////////////////////////////////////////////////
	
	public static void main(String[] args) throws Exception {
		
		String xq = 
			"declare namespace xf =\"http://www.example.org/test\";\n\n" +
			"declare function xf:TestStrings($str1 as xs:string)\n" +
			"as xs:string {\n" +
			"    fn:concat(\"Hello \", $str1, \"!\")\n" +
			"};\n" +
			"declare variable $str1 as xs:string external;\n" +
			"xf:TestStrings($str1)";
				
		WebLogicXQueryTestFramework test = new WebLogicXQueryTestFramework();
		test.setParameter("str1", "James Nash");
		test.executeQuery(xq);
		System.out.println(test.getResult());
		
	}
}
