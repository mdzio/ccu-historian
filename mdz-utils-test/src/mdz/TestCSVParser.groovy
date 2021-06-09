package mdz

import static org.junit.Assert.*

import org.junit.Test

class TestCSVParser {

	@Test
	public void testValidCSV() {
		def tests = [
			['', []],
			['\r\n', [['']]],
			['\n\n', [[''], ['']]],
			['\na\n\n', [[''], ['a'], ['']]],
			['a\nb\n', [['a'], ['b']]],
			['abc', [['abc']]],
			[',', [['', '']]],
			[',,', [['', '', '']]],
			[',\r\n,\n', [['', ''], ['', '']]],
			['abc,def', [['abc', 'def']]],
			[' abc ,\tdef ', [[' abc ', '\tdef ']]],
			['abc,def\nhij,klm', [['abc', 'def'], ['hij', 'klm']]],
			[' " , "', [[' " ', ' "']]],
			['"""abc","de""f"\nhij,"klm"""', [['"abc', 'de"f'], ['hij', 'klm"']]],
			['"a\r\nb"\r\nc\r\n', [['a\r\nb'], ['c']]],
		]

		def p=new CSVParser()
		tests.each { i, w ->
			def r=[]
			p.parse(new StringReader(i), {fs -> r << fs.clone(); true })
			assert r==w
		}
	}

	@Test
	public void testInvalidCSV() {
		def tests = [
			['"', 'EOF within quotes'],
			['"abc', 'EOF within quotes'],
			['"abc","', 'EOF within quotes'],
			['"abc" ,', 'Invalid quote escape'],
		]
		def p=new CSVParser()
		tests.each { i, w ->
			try {
				p.parse(new StringReader(i), {fs -> true })
			} catch (e) {
				assert e.message==w
			}
		}
	}
}
