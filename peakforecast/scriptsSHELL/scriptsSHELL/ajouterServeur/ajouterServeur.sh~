#!/bin/bash
# indique au système que l'argument qui suit est le programme utilisé pour exécuter ce fichier.
server=$1
user=$2
>/etc/nginx/sites-available/loadbalencerback  
while read line  
do      
    if [ "$line" = "#fin" ]; then
         echo "server $server;">> /etc/nginx/sites-available/loadbalencerback  
    fi 
    echo $line>> /etc/nginx/sites-available/loadbalencerback 

done < /etc/nginx/sites-available/loadbalencer

 rm /etc/nginx/sites-available/loadbalencer
 mv /etc/nginx/sites-available/loadbalencerback /etc/nginx/sites-available/loadbalencer
 rm -r /home/$user/ajouterServeur
 rm /home/$user/ajouterServeur.tar.gz

exit 0
