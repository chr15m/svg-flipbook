svg-animation-assistant.zip: svg-animation-assistant.exe
	zip -r $@ . -x '*workspace*' '*src*' '*.git/*' Makefile .gitignore

