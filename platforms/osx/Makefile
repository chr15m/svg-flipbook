ZIP=SVGAnimationAssistant-mac.zip
APP=SVGAnimationAssistant.app
APPDIR=$(APP)/Contents/Resources/app.nw/
APPICON=$(APP)/Contents/Resources/app.icns
NWJS=nwjs-v0.14.7-osx-x64
PLIST=$(APP)/Contents/Info.plist

$(ZIP): $(APPDIR)/icon.png $(APPICON) $(APPDIR)/package.json $(APPDIR)/index.html $(PLIST)
	zip -q -r $(ZIP) $(APP)
	touch $@

$(NWJS).zip:
	wget http://dl.nwjs.io/v0.14.7/nwjs-v0.14.7-osx-x64.zip

$(NWJS): $(NWJS).zip
	unzip -qq $<
	touch $@

$(NWJS)/nwjs.app: $(NWJS)
	touch $@

$(APP): nwjs-v0.14.7-osx-x64/nwjs.app
	cp -a nwjs-v0.14.7-osx-x64/nwjs.app $@

$(APPICON): $(APPDIR)/icon.png
	png2icns $@ $<
	touch $@

$(APPDIR): $(APP)
	mkdir -p $(APPDIR)
	touch $(APPDIR)

$(PLIST): Info.plist
	cp $< $@

$(APPDIR)/icon.png: ../icon.png $(APPDIR)
	cp $< $@

$(APPDIR)/index.html: ../../lib/index.html $(APPDIR)
	cp -a ../../lib/* $(APPDIR)

$(APPDIR)/package.json: ../package.json $(APPDIR)
	cp $< $@

../icon.png: ../icon.svg
	make -C ..
	touch $@

.PHONY: clean

clean:
	rm -rf $(APP) $(ZIP) $(NWJS) $(NWJS).zip

