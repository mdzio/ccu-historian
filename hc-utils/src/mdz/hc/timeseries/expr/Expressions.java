package mdz.hc.timeseries.expr;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;

import com.cronutils.builder.CronBuilder;
import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;

import mdz.hc.ProcessValue;

public class Expressions {

	// SPRING53 has most features enabled.
	private static CronDefinition cronDef = CronDefinitionBuilder.instanceDefinitionFor(CronType.SPRING53);
	private static CronParser cronParser = new CronParser(cronDef);

	/**
	 * Wraps a Reader into an Expression.
	 */
	public static Expression from(Reader reader) {
		return new FromReaderExpression(reader);
	}

	/**
	 * Wraps a constant value into an Expression. The time series has the
	 * characteristic HOLD.
	 */
	public static Expression from(Number value) {
		return new FromConstantExpression(value);
	}

	/**
	 * Wraps an Iterable into an Expression. Begin and end of reads are ignored. Use
	 * sanitize() to remove out of range timestamps.
	 */
	public static Expression from(Iterable<ProcessValue> iterable, int characteristics) {
		return new FromIterableExpression(iterable, characteristics);
	}

	public static Expression step(Number firstValue, Number secondValue, Date stepTimestamp) {
		return new Expression() {
			@Override
			public int getCharacteristics() {
				return Characteristics.HOLD;
			}

			@Override
			public Iterator<ProcessValue> read(Date begin, Date end) {
				if (end.before(begin)) {
					return Collections.emptyIterator();
				}
				ArrayList<ProcessValue> timeSeries = new ArrayList<ProcessValue>(3);
				if (!stepTimestamp.after(begin)) {
					timeSeries.add(new ProcessValue(begin, secondValue, ProcessValue.STATE_QUALITY_GOOD));
					if (end.after(begin)) {
						timeSeries.add(new ProcessValue(end, secondValue, ProcessValue.STATE_QUALITY_GOOD));
					}
				} else if (!stepTimestamp.after(end)) {
					timeSeries.add(new ProcessValue(begin, firstValue, ProcessValue.STATE_QUALITY_GOOD));
					timeSeries.add(new ProcessValue(stepTimestamp, secondValue, ProcessValue.STATE_QUALITY_GOOD));
					if (stepTimestamp.before(end)) {
						timeSeries.add(new ProcessValue(end, secondValue, ProcessValue.STATE_QUALITY_GOOD));
					}
				} else {
					timeSeries.add(new ProcessValue(begin, firstValue, ProcessValue.STATE_QUALITY_GOOD));
					if (end.after(begin)) {
						timeSeries.add(new ProcessValue(end, firstValue, ProcessValue.STATE_QUALITY_GOOD));
					}
				}
				return timeSeries.iterator();
			}
		};
	}

	public static Expression positiveEdge(Date edgeTimestamp) {
		return step(0.0, 1.0, edgeTimestamp);
	}

	public static Expression negativeEdge(Date edgeTimestamp) {
		return step(1.0, 0.0, edgeTimestamp);
	}

	public static Expression yearly() {
		return from(CronBuilder.yearly(cronDef));
	}

	public static Expression monthly() {
		return from(CronBuilder.monthly(cronDef));
	}

	public static Expression weekly() {
		return from(CronBuilder.weekly(cronDef));
	}

	public static Expression daily() {
		return from(CronBuilder.daily(cronDef));
	}

	public static Expression hourly() {
		return from(CronBuilder.hourly(cronDef));
	}

	public static Expression everyMinute() {
		return from(cronParser.parse("0 * * * * ?"));
	}

	public static Expression cron(String cronText) {
		return from(cronParser.parse(cronText));
	}

	static Expression from(Cron cron) {
		ExecutionTime execTime = ExecutionTime.forCron(cron);
		return new Expression() {
			@Override
			public Iterator<ProcessValue> read(Date begin, Date end) {
				ZonedDateTime zdtEnd = ZonedDateTime.ofInstant(end.toInstant(), ZoneId.systemDefault());
				return new Iterator<ProcessValue>() {
					boolean done, present;
					ZonedDateTime next;

					@Override
					public boolean hasNext() {
						prepare();
						return present;
					}

					@Override
					public ProcessValue next() {
						prepare();
						if (!present) {
							throw new NoSuchElementException();
						}
						ProcessValue pv = new ProcessValue(Date.from(next.toInstant()), 1.0,
								ProcessValue.STATE_QUALITY_GOOD);
						present = false;
						return pv;
					}

					private void prepare() {
						if (present || done) {
							return;
						}
						if (next == null) {
							// start time just before begin
							next = ZonedDateTime.ofInstant(begin.toInstant(), ZoneId.systemDefault()).minusNanos(1);
						}
						Optional<ZonedDateTime> optNext = execTime.nextExecution(next);
						if (!optNext.isPresent()) {
							done = true;
							return;
						}
						next = optNext.get();
						if (next.isAfter(zdtEnd)) {
							done = true;
							return;
						}
						present = true;
					}
				};
			}

			@Override
			public int getCharacteristics() {
				return Characteristics.EVENT;
			}
		};
	}

	public static Expression entire() {
		return new Expression() {
			@Override
			public Iterator<ProcessValue> read(Date begin, Date end) {
				return Arrays.asList(new ProcessValue(begin, 1.0, ProcessValue.STATE_QUALITY_GOOD),
						new ProcessValue(end, 1.0, ProcessValue.STATE_QUALITY_GOOD)).iterator();
			}

			@Override
			public int getCharacteristics() {
				return Characteristics.EVENT;
			}
		};
	}
}
