Default: all

TOP=.

include $(TOP)/customize.mk

ifeq (Windows, $(findstring Windows,$(OS)))
    CLN=;
else
    CLN=:
endif

NULL:=
SPACE:=$(NULL) # end of the line
SHELL=/bin/sh

JAVA=$(JAVA_HOME)/bin/java
JAVAC=$(JAVA_HOME)/bin/javac
JAR=$(JAVA_HOME)/bin/jar
JAVACFLAGS=-Xlint:unchecked -deprecation
CLASSLIB=$(JAVA_HOME)/jre/lib/rt.jar
RSRC=rsrc
LIBDIR=$(RSRC)/lib
LIBS=$(subst $(SPACE),$(CLN),$(filter %.jar %.zip, $(wildcard $(LIBDIR)/*)))
BUILD=build
SRC=src
DOCS=docs
CWD=$(shell pwd)

include classes.mk

CLASS_FILES:=$(foreach class, $(CLASSES), $(BUILD)/$(subst .,/,$(class)).class)
PACKAGES=$(sort $(basename $(CLASSES)))
PACKAGEDIRS=$(subst .,/,$(PACKAGES))

JSAF_SRC=$(JSAF_HOME)/src
JSAF_COMPONENTS=$(JSAF_HOME)/components
JSAF_CORE=$(JSAF_COMPONENTS)/facade
JSAF_CORE_LIB=$(JSAF_CORE)/jSAF.jar
JSAF_CORE_DEPS=$(subst $(SPACE),$(CLN),$(filter %.jar %.zip, $(wildcard $(JSAF_CORE)/$(LIBDIR)/*)))
JSAF_PROVIDER_LIB=$(JSAF_PROVIDER_HOME)/jSAF-Provider.jar
JSAF_PROVIDER_DEPS=$(subst $(SPACE),$(CLN),$(filter %.jar %.zip, $(wildcard $(JSAF_PROVIDER_HOME)/$(LIBDIR)/*)))
JSAF_API=$(JSAF_CORE_LIB)$(CLN)$(JSAF_CORE_DEPS)$(CLN)$(JSAF_PROVIDER_LIB)$(CLN)$(JSAF_PROVIDER_DEPS)

CLASSPATH="$(CLASSLIB)$(CLN)$(LIBS)$(CLN)$(JSAF_API)$(CLN)$(SRC)"
RUNTIMECP="$(CLASSLIB)$(CLN)$(LIBS)$(CLN)$(JSAF_API)$(CLN)jPE.jar"

all: jPE.jar

jPE.jar: classes resources
	$(JAR) cvf $@ -C $(BUILD)/ .

javadocs:
	mkdir -p $(DOCS)
	$(JAVA_HOME)/bin/javadoc -d $(DOCS) -classpath $(CLASSPATH) $(PACKAGES)

clean:
	rm -f jPE.jar
	rm -rf $(BUILD)

resources:
	cp $(RSRC)/windows.locales.properties $(BUILD)

classes: classdirs $(CLASS_FILES)

classdirs: $(foreach pkg, $(PACKAGEDIRS), $(BUILD)/$(pkg)/)

$(BUILD)/%.class: $(SRC)/%.java
	$(JAVAC) $(JAVACFLAGS) -d $(BUILD) -classpath $(CLASSPATH) $<

$(BUILD)/%/:
	mkdir -p $(subst PKG,,$@)

test:
#	$(JAVA) -Djava.library.path=$(JSAF_PROVIDER_HOME)/$(LIBDIR) -classpath $(RUNTIMECP) jpe.test.PE "%SystemRoot%\\notepad.exe"
	$(JAVA) -Djava.library.path=$(JSAF_PROVIDER_HOME)/$(LIBDIR) -classpath $(RUNTIMECP) jpe.test.PE "C:\\Program Files\\Microsoft SQL Server\\MSSQL10.SQLEXPRESS\\MSSQL\\Binn\\sqlservr.exe"
