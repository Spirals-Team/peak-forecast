from fabric.api import *
from fabric.operations import *
from properties import *
from installFrascati import *
  
@task
def install(namehost,username,passworduser):
    #initilisation des proprietes de l'image
    properties(namehost,username,passworduser)
    installFrascatiWithLocalPackage(username)
       

