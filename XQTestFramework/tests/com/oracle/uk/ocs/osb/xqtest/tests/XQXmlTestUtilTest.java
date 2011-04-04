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

package com.oracle.uk.ocs.osb.xqtest.tests;

import java.io.StringWriter;

import javax.xml.crypto.dsig.XMLObject;

import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.junit.Test;

import com.oracle.uk.ocs.osb.xqtest.XQXmlTestUtil;

import static org.junit.Assert.*;


public class XQXmlTestUtilTest {

	@Test
	public void testXMLStripWhitespace() throws Exception {
		
		String booklistXmlFull =
			"<BookList>\n" +
			"    <Book isbn=\"123456789\">\n" +
			"        <Title>How to Test XML</Title>\n" +
			"        <Author>Joe Bloggs</Author>\n" +
			"    </Book>\n" + 
			"</BookList>";
		
		String booklistXmlCompact =
			"<BookList>" +
			"<Book isbn=\"123456789\">" +
			"<Title>How to Test XML</Title>" +
			"<Author>Joe Bloggs</Author>" +
			"</Book>" + 
			"</BookList>";
			
		XmlObject booklistFull = XmlObject.Factory.parse(booklistXmlFull);
		
		StringWriter sw;
		
		sw = new StringWriter();
		booklistFull.save(sw);
		assertEquals(booklistXmlFull, sw.toString());
		
		XmlOptions opts = new XmlOptions();
		opts.setLoadStripWhitespace();
		XmlObject booklistCompact = XmlObject.Factory.parse(booklistFull.getDomNode(), opts);
		
		sw = new StringWriter();
		booklistCompact.save(sw);
		assertEquals(booklistXmlCompact, sw.toString());
	}

	@Test
	public void testXMLStripComments() throws Exception {
		
		String booklistXmlWithComments =
			"<!--\n" +
			"    This is a comment block\n" +
			"-->" + 
			"<BookList>\n" +
			"    <Book isbn=\"123456789\">\n" +
			"        <Title>How to Test XML</Title>\n" +
			"        <Author>Joe Bloggs</Author>\n" +
			"    </Book>\n" + 
			"</BookList>";
		
		String booklistXmlStripped =
			"<BookList>" +
			"<Book isbn=\"123456789\">" +
			"<Title>How to Test XML</Title>" +
			"<Author>Joe Bloggs</Author>" +
			"</Book>" + 
			"</BookList>";			
		
		XmlObject booklistComments = XmlObject.Factory.parse(booklistXmlWithComments);

		StringWriter sw;
		
		sw = new StringWriter();
		booklistComments.save(sw);
		assertEquals(booklistXmlWithComments, sw.toString());		
		
		XmlOptions opts = new XmlOptions();
		opts.setLoadStripComments();
		opts.setLoadStripWhitespace();
		
		XmlObject booklistStripped = XmlObject.Factory.parse(booklistComments.getDomNode(), opts);
		
		sw = new StringWriter();
		booklistStripped.save(sw);
		assertEquals(booklistXmlStripped, sw.toString());
	}
	
	///////////////////////////////////////////////////////////////////////////
	// Test: equals method
	///////////////////////////////////////////////////////////////////////////
	
