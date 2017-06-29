JAR := pal.jar# Your jar file, with the .jar.
ARGS := $(shell cat auth.txt)# Arguments for your jar file when running.
CARG := -Xlint:all,-path# Arguments for javac. When debugging, use -g for local var debug ability.
PROP := # System properties when launching the jar.
SRCDIR := src/main/java# Directory where your .java files are. No trailing /.
BINDIR := bin/main/java# Directory where your .class files should be. No trailing /.
LIBDIR := lib# Where you want your libraries. No trailing /.
RESDIR := res# File resources, other than manifest.
JARDIR := ext# Set this to a directory if you want to add external jars. No trailing /.
MANIFEST := $(SRCDIR)/MANIFEST.MF# Your manifest file.

# Don't change stuff after here.
LIBS := libs.conf
JFILE := $(shell find $(SRCDIR) -name "*.java")
CFILE := $(patsubst $(SRCDIR)/%.java,$(BINDIR)/%.class,$(JFILE))
EMPTY :=
SPACE := $(EMPTY) $(EMPTY)
SEP := $$
ARTIFACTS := $(patsubst %,$(LIBDIR)/%,$(shell cat $(LIBS)))



all : build run

build : $(JAR)

dep : $(ARTIFACTS)

$(SRCDIR) :
	-mkdir $(SRCDIR)

$(BINDIR) :
	-mkdir $(BINDIR)

$(LIBDIR) :
	-mkdir $(LIBDIR)

$(RESDIR) :
	-mkdir $(RESDIR)

$(ARTIFACTS) : $(LIBDIR) ivysettings.xml ivy.jar $(LIBS)
	if [ ! -e "$@" ]; then java -jar ivy.jar -retrieve "$(LIBDIR)/[artifact](-[revision])(-[classifier]).[ext]" -dependency $(subst $(SEP),$(SPACE),$(patsubst $(LIBDIR)/%,%,$@)) -settings ivysettings.xml && touch $@; fi

ivy.jar :
	wget http://archive.apache.org/dist/ant/ivy/2.4.0/apache-ivy-2.4.0-bin.zip
	unzip apache-ivy-2.4.0-bin.zip
	mv apache-ivy-2.4.0/ivy-2.4.0.jar ./ivy.jar
	rm -rf apache-ivy-2.4.0 apache-ivy-2.4.0-bin.zip

$(JAR) : $(ARTIFACTS) $(CFILE) $(MANIFEST)
	cp $(MANIFEST) $(BINDIR)/manifest
	truncate -s-1 $(BINDIR)/manifest
	printf "Class-Path: $(subst $(SPACE),$(SPACE)\n$(SPACE),$(wildcard $(LIBDIR)/*.jar))$(SPACE)\n$(SPACE)$(subst $(SPACE),$(SPACE)\n$(SPACE),$(wildcard $(JARDIR)/*.jar))$(SPACE)\n$(SPACE).$(SPACE)\n" >> $(BINDIR)/manifest
	rsync -a $(RESDIR)/* $(BINDIR)
	jar cmf $(BINDIR)/manifest $(JAR) $(patsubst $(BINDIR)/%,-C $(BINDIR) %,$(shell find $(BINDIR) -type f -not -name "manifest"))

$(BINDIR)/%.class : $(SRCDIR)/%.java $(SRCDIR) $(BINDIR) $(ARTIFACTS)
	javac -d $(BINDIR) -cp ".:$(LIBDIR)/*:$(JARDIR)/*:$(BINDIR):$(SRCDIR)" $(CARG) $<

run : $(JAR)
	java $(PROP) -jar $(JAR) $(ARGS)

debug : build
	java -Xdebug -Xnoagent -Djava.compiler=NONE  -Xrunjdwp:transport=dt_socket,server=y,address=8888,suspend=y $(PROP) $(JAR) $(ARGS)

jdb :
	jdb -attach localhost:8888

clean :
	-rm $(JAR) ivy.jar
	-rm -r $(BINDIR) $(LIBDIR)

.PHONY : all build dep dirs run debug jdb clean

