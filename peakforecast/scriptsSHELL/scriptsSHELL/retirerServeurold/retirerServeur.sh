#!/bin/bash
# indique au système que l'argument qui suit est le programme utilisé pour exécuter ce fichier.
server=$1
user=$2
>//etc/nginx/sites-available/repartiteurback  
while read line  
do      
    if [ "$line" != "server $server ;" ]; then
       echo $line>>/etc/nginx/sites-available/repartiteurback   
    fi 
   
done < /etc/nginx/sites-available/repartiteur

 rm /etc/nginx/sites-available/repartiteur
 mv /etc/nginx/sites-available/repartiteurback  /etc/nginx/sites-available/repartiteur
 rm -r /home/$user/retirerServeur
 rm /home/$user/retirerServeur.tar.gz
exit 0