	@Test
	public void equals_SameObject() throws Exception {	
		String xml =
			"<BookList>\n" +
			"    <Book isbn=\"123456789\">\n" +
			"        <Title>How To Test XML</Title>\n" +
			"        <Author>Joe Bloggs</Author>\n" +
			"    </Book>\n" +
			"</BookList>";
		XmlObject a = XmlObject.Factory.parse(xml);
		assertTrue(XQXmlTestUtil.equals(a, a));
	}
	
	
	@Test
	public void equals_SameXml() throws Exception {
		String aXml =
			"<BookList>\n" +
			"    <Book isbn=\"123456789\">\n" +
			"        <Title>How To Test XML</Title>\n" +
			"        <Author>Joe Bloggs</Author>\n" +
			"    </Book>\n" +
			"</BookList>";
		XmlObject a = XmlObject.Factory.parse(aXml);
		XmlObject b = XmlObject.Factory.parse(aXml);	
		assertTrue(XQXmlTestUtil.equals(a, b));
	}
	
	
	@Test
	public void equals_DifferentXml() throws Exception {
		
		String aXml =
			"<BookList>\n" +
			"    <Book isbn=\"123456789\">\n" +
			"        <Title>How To Test XML</Title>\n" +
			"        <Author>Joe Bloggs</Author>\n" +
			"    </Book>\n" +
			"</BookList>";
		
		String bXml =
			"<BookListAA>\n" +
			"    <Book isbn=\"123456789\">\n" +
			"        <Title>How To Test XML</Title>\n" +
			"        <Author>Joe Bloggs</Author>\n" +
			"    </Book>\n" +
			"</BookListAA>";
		
		XmlObject a = XmlObject.Factory.parse(aXml);
		XmlObject b = XmlObject.Factory.parse(bXml);
		
		assertFalse(XQXmlTestUtil.equals(a, b));
	}	
	
	@Test
	public void testXmlEqualsIgnoreCommentsAndWhitespace() throws Exception {
		String aXml =
			"<BookList>\n" +
			"    <Book isbn=\"123456789\">\n" +
			"        <Title>How To Test XML</Title>\n" +
			"        <Author>Joe Bloggs</Author>\n" +
			"    </Book>\n" +
			"</BookList>";
		
		String bXml =
			"<!--\n" +
			"    This is a comment block\n" +
			"-->\n" +
			"\n" +
			"<BookList>\n" +
			"<Book isbn=\"123456789\">\n" +
			"        <Title>How To Test XML</Title>\n" +
			"        <Author>Joe Bloggs</Author>\n" +
			"</Book>\n" +
			"</BookList>";
		
		XmlObject a = XmlObject.Factory.parse(aXml);
		XmlObject b = XmlObject.Factory.parse(bXml);
		
		assertTrue(XQXmlTestUtil.equals(a, b));
	}
	
	@Test
	public void testXmlEqualsNamespaceSimple() throws Exception {
		String aXml =
			"<bs:BookList xmlns:bs=\"http://www.example.org/bookstore\">\n" +
			"    <bs:Book isbn=\"123456789\">\n" +
			"        <bs:Title>How To Test XML</bs:Title>\n" +
			"        <bs:Author>Joe Bloggs</bs:Author>\n" +
			"    </bs:Book>\n" +
			"</bs:BookList>";
		
		String bXml =
			"<bs:BookList xmlns:bs=\"http://www.example.org/bookstore\">\n" +
			"    <bs:Book isbn=\"123456789\">\n" +
			"        <bs:Title>How To Test XML</bs:Title>\n" +
			"        <bs:Author>Joe Bloggs</bs:Author>\n" +
			"    </bs:Book>\n" +
			"</bs:BookList>";
		
		XmlObject a = XmlObject.Factory.parse(aXml);
		XmlObject b = XmlObject.Factory.parse(bXml);
		
		assertTrue(XQXmlTestUtil.equals(a, b));
	}
	
	@Test
	public void testXmlEqualsNamespacePositive() throws Exception {
		String aXml =
			"<bs:BookList xmlns:bs=\"http://www.example.org/bookstore\">\n" +
			"    <bs:Book isbn=\"123456789\">\n" +
			"        <bs:Title>How To Test XML</bs:Title>\n" +
			"        <bs:Author>Joe Bloggs</bs:Author>\n" +
			"    </bs:Book>\n" +
			"</bs:BookList>";
		
		String bXml =
			"<book:BookList xmlns:book=\"http://www.example.org/bookstore\">\n" +
			"    <book:Book isbn=\"123456789\">\n" +
			"        <book:Title>How To Test XML</book:Title>\n" +
			"        <book:Author>Joe Bloggs</book:Author>\n" +
			"    </book:Book>\n" +
			"</book:BookList>";
		
		XmlObject a = XmlObject.Factory.parse(aXml);
		XmlObject b = XmlObject.Factory.parse(bXml);
		
		assertTrue(XQXmlTestUtil.equals(a, b));
	}
	
