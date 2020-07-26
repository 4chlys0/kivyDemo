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

Reverse Engineering:

There's a few ways to reverse engineer an apk, jadx-gui is one of the simplest. I opened the apk in jadx and exported it in gradle folder format and as a jadx project in the reverse_engineering folder. 

Here's some detail on the build process if curious 
https://developer.android.com/studio/build
