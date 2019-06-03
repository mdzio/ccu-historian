package mdz.ccuhistorian

import java.sql.Timestamp

public class DatabaseExtensions {
	
	public static Long TS_TO_UNIX(Timestamp ts) {
		null==ts?null:(long)(ts.time.intdiv(1000l))
	}
	
	public static Timestamp UNIX_TO_TS(Long unix) {
		null==unix?null:new Timestamp(unix*1000l)
	}
}
