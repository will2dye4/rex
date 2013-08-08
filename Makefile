JAVA_COMPILER=javac
JAVA_RUNTIME=java
PWD=$(shell pwd)
SRC_DIRECTORY=src/edu/gatech/cs3240/project
PART_1_MAIN_CLASS=edu.gatech.cs3240.project1.ScannerDriver
PART_2_MAIN_CLASS=edu.gatech.cs2340.project2.Foo  # TODO

# Run Part 2 by default.
default: run2

all: doc2 run2

# Use `make proj1 SPEC_FILE=path/to/spec.txt INPUT_FILE=path/to/input.txt` to Javadoc and execute just Part 1.
proj1: doc run

run: compile
	@$(JAVA_RUNTIME) -classpath "$(PWD)/out/" $(PART_1_MAIN_CLASS) $(SPEC_FILE) $(INPUT_FILE)

run2: compile2
    @$(JAVA_RUNTIME) -classpath "$(PWD)/out/" $(PART_2_MAIN_CLASS)

compile:
    @test -d out || mkdir out
	@$(JAVA_COMPILER) -d "$(PWD)/out/" $(SRC_DIRECTORY)1/*.java

compile2: compile
    @$(JAVA_COMPILER) -d "$(PWD)/out/" $(SRC_DIRECTORY)2/*.java

doc:
    @test -d doc || mkdir doc
	@javadoc -d "$(PWD)/doc/" $(SRC_DIRECTORY)1/*.java

doc2: doc
    @javadoc -d "$(PWD)/doc/" $(SRC_DIRECTORY)2/*.java

clean:
	@rm -rf out/ doc/
