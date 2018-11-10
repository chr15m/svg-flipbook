svg-animation-assistant.zip: svg-animation-assistant.exe lib/index.html
	zip -r $@ . -x '*workspace*' '*src*' '*.git/*' '.*.swp' Makefile .gitignore

svg-animation-assistant.exe:
	$(MAKE) -C launcher-src

lib/index.html:
	$(MAKE) -C app-src

clean:
	rm -f svg-animation-assistant.*
	$(MAKE) -C app-src clean
	$(MAKE) -C launcher-src clean
