# Air Purrr

An app that handles controlling of self-made air purifier (consists of 230V fan and filter mats) and reading PM2.5/10 values from air quality detector and closest location (using Airly API). App is currently in alpha release.

### Getting Started

This is my first own project ever created so bugs are more than likely. Also, keep in mind that app is currently in development, including vast code refactoring.

### Self-Made Air Purifier

It uses:
* [Raspberry Pi 3](https://www.raspberrypi.org/products/raspberry-pi-3-model-b/) - The mini-computer with Apache2 server on it
* [Nova Fitness SDS011](https://www.aliexpress.com/item/nova-PM-sensor-SDS011-High-precision-laser-pm2-5-air-quality-detection-sensor-module-Super-dust/32617788139.html?spm=a2g17.10010108.1000016.1.cfbe645O7s0gk&isOrigTitle=true) - PM2.5/10 detector
* ~~[QinHeng Electronics HL-340](https://www.aliexpress.com/item/nova-PM-sensor-SDS011-High-precision-laser-pm2-5-air-quality-detection-sensor-module-Super-dust/32617788139.html?spm=a2g17.10010108.1000016.1.cfbe645O7s0gk&isOrigTitle=true) - default USB-Serial adapter~~ - DON'T use it! unless you want to have some uncool bugs
* [Prolific PL2303](https://www.waveshare.com/product/PL2303-USB-UART-Board-type-A.htm) - use this one (or anything based on PL2303)! it seems bug-free for now
* 2-channel relay board - to control the fan
* Raspberry Pi's deconstructed 5V/3A charger
* [Case](http://allegro.pl/g750-obudowa-uniwersalna-z-abs-i7025164953.html) - I used this one but could be anything
* [230V fan](http://www.cata.es/en/catalog/a%C3%A9ration/tubular-extraction/duct-in-line/151?_locale=es&_region=lenguage.country.resto.europa) - I used one of these
* Cables, jumpers etc.
* [SKILL WITH MAINS ELECTRICITY](https://www.youtube.com/watch?v=sskSFYxzkpE) - no joke here, this is a must

### SDS011 PM2.5/10 Results

I use ThingSpeak to show data from my device. Here's the [link](https://thingspeak.com/channels/462987) to charts.

### Airly API

From now, app uses Airly API instead of GIOS's one. You need to register your account [here](https://developer.airly.eu) (free), obtain an API key and add it to `gradle.properties` file in your .gradle home directory. More instructions [here](https://medium.com/code-better/hiding-api-keys-from-your-android-repository-b23f5598b906).

## Screenshots

<img src="https://i.imgur.com/pW3y7QT.png" width="285"> <img src="https://i.imgur.com/AaJY6o9.png" width="285"> <img src="https://i.imgur.com/mNsNVH2.png" width="285">
<img src="https://i.imgur.com/wPxHUAN.png" width="285"> <img src="https://i.imgur.com/UaosFkF.png" width="285"> <img src="https://i.imgur.com/xNHDOVQ.png" width="285">

