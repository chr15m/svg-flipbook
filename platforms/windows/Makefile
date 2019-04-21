ZIP=SVGAnimationAssistant-win.zip
APP=SVGAnimationAssistant
APPDIR=app
APPZIP=$(APP)/app.nw
#APPICON=$(APP)/Contents/Resources/app.icns
NWJS=nwjs-v0.14.7-win-ia32
EXE=$(APP)/SVGAnimationAssistant.exe
APPTARGETS=$(APPDIR)/icon.png $(APPDIR)/index.html $(APPDIR)/package.json

$(ZIP): $(APPZIP) $(EXE)
	zip -q -r $(ZIP) $(APP)
	touch $@

$(NWJS).zip:
	wget http://dl.nwjs.io/v0.14.7/nwjs-v0.14.7-win-ia32.zip

$(NWJS): $(NWJS).zip
	unzip -qq $<
	touch $@

$(APP): $(NWJS)
	cp -a $(NWJS) $@

$(EXE): ResourceHacker.exe $(APP) AppIcon.ico
	wine ResourceHacker.exe -open $(APP)/nw.exe -save $(APP)/nw.exe -action addoverwrite -res AppIcon.ico -mask ICONGROUP,IDR_MAINFRAME,
	#cd $(APP) && wine copy /b nw.exe+%~dp0app.nw SVGAnimationAssistant.exe
	cat $(APP)/nw.exe $(APP)/app.nw > $(EXE)

$(APPZIP): $(APPTARGETS) $(APP)
	cd app && zip -r ../$@ *

$(APPDIR):
	mkdir -p $(APPDIR)
	touch $(APPDIR)

$(APPDIR)/icon.png: ../icon.png $(APPDIR)
	cp $< $@

$(APPDIR)/index.html: ../../lib/index.html $(APPDIR)
	cp -a ../../lib/* $(APPDIR)

$(APPDIR)/package.json: ../package.json $(APPDIR)
	cp $< $@

AppIcon.ico: ../icon.png
	convert -background transparent "../icon.png" -define icon:auto-resize=16,24,32,48,64,72,96,128,256 "AppIcon.ico"

../icon.png: ../icon.svg
	make -C ..
	touch $@

ResourceHacker.exe:
	wget http://www.angusj.com/resourcehacker/resource_hacker.zip
	unzip resource_hacker.zip ResourceHacker.exe

.PHONY: clean

clean:
	rm -rf $(APP) $(ZIP) $(NWJS) $(APPDIR) $(NWJS).zip AppIcon.ico resource_hacker.zip ResourceHacker.*

