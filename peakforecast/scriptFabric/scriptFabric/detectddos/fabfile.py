from fabric.api import *
from fabric.operations import *
from properties import *
  
@task
def detetedDDOS(namehost,username,passworduser):
    #initilisation des proprietes de l'image
    properties(namehost,username,passworduser)
    put('/home/daniel/scriptPython/detectDDOS.tar.gz','detectDDOS.tar.gz')
    run('tar -zxf detectDDOS.tar.gz')          
    with cd('detectDDOS/'):
         commande="./check_ddos.pl -w 50 -c 70"
         sudo(commande) 
    sudo('rm detectDDOS.tar.gz')
    sudo('rm -r detectDDOS') 
       

