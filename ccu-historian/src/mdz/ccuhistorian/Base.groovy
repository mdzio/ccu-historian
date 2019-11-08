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
import groovy.util.ResourceException
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer
import org.codehaus.groovy.control.customizers.SecureASTCustomizer
import mdz.Exceptions
import java.util.logging.Level

/* The basic module is stopped in two steps: First, the scheduler is stopped 
 * before all other modules are stopped. As a result, pending tasks of all 
 * modules are completed but no new tasks are accepted. After all other 
 * modules have been stopped, the base module is finally stopped.
 */
@Log
class Base {
	
	private final static long SHUTDOWN_TIMEOUT = 15000 // ms

	private BaseConfig config
	private ScheduledThreadPoolExecutor executor
	private GroovyScriptEngine scriptEngine
	
	public Base(BaseConfig config) {
		log.info 'Starting base services'
		this.config=config	
		config.logDebug()
		executor=new ScheduledThreadPoolExecutor(config.threadPoolSize, new ThreadPoolExecutor.DiscardPolicy())
		executor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
		def scriptDir=new File(config.scriptDir)
		
		// create script engine
		scriptEngine=new GroovyScriptEngine([scriptDir.toURI().toURL()] as URL[])
		// configure script compiler
		CompilerConfiguration compilerConfig=[]
		// default imports
		ImportCustomizer importCustomizer=[]
		importCustomizer.addImports 'mdz.ccuhistorian.TrendDesign', 'java.util.logging.Level',
			'java.awt.Color', 'org.jfree.chart.ChartColor', 'java.awt.BasicStroke',
			'java.awt.GradientPaint', 'org.jfree.chart.title.TextTitle'
		compilerConfig.addCompilationCustomizers importCustomizer
		scriptEngine.config=compilerConfig
	}
	
	protected synchronized stopScheduler() {
		if (executor) {
			log.fine 'Stopping main scheduler'
			executor.shutdownNow()
			try {
				executor.awaitTermination(SHUTDOWN_TIMEOUT, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
			}
		}
	}
	
	protected synchronized stop() {
		log.info 'Stopping base services'
		if (executor) {
			if (!executor.terminated)
				log.severe 'Main scheduler is not terminated'
			executor=null
		}
	}
	
	public synchronized ScheduledExecutorService getExecutor() { executor }
	
	public void runScript(String scriptName, Binding binding) {
		try {
			log.finer "Executing script $scriptName"
			scriptEngine.run(scriptName, binding)
		} catch (ResourceException e) {
			// ignore missing scripts
			log.finer "Script $scriptName not found"
		} catch (e) {
			Exceptions.logTo log, Level.SEVERE, e
		}
	}
}
