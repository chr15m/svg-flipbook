../svg-animation-assistant.exe: launch.c iaa.res
	i686-w64-mingw32-gcc -o $@ $^ -Wl,-subsystem,windows

iaa.res: iaa.rc
	i686-w64-mingw32-windres $< -O coff -o $@
