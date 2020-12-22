def vmDataJson = "az vm list -d -o json".execute()
new groovy.json.JsonSlurper().parseText(vmDataJson.text).each { it -> 
	println """
Host ${it.name}
	HostName ${it.publicIps}
	User gorg
	IdentityFile azure_key

"""
}


 
