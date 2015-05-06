#!/bin/sh
#Récuperation des paramètres
NameImage=$1

#Suppression de l'image
echo "Suppression de l'image"
VBoxManage unregistervm $NameImage --delete

exit 0
