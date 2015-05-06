#!/bin/sh
#Récuperation des paramètres
NameBaseImage=$1
NameNewImage=$2
Folder=$3

#Création de la nouvelle image
echo "Création de la nouvelle image date départ"
date +%kh%Mmin%Ss
vboxmanage clonevm $NameBaseImage --name $NameNewImage --basefolder $Folder --register
#Démarrage de la nouvelle image
echo "Démarrage de la nouvelle image"
VBoxManage startvm $NameNewImage
date +%kh%Mmin%Ss
echo "date final"
exit 0


