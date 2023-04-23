/*
    CCU-Historian, a long term archive for the HomeMatic CCU
    Copyright (C) 2011-2022 MDZ (info@ccu-historian.de)

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
package mdz.hc.timeseries.expr;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
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

public class IntervalExpressions {

	// SPRING53 has most features enabled.
	private static CronDefinition cronDef = CronDefinitionBuilder.instanceDefinitionFor(CronType.SPRING53);
	private static CronParser cronParser = new CronParser(cronDef);

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
