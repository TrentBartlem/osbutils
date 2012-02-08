package com.oracle.uk.ocs.osb.xqtest;

import org.junit.Test;
import static org.junit.Assert.*;

import weblogic.xml.query.exceptions.XQueryException;


public class WebLogicXQueryTestFrameworkTest extends WebLogicXQueryTestFramework {
	
	public WebLogicXQueryTestFrameworkTest() throws XQueryException {
		super();
	}

	@Test
	public void testStringReturnValue () throws Exception {
		String xq = 
			"declare namespace xf =\"http://www.example.org/test\";\n\n" +
			"declare function xf:TestStringReturnValue()\n" +
			"as xs:string {\n" +
			"    \"Hello World!\"" +
			"};\n" +
			"xf:TestStringReturnValue()";
		executeQuery(xq);
		assertEquals("Hello World!", getResult());
	}
	
	@Test
	public void testIntegerReturnValue () throws Exception {
		String xq = 
			"declare namespace xf =\"http://www.example.org/test\";\n\n" +
			"declare function xf:TestIntegerReturnValue()\n" +
			"as xs:integer {\n" +
			"    12345" +
			"};\n" +
			"xf:TestIntegerReturnValue()";
		executeQuery(xq);
		assertEquals(12345, getResult());
	}
	
	@Test
	public void testBooleanReturnValue () throws Exception {
		String xq = 
			"declare namespace xf =\"http://www.example.org/test\";\n\n" +
			"declare function xf:TestBooleanReturnValue()\n" +
			"as xs:boolean {\n" +
			"    xs:boolean(\"true\")" +
			"};\n" +
			"xf:TestBooleanReturnValue()";
		executeQuery(xq);
		assertEquals(true, getResult());
	}		
	
}
