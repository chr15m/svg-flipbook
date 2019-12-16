STATIC=../lib/index.html ../lib/style.css ../lib/animate.min.js ../lib/icon.png ../lib/layers.png
BUILD=../lib/js/app.js

build: $(BUILD) $(STATIC)

node_modules:
	npm i

animate.js: src/inkscape_animation_assistant/animation.cljs ./shims.js node_modules
	cat ./shims.js > $@
	./node_modules/.bin/wisp --no-map < src/inkscape_animation_assistant/animation.cljs >> $@

public/animate.min.js: animate.js node_modules
	./node_modules/.bin/uglifyjs animate.js > $@

../lib/%: public/%
	cp -v $< $@

$(BUILD): src/**/** project.clj
	lein clean
	lein package

clean:
	lein clean
	rm -f $(STATIC) $(BUILD)
