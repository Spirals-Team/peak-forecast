from fabric.api import *
from fabric.operations import *

@task(alias='installNodejsWithLocalPackage')
def configImage1():
    print("DEBUT install node.js")
    print("Debut copy package") 
    put('/home/daniel/tmp/node-v0.6.18.tar.gz','node-v0.6.18.tar.gz')
    print("copy completed") 
    print("Debut unpack")         
    run('tar -zxf node-v0.6.18.tar.gz')   
    print("unpack completed")
    with cd('node-v0.6.18/'):
         sudo('./configure')
         sudo('make')
         sudo('sudo make install')
         print("END install node.js")
        
