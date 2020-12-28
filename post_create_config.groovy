#!/usr/bin/env groovy

@Grab('com.aestasit.infrastructure.sshoogr:sshoogr:0.9.28')
import static com.aestasit.infrastructure.ssh.DefaultSsh.*
import groovyx.gpars.GParsPool

def vmDataJson = "az vm list -d -o json".execute()

def hosts = []

new groovy.json.JsonSlurper().parseText(vmDataJson.text).each { it ->
	hosts << [name: it.name, ip: it.publicIps]
}

println "======================================"
println "|       Writing ssh_config           |"
println "======================================"
println()
new File('ssh_config').withWriter { writer ->
	hosts.each { it -> 
		writer.write """
Host ${it.name}
	HostName ${it.ip}
	User gorg
	IdentityFile azure_key

	"""
	}	
}


println "======================================"
println "|          Writing hosts             |"
println "======================================"
println()
new File('hosts').withWriter { writer ->
	hosts.each { it -> 
		writer.writeLine "${it.ip}\t${it.name}"
	}	
}

GParsPool.withPool {
	hosts.eachParallel { host ->
		trustUnknownHosts = true
		remoteSession("gorg@${host.ip}") {
			// Host = "${host.ip}"
			keyFile = new File('azure_key')
			connect()
			exec 'mkdir -p ~/.ssh'
			exec 'rm -f /home/gorg/.ssh/id_rsa'
			exec 'rm -f /home/gorg/.ssh/id_rsa.pub'
			remoteFile('/home/gorg/.ssh/id_rsa').text = new File('azure_key').text
			remoteFile('/home/gorg/.ssh/id_rsa.pub').text = new File('azure_key.pub').text
			exec 'chmod 400 ~/.ssh/id_rsa'
			def tempFile = exec('mktemp').getOutput().trim()
			def tmpFileRemote = remoteFile(tempFile)
			hosts.each { hostLine -> 
				tmpFileRemote << "${hostLine.ip}\t${hostLine.name}"
			}
			exec "sudo bash -c 'cat ${tempFile} >> /etc/hosts'"
		}
	}	
}

