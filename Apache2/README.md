### Protips

* create this file to launch any script at startup: ```/home/pi/.config/autostart/lxterm-autostart.desktop```
* ```libapache2-mod-wsgi-py3``` for Python3 codes
* ```WSGIPassAuthorization On``` in ```sites-available/default-ssl.conf``` to pass auth header
* ```chmod 755``` in ```flask-prod```
* don't use ```redirect()``` in Flask - worked in Postman, didn't work in Android app
* ```WSGIDaemonProcess threads=25``` and ```processes=2``` in ```sites-available/default-ssl.conf```
