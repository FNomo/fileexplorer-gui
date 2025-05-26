.SILENT:
.PHONY: clean init application jdocs

## ---------------------- Variables -------------------- ##
## ----------------------------------------------------- ##

# Répertoire des fichiers sources
SRC_DIR = src/main
# Répertoire de la compilation
BIN_BIR = bin
# Répertoire des classes compilées
CLASSES_DIR = $(BIN_BIR)/classes
# Répertoire de Logging
LOG_DIR = log
# Répertoire de documentation
DOCU_DIR = docs/html
# Répertoire poubelle
TRASH_DIR = .trash

# Compilateur Java
JC = javac
# Flags du compilateurs Java
JFLAGS = -d "$(CLASSES_DIR)"
# Librairies à ajouter pour la compilation Java
JLIBS = -classpath ".:libs/*:$(CLASSES_DIR)"
# Les fichiers sources pour la compilation Java
JSRC = -sourcepath $(SRC_DIR)
# Compilateur pour le Jar
JARC = jar
# Flags pour la compilation Jar
JARFLAGS = cfm
# Compilateur pour la documentation
JDOCC = javadoc

# Tout les packages
ALL_PACKAGES = 	fr.univ_paris_diderot.utils \
				fr.univ_paris_diderot.file_explorer \
				fr.univ_paris_diderot.file_explorer.controller \
				fr.univ_paris_diderot.file_explorer.model \
				fr.univ_paris_diderot.file_explorer.view.components \
				fr.univ_paris_diderot.file_explorer.view.frame \
# Tout les fichiers à compiler
ALL_FILES = $(subst .java,.class,$(shell find $(SRC_DIR)/ -type f -name '*.java'))
JAR_FILE = $(BIN_BIR)/file-explorer.jar
MAIN_CLASS = fr.univ_paris_diderot.file_explorer.ApplicationGUI

## ----------------------------------------------------- ##
## ----------------------------------------------------- ##




## ------------------- Princiaple ---------------------- ##
## ----------------------------------------------------- ##

all: init application
	@echo [Makefile] done.

application: $(ALL_FILES) build_jar

## ----------------------------------------------------- ##
## ----------------------------------------------------- ##




## ----------------------------------------------------- ##
## ----------------------------------------------------- ##

# Création d'un répertoire
define create_folder
	@if [ ! -d $(1) ]; then \
		mkdir $(1); \
		echo [Makefile] $(1)/ folder has been created.; \
	else \
		echo [Makefile] $(1)/ folder already exists.; \
	fi
endef

# Inialisation
init:
	$(call create_folder,$(BIN_BIR))
	$(call create_folder,$(CLASSES_DIR))
	$(call create_folder,$(LOG_DIR))
	$(call create_folder,$(TRASH_DIR))

## ----------------------------------------------------- ##
## ----------------------------------------------------- ##





## ---- ------ ----- -- Lancement --- ----- ------ ----- ##
## ----------------------------------------------------- ##

# Lancement sans Jar
run:
	java $(JLIBS) $(MAIN_CLASS)

# Lancement avec Jar
run_jar:
	java -jar $(JAR_FILE)

## ----------------------------------------------------- ##
## ----------------------------------------------------- ##




## ------------------- Compilation --------------------- ##
## ----------------------------------------------------- ##

# Création du Jar
build_jar:
	$(JARC) $(JARFLAGS) $(JAR_FILE) MANIFEST.MF -C bin/classes .
	@echo [Makefile] Jar $(JAR_FILE): created.

# Compilation d'une classe
%.class:
	$(eval FILE_NAME = $(subst $(SRC_DIR)/,,$*))
	$(JC) $(JFLAGS) $(JLIBS) $(JSRC) $*.java
	echo [Makefile] Class $(subst /,.,$(FILE_NAME)) : compiled.

# Compilation de la documention
jdocs:
	$(JDOCC) -private -d $(DOCU_DIR) $(JLIBS) $(JSRC) $(ALL_PACKAGES)

## ----------------------------------------------------- ##
## ----------------------------------------------------- ##




## -------------------- Nettoyage ---------------------- ##
## ----------------------------------------------------- ##

# Nettoyage de l'application
clean: reset
	rm -rf $(BIN_BIR)
	@echo '[Makefile] All compilation has been deleted.'
	@echo '[Makefile] Compilation directory has been deleted.'
	rm -rf $(LOG_DIR)
	@echo '[Makefile] Logging directory has been deleted.'
	rm -rf $(TRASH_DIR)
	@echo '[Makefile] Trash directory has been deleted.'
	@echo '[Makefile] All done.'

# Remise à zéro
reset:
	rm -rf $(LOG_DIR)/*
	@echo '[Makefile] All logging files has been deleted.'
	rm -rf $(TRASH_DIR)/*
	@echo '[Makefile] All files in trash directory has been deleted.'
	rm -f settings/setting.conf
	@echo '[Makefile] setting file has been deleted.'

## ----------------------------------------------------- ##
## ----------------------------------------------------- ##