from fabric.api import *
from fabric.operations import *
from properties import *
  
@task
def kubectlresize(namehost,username,passworduser,namereplicationcontroller,newsize):
    #initilisation des proprietes de l'image
    properties(namehost,username,passworduser)
    print("Commande resize replication controller") 
    commande="./kubernetes/cluster/kubectl.sh resize rc "+namereplicationcontroller+" --replicas="+newsize
    with prefix('export KUBERNETES_PROVIDER=local'):
        with prefix('export KUBERNETES_MASTER=http://127.0.0.1:8080'):  
            sudo(commande) 




