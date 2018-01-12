package mdz;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Text {

	private static Pattern unescapeXmlPattern = Pattern
			.compile("&((lt)|(gt)|(quot)|(apos)|(amp)|#(\\d+)|#x(\\p{XDigit}+));");

	public static String unescapeXml(String str) {
		if (str == null || str.isEmpty())
			return "";
		StringBuffer sb = new StringBuffer();
		Matcher m = unescapeXmlPattern.matcher(str);
		while (m.find()) {
			String r = "";
			if (m.group(2) != null) {
				r = "<";
			} else if (m.group(3) != null) {
				r = ">";
			} else if (m.group(4) != null) {
				r = "\"";
			} else if (m.group(5) != null) {
				r = "'";
			} else if (m.group(6) != null) {
				r = "&";
			} else if (m.group(7) != null) {
				StringBuffer c = new StringBuffer();
				c.appendCodePoint(Integer.parseInt(m.group(7)));
				r = c.toString();
			} else if (m.group(8) != null) {
				StringBuffer c = new StringBuffer();
				c.appendCodePoint(Integer.parseInt(m.group(8), 16));
				r = c.toString();
			}
			m.appendReplacement(sb, r);
		}
		m.appendTail(sb);
		return sb.toString();
	}

	public static boolean asBoolean(Object value) {
		if (value instanceof Boolean)
			return (Boolean) value;
		else if (value instanceof Number)
			return ((Number) value).doubleValue() != 0.0;
		else if (value instanceof String)
			switch (((String) value).trim().toLowerCase()) {
			case "0":
			case "false":
			case "off":
				return false;
			case "1":
			case "true":
			case "on":
				return true;
			}
		throw new IllegalArgumentException("Can't interpret " + value + " as boolean value");
	}
}
