STATIC=../lib/index.html ../lib/style.css
BUILD=../lib/js/app.js

build: $(BUILD) $(STATIC)

$(STATIC): public/index.html public/style.css
	cp -av $^ ../lib/
	touch $@

$(BUILD): src/**/** project.clj
	lein clean
	lein package

