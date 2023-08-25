package mdz.ccuhistorian

import mdz.hc.persistence.Storage
import mdz.hc.timeseries.expr.Expression
import mdz.hc.timeseries.expr.ExpressionCategory
import mdz.hc.timeseries.expr.Reader
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer
import org.codehaus.groovy.control.customizers.SecureASTCustomizer

public class ExpressionParser {

	private final DatabaseExpressionAdapter exprAdapter
	private final GroovyShell shell

	public ExpressionParser(Storage storage) {
		// expression adapter for database
		exprAdapter=new DatabaseExpressionAdapter(storage: storage)

		// build compiler configuration
		CompilerConfiguration config=[]

		// default imports
		ImportCustomizer importCustomizer=[]
		importCustomizer.addStaticStars 'java.lang.Math',
				'mdz.hc.ProcessValue',
				'mdz.hc.timeseries.expr.Characteristics',
				'mdz.hc.timeseries.expr.Expressions'
		importCustomizer.addImports 'mdz.hc.ProcessValue'
		config.addCompilationCustomizers importCustomizer

		// security
		SecureASTCustomizer secureCustomizer=[]
		secureCustomizer.with {
			staticStarImportsWhitelist=[
				'java.lang.Math',
				'mdz.hc.ProcessValue',
				'mdz.hc.timeseries.expr.Characteristics',
				'mdz.hc.timeseries.expr.Expressions'
			]
			importsWhitelist=[
				'mdz.hc.ProcessValue'
			]
			staticImportsWhitelist=[]
			receiversBlackList=['java.lang.System']
		}
		config.addCompilationCustomizers secureCustomizer

		// configured groovy shell
		shell=[config]
	}

	Reader parse(String expr) {
		// create binding
		Binding binding=[]
		binding.dataPoint=exprAdapter.&dataPoint

		// create script and execute with category
		def script=shell.parse(expr)
		script.binding=binding
		def result=use(ExpressionCategory) { script.run() }

		// check result
		if (!(result instanceof Reader)) {
			throw new Exception("Invalid expression result: ${result.class.name}")
		}
		result
	}
}
