FUNCTIONAL FEATURES
-------------------
Gather user location details and communicate with server	
Receive emergencies in immediate surrounding and alert user	
If user is not able to leave the emergency area, send location coordi-nates as text message to default emergency number	
If user is not able to leave the emergency area, enable Bluetooth to as-sist in tracking 	
Users can set a customized, emergency contact number	
Appropriate Bluetooth name, to distinguish from random Bluetooth signals.	
If user is not able to leave the emergency area, start a voice call to the emergency contact number	
Flash torch light at regular intervals to serve as a beacon for rescue	
Polish application to display addition information about the nature of emergency and possible security tips	
I-Am-Safe button to override emergency action plan.	

DEVICE REQUIREMENTS
-------------------
Internet	
Location Services	
Text Messaging	
Bluetooth	
Voice Calls	
Torch	

IDE & INSTALLATION NOTES
------------------------
1) Downloading from github will provide warning about two missing iml files, remove the files and continue.
2) install android 25 SDK, will be required if not installed already
3) install Build tools 26.0.0,, will be required if not installed already
4) If "Please select android SDK" error is encountered, File->Settings->Android SDK->Location EDIT->Next->Next->Finish
5) Run the app on mobile devices having OS 6.0.1 (Running in emulators will have undesirable effect due to the absence of device features)

SERVER SETUP
------------
Python based server was used as stub for the app.
The server can be setup in any windows based machine that contains python.
(Preferrably WinPython-64bit-3.6.2.0Qt5, can be downloaded from https://sourceforge.net/projects/winpython/files/WinPython_3.6/3.6.2.0/)
Server.py file contains the script for server.
Open the file from the Spyder IDE, provided in the installation.
Line #35, should be set with the IP of the windows machine running it.(Do not use 127.0.0.1)
The same IP should be configure as the URL in Connect.java.
The windows machine containing the server and the mobile device should be in the same network or can be tethered.
The APP might display error page, if it is not able to reach the server.
By default the server sends Safe response.
Comment line #23, 24 set alert response from server. 
Changes in the server needs restart of complete python IDE to free and reuse ports.
or
Stop server. Execute below commands in command prompt. Then start server.
netstat -ano | findstr :8081
taskkill /PID <8656> /F