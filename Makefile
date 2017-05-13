ARTCORD:=com.github.austinv11 Discord4J 2.8.1
ARTBIN:=com.github.kennedyoliveira pastebin4j 1.2.0
#ARTCLEV:=ca.pjer chatter-bot-api 1.4.5
ARTLOG:=ch.qos.logback logback-classic 1.2.1
ARTGSO:=com.google.code.gson gson 2.8.0

JARGS:=-cp ".:libs/*" Mspa $(shell cat auth.txt)

all : run

build : dep Mspa.class

dep : libs

libs : ivysettings.xml ivy.jar
	-mkdir libs
	java -jar ivy.jar -retrieve "libs/[artifact](-[classifier]).[ext]" -dependency $(ARTCORD) -settings ivysettings.xml
	java -jar ivy.jar -retrieve "libs/[artifact](-[classifier]).[ext]" -dependency $(ARTBIN) -settings ivysettings.xml
	#java -jar ivy.jar -retrieve "libs/[artifact](-[classifier]).[ext]" -dependency $(ARTCLEV) -settings ivysettings.xml
	java -jar ivy.jar -retrieve "libs/[artifact](-[classifier]).[ext]" -dependency $(ARTLOG) -settings ivysettings.xml
	java -jar ivy.jar -retrieve "libs/[artifact](-[classifier]).[ext]" -dependency $(ARTGSO) -settings ivysettings.xml

ivy.jar :
	wget http://archive.apache.org/dist/ant/ivy/2.4.0/apache-ivy-2.4.0-bin.zip
	unzip apache-ivy-2.4.0-bin.zip
	mv apache-ivy-2.4.0/ivy-2.4.0.jar ./ivy.jar
	rm -rf apache-ivy-2.4.0 apache-ivy-2.4.0-bin.zip

Mspa.class : Mspa.java
	javac -cp ".:libs/*" Mspa.java

run : build
	while true; do java $(JARGS); done

debug : build
	while true; do java -Xdebug -Xnoagent -Djava.compiler=NONE  -Xrunjdwp:transport=dt_socket,server=y,address=8888,suspend=y $(JARGS); done

jdb :
	jdb -attach localhost:8888

clean :
	rm Msp*.class ivy.jar libs/*
	rmdir libs

.PHONY : all build dep run clean
