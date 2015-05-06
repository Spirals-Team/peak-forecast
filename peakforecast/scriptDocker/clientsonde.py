import zmq
import time
import os
import re
import sys
context = zmq.Context()
socket = context.socket(zmq.REQ)
#recuperation du port passe en parametre en ligne de commande
serveurport = "tcp://172.17.0.30:%s"%sys.argv[1]
socket.connect(serveurport)

print "Demarrage de lenregistrement des sondes cpu memoire ... pour une duree de 2 fois 150 secondes "
os.system("nmon -f -s2 -c 150")
time.sleep(3)

print "Recuperation du nom du fichier dans le quel est sauvegarde les sondes tous les 2 secondes"
listfichier=os.listdir('./')
for nom in listfichier:
    if re.search('.nmon', nom): nomfichiersonde=nom
print "nom du fichier: ", nomfichiersonde

i=0
while i<100:
    i=i+1
    f = open(nomfichiersonde,"r")
    sonde = f.read()
    f.close();
    #envoi du contenu vers le serveur
    socket.send(sonde)
    msg_in = socket.recv()
    time.sleep(3)


   
