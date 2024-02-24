# Sony Xperia Hello Control
A while ago I got my hands on a Sony Xperia Hello robot for a very low price since Sony has started discontinuing support for them. They are very nice looking robots (and nicely built), but they have a lot more potential uses beyond what Sony supported... if only somebody creative got their hands on one. This repository is a collection of what I know, some guides and a simple demo android app that can be used to control the robot directly.

![Main image](https://github.com/r00li/Sony-Xperia-Hello-Control/blob/main/Misc/app_image.png?raw=true)

You can watch the demo video of this on [Youtube](https://youtu.be/1jyovW2SpgI)

## Rooting guide
In order to do most of the stuff with these, you really should be able to root them. Luckily Sony made this quite easy. So let's begin...

### Step 1: Enable developer mode
 1. Go to Settings -> System -> About device 
 2. First check that your "Android version" is 8.0.0. If not update the system and then continue. 
 3. Tap 7 times on Build number. You will see "You are now a developer" toast show up 
 4. Go back to System. A new menu "Developer options" is now available. Open it 
 5. Scroll down and find "USB Debugging". Enable it 
 6. Connect your Hello to the computer with USB. You will get a popup if you want to allow debugging. Set it to yes and select always trust this computer.

### Step 2: Get some tools 
You will need adb and fastboot at a minimum. I also recommend a small tool for easier installation of .apks. Though you can also do it with adb directly... 
1. Adb and fastboot can be obtained from google directly. They are part of Android Platform tools. Get them here (unzip them to somewhere and you will see a bunch of small tools in the folder): [https://developer.android.com/studio/releases/platform-tools](https://developer.android.com/studio/releases/platform-tools "https://developer.android.com/studio/releases/platform-tools")  
2. The tool that I recommend for installing apks is here (generally it is just a UI for adb): [http://jocala.com/adblink2.html](http://jocala.com/adblink2.html "http://jocala.com/adblink2.html")  

**If you brick the Hello**, you will need a few tools to unbrick it. Generally skip this section. For now don't worry about it, but if you need it... it's here... You will need Xperia flashtool... which is a slightly sketchy piece of software. And despite having a mac version I only ever got it to run with Windows: [https://github.com/Androxyde/Flashtool/releases](https://github.com/Androxyde/Flashtool/releases "https://github.com/Androxyde/Flashtool/releases") You will need the original flash image from Sony. You can get it here: [https://ftf.andro.plus/#panel-element-G1209](https://ftf.andro.plus/#panel-element-G1209 "https://ftf.andro.plus/#panel-element-G1209") Get the latest one - 46.3.A.0.58
Re-flash the stock image using the following guide: [https://xperiastockrom.com/flash-sony-xperia-stock-rom](https://xperiastockrom.com/flash-sony-xperia-stock-rom "https://xperiastockrom.com/flash-sony-xperia-stock-rom")

 ### Step 3: Unlock the bootloader 
 1. Open adblink2 tool. You should see your xperia under the connected devices section 
 2. Press the "ADB Shell" button. A command line window will open 
 3. Type in `am start -a android.intent.action.MAIN -n com.sonyericsson.android.servicemenu/.ServiceMainMenu` 
 4. A service menu will appear on your Xperia. 
 5. Go to Service Info -> Configuration 
 6. Write down the IDID number 
 7. Go to [https://developer.sony.com/develop/open-devices/get-started/unlock-bootloader/](https://developer.sony.com/develop/open-devices/get-started/unlock-bootloader/ "https://developer.sony.com/develop/open-devices/get-started/unlock-bootloader/")  
 8. Under unlock your device select Xperia Z2 Tablet (it most likely doesn't matter which one you select, but Xperia Hello is not listed) 
 9. A field will open. Type in the IDID number that you copied. 
 10. Click the acknowledgements and stuff and click submit 
 11. You will get the unlock code that you will need 
 12. Close the adb shell and adblink2 if you have it open 
 13. Disconnect USB cable from Xperia and shut it down 
 14. Hold the Volume up button on the bottom and plug the USB cable in (while holding the button) then release the button. Nothing will appear to happen, but that is ok 
 15. Open a terminal window and navigate to the folder where you have platform tools unzipped (on mac you can type in `cd` and then just drag the folder with the tools into the terminal window 
 16. Type in `./fastboot devices`  (this is for Mac, in windows you write it without `./`). You should see your xperia listed 
 17. Type in `fastboot oem unlock 0x<insert your unlock code>` That's it. If I remember correctly this should restart your device. If it doesn't - unplug the USB cable and power it on manually. Every time the thing boots you will now see a message that your device is insecure. Ignore it. 

This guide can also be found on the Sony developer site: [https://developer.sony.com/develop/open-devices/get-started/unlock-bootloader/how-to-unlock-bootloader/](https://developer.sony.com/develop/open-devices/get-started/unlock-bootloader/how-to-unlock-bootloader/ "https://developer.sony.com/develop/open-devices/get-started/unlock-bootloader/how-to-unlock-bootloader/")

**Oh and this will actually reset your hello to factory settings. Set it up again**

You just need to come back to the home screen. Don't touch the hello button

### Step 4: Root the device
Before you begin rooting your Hello, download the Hello Root.zip file from this repository. This contains three things - Magisk, a patched root image for the Hello and a precompiled version of my testing app.

1. Enable developer mode again (it was disabled after factory reset) 
2. Open up adblink2 again 
3. Select "Install APK" 
4. Select the Magisk .apk from the zip and let it install 
5. Magisk should be installed now. Open it. You don't need to do anything with it for now. Just check that it starts 
6. Close adblink2 
7. Copy the magisk patched .img file from the zip to the folder where you have your platform tools. 
8. Again open a command line window in the folder with platform tools 
9. Type in `./adb reboot bootloader`. Your Hello should reboot and you will see a black screen 
10. Type in `./fastboot flash boot magisk_patched-25200_qWpgu.img`  
11. Type in `./fastboot reboot`. Your xperia should boot again 
12. Open up Magisk again. Superuser tab should now be enabled. Click on it to check. If you tap on Modules tab Magisk will most likely say that it needs to reboot to finish up the installation (or it may ask you this before). Let it do its thing and reboot the device. 
13. You are ready

### Play with control app
In the zip file I provided my test app. Install it with adblink2. When you open it, you will get a popup to allow root access. Grant that forever. The app may crash the first time or not work properly. If it doesn't... open it again. You can now control most of the stuff on the robot. There are a few bugs in the app still (it doesn't properly remember the state if you minimize and expand panels). And stay away from the Experimental section for now.

Note that when this app gets started Security Extensions for linux are disabled, which isn't the best security practice (but sadly I can't do it otherwise without way too much effort). Generally - don't store too much personal information on the Hello and visit any shady websites. If you reboot the Hello then security extensions get enabled again - until you start the app.

## Control protocol
This is still very much work in progress, and for more information you should look into the example Android app to control it. Look for the `XperiaSerialManager.kt`, that should hold all of the secrets.

A lot of this information has been gained by sniffing what the built in service app does. You can start it using adb:
`am start -a android.intent.action.MAIN -n com.sonyericsson.android.servicemenu/.ServiceMainMenu`

Otherwise the robot gets controlled by very standard serial protocol. And what makes things easier for us - it's a normal character serial. You can simply connect to `/dev/ttyHS1` using `115200 baud` and you should have all the control that you need.

### Neck LED
This one is the most simple command since it just takes one value:

`:LED2<rgbColor>\r\n`
Simply replace the `<rgbColor>`with a HEX color string and send it to the robot. The color of the LED neck ring should change to what you specified.

### Eye LEDs
This one is quite simple as well. Each eye is represented by 5 different LED lights. Each eye is represented by one byte, where each bit controls one of the LEDs.

`:LED4<leftValue><rightValue>\r\n`
Simply replace `<leftValue>` and `<rightValue>` with 1 byte each representing the state of the LEDs.

### Motors
This one is more complicated...

`:P2P<motor><sign><newPosition><speed>\r\n` 
The `<motor>`part is the number of the motor you want to control: `0: Tilt, 1: Pan, 2: Body`.
The second part is the `<sign>`, which is basically up/down, left/right. It is either `0` or `F`.
The rest are more complicated and I suggest you check out the included android code for more details. For speed I recommend using `1F40` for now. It seems to be a good default value that works for the whole range of motion.

Note that there are more different motor control commands that I have not deciphered yet. But this one is basically "move motor to position with speed". 
