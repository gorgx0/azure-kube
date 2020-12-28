#!/usr/bin/env groovy
import groovyx.gpars.GParsPool

def resources = "az resource list -o json".execute()

def ids = []

new groovy.json.JsonSlurper().parseText(resources.text).each {
	ids << it.id
}

println "====================================="	
println "|        Removing resources         |"
println "====================================="	
"az resource delete --ids ${ids.join(' ')}".execute()