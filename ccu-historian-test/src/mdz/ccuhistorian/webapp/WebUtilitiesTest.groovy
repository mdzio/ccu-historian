package mdz.ccuhistorian.webapp

import groovy.time.BaseDuration

class WebUtilitiesTest extends GroovyTestCase {

	public void testParseDate() {
		WebUtilities wu=[]
		
		assert wu.parseDate('')==null
		assert wu.parseDate('31.12.2017 23:59:58').time==1514761198000
		assert wu.parseDate('20171231235957').time==1514761197000
	}
	
	public void testParseRelativeDate() {
		WebUtilities wu=[]
		def d

		d=wu.parseDate(null, '')
		assert d==null

		def rd=new Date()
		d=wu.parseDate(rd, '')
		assert d==rd
		// should create new date instance
		assert !(d.is(rd))
		
		d=wu.parseDate(wu.parseDate('1.1.2018'), '')
		assert wu.format(d)=='01.01.2018 00:00:00'
		
		d=wu.parseDate(wu.parseDate('1.2.2018'), '1Y')
		assert wu.format(d)=='01.02.2019 00:00:00'

		d=wu.parseDate(wu.parseDate('2.1.2018'), '   -1Y  ')
		assert wu.format(d)=='02.01.2017 00:00:00'

		d=wu.parseDate(wu.parseDate('1.2.2018 11:22:33'), '-1Y')
		assert wu.format(d)=='01.02.2017 11:22:33'
		
		d=wu.parseDate(wu.parseDate('1.2.2018 11:22:33'), '-1Y+3D -1h-13M 10m-33s +1W')
		assert wu.format(d)=='11.01.2016 10:32:00'

		d=wu.parseDate(wu.parseDate('1.1.2018'), '2017=Y2=M3=D12=h31=m12=s')
		assert wu.format(d)=='03.02.2017 12:31:12'

		d=wu.parseDate(wu.parseDate('1.1.2018'), '  2017=Y 2=M 3=D 12=h 31=m 12=s \t')
		assert wu.format(d)=='03.02.2017 12:31:12'
		
		d=wu.parseDate(wu.parseDate('1.1.2018'), '2=W')
		assert wu.format(d)=='08.01.2018 00:00:00'
		
		d=wu.parseDate(wu.parseDate('3.1.2018 11:12:13'), 'z')
		assert wu.format(d)=='03.01.2018 00:00:00'
	}
	
	public void testParseDuration() {
		WebUtilities wu=[]
		
		BaseDuration d=wu.parseDuration('+2D')
		assert d.getDays()==2
	}
}
