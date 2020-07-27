Background:
I had read python could be used to develop mobile applications, I wanted to test this.

Thanks to Professor Angelos Stavrou for introducing me to mobile application security in 2015, eLearnSecurity for familiarity with a wide range of Mobile App security tools.

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

CMU SEI SCALe
I attempted to install SCALe using the Ubuntu 16.04 instructions on a new virtual machine. Due to dependency issues beyond what could be easily troubleshooted, I gave up on this approach. CMU SEI is the only software engineering FFRDC in the United States. The primary motivation is testing the code base against SEI's tool is 1) looking for Java issues as I'm familiar with their ruleset and 2) checking if the Android rules are applied through this tool.

LinkedIn QARK
linkedin provides an APK analysis tool. If you note the output, it performs reverse engineering using dex2jar. Report saved in "qark" subfolder

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


Here's some detail on android's build process if curious 
https://developer.android.com/studio/build
