from fabric.api import *
from fabric.operations import *

@task
def installFrascatiWithLocalPackage(username):
    run('echo "DEBUT install MediaWiki"')
    run('echo "Debut copy package"') 
    put('/home/daniel/tmp/mediawiki.zip','mediawiki.zip',)
    run('echo "copy completed"') 
    run('echo "Debut unpack"')         
    sudo('unzip -u mediawiki.zip -d /var/www/')   
    run('echo "unpack completed"')
    run('rm mediawiki.zip') 

