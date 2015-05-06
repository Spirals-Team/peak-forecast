FROM fouomene/ApacheZeromq
EXPOSE 80
EXPOSE 22
EXPOSE 5000
ADD index.html /var/www/mediawiki/index.html
ADD LocalSettings.php /var/www/mediawiki/LocalSettings.php
RUN chmod 777 /var/www/mediawiki/LocalSettings.php
ADD clientsonde.py clientsonde.py
ADD clientsonde.conf /etc/supervisor/conf.d/clientsonde.conf
ADD start start
RUN chmod 777 start
CMD ["sh" ,"start"]
