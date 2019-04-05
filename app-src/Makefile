STATIC=../lib/index.html ../lib/style.css
BUILD=../lib/js/app.js
ANIM=../lib/animate.js

build: $(BUILD) $(STATIC) $(ANIM)

./app-src/src/js/animate.js: app-src/src/inkscape_animation_assistant/animation.cljs ./app-src/shims.js
	cat ./app-src/shims.js > $@
	wisp --no-map < app-src/src/inkscape_animation_assistant/animation.cljs >> $@

$(STATIC): public/index.html public/style.css
	cp -av $^ ../lib/
	touch $@

$(BUILD): src/**/** project.clj
	lein clean
	lein package

clean:
	lein clean
	rm -f $(STATIC) $(BUILD)
