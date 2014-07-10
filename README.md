SnapGlass - Snapchat for Google Glass

I created this application as a research project that I was required to do. I don't have Glass right now so I can't progress further into development. Right now, it has basic functionalities. You can open unread snaps, and send new snaps from it. It uses an unofficial snapchat library (Package: com.habosa.javasnap) to do all of this. Functionality is limited but it can still be used to have fun :-)


Installation: Easy way (reccomended)
---
Download the most recent .apk from the apk folder. Connect Glass to adb and run:



      adb install /path/to/SnapGlass.apk


Installation: Hard way (not reccomended)
---
Checkout the project and import it into eclipse. Run the project when Glass is connected to adb, it will launch and appear on the screen. From here you can use the application (running it from eclipse installs it to the device) and edit the source code to get your own functionality.

Configuration (required)
---
I did not implement a very user friendly way to input a credential. I had to slap on a password-file-reader class and hope for the best. In order to add a username and password (you need this) you'll need to: run the application once, edit the file located at /sdcard/Documents/snapglass.xml so it's contents look like this:




	  <?xml version="1.0" encoding="utf-8"?>
	  <credential>
	  		<username>myusername</username>
	 		<password>myp455w0rd</password>
	  </credential>
	  
If the Documents folder doesn't exist, create it and run the application again.	  


Using it
---
The main activity is a camera preview. To create a new snap, tap and tap again to accept the picture. The application will attempt to login (it may crash if it fails, use eclipse to view error logs) and if it succeeds it will display a contact list. Tap the contact to send.

At the main preview, you can swipe toward your ear (left swipe) and the application will attempt to login and download a list of unopened snaps. Tap it to open it for the full time. Video snaps are not supported.

Broken?
---
I will not be able to develop this further because I don't own Glass (research institute does) but if you have questions, don't hesitate to ask me. I'd love to see somebody continue this project.
