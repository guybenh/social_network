CFLAGS:=-c -Wall -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system -lpthread

all: BGSclient

	g++ -o bin/BGSclient bin/connectionHandler.o bin/ReadFromKeyboard.o bin/ReadFromServer.o bin/BGSclient.o $(LDFLAGS) 

BGSclient: bin/connectionHandler.o bin/BGSclient.o bin/ReadFromKeyboard.o bin/ReadFromServer.o

	
bin/ReadFromKeyboard.o: src/ReadFromKeyboard.cpp
	g++ $(CFLAGS) -o bin/ReadFromKeyboard.o src/ReadFromKeyboard.cpp

bin/connectionHandler.o: src/connectionHandler.cpp
	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp

bin/BGSclient.o: src/BGSclient.cpp
	g++ $(CFLAGS) -o bin/BGSclient.o src/BGSclient.cpp

bin/ReadFromServer.o: src/ReadFromServer.cpp
	g++ $(CFLAGS) -o bin/ReadFromServer.o src/ReadFromServer.cpp

	
.PHONY: clean
clean:
	rm -f bin/*
