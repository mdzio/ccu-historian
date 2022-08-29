package mdz;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * LogWriter is a Writer that writes its output to a logger. A log message is
 * written after each end of line or when flush() is called. The log level can
 * be adjusted before each write.
 */
public class LogWriter extends Writer {

	private StringWriter line = new StringWriter();
	private Logger log;
	private Level level = Level.INFO;

	public LogWriter(Logger log) {
		this.log = log;
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		for (int idx = off; len > 0; idx++, len--) {
			char ch = cbuf[idx];
			switch (ch) {
			case '\r':
				// discard
				break;
			case '\n':
				// write to log
				flush();
				break;
			default:
				line.append(ch);
			}
		}
	}

	@Override
	public void flush() throws IOException {
		// write the current line to log
		log.log(level, line.toString());
		line = new StringWriter();
	}

	@Override
	public void close() throws IOException {
		flush();
	}
}
