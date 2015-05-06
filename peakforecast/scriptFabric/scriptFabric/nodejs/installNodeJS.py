from __future__ import with_statement
from fabric.api import *
from fabric.operations import *

@task
def installNodejsWithLocalPackage():
    print("BEGIN install node.js with Local package")
    print("BEGIN copy package") 
    put('/home/daniel/tmp/node-v0.6.18.tar.gz','node-v0.6.18.tar.gz')
    print("copy completed") 
    print("BEGIN unpack")         
    run('tar -zxf node-v0.6.18.tar.gz')   
    print("unpack completed")
    with cd('node-v0.6.18/'):
         sudo('./configure')
         sudo('make')
         sudo('sudo make install')
         print("END install node.js")


@task
def installNodejsWithWebPackage():
    print("BEGIN install node.js with web package")
    put('/home/daniel/tmp/hello.js','hello.js')
    with settings(warn_only=True):
        result = sudo('sudo apt-add-repository ppa:chris-lea/node.js')
        if result.failed :
           print("Intall with web package failed.")
           return 0
        else:
           result = sudo('sudo apt-get update') 
           if result.failed :
              print("Intall with web package failed.")
              return 0
           else:
              result = sudo('sudo apt-get install nodejs npm')               
              if result.failed :
                 print("Intall with web package failed.")
                 return 0
              else:
                 print("END install node.js")    
                 return 1
