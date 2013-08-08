William Dye, TJ Harrison, Taylor Holden, Matt Hooper, and Ryan McCaffrey
CS 3240 Project 1
11/17/2012


To compile and execute the program, run `make` in the project's top-level directory. It is
necessary to provide two arguments to make, one called SPEC_FILE and the other called
INPUT_FILE. Using the test cases we have provided, the program may be invoked with a
command such as `make SPEC_FILE=test/WilliamsSpec.txt INPUT_FILE=test/input/WilliamsInput.txt`.
Run `make doc` to generate HTML Javadoc pages in a subdirectory called "doc". Run `make
all` to generate the Javadocs and run the program (SPEC_FILE and INPUT_FILE must be
provided as described above). To delete Javadoc files and Java bytecode (.class) files,
run `make clean`.


The code is entirely contained within one package, namely edu.gatech.cs3240.project1. The
ScannerDriver class contains the entry point for the program, which makes use of the other
classes in the package. The RecursiveDescentParser interface is used to create a list of
NFAs corresponding to all identifiers in the specification. That list is combined into a 
single NFA, which is then converted to a DFA using the static convert() method of the
NFAToDFAConverter class. Finally, for each input file, a TableWalker is instantiated to
scan the file and return the tokens contained therein.