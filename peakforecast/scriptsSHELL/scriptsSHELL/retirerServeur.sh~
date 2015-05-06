#!/bin/bash
# indique au système que l'argument qui suit est le programme utilisé pour exécuter ce fichier.
server=$1
>/etc/nginx/sites-available/loadbalencerback  
while read line  
do      
    if [ "$line" != "server $server;" ]; then
       echo $line>> /etc/nginx/sites-available/loadbalencerback   
    fi 
   
done < /etc/nginx/sites-available/loadbalencer

 rm /etc/nginx/sites-available/loadbalencer
 mv /etc/nginx/sites-available/loadbalencerback /etc/nginx/sites-available/loadbalencer
 rm -r /home/$user/retirerServeur
 rm /home/$user/retirerServeur.tar.gz
exit 0
