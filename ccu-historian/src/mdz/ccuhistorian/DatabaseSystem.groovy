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
package mdz.ccuhistorian

import groovy.util.logging.Log

import static com.cronutils.model.CronType.QUARTZ

import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
import java.util.logging.Logger
import mdz.Exceptions
import com.cronutils.model.Cron
import com.cronutils.model.definition.CronDefinitionBuilder
import com.cronutils.model.time.ExecutionTime
import com.cronutils.parser.CronParser

import groovy.transform.CompileStatic

@Log
@CompileStatic
public class DatabaseSystem extends BaseSystem {

	private final static long ATSTART_TASK_DELAY = 3000; // [ms]
	
	ExtendedStorage extendedStorage
	
	private Database internalDatabase
	
	public DatabaseSystem(Configuration config) {
		super(config)
		// set webExternalNames, if not specified
		def dbConfig=config.databaseConfig
		def webConfig=config.webServerConfig
		if (!dbConfig.webExternalNames && webConfig.historianAddress) {
			dbConfig.webExternalNames=webConfig.historianAddress
		}
		internalDatabase=new Database(dbConfig, base)
		extendedStorage=new ExtendedStorage(storage:internalDatabase)
		
		// tasks
		startTasks()
	}

	public synchronized void stop()  {
		internalDatabase.stop()
		super.stop()
	}
	
	public ExtendedStorage getDatabase() { extendedStorage }
	
	// Unfortunately, the Binding class cannot be used. Function calls to properties that
	// contain Closure's can only be forwarded with a separate class.
	private static class Delegate {
		def log
		def database
		def DP
		def dataPoint
	}
	
	private startTasks() {
		def cparser=new CronParser(CronDefinitionBuilder.instanceDefinitionFor(QUARTZ))
		config.databaseConfig.tasks.each { String name, DatabaseConfig.Task task ->
			if (task.enable) {
				log.fine "Initializing task: $name, $task.cron"
				if (task.cron==null || task.cron.isEmpty()) {
					throw new Exception("Task $name has no cron expression")
				}
				if (task.script==null) {
					throw new Exception("Task $name has no script")
				}
				
				def delegate=new Delegate()
				def scriptLog=Logger.getLogger("mdz.task.$name")
				def exprAdapter=new DatabaseExpressionAdapter(storage: extendedStorage)
				delegate.log=scriptLog
				delegate.database=extendedStorage
				// dataPoint() has several overloads that will be resolved dynamically!
				delegate.dataPoint=exprAdapter.&dataPoint
				delegate.DP=exprAdapter.&dataPoint
				task.script.delegate=delegate
				task.script.resolveStrategy=Closure.DELEGATE_ONLY
				
				// schedule task
				if (task.cron=="@start") {
					// run once
					executeTask(task, ATSTART_TASK_DELAY, {})
				} else {
					// setup cron
					Cron cron=cparser.parse(task.cron)
					ExecutionTime execTime=ExecutionTime.forCron(cron)
					executeCyclic(execTime, task)
				}
			}
		}
	}

	private executeTask(DatabaseConfig.Task task, long delay, Closure onCompleted) {
		base.executor.schedule({
			log.fine "Executing task: $task.name"
			// execute script
			Exceptions.catchToLog(log) {
				task.script.call()
			}
			onCompleted()
		} as Runnable, delay, TimeUnit.MILLISECONDS)
	}

	private executeCyclic(ExecutionTime execTime, DatabaseConfig.Task task) {
		def now=ZonedDateTime.now()
		def nextExec=execTime.nextExecution(now).get()
		log.fine "Next execution of task $task.name: ${nextExec.toLocalDateTime()}"
		def delay=ChronoUnit.MILLIS.between(now, nextExec)
		executeTask(task, delay, { executeCyclic(execTime, task) })
	}
}