	@Test
	public void testXmlEqualsNamespaceNegative() throws Exception {
		String aXml =
			"<bs:BookList xmlns:bs=\"http://www.example.org/bookstore\">\n" +
			"    <bs:Book isbn=\"123456789\">\n" +
			"        <bs:Title>How To Test XML</bs:Title>\n" +
			"        <bs:Author>Joe Bloggs</bs:Author>\n" +
			"    </bs:Book>\n" +
			"</bs:BookList>";
		
		String bXml =
			"<bs:BookList xmlns:bs=\"http://www.example.org/bookstore2\">\n" +
			"    <bs:Book isbn=\"123456789\">\n" +
			"        <bs:Title>How To Test XML</bs:Title>\n" +
			"        <bs:Author>Joe Bloggs</bs:Author>\n" +
			"    </bs:Book>\n" +
			"</bs:BookList>";
		
		XmlObject a = XmlObject.Factory.parse(aXml);
		XmlObject b = XmlObject.Factory.parse(bXml);
		
		assertFalse(XQXmlTestUtil.equals(a, b));
	}
	
	@Test
	public void testXmlEqualsAttrsSimple() throws Exception {
		String aXml =
			"<bs:BookList xmlns:bs=\"http://www.example.org/bookstore\">\n" +
			"    <bs:Book isbn=\"123456789\">\n" +
			"        <bs:Title>How To Test XML</bs:Title>\n" +
			"        <bs:Author>Joe Bloggs</bs:Author>\n" +
			"    </bs:Book>\n" +
			"</bs:BookList>";
		
		String bXml =
			"<bs:BookList xmlns:bs=\"http://www.example.org/bookstore\">\n" +
			"    <bs:Book isbn=\"123456789\">\n" +
			"        <bs:Title>How To Test XML</bs:Title>\n" +
			"        <bs:Author>Joe Bloggs</bs:Author>\n" +
			"    </bs:Book>\n" +
			"</bs:BookList>";
		
		XmlObject a = XmlObject.Factory.parse(aXml);
		XmlObject b = XmlObject.Factory.parse(bXml);
		
		assertTrue(XQXmlTestUtil.equals(a, b));
	}
	
	@Test
	public void testXmlEqualsAttrsPositive() throws Exception {
		String aXml =
			"<bs:BookList xmlns:bs=\"http://www.example.org/bookstore\">\n" +
			"    <bs:Book isbn=\"123456789\">\n" +
			"        <bs:Title>How To Test XML</bs:Title>\n" +
			"        <bs:Author>Joe Bloggs</bs:Author>\n" +
			"    </bs:Book>\n" +
			"</bs:BookList>";
		
		String bXml =
			"<bs:BookList xmlns:bs=\"http://www.example.org/bookstore\">\n" +
			"    <bs:Book isbn=\"123456789\">\n" +
			"        <bs:Title>How To Test XML</bs:Title>\n" +
			"        <bs:Author>Joe Bloggs</bs:Author>\n" +
			"    </bs:Book>\n" +
			"</bs:BookList>";
		
		XmlObject a = XmlObject.Factory.parse(aXml);
		XmlObject b = XmlObject.Factory.parse(bXml);
		
		assertTrue(XQXmlTestUtil.equals(a, b));
	}
	
