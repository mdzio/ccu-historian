//----------------------------------------------------------------------------
// configuration
repoUrl='https://api.github.com/repos/mdzio/ccu-historian'
//----------------------------------------------------------------------------
import java.net.http.*
import java.nio.charset.Charset
import groovy.json.JsonSlurper
import groovy.json.JsonOutput

try {
    // github token
    token=System.getenv('GITHUB_TOKEN')
    if (!token) {
        throw new Exception('Missing environment variable (GITHUB_TOKEN)')
    }
    
    // setup
    client=HttpClient.newHttpClient()

    // arguments
    if (args.size()==0) {
        throw new Exception('Missing arguments (milestones)')
    }
    def relevantMilestones=args

    // get milestones
    def milestones=request('/milestones?state=all&per_page=200').sort { it.created_at }
    println "Number of milestones: ${milestones.size()}"
    milestones.each { println "* $it.title" }
    println ''
    milestones=relevantMilestones.collect { searchMilestone -> 
        def milestone=milestones.find { it.title==searchMilestone }
        if (milestone==null) {
            throw new Exception("Milestone $searchMilestone not found")
        }
        milestone
    }
    
    // get issues
    def allIssues=milestones.collectMany { milestone -> 
        def issues=request("/issues?state=all&milestone=$milestone.number")
        println "Number of issues for milestone $milestone.title: ${issues.size()}"
        issues.each { issue ->
            println "* $issue.title"
        }
        println ''
        issues  
    }

    println 'Writing forum-post.txt'
    new File('forum-post.txt').withPrintWriter('ISO-8859-1') { pw ->
        pw.print """\
            [b][url=https://github.com/mdzio/ccu-historian/releases]Die neue Version ist wie immer auf GitHub zu finden.[/url][/b]

            [b]Fehlerbehebungen / Verbesserungen[/b]

            [list]
        """.stripIndent()
        allIssues.each { issue ->
            pw.println "[*] [url=$issue.html_url]$issue.title[/url] ($issue.milestone.title)"
        }
        pw.print """\
            [/list]

            Gruß
            Mathias
        """.stripIndent()
    }

    println 'Writing github-post.txt'
    new File('github-post.txt').withPrintWriter('ISO-8859-1') { pw ->
		// strip v prefix
        def v=relevantMilestones[0].drop(1)
        // synology version
        def cv
        if (v.contains('-alpha')) {
          cv=v.replaceAll('-alpha','.1')
        } else if (v.contains('-beta')) {
          cv=v.replaceAll('-beta','.2')
        } else {
          cv=v+'.3.0'
        }
        pw.print """\
            Spenden für den CCU-Historian:
            [![Donate](https://img.shields.io/badge/Donate-PayPal-green.svg)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=SF4BR9ZE2JUBS)    
        
            Spenden insbesondere für die H2-HighChart-Erweiterung von wakr:
            [![Donate](https://img.shields.io/badge/Donate-PayPal-green.svg)](https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=UNR7TVPVH74TE&currency_code=EUR&source=url)

            ## Distributionen

            Plattformunabhängige Distribution (Windows, Linux, MacOS): **[ccu-historian-${v}-bin.zip](https://github.com/mdzio/ccu-historian/releases/download/v${v}/ccu-historian-${v}-bin.zip)**\\
            CCU3/RaspberryMatic/YAHM/piVCCU-Distribution: **[ccu-historian-addon-${v}.tar.gz](https://github.com/mdzio/ccu-historian/releases/download/v${v}/ccu-historian-addon-${v}.tar.gz)**\\
            Synology-Distribution DSM 6: **[ccu-historian-${cv}.spk](https://github.com/mdzio/ccu-historian/releases/download/v${v}/ccu-historian-${cv}.spk)**\\
            Synology-Distribution DSM 7: **[ccu-historian-dsm7-${cv}-noarch.spk](https://github.com/mdzio/ccu-historian/releases/download/v${v}/ccu-historian-dsm7-${cv}-noarch.spk)**\\
            Docker-Image (linux: amd64, arm64, arm/v7, riscv64): https://hub.docker.com/r/mdzio/ccu-historian

            ## Fehlerbehebungen / Verbesserungen

        """.stripIndent()
        allIssues.each { issue ->
            pw.println "* [$issue.title]($issue.html_url) ($issue.milestone.title)"
        }
    }
 
} catch (e) {
    println "Error: " + e.message
}

def request(apiUrl) {
    def req=HttpRequest.newBuilder()
        .uri(URI.create(repoUrl + apiUrl))
        .header('Authorization', token)
        .build()
    def resp=client.send(req, HttpResponse.BodyHandlers.ofString(Charset.forName('UTF-8')))
    if (resp.statusCode()!=200) {
        throw new Exception("Request failed: HTTP status code=" + resp.statusCode())
    }
    new JsonSlurper().parseText(resp.body())
}

def printJson(json) {
    println JsonOutput.prettyPrint(JsonOutput.toJson(json))
}
     