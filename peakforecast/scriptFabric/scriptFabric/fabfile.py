from __future__ import with_statement
from fabric.contrib.console import confirm
from fabric.api import *
from fabric.operations import *
from properties import *
from installNodeJS import *
  
@task
def testNodeJS(namehost,username,passworduser):
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
       resultInstall=installNodejsWithWebPackage()
       if not resultInstall and confirm("Continue install node.js with Local package anyway ?"): 
          installNodejsWithLocalPackage()       
          run('node ./hello')
       elif resultInstall:
            run('node ./hello') 
    else: 
       print("node.js is installed")
    print("END Test node.js")
        

