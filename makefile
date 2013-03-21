#globals
default: all
clean:
	tools/cleandir .
	tools/cleandir assets
	tools/cleandir objects
freshen: clean default
all: locals assets objects

#git
git-prepare: clean
	git add -u
	git add *

#groups
locals: \
	Driver.class
assets: \
	assets/Configuration.class \
	assets/Gui.class \
	assets/Plot.class \
	assets/Simulation.class
objects: \
	objects/Ball.class \
	objects/Box.class \
	objects/Line.class \
	objects/Pointd.class \

test: Driver.class
	java Driver

#locals
Driver.class: Driver.java \
		assets
	javac Driver.java

#assets
assets/Configuration.class: assets/Configuration.java \
		assets/Gui.class \
		assets/Simulation.class
	javac assets/Configuration.java
assets/Gui.class: assets/Gui.java \
		assets/Plot.class \
		assets/Simulation.class
	javac assets/Gui.java
assets/Simulation.class: assets/Simulation.java \
		objects
	javac assets/Simulation.java
assets/Plot.class: assets/Plot.java
	javac assets/Plot.java

#objects
objects/Ball.class: objects/Ball.java \
		objects/Line.class
	javac objects/Ball.java
objects/Box.class: objects/Box.java \
		objects/Line.class
	javac objects/Box.java
objects/Line.class: objects/Line.java \
		objects/Pointd.class
	javac objects/Line.java
objects/Pointd.class: objects/Pointd.java
	javac objects/Pointd.java