	@Test
	public void testXmlEqualsAttrsNegative() throws Exception {
		String aXml =
			"<bs:BookList xmlns:bs=\"http://www.example.org/bookstore\">\n" +
			"    <bs:Book isbn=\"123456789\">\n" +
			"        <bs:Title>How To Test XML</bs:Title>\n" +
			"        <bs:Author>Joe Bloggs</bs:Author>\n" +
			"    </bs:Book>\n" +
			"</bs:BookList>";
		
		String bXml =
			"<bs:BookList xmlns:bs=\"http://www.example.org/bookstore\">\n" +
			"    <bs:Book isbn=\"111111111\">\n" +
			"        <bs:Title>How To Test XML</bs:Title>\n" +
			"        <bs:Author>Joe Bloggs</bs:Author>\n" +
			"    </bs:Book>\n" +
			"</bs:BookList>";
		
		XmlObject a = XmlObject.Factory.parse(aXml);
		XmlObject b = XmlObject.Factory.parse(bXml);
		
		assertFalse(XQXmlTestUtil.equals(a, b));
	}
	
	@Test
	public void testXmlEqualsAttrsMissing() throws Exception {
		String aXml =
			"<bs:BookList xmlns:bs=\"http://www.example.org/bookstore\">\n" +
			"    <bs:Book isbn=\"123456789\">\n" +
			"        <bs:Title>How To Test XML</bs:Title>\n" +
			"        <bs:Author>Joe Bloggs</bs:Author>\n" +
			"    </bs:Book>\n" +
			"</bs:BookList>";
		
		String bXml =
			"<bs:BookList xmlns:bs=\"http://www.example.org/bookstore\">\n" +
			"    <bs:Book>\n" +
			"        <bs:Title>How To Test XML</bs:Title>\n" +
			"        <bs:Author>Joe Bloggs</bs:Author>\n" +
			"    </bs:Book>\n" +
			"</bs:BookList>";
		
		XmlObject a = XmlObject.Factory.parse(aXml);
		XmlObject b = XmlObject.Factory.parse(bXml);
		
		assertFalse(XQXmlTestUtil.equals(a, b));
	}
	
	@Test
	public void testXmlEqualsValueNegative() throws Exception {
		String aXml =
			"<bs:BookList xmlns:bs=\"http://www.example.org/bookstore\">\n" +
			"    <bs:Book isbn=\"123456789\">\n" +
			"        <bs:Title>How To Test XML</bs:Title>\n" +
			"        <bs:Author>Joe Bloggs</bs:Author>\n" +
			"    </bs:Book>\n" +
			"</bs:BookList>";
		
		String bXml =
			"<bs:BookList xmlns:bs=\"http://www.example.org/bookstore\">\n" +
			"    <bs:Book isbn=\"123456789\">\n" +
			"        <bs:Title>How To Test XML</bs:Title>\n" +
			"        <bs:Author>Jane Bloggs</bs:Author>\n" +
			"    </bs:Book>\n" +
			"</bs:BookList>";
		
		XmlObject a = XmlObject.Factory.parse(aXml);
		XmlObject b = XmlObject.Factory.parse(bXml);
		
		assertFalse(XQXmlTestUtil.equals(a, b));
	}
	
	@Test
	public void testXmlEqualsDifferentChildren() throws Exception {
		String aXml =
			"<bs:BookList xmlns:bs=\"http://www.example.org/bookstore\">\n" +
			"    <bs:Book isbn=\"123456789\">\n" +
			"        <bs:Title>How To Test XML</bs:Title>\n" +
			"        <bs:Author>Joe Bloggs</bs:Author>\n" +
			"    </bs:Book>\n" +
			"</bs:BookList>";
		
		String bXml =
			"<bs:BookList xmlns:bs=\"http://www.example.org/bookstore\">\n" +
			"    <bs:Book isbn=\"123456789\">\n" +
			"        <bs:Title>How To Test XML</bs:Title>\n" +
			"        <bs:Price>£2.00</bs:Price>\n" +
			"    </bs:Book>\n" +
			"</bs:BookList>";
		
		XmlObject a = XmlObject.Factory.parse(aXml);
		XmlObject b = XmlObject.Factory.parse(bXml);
		
		assertFalse(XQXmlTestUtil.equals(a, b));
	}
	
