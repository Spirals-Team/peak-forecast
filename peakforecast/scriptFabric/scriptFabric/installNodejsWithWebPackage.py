@task(alias='installNodejsWithWebPackage')
def configImage2():
    print("DEBUT install node.js")
    put('/home/daniel/tmp/hello.js','hello.js')
    sudo('sudo apt-add-repository ppa:chris-lea/node.js')
    sudo('sudo apt-get update')
    sudo('sudo apt-get install nodejs npm')
    print("END install node.js")
