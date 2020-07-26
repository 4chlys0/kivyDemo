Background:
I had read python could be used to develop mobile applications, I wanted to tes>

Project:
I pulled CSV data on android OS versions from https://gs.statcounter.com/, used>

known limitations:
-Did not implement saving to /images/ directory
-Need to remove y access labeling for subplots 

After achieving a scaling image that launches in a window on OSX/Ubuntu, I atte>

I generated a valid (i.e. builds properly) APK (in the deploy folder)

Testing:

Warning: as avd manager warns, armabi-v7a emulation will be slow  

it does not run as-is...

Security Scanning:

Runs clean through virustotal, hash is below to verify
b7c6eed10520e237160c7d71b5fd50e70328a01f7adfec74871e29882a93ddb5
This detects intentionally malicious code (i.e. NOT framework vulnerabilities)

Reverse Engineering:

There's a few ways to reverse engineer an apk, jadx-gui is one of the simplest. I opened the apk in jadx and exported it in gradle folder format and as a jadx project in the reverse_engineering folder. 

findings
Jnius java NativeInvocationHandler
https://github.com/kivy/pyjnius
Utilities from kamranzafar 
https://github.com/draekko/android-jtar/tree/master/src/main/java/org/kamranzafar/jtar
Libsdl for cross-platform hardware access
https://www.libsdl.org/
Renpy presumably for assisting running python code on android
https://www.renpy.org/

Reading through androidmanifest.xml, there appears to be only one activity- org.kivy.android.PythonActivity


Here's some detail on the build process if curious 
https://developer.android.com/studio/build
