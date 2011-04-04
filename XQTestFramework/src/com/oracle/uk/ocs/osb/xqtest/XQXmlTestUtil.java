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
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.ValidationException;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * H
 * @author James Nash (james.nash@oracle.com)
 *
 */

public class XQXmlTestUtil {
	
	///////////////////////////////////////////////////////////////////////////
	// Tests whether some XML is equal
	///////////////////////////////////////////////////////////////////////////
	
	
	public static boolean equals (XmlObject xmlObjA, String xPathA, XmlObject xmlObjB, String xPathB) throws XmlException {
		
		XmlObject[] a = xmlObjA.selectPath(xPathA);
		XmlObject[] b = xmlObjB.selectPath(xPathB);
		
		if (a.length == 0 || b.length == 0) {
			String error = "There is a problem with the result of the XPaths:";
			if (a.length == 0)
				error += " XPath[A]: '" + xPathA + "' did not return any data";
			if (b.length == 0)
				error += " XPath[B]: '" + xPathB + "' did not return any data";
			throw new XmlException(error);
		}
		
		return equals(a[0], b[0]);
		
	}
	
	/**
	 * Get the value of either an XML
	 * @param xmlObj
	 * @return
	 */
		
	public static String xmlToString (XmlObject xmlObj) {
		if (
				(xmlObj.getDomNode() == null) ||
				(xmlObj.getDomNode().getNodeType() != Node.DOCUMENT_FRAGMENT_NODE) ||
				(xmlObj.getDomNode().getChildNodes().getLength() != 1) ||
				(xmlObj.getDomNode().getFirstChild() == null) ||
				(xmlObj.getDomNode().getFirstChild().getNodeType() != Node.TEXT_NODE)
				) {
					throw new IllegalArgumentException();
				}
		else {
			return xmlObj.getDomNode().getFirstChild().getNodeValue();
		}
	}
	

	/**
	 * Get the value of some XML as a long
	 * 
	 * @param xmlObj	The XML Bean
	 * @return			The value as a long
	 * @see				xmlToString
	 */
	
	public static long xmlToLong (XmlObject xmlObj) {
		return Long.parseLong(xmlToString(xmlObj));
	}
	
	
	public static void printHelperXml(XmlObject xmlObj) {
		printHelperXml(xmlObj.getDomNode());
	}
	
	public static void printHelperXml(Node node) {
		System.out.println(String.format("%1$-20s %2$s %3$s", node.getNodeName(), node.getLocalName(), node.getNodeValue()));
		
		NodeList children = node.getChildNodes();
		for (int i=0; i<children.getLength(); i++) {
			printHelperXml(children.item(i));
		}
	}
	
	
	public static boolean equals (Node a, Node b) {		
		if (a.getNodeType() == b.getNodeType()) {
			switch (a.getNodeType()) {
				case Node.DOCUMENT_NODE:
					return equalsDocument(a, b);
				case Node.ELEMENT_NODE:
					return equalsElement(a, b);
				case Node.TEXT_NODE:
					if (a.getNodeValue().trim().isEmpty()) {
						return true;
					} else {
						if (!a.getNodeValue().equals(b.getNodeValue())) {
							System.err.println("Value of text node '" + a.getNodeValue() + "' not equal to '" + b.getNodeValue() + "'");
							return false;
						}
					}
				default:
					System.out.println("Skipping: " + a.getNodeName() + a.getNodeValue());
					break;					
			}
		}
		return true;
	}
	
	public static boolean equalsDocument(Node a, Node b) {
		System.out.println("Checking Document");
		return equalsChildren(a, b);
	}
	
	public static boolean equalsChildren(Node a, Node b) {
		List <Node> childrenOfA = filterCommentNodes(a);
		List <Node> childrenOfB = filterCommentNodes(b);
		if (childrenOfA.size() != childrenOfB.size()) {
			return false;
		}
		for (int i=0; i<childrenOfA.size(); i++) {
			if (findChild(childrenOfA.get(i), b) == null) {
				System.err.println("Unable to match node!");
				return false;
			}
			//if (! equals(childrenOfA.get(i), childrenOfB.get(i))) {
			//	System.err.println("" + "");
			//	return false;
			//}
		}
		return true;
	}
	
	public static boolean equalsAttributes(Node a, Node b) {
		if (a.getAttributes().getLength() != b.getAttributes().getLength())
			return false;
		
		NamedNodeMap attributes = a.getAttributes();
		for (int i=0; i<attributes.getLength(); i++) {
			
			Node attrA = attributes.item(i);
			
			if (attrA.getNamespaceURI().equals("http://www.w3.org/2000/xmlns/")) {
				
			} else {
			
				String namespace = attributes.item(i).getNamespaceURI();
				String localname = attributes.item(i).getLocalName();
				String attrvalue = attributes.item(i).getNodeValue();
				Node attrInB = b.getAttributes().getNamedItemNS(namespace, localname);
								
				if (attrInB == null) {
					System.err.println("Attribute " + localname + "{@" + namespace + "} missing in node " + a.getLocalName() + "{@" + a.getNamespaceURI() + "}");
					return false;
				} else {
					if (!a.getAttributes().item(i).getNodeValue().equals(attrInB.getNodeValue())) {
						System.err.println("Attribute " + localname + "{@" + namespace + "} in node " + a.getLocalName() + "{@" + a.getNamespaceURI() + "} has value '" + attrInB.getNodeValue() +"' not '" + attrvalue + "'");
						return false;
					}
				}
			}
		}
		return true;
	}
	
