package mdz.ccuhistorian

import static org.junit.Assert.*

import org.junit.Test

class DatabaseTest {

	@Test
	public void testFormatTimestamp() {
		def tests=[
			["2025-12-28", "2025-52", "2025"],
			["2025-12-29", "2026-01", "2025"],
			["2025-12-30", "2026-01", "2025"],
			["2025-12-31", "2026-01", "2025"],
			["2026-01-01", "2026-01", "2026"],
			
			["2026-12-27", "2026-52", "2026"],
			["2026-12-28", "2026-53", "2026"],
			["2027-01-03", "2026-53", "2027"],
			["2027-01-04", "2027-01", "2027"],
		]
		tests.each {
			def (ds, yw, y)=it
			def d=Date.parse("yyyy-MM-dd", ds)
			assertEquals(yw, Database.formatTimestamp("%X-%W", d))
			assertEquals(y, Database.formatTimestamp("%Y", d))
		}
	}
}
