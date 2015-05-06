from fabric.api import *
from fabric.operations import *
from properties import *
  
@task
def tail(namehost,username,passworduser,nbreligne):
    #initilisation des proprietes de l'image
    properties(namehost,username,passworduser)
    print("transfere des "+nbreligne+" dernieres lignes du fichier access.log du loadbalancer dans le fichier temporaire tmp") 
    commande="tail -n "+nbreligne+" /var/log/nginx/access.log >/var/log/nginx/tmp"
    sudo(commande) 
    


