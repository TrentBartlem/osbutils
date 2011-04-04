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

import java.io.File;

import org.apache.xmlbeans.XmlObject;
import org.junit.Before;
import org.junit.Test;

import com.oracle.uk.ocs.osb.xqtest.XQAbstractTest;

/**
 * JUnit tests for the <code>AbstractXQTest</code> class
 * @author James Nash (james.nash@oracle.com)
 *
 */

public class BookXQTest extends XQAbstractTest {
	
	@Before
	public void setup() {
		declareNamespace("book", "http://www.example.org/bookstore");
	}	
	
	@Test
	public void test_assertEquals_simpleTypeResult() throws Exception {
		setParameter("booklist", new File("testdata/BookList1.xml"));
		executeQuery(new File("testdata/Total.xq"));
		assertEquals(0, "123.97");
	}
	
	@Test
	public void test_assertEquals_complexTypeResult() throws Exception {
		setParameter("booklist1", new File("testdata/BookList1.xml"));
		setParameter("booklist2", new File("testdata/BookList2.xml"));
		executeQuery(new File("testdata/Merge.xq"));
		assertEquals(0, ".", new File("testdata/BookListMergeResult.xml"), ".");
	}
	
	
}
