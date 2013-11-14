Protocoder
==========

Protocoder is a coding environment + framework in Javascript for quick prototyping on Android devices having some emphasis on rapid hardware hacking. 

It all started during the http://www.makewithmoto.com tour. A 5 month cross country roadtrip across the US with a fearless crew from Motorola ATAP on a velcro-covered van and a VW bus full of 3d printers, hackable phones, arduinos, ioios, electronics components. 
During the roadtrip we hacked together with university students and hackerspaces fellows and we realized we needed a better tool for prototyping, which we decided to start coding during the trip. Protocoder has been developed inside cars, hotel lobbys, cafes, restaurants, parks, here and there.

Install the app in your Android device and access the web IDE from your computer. 
Code in javascript using the protocoder framework. No needs to write dozends of lines to access sensors or write an UI, simple to use, fast to code.

```
//how to get sensor data
sensors.startAccelerometer(function(x, y, z)) { 
	console.log(x + " " + " " + y + " " + z); 
}

//send and sms
android.sendSMS(number, "text");

//play a video
ui.addVideoView("fileName", 0, 0, 500, 200);
```

It uses a webserver and a websockets server inside the project to do some of the magic and has support of most android hardware functionality, networking using OSC and websockets and audio synthesis and processing using Pure Data. 



AndroidApp 
----------
Contains Protocoder android app, the framework in the JInterface folder and the web IDE inside the asset folder


DesktopApp
----------
is the app companion for USB support. It is based on node-webkit and basically runs a set of adb commands to forward the connections to USB. At the moment only it only has Mac OS X support. Other platforms are welcome!

How to compile 
--------------
Clone the project, import it in eclipse, make sure the API version you are using to compile is 18 or greater. Eclipse tends to change it when importing projects. 
Press compile et voila.