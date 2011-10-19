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

import org.apache.xmlbeans.XmlObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * A example of using the XQuery test framework
 * 
 * @author James Nash (james.nash@oracle.com)
 *
 */

public class XQExampleTest extends XQAbstractTest {

	@Before
	public void setup() {
		declareNamespace("test", "http://www.example.org/test");
		declareNamespace("test2", "http://www.example.org/test2");
	}
	
	@Test
	public void test1() throws Exception {
		setParameter("test", new File("testdata/Test1.xml"));
		executeQuery(new File("testdata/TestQuery.xq"));
		assertEquals(0, "elemA elemB");
	}
	
	@Test
	public void test2() throws Exception {
		setParameter("test", new File("testdata/Test1.xml"));
		executeQuery(new File("testdata/TestQuery2.xq"));
		assertEquals(0, "./test2:Test2/test2:elem", "elemAelemB");
		assertEquals(0, "./test2:Test2/@attr", "attrA");
	}
	
	@Test
	public void test3() throws Exception {
		setParameter("test", new File("testdata/Test1.xml"));
		executeQuery(new File("testdata/TestQuery2.xq"));
		getResult()[0].save(System.out);
		XmlObject expected = XmlObject.Factory.parse(
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<test2:Test2 attr=\"attrA\" xmlns:test2=\"http://www.example.org/test2\">\n" +
			"    <test2:elem>elemAelemB</test2:elem>\n" +
			"</test2:Test2>"
		);
		System.out.println();
		System.out.println("-------");
		expected.save(System.out);
		//Assert.assertEquals(true, expected.valueEquals(expected));
		assertDeepEquals(0, expected);
	}
	
		
	/**
	 * Demonstrates how to execute an XQuery and compare the results to an
	 * expected value stored in an external file
	 * 
	 * @throws Exception	IOException or XmlException
	 */
	@Test
	public void test4() throws Exception {
		
		// Bind the xquery named parameter 'test' to the contents of Test1.xml file
		setParameter("test", new File("testdata/Test1.xml"));
		
		// Execute the XQuery
		executeQuery(new File("testdata/TestQuery2.xq"));
		
		//
	}
	
}
