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

import org.apache.xmlbeans.XmlObject;
import org.junit.Before;
import org.junit.Test;

import com.oracle.uk.ocs.osb.xqtest.XQAbstractTest;

/**
 * JUnit tests for the <code>AbstractXQTest</code> class
 * @author James Nash (james.nash@oracle.com)
 *
 */

public class XQAbstractTestTest extends XQAbstractTest {
	
	@Before
	public void setup() {
		declareNamespace("test", "http://www.example.org/test");
		declareNamespace("test2", "http://www.example.org/test2");
	}	
	
	public static String testXmlSimpleA =
		"<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
		"<Book></Book>";
	
	public static String testXmlFull =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
		"<BookStore xmlns=\"http://www.example.org/test\">\n" +
		"\t<Book isbn=\"123-123-123-001-1\">\n" +
		"\t\t<Title>OSB Fundamentals</Title>\n" +
		"\t\t<Author>Joe Bloggs</Author>\n" +
		"\t\t<Price>9.99</Price>\n" +
		"\t</Book>\n" +
		"</BookStore>\n";
	
	public static String testXQueryFull_total =
		"declare namespace test =\"http://www.example.org/test\";\n\n" +
		"declare function test:Total($booklist as element(test:BookList)\n" +
		"as xs:double {\n" +
		"    fn:sum($booklist/test:Book/test:Price)\n" +
		"};\n" +
		"declare variable $booklist as element(test:BookList) external;\n" +
		"test:Total($booklist)";
	
	@Test
	public static void testXQueryFull_change() throws Exception {
		
	}
	
	@Test
	public void test_AssertValid_Simple() throws Exception {
		XmlObject a = XmlObject.Factory.parse(testXmlSimpleA);

	}
	
}
