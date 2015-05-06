from fabric.api import *
from fabric.operations import *
from properties import *
  
@task
def listeServeur(namehost,username,passworduser):
    #initilisation des proprietes de l'image
    properties(namehost,username,passworduser)
    put('/home/daniel/scriptsSHELL/listeServeur.tar.gz','listeServeur.tar.gz')
    run('tar -zxf listeServeur.tar.gz')          
    with cd('listeServeur/'):
         commande="./listeServeur.sh "+username
         sudo(commande) 
       

