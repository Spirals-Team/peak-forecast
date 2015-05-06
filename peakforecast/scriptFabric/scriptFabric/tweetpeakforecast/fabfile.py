from fabric.api import *
from fabric.operations import *
from properties import *
  
@task
def tweetpeakforecast(namehost,username,passworduser,message):
    #initilisation des proprietes de l'image
    properties(namehost,username,passworduser)
    put('/home/daniel/scriptPython/tweetpeakforecast.tar.gz','tweetpeakforecast.tar.gz')
    run('tar -zxf tweetpeakforecast.tar.gz')         
    with cd('tweetpeakforecast/'):
         	commande="./tweetpeakforecastcli.py  '"+message+"'"
       	        sudo(commande) 
    sudo('rm -r tweetpeakforecast') 
    sudo('rm tweetpeakforecast.tar.gz')      


