/*
    CCU-Historian, a long term archive for the HomeMatic CCU
    Copyright (C) 2021 MDZ (info@ccu-historian.de)

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

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * A fast streaming parser for large CSV files. Quoting of cell content is
 * supported, if cell contains new lines or separators. Embedded quotes in
 * quoted cells must be escaped with another quote.
 */
public class CSVParser {

	/**
	 * Handler interface for parsed CSV lines.
	 */
	public interface Handler {
		/**
		 * @param fields CSV fields of one line. The list object is reused between
		 *               calls. It must be cloned, if stored by the handler.
		 * @return true: continue parsing, false: stop parsing
		 * @throws Exception An exception in the handler stops parsing.
		 */
		boolean handle(List<String> fields) throws Exception;
	}

	private char separator;
	private char quote;

	public CSVParser(char separator, char quote) {
		this.separator = separator;
		this.quote = quote;
	}

	public CSVParser() {
		separator = ',';
		quote = '"';
	}

	public void parse(Reader reader, Handler handler) throws Exception {
		// preallocate needed objects
		ArrayList<String> fields = new ArrayList<>(20);
		StringBuilder sb = new StringBuilder(64);

		// read CSV lines until EOF
		int ch = reader.read();
		while (ch != -1) {
			
			// read line until EOL or EOF
			do {
				// quoted cell?
				if (ch == quote) {
					ch = reader.read();
					// read until a single quote or EOF
					while (true) {
						if (ch == -1) {
							throw new IllegalArgumentException("EOF within quotes");
						}
						// quote?
						if (ch == quote) {
							// look at next char
							ch = reader.read();
							// EOF or EOL or separator?
							if (ch == -1 || ch == '\r' || ch == '\n' || ch == separator) {
								// valid end of quoted cell
								break;
								// another quote?
							} else if (ch == quote) {
								// output a single quote
								sb.append((char) quote);
								ch = reader.read();
							} else {
								// invalid quote escape
								throw new IllegalArgumentException("Invalid quote escape");
							}
						} else {
							// no quote
							sb.append((char) ch);
							ch = reader.read();
						}
					}

				} else {
					// unquoted cell: read until separator or EOL or EOF
					while (ch != '\r' && ch != '\n' && ch != separator && ch != -1) {
						sb.append((char) ch);
						ch = reader.read();
					}
				}

				// store field
				fields.add(sb.toString());
				// reuse string builder
				sb.setLength(0);

				// skip separator
				if (ch == separator) {
					ch = reader.read();
					// EOL or EOF after separator?
					if (ch == '\r' || ch == '\n' || ch == -1) {
						// store an additional empty cell
						fields.add("");
					}
				}
			} while (ch != '\r' && ch != '\n' && ch != -1);

			// handle line endings (\n or \r\n)
			if (ch == '\r') {
				ch = reader.read();
				if (ch == '\n') {
					ch = reader.read();
				}
			} else if (ch == '\n') {
				ch = reader.read();
			}

			// output fields
			boolean ok = handler.handle(fields);
			// reuse list
			fields.clear();
			if (!ok) {
				return;
			}
		}
	}
}
