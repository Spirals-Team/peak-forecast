from fabric.api import *
from fabric.operations import *
from properties import *
  
@task
def ajouterServeur(namehost,username,passworduser,serveur):
    #initilisation des proprietes de l'image
    properties(namehost,username,passworduser)
    print("copy du script shell qui permet d'ajouter du nouveau serveur dans le fichier de configuration du repartiteur") 
    put('/home/daniel/scriptsSHELL/ajouterServeur.tar.gz','ajouterServeur.tar.gz')
    run('tar -zxf ajouterServeur.tar.gz')          
    with cd('ajouterServeur/'):
         print("Execution du script shell") 
         commande="./ajouterServeur.sh "+serveur+" "+username
         sudo(commande) 
    print("Signaler au repartiteur la modification de son fichier de configuration pour qu il le recharge dans son context")
    sudo("kill -HUP $(cat /run/nginx.pid)")
@task
def retirerServeur(namehost,username,passworduser,serveur):
    #initilisation des proprietes de l'image
    properties(namehost,username,passworduser)
    print("copy du script shell qui permet de retirer un serveur dans le fichier de configuration du repartiteur") 
    put('/home/daniel/scriptsSHELL/retirerServeur.tar.gz','retirerServeur.tar.gz')
    run('tar -zxf retirerServeur.tar.gz')          
    with cd('retirerServeur/'):
         print("Execution du script shell") 
         commande="./retirerServeur.sh "+serveur+" "+username
         sudo(commande) 
    print("Signaler au repartiteur la modification de son fichier de configuration pour qu il le recharge dans son context")
    sudo("kill -HUP $(cat /run/nginx.pid)")
       

