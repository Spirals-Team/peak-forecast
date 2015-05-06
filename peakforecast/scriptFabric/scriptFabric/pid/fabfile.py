from fabric.api import *
from fabric.operations import *
from properties import * 
@task
def pidNginx(namehost,username,passworduser):
    #initilisation des proprietes de l'image
    properties(namehost,username,passworduser)
    run('pgrep nginx')
       

