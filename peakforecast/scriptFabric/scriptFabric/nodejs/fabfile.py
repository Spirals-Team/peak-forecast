from __future__ import with_statement
from fabric.contrib.console import confirm
from fabric.api import *
from fabric.operations import *
from properties import *
from installNodeJS import *
  
@task
def install(namehost,username,passworduser):
    #initilisation des proprietes de l'image
    properties(namehost,username,passworduser)
    with settings(warn_only=True):
       resultInstall=installNodejsWithWebPackage()
       if not resultInstall: 
          installNodejsWithLocalPackage() 

@task
def test(namehost,username,passworduser):
    #initilisation des proprietes de l'image
    properties(namehost,username,passworduser)
    print("BEGIN Test node.js")
    print("BEGIN copy example hello-word") 
    put('/home/daniel/tmp/hello.js','hello.js')
    print("copy completed") 
    with settings(warn_only=True):
        resultest = run('node ./hello')
    if resultest.failed :
       print("node.js is not installed")      


