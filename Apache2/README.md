## Protips

### RPi autostart
* create this file to launch any script at startup: ```/home/pi/.config/autostart/lxterm-autostart.desktop```

### Server-side code (Flask)
* use ```chmod 755``` in ```flask-prod``` directory
* don't use ```redirect()``` in Flask - it worked in Postman but didn't work in an Android app

### Server configuration (Apache2)
* use Step 2 and Step 4 from [here](hhttps://www.digitalocean.com/community/tutorials/how-to-create-a-self-signed-ssl-certificate-for-apache-in-ubuntu-16-04) to configure basic SSL modules
* use ```libapache2-mod-wsgi-py3``` for Python3 codes
* use ```WSGIPassAuthorization On``` in ```sites-available/default-ssl.conf``` to pass auth header
* use ```WSGIDaemonProcess threads=25``` and ```processes=2``` in ```sites-available/default-ssl.conf```
* current info about Apache2 configuration [here](https://www.digitalocean.com/community/tutorials/how-to-install-the-apache-web-server-on-debian-9)


## Directories cheatsheet
* ```/etc/apache2/conf-available``` for ```ssl-params.conf``` - some SSL configuration
* ```/etc/apache2/sites-available``` for ```000-default.conf``` and ```default-ssl.conf``` - **important server configuration for HTTP and HTTPS respectively**
* ```/etc/ssl/certs``` - **SSL certificates location** (along with CA bundle certficate, both used above)
* ```/etc/ssl/private``` - SSL private key location
* ```/var/www/flask-prod``` - **server-side code for HTTPS (Flask)**
* ```/var/www/html``` - server-side code for HTTP (plain PHP)
* ```/var/log/apache2``` - ```access.log``` and ```error.log``` logs
