#globals
default: all
clean: clean-special
	rm -rf bin/*
clean-special:
freshen: clean default
all: locals assets objects

#git
git-prepare: clean
	git add -u
	git add *

#variables
cp = -cp src:bin:libs/*
dest = -d bin

#groups
locals: \
	bin/Driver.class
assets: \
	bin/assets/Configuration.class \
	bin/assets/Controller.class \
	bin/assets/DrawArea.class \
	bin/assets/Gui.class \
	bin/assets/Overlay.class \
	bin/assets/Plot.class \
	bin/assets/Simulation.class
objects: \
	bin/objects/Ball.class \
	bin/objects/Box.class \
	bin/objects/Crowd.class \
	bin/objects/Entity.class \
	bin/objects/Environment.class \
	bin/objects/Line.class \
	bin/objects/Pointd.class

#special
test: bin/Driver.class
	java $(cp) Driver

#locals
bin/Driver.class: src/Driver.java \
		bin/assets/Configuration.class \
		bin/assets/Controller.class \
		bin/assets/DrawArea.class \
		bin/assets/Gui.class \
		bin/assets/Overlay.class \
		bin/assets/Plot.class \
		bin/assets/Simulation.class \
		bin/objects/Ball.class \
		bin/objects/Box.class \
		bin/objects/Crowd.class \
		bin/objects/Entity.class \
		bin/objects/Environment.class \
		bin/objects/Line.class \
		bin/objects/Pointd.class
	javac $(cp) $(dest) src/Driver.java

#assets
bin/assets/Configuration.class: src/assets/Configuration.java \
		bin/assets/Gui.class \
		bin/assets/Plot.class \
		bin/assets/Simulation.class
	javac $(cp) $(dest) src/assets/Configuration.java

bin/assets/Controller.class: src/assets/Controller.java \
		bin/assets/DrawArea.class \
		bin/assets/Gui.class \
		bin/assets/Plot.class \
		bin/assets/Simulation.class
	javac $(cp) $(dest) src/assets/Controller.java

bin/assets/DrawArea.class: src/assets/DrawArea.java \
		bin/assets/Gui.class \
		bin/assets/Plot.class \
		bin/assets/Simulation.class
	javac $(cp) $(dest) src/assets/DrawArea.java

bin/assets/Gui.class: src/assets/Gui.java \
		bin/assets/Plot.class \
		bin/assets/Simulation.class
	javac $(cp) $(dest) src/assets/Gui.java

bin/assets/Overlay.class: src/assets/Overlay.java \
		bin/assets/Gui.class
	javac $(cp) $(dest) src/assets/Overlay.java

bin/assets/Plot.class: src/assets/Plot.java \
		bin/objects/Ball.class \
		bin/objects/Box.class \
		bin/objects/Line.class \
		bin/objects/Pointd.class
	javac $(cp) $(dest) src/assets/Plot.java

bin/assets/Simulation.class: src/assets/Simulation.java \
		bin/objects/Ball.class \
		bin/objects/Box.class \
		bin/objects/Line.class \
		bin/objects/Pointd.class
	javac $(cp) $(dest) src/assets/Simulation.java

#objects
bin/objects/Ball.class: src/objects/Ball.java \
		bin/objects/Line.class
	javac $(cp) $(dest) src/objects/Ball.java

bin/objects/Box.class: src/objects/Box.java \
		bin/objects/Line.class
	javac $(cp) $(dest) src/objects/Box.java

bin/objects/Crowd.class: src/objects/Crowd.java \
		bin/objects/Entity.class
	javac $(cp) $(dest) src/objects/Crowd.java

bin/objects/Entity.class: src/objects/Entity.java \
		bin/objects/Ball.class
	javac $(cp) $(dest) src/objects/Entity.java

bin/objects/Environment.class: src/objects/Environment.java \
		bin/objects/Box.class \
		bin/objects/Entity.class
	javac $(cp) $(dest) src/objects/Environment.java

bin/objects/Line.class: src/objects/Line.java \
		bin/objects/Pointd.class
	javac $(cp) $(dest) src/objects/Line.java

bin/objects/Pointd.class: src/objects/Pointd.java
	javac $(cp) $(dest) src/objects/Pointd.java

