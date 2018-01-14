/*
    CCU-Historian, a long term archive for the HomeMatic CCU
    Copyright (C) 2011-2017 MDZ (info@ccu-historian.de)

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package mdz;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Text {

	public static void forEachMatch(String str, Pattern pat, Consumer<String[]> cons) {
		Objects.requireNonNull(str);
		Objects.requireNonNull(pat);
		Objects.requireNonNull(cons);

		Matcher mat = pat.matcher(str);
		while (mat.find()) {
			String[] grps = new String[mat.groupCount() + 1];
			for (int idx = 0; idx < mat.groupCount() + 1; idx++) {
				grps[idx] = mat.group(idx);
			}
			cons.accept(grps);
		}
	}

	public static String replaceAll(String str, Pattern pat, Function<String[], String> replFunc) {
		Objects.requireNonNull(str);
		Objects.requireNonNull(pat);
		Objects.requireNonNull(replFunc);

		StringBuffer sb = new StringBuffer();
		Matcher mat = pat.matcher(str);
		while (mat.find()) {
			String[] grps = new String[mat.groupCount() + 1];
			for (int idx = 0; idx < mat.groupCount() + 1; idx++) {
				grps[idx] = mat.group(idx);
			}
			String repl = replFunc.apply(grps);
			mat.appendReplacement(sb, repl);
		}
		mat.appendTail(sb);
		return sb.toString();
	}

	private static Pattern unescapeXmlPattern = Pattern
			.compile("&((lt)|(gt)|(quot)|(apos)|(amp)|#(\\d+)|#x(\\p{XDigit}+));");

	public static String unescapeXml(String str) {
		if (str == null || str.isEmpty())
			return "";
		return replaceAll(str, unescapeXmlPattern, groups -> {
			if (groups[2] != null) {
				return "<";
			} else if (groups[3] != null) {
				return ">";
			} else if (groups[4] != null) {
				return "\"";
			} else if (groups[5] != null) {
				return "'";
			} else if (groups[6] != null) {
				return "&";
			} else if (groups[7] != null) {
				StringBuilder c = new StringBuilder();
				c.appendCodePoint(Integer.parseInt(groups[7]));
				return c.toString();
			} else if (groups[8] != null) {
				StringBuilder c = new StringBuilder();
				c.appendCodePoint(Integer.parseInt(groups[8], 16));
				return c.toString();
			}
			return "";
		});
	}

	public static String escapeXml(String str) {
		if (str == null || str.isEmpty())
			return "";
		StringBuffer sb = new StringBuffer();
		str.codePoints().forEachOrdered(cp -> {
			switch (cp) {
			case '<':
				sb.append("&lt;");
				break;
			case '>':
				sb.append("&gt;");
				break;
			case '"':
				sb.append("&quot;");
				break;
			case '\'':
				sb.append("&apos;");
				break;
			case '&':
				sb.append("&amp;");
				break;
			default:
				sb.appendCodePoint(cp);
				break;
			}
		});
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
		throw new IllegalArgumentException(
				"Can't interpret " + value + " (type: " + value.getClass().getName() + ") as boolean value");
	}

	private static final int BYTES_PER_ROW = 32;

	public static String prettyPrint(byte[] data) {
		int pos = 0;
		boolean firstLine = true;
		StringBuilder sb = new StringBuilder();
		while (data.length > pos) {
			if (firstLine) {
				firstLine = false;
			} else {
				sb.append('\n');
			}
			sb.append(String.format("%04X: ", pos));
			for (int pos2 = 0; pos2 < BYTES_PER_ROW; pos2++) {
				if (pos + pos2 >= data.length) {
					sb.append(' ');
				} else {
					char ch = (char) data[pos + pos2];
					if (ch >= 0x20 && ch <= 0x7e)
						sb.append(ch);
					else
						sb.append('.');
				}
			}
			sb.append("  ");
			for (int pos2 = 0; pos2 < BYTES_PER_ROW && pos + pos2 < data.length; pos2++) {
				int ch = data[pos + pos2] & 0xff;
				sb.append(String.format("%02X ", ch));
			}
			pos += BYTES_PER_ROW;
		}
		return sb.toString();
	}
}
