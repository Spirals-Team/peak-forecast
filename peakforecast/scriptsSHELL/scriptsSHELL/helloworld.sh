#!/bin/bash
# indique au système que l'argument qui suit est le programme utilisé pour exécuter ce fichier.
# En cas général les "#" servent à faire des commentaires comme ici
#echo Mon premier script
#echo Liste des fichiers :
#ls -la
 
#exit 0
#echo -n "Entrez un nom de fichier: "
#read file
#if [ -e "$file" ]; then
#        echo "Le fichier existe!"
#else
#        echo "Le fichier n'existe pas, du moins n'est pas dans le répertoire d'exécution du script"
#fi
#exit 0
#Récuperation du parametre
#server=$1
#>/home/daniel/scriptsSHELL/repartiteurback  
#while read line  
#do      
#    if [ "$line" = "#fin" ]; then
#         echo "server $server;">> /home/daniel/scriptsSHELL/repartiteurback  
#    fi 
#    echo $line>> /home/daniel/scriptsSHELL/repartiteurback 

#done < /home/daniel/scriptsSHELL/repartiteur

#rm /home/daniel/scriptsSHELL/repartiteur
#mv /home/daniel/scriptsSHELL/repartiteurback /home/daniel/scriptsSHELL/repartiteur

PID= pgrep nginx

echo ${PID[1]} ;

exit 0
