#!/bin/bash
# indique au système que l'argument qui suit est le programme utilisé pour exécuter ce fichier.
server=$1
user=$2
>/etc/nginx/sites-available/repartiteurback 
while read line  
do      
    if [ "$line" = "#fin" ]; then
         echo "server $server ;">> /etc/nginx/sites-available/repartiteurback  
    fi 
    echo $line>> /etc/nginx/sites-available/repartiteurback  

done </etc/nginx/sites-available/repartiteur

 rm /etc/nginx/sites-available/repartiteur
 mv /etc/nginx/sites-available/repartiteurback /etc/nginx/sites-available/repartiteur
 rm -r /home/$user/ajouterServeur
 rm /home/$user/ajouterServeur.tar.gz

exit 0
