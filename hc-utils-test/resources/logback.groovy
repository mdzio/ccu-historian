import static ch.qos.logback.classic.Level.*
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.FileAppender
import ch.qos.logback.classic.encoder.PatternLayoutEncoder

appender('CONSOLE', ConsoleAppender) {
	encoder(PatternLayoutEncoder) {
		pattern = '%d %-5level [%thread] %logger{0}: %msg%n'
	}
}

appender("FILE", FileAppender) {
  file = "test.log"
  append = false
  encoder(PatternLayoutEncoder) {
		pattern = '%d %-5level [%thread] %logger{0}: %msg%n'
  }
}

root TRACE, ['CONSOLE' /*, 'FILE'*/]

logger 'mdz.hm.binrpc', DEBUG
