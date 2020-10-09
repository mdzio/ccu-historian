package mdz.ccuhistorian.webapp

import static mdz.ccuhistorian.webapp.WebUtilities.*

import org.junit.Test

import groovy.xml.MarkupBuilder

class WebUtilitiesTest {

	@Test
	public void testFormatString() {
		WebUtilities wu=[]
		
		assert wu.format('12345678901234567890')=='12345678901234567890'
		assert wu.format('123456789012345678901')=='12345678901234567890...'
	}
	
	@Test
	public void testBuildUrl() {
		assert ''==buildUrl(null)
		
		assert 'x'==buildUrl('x')
		
		assert 'x?a=1'==buildUrl('x', [a:1])
		assert 'x?a=1'==buildUrl('x', [a:[1]])
		assert 'x?a=1'==buildUrl('x', [a:([1] as Object[])])
		assert 'x?a=1'==buildUrl('x', [a:([1] as String[])])
		
		assert 'x?a=1&a=1'==buildUrl('x', [a:1], [a:1])
		assert 'x?a=1&a=2'==buildUrl('x', [a:1], [a:2])
		assert 'x?a=1&a=2'==buildUrl('x', [a:[1, 2]])
		assert 'x?a=1&a=2&a=2&a=3'==buildUrl('x', [a:[1, 2]], [a:[2, 3]])

		assert 'x?a=1&b=2&b=3'==buildUrl('x', [a:1], [b:[2, 3]])
		
		assert 'x?a=+'==buildUrl('x', [a:' '])
		assert 'x?%C3%A4=%C3%9F'==buildUrl('x', ['ä':'ß'])
	}
	
	@Test
	public void testInsertHiddenInputs() {
		def writer=new StringWriter()
		def html=new MarkupBuilder(writer)
		
		buildHiddenInputs(html, [a:[1, 2]], [a:[2, 3]])
		// ignore line endings
		def lines=writer.toString().readLines()
		assert lines==[
			"<input type='hidden' name='a' value='1' />",
			"<input type='hidden' name='a' value='2' />",
			"<input type='hidden' name='a' value='2' />",
			"<input type='hidden' name='a' value='3' />"
		]
		
		writer=new StringWriter()
		html=new MarkupBuilder(writer)
		buildHiddenInputs(html, ['>ä':'ß\''])
		assert writer.toString()=="<input type='hidden' name='&gt;ä' value='ß&apos;' />"
	}
}
