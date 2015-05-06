#!/bin/bash
# indique au système que l'argument qui suit est le programme utilisé pour exécuter ce fichier.
user=$1
debut=vide
while read line  
do      
    if [ "$line" = "#debut" ]; then
         debut=#debut
    fi 
    if [ "$debut" = "#debut" ]; then
         echo $line
    fi 
    if [ "$line" = "#fin" ]; then
        debut=vide
    fi  
done < /etc/nginx/sites-available/repartiteur

 rm -r /home/$user/listeServeur
 rm /home/$user/listeServeur.tar.gz

exit 0
