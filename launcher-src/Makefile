all: ../lib/icon.png ../lib/App/AppInfo/Launcher/Splash.jpg ../svg-animation-assistant.exe

../lib/icon.png: icon.png
	cp $< $@

../lib/App/AppInfo/Launcher/Splash.jpg: splash.svg
	inkscape -z -e splash.png -w 416 -h 257 $<
	convert splash.png $@

../svg-animation-assistant.exe: launch.c iaa.res
	i686-w64-mingw32-gcc -o $@ $^ -Wl,-subsystem,windows

iaa.res: iaa.rc AppIcon.ico
	i686-w64-mingw32-windres $< -O coff -o $@

AppIcon.ico: icon.png
	convert -background transparent "icon.png" -define icon:auto-resize=16,24,32,48,64,72,96,128,256 "AppIcon.ico"

icon.png: icon.svg
	inkscape -z -e $@ -w 256 -h 256 $<

clean:
	rm -f iaa.res icon.png AppIcon.ico ../lib/icon.png ../lib/App/AppInfo/Launcher/Splash.jpg splash.png
