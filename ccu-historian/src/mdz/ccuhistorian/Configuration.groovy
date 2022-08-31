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

import groovy.transform.CompileStatic
import groovy.util.ConfigObject;
import groovy.util.logging.Log
import mdz.LogWriter
import mdz.ccuhistorian.webapp.WebServerConfig
import groovy.cli.commons.CliBuilder
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer
import java.util.logging.Level

@Log
class Configuration {

	CmdLineConfig cmdLineConfig
	
	LogSystemConfig logSystemConfig
	BaseConfig baseConfig
	DatabaseConfig databaseConfig
	ConfigObject deviceConfigs
	HistorianConfig historianConfig
	WebServerConfig webServerConfig
	
	private long configTimestamp
	
	public void readCommandLine(String[] args) {
		cmdLineConfig=[]
		
		CliBuilder cli=new CliBuilder(usage:'ccu-historian [options]')
		StringWriter sw=[]
		cli.writer=new PrintWriter(sw)
		cli.header='options:'
		cli.config(args:1, argName:'file', 'changes the path to the configuration file')		
		cli.help('prints this help message')
		cli.loglevel(args:1, argName:'level', 'sets the console log level (off, severe, warning, info, fine, ' +
			'finer, finest) before the configuration file is read')
		cli.recalc('recalculates compressed data points and then shuts down')
		cli.compact('compacts the database and then shuts down')
		cli.clean(args:1, argName:'date', 'deletes all time series entries before the specified date (format is DD.MM.YYYY or YYYY-MM-DD) and then shuts down')
		cli.runscript(args:1, argName:'file', 'runs an SQL script from file and then shuts down')
		cli.createscript(args:1, argName:'file', 'dumps the database to a file as SQL scipt and then shuts down')
		
		def options=cli.parse(args)
		if (!options || options.arguments()) {
			if (options?.arguments()) cli.usage()
			log.info "$sw"
			throw new Exception('Invalid command line')
		}
		if (options.config)
			cmdLineConfig.configFileName=options.config
		cmdLineConfig.help=options.help
		if (options.help) {
			cli.usage()
			log.info "$sw"
			return
		}
		if (options.loglevel) {
			try {
				cmdLineConfig.logLevel=Level.parse(options.loglevel.toUpperCase())
			} catch (Exception e) {
				throw new Exception('Invalid command line option -loglevel')
			}
		}
		cmdLineConfig.recalculation=options.recalc
		cmdLineConfig.compaction=options.compact
		if (options.createscript)
			cmdLineConfig.scriptFileName=options.createscript
		if (options.runscript)
			cmdLineConfig.runScriptFileName=options.runscript
		if (options.clean) {
			cmdLineConfig.clean=['yyyy-MM-dd', 'dd.MM.yyyy'].findResult {
				try { Date.parse(it, options.clean)	} catch(Exception e) { null }
			}
			if (cmdLineConfig.clean==null)
				throw new Exception('Invalid command line option -clean')
		}
	}

	@CompileStatic
	public boolean isFileModified() {
		File file=new File(cmdLineConfig.configFileName)
		if (!file.canRead())
			throw new Exception("Can't access configuration file $cmdLineConfig.configFileName")
		if (file.lastModified()!=configTimestamp) {
			configTimestamp=file.lastModified()
			true
		} else false
	}
	
	public void readFile() {
		log.fine "Reading configuration file $cmdLineConfig.configFileName"
		
		Binding binding=new Binding()
		binding.log=log
		binding.logSystem=new LogSystemConfig()
		binding.base=new BaseConfig()
		binding.database=new DatabaseConfig()
		binding.devices=new ConfigObject()
		binding.historian=new HistorianConfig()
		binding.webServer=new WebServerConfig()
		// redirect print... to log
		binding.out=new PrintWriter(new LogWriter(log))
		
		String script
		File file=new File(cmdLineConfig.configFileName)
		try {
			script=file.text
		} catch (FileNotFoundException ex) {
			throw new Exception("Can't read configuration file $cmdLineConfig.configFileName", ex)
		}
		
		ImportCustomizer customizer=[]
		customizer.addStaticStars 'java.lang.Math', 'mdz.ccuhistorian.webapp.TextFormat'
		customizer.addImport 'PreprocType', 'mdz.ccuhistorian.eventprocessing.Preprocessor.Type'
		customizer.addImports 'mdz.ccuhistorian.TrendDesign', 'java.util.logging.Level',
			'java.awt.Color', 'org.jfree.chart.ChartColor', 'java.awt.BasicStroke',
			'java.awt.GradientPaint', 'org.jfree.chart.title.TextTitle',
			'mdz.hc.DataPoint', 'mdz.hc.DataPointIdentifier', 'mdz.hc.Event',
			'mdz.hc.ProcessValue', 'mdz.hc.RawEvent', 'mdz.hc.timeseries.TimeSeries'
		customizer.addStaticStars 'mdz.ccuhistorian.ManagerConfigurator.DeviceTypes'
		customizer.addStaticStars 'mdz.ccuhistorian.ManagerConfigurator.PlugInTypes'
		CompilerConfiguration config=[]
		config.addCompilationCustomizers customizer
		
		GroovyShell shell=[binding, config]
		try {
			shell.evaluate script, 'Config'
		} catch (Exception ex) {
			throw new Exception("Configuration file $cmdLineConfig.configFileName is invalid", ex)
		}
		
		logSystemConfig=binding.logSystem
		baseConfig=binding.base
		databaseConfig=binding.database
		deviceConfigs=binding.devices
		historianConfig=binding.historian
		webServerConfig=binding.webServer
		
		configTimestamp=file.lastModified()
	}
}