	@Test
	public void equals_WithXPathsSimple () throws Exception {
		String aXml =
			"<BookList>\n" +
			"    <Book isbn=\"123456789\">\n" +
			"        <Title>How To Test XML</Title>\n" +
			"        <Author>Joe Bloggs</Author>\n" +
			"    </Book>\n" +
			"</BookList>";
		
		String bXml =
			"<Title>How To Test XML</Title>";
		
		XmlObject a = XmlObject.Factory.parse(aXml);
		XmlObject b = XmlObject.Factory.parse(bXml);
		
		assertTrue(XQXmlTestUtil.equals(a, "/BookList/Book/Title",  b, "."));		
	}

	@Test
	public void equals_WithXPaths_WithNamespaces () throws Exception {
		String aXml =
			"<BookList xmlns=\"http://www.example.org/test\">\n" +
			"    <Book isbn=\"123456789\">\n" +
			"        <Title>How To Test XML</Title>\n" +
			"        <Author>Joe Bloggs</Author>\n" +
			"    </Book>\n" +
			"</BookList>";
		
		String bXml =
			"<Title xmlns=\"http://www.example.org/test\">How To Test XML</Title>";
		
		XmlObject a = XmlObject.Factory.parse(aXml);
		XmlObject b = XmlObject.Factory.parse(bXml);
		
		assertTrue(XQXmlTestUtil.equals(a, "/*:BookList/*:Book/*:Title",  b, "."));		
	}
	
	@Test
	public void equals_WithXPaths_WithNamespacesPrefixes () throws Exception {
		String aXml =
			"<BookList xmlns=\"http://www.example.org/test\">\n" +
			"    <Book isbn=\"123456789\">\n" +
			"        <Title>How To Test XML</Title>\n" +
			"        <Author>Joe Bloggs</Author>\n" +
			"    </Book>\n" +
			"</BookList>";
		
		String bXml =
			"<Title xmlns=\"http://www.example.org/test\">How To Test XML</Title>";
		
		XmlObject a = XmlObject.Factory.parse(aXml);
		XmlObject b = XmlObject.Factory.parse(bXml);
		
		String xpathA =
			"declare namespace book=\"http://www.example.org/test\";\n" +
			"/book:BookList/book:Book/book:Title";
		
		assertTrue(XQXmlTestUtil.equals(a, xpathA,  b, "."));		
	}

	
	
	/*
	
	@Test
	public void testDeepEquals_SimplePositive() throws Exception {
		String simpleXml = "<parent><child>Hello</child></parent>";
		XmlObject a = XmlObject.Factory.parse(simpleXml);
		assertTrue(XQXmlTestUtil.deepEqual(a, a));
	}
	
	@Test
	public void testDeepEquals_SimplePositive2() throws Exception {
		String simpleXmlA = "<parent><child>Hello</child></parent>";
		String simpleXmlB = "<parent><child>Hello</child></parent>";
		XmlObject a = XmlObject.Factory.parse(simpleXmlA);
		XmlObject b = XmlObject.Factory.parse(simpleXmlB);
		assertTrue(XQXmlTestUtil.deepEqual(a, b));
	}	
	
	@Test
	public void testDeepEquals_SimpleNegative() throws Exception {
		String simpleXmlA = "<parent><child>Hello</child></parent>";
		String simpleXmlB = "<parent><child>Other</child></parent>";
		XmlObject a = XmlObject.Factory.parse(simpleXmlA);
		XmlObject b = XmlObject.Factory.parse(simpleXmlB);
		assertFalse(XQXmlTestUtil.deepEqual(a, b));
	}

	@Test
	public void testDeepEquals_WhitespacePostive() throws Exception {
		String simpleXmlA = "<parent><child>Hello</child></parent>";
		String simpleXmlB = "<parent>\n<child>Hello</child>\n</parent>";
		XmlObject a = XmlObject.Factory.parse(simpleXmlA);
		XmlObject b = XmlObject.Factory.parse(simpleXmlB);
		assertTrue(XQXmlTestUtil.deepEqual(a, b));
	}
	
	*/

}