	public static Node findChild(Node child, Node parent) {
		NodeList nodelist = parent.getChildNodes();
		for (int i=0; i<nodelist.getLength(); i++) {
			Node item = nodelist.item(i);
			if (equals(child, item)) {
				return item;
			}
		}
		return null;
	}
	
	public static boolean equalsElement(Node a, Node b) {
		
		String namespace = a.getNamespaceURI();
		String localname = a.getLocalName();
		String nodevalue = a.getNodeValue();
		
		System.out.println("Checking Element: " + localname + "@{" + namespace + "}");
		
		if (! namespace.equals(b.getNamespaceURI())) {
			return false;
		}
		
		if (! localname.equals(b.getLocalName()))
			return false;
		
		if (! equalsAttributes(a, b))
			return false;
		
		return equalsChildren(a, b);
	}
	
	public static List <Node> filterCommentNodes(Node parent) {
		List <Node> nodeList = new ArrayList <Node> ();
		for (int i=0; i<parent.getChildNodes().getLength(); i++) {
			Node child = parent.getChildNodes().item(i);
			if (child.getNodeType() != Node.COMMENT_NODE) {
				nodeList.add(child);
			}
		}
		return nodeList;
	}
	
	public static void printXml(XmlObject xmlObj) throws IOException {
		String hexFormat = "%1$x %2$x %3$x %4$x %5$x %6$x %7$x %8$x %9$x %10$x %11$x %12$x %13x %14$x %15$x %16$x";
		Reader r = xmlObj.newReader();
		char[] cbuf = new char[16];
		int len = 0;
		while ((len = r.read(cbuf)) != -1) {
			
			System.out.print(new String(cbuf, 0, len));
			//System.out.print("  ::  ");
			//System.out.println(String.format(hexFormat, cbuf));
		}
	}
	
	public static Node clean(Node node) {
		switch (node.getNodeType()) {
			case Node.DOCUMENT_FRAGMENT_NODE:
			case Node.DOCUMENT_NODE:
			default:
				NodeList children = node.getChildNodes();
				Node[] removalList = new Node[children.getLength()];
				int removalCount = 0;
				for (int i=0; i<children.getLength(); i++) {
					Node child = children.item(i);
					switch (child.getNodeType()) {
						case Node.COMMENT_NODE:
						case Node.PROCESSING_INSTRUCTION_NODE:
							removalList[removalCount++] = child;
							continue;
						case Node.TEXT_NODE:
							if (child.getNodeValue().matches("\\s*")) {
								removalList[removalCount++] = child;
								continue;
							}
						default:
							clean(child);
					}
				}
				for (int i=0; i<removalCount; i++) {
					node.removeChild(removalList[i]);
				}
		}
		return node;
	}	
	
	public static boolean equals (XmlObject a, XmlObject b) throws XmlException {
		String deepEqualsXQuery =
			"declare namespace xf = 'http://www.oracle.com/uk/ocs/osb/xqtest/';\n" + 
			"declare function xf:deep-equals($a as element(*), $b as element(*)) as xs:boolean {\n" +
			"    fn:deep-equal($a, $b)" +
			"};" +
			"declare variable $a as element(*) external;\n" +
			"declare variable $b as element(*) external;\n" +
			"xf:deep-equals($a, $b)";
		
		clean(a.getDomNode());
		clean(b.getDomNode());
		XmlObject deepEqualsXml = XmlObject.Factory.newInstance();
		XmlOptions deepEqualsXmlOpts = new XmlOptions();		
		HashMap <String, Object> deepEqualsParameters = new HashMap <String, Object> ();
		
		Node na = a.getDomNode();
		Node nb = b.getDomNode();

		if (a.getDomNode().getNodeType() == Node.DOCUMENT_NODE)
			a = a.selectPath("./*[1]")[0];
		
		if (b.getDomNode().getNodeType() == Node.DOCUMENT_NODE)
			b = b.selectPath("./*[1]")[0];
		
		//deepEqualsParameters.put("a", a.selectPath("./*[1]")[0]);
		//deepEqualsParameters.put("b", b.selectPath("./*[1]")[0]);
		deepEqualsParameters.put("a", a);
		deepEqualsParameters.put("b", b);
		deepEqualsXmlOpts.setXqueryVariables(deepEqualsParameters);
		XmlObject[] deepEqualsResult = deepEqualsXml.execQuery(deepEqualsXQuery, deepEqualsXmlOpts);
		
		return "true".equals(deepEqualsResult[0].getDomNode().getFirstChild().getNodeValue());
	}
	
}
