from fabric.api import *
from fabric.operations import *
from properties import *
  
@task
def pause(namehost,username,passworduser,namevm):
    #initilisation des proprietes de l'image
    properties(namehost,username,passworduser)
    print("Mise en pause de la VM "+namehost) 
    commande="VBoxManage controlvm "+namevm+" pause"
    run(commande) 

@task
def createcontainer(namehost,username,passworduser,namevm):
    #initilisation des proprietes de l'image
    properties(namehost,username,passworduser)
    print("Create Container "+namehost)     
    with cd(namevm+"/"):  
          commande="docker build -t  "+namevm+" ."
          sudo(commande) 

@task
def startcontainer(namehost,username,passworduser,namevm,numcpu):
    #initilisation des proprietes de l'image
    properties(namehost,username,passworduser)
    print("Start Container "+namehost)     
    commande="docker run --cpuset="+numcpu+" --memory=1024m -d "+namevm
    sudo(commande) 

@task
def stopcontainer(namehost,username,passworduser,namevm):
    #initilisation des proprietes de l'image
    properties(namehost,username,passworduser)
    print("Stop Container "+namehost)     
    commande="docker stop "+namevm
    sudo(commande) 

@task
def idcontainer(namehost,username,passworduser,namevm):
    #initilisation des proprietes de l'image
    properties(namehost,username,passworduser)
    print("Id Container "+namehost)     
    commande="docker ps"
    sudo(commande) 


