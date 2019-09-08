STATIC=../lib/index.html ../lib/style.css
BUILD=../lib/js/app.js

build: $(BUILD) $(STATIC)

node_modules:
	npm i

animate.js: src/inkscape_animation_assistant/animation.cljs ./shims.js node_modules
	cat ./shims.js > $@
	./node_modules/.bin/wisp --no-map < src/inkscape_animation_assistant/animation.cljs >> $@

public/animate.min.js: animate.js node_modules
	./node_modules/.bin/uglifyjs animate.js > $@

$(STATIC): public/index.html public/style.css public/animate.min.js
	cp -av $^ ../lib/
	touch $@

$(BUILD): src/**/** project.clj
	lein clean
	lein package

clean:
	lein clean
	rm -f $(STATIC) $(BUILD)
