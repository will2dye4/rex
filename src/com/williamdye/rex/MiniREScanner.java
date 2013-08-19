package com.williamdye.rex;

import com.williamdye.rex.automata.*;
import com.williamdye.rex.parser.*;
import com.williamdye.rex.scanner.*;
import com.williamdye.rex.tokens.*;

import java.io.File;
import java.text.ParseException;
import java.util.*;

/**
 * The driver for the scanner generator.
 * Opens a file (named in <code>args[0]</code>) containing a lexical specification,
 * then parses the file and generates an NFA representing the specified language.
 * Converts the generated NFA into a DFA capable of recognizing the token classes
 * defined in the specification file. Then reads the files named in <code>args[1]</code>
 * and beyond and prints the tokens it finds therein.
 * @author William Dye
 * @author Taylor Holden
 */
public class MiniREScanner
{

    private MiniREScanner() { /* prevent instantiation */ }

    /**
     * The entry point for the program.
     * @param args the names of the files containing the lexical specification and input to test
     */
    public static void main(String[] args)
    {
        if (args.length < 2) {
            out("Usage: java ScannerDriver <lexical-specification> <input-file> [<input-file> ...]");
            return;
        }

        out("Welcome to the Scanner Generator!");

        File spec = new File(args[0]);
        if (!spec.exists()) {
            out("Invalid file path: could not find file \"" + args[0] + "\".");
            return;
        }
		
		/* spec file -> primitive NFAs */
        print("Parsing lexical specification \"" + args[0] + "\" ...");
        Set<NFA> nfas;
        try {
            nfas = parseSpecFile(spec);
        } catch (ParseException except) {
            out("\n\nCaught parse exception: " + except.getMessage() + " (" + except.getErrorOffset() + ")");
            return;
        }
        out(" Done.");

        Set<NFA> charClasses = filterCharClasses(nfas);
        out("-- Character classes: " + formatNFAList(charClasses));
        out("-- Token classes: " + formatNFAList(nfas) + "\n");
        Map<String, Set<State>> stateMap = createIdentifierStateMapping(nfas);
		
        /* primitive NFAs -> combined NFA */
        print("Combining the primitive NFAs into a single, encompassing NFA ...");
        NFA combinedNFA = combinePrimitiveNFAs(nfas);
        out(" Done.\n");
        
		/* combined NFA -> DFA table */
        print("Converting the large NFA into a DFA ...");
        DFA dfa = NFAToDFAConverter.convert(combinedNFA, stateMap);
        
		/* DFA table + input file(s) -> table walker -> tokens */
        out(" Done.\n");
        Scanner scan = new Scanner(System.in);
        scan.useDelimiter("");
        for (int i = 1; i < args.length; i++) {
            File source = new File(args[i]);
            if (!source.exists())
                out("Invalid file path: skipping \"" + args[i] + "\".");
            else {
                final String KEY = (System.getProperty("os.name").contains("Mac") ? "Return" : "Enter");
                print("Ready to traverse input file \"" + args[i] + "\". Press " + KEY + " to continue. ");
                scan.next();
                walkInputFile(dfa, source, stateMap);
                out("\n");
            }
        }
        scan.close();

        out("Finished reading all input files. Goodbye!");
    }

    /* Parses the specification file and returns a list of NFAs corresponding to
     * the character classes and token classes in the specification. */
    private static Set<NFA> parseSpecFile(File specFile) throws ParseException
    {
        Tokenizer tokenizer = new TokenizerImpl(specFile);
        Set<NFA> result = new LinkedHashSet<NFA>();
        boolean charClass = true;
        Token next;
        do {
            next = tokenizer.getNextToken();
            if (TokenType.IDENTIFIER == next.getTokenType()) {
                RecursiveDescentParser parser = new RecursiveDescentParserImpl(tokenizer, result);
                NFA nfa = parser.parse();
                nfa.setIdentifier(next.getTokenString());
                nfa.setCharClass(charClass);
                result.add(nfa);
            } else if (TokenType.NEWLINE == next.getTokenType()) {
                if (TokenType.NEWLINE == tokenizer.peek().getTokenType() && result.size() > 0)
                    charClass = false;
            } else if (TokenType.COMMENT != next.getTokenType())
                throw new ParseException("Lines must begin with the name of an identifier (e.g., $FOO)", -1);

        } while (tokenizer.advance());
        return result;
    }

    /* Creates a mapping from the identifier of each specified NFA to the set of accepting states for that NFA. */
    private static Map<String, Set<State>> createIdentifierStateMapping(Set<NFA> nfas)
    {
        Map<String, Set<State>> map = new LinkedHashMap<String, Set<State>>();
        for (NFA nfa : nfas)
            map.put(nfa.getIdentifier(), nfa.getAcceptingStates());
        return map;
    }


    /* Combines a list of NFAs by creating a new NFA and adding an epsilon transition
     * from the new start state to each NFA in the list. */
    private static NFA combinePrimitiveNFAs(Set<NFA> nfas)
    {
        NFA combined = new NFAImpl("<COMBINED>", false);
        State start = combined.getStartState();
        start.setAccepting(false);
        for (NFA nfa : nfas) {
            combined.addAllStates(nfa.getStates());
            start.addTransition(NFAImpl.EPSILON, nfa.getStartState());
        }
        return combined;
    }

    /* Walks through the specified DFA using the source file as input and prints the tokens it finds. */
    private static void walkInputFile(DFA dfa, File source, Map<String, Set<State>> map)
    {
        final int MAX = getMaxStringLength(map.keySet());
        TableWalker walker = new TableWalkerImpl(dfa, source, map);
        SourceToken token;
        while (walker.hasNextToken()) {
            token = walker.getNextToken();
            out(String.format("%1$-" + MAX + "s\t", token.getTokenType()) + token.getTokenString());
        }
    }

    /* Returns a list of the character-class NFAs in the specified list,
     * while removing said NFAs from the original list. */
    private static Set<NFA> filterCharClasses(Set<NFA> all)
    {
        Set<NFA> charClasses = new LinkedHashSet<NFA>();
        Iterator<NFA> iterator = all.iterator();
        while (iterator.hasNext()) {
            NFA nfa = iterator.next();
            if (nfa.isCharClass()) {
                charClasses.add(nfa);
                iterator.remove();
            }
        }
        return charClasses;
    }

    /* Returns a formatted string of the identifiers of all NFAs in the specified list. */
    private static String formatNFAList(Set<NFA> list)
    {
        String result = (list.size() == 0 ? "<none>" : "");
        NFA[] nfas = list.toArray(new NFA[list.size()]);
        for (int i = 0; i < nfas.length; i++)
            result += (i == 0 ? "" : ", ") + nfas[i].getIdentifier();
        return result;
    }

    /* Returns the length of the longest string in the set. */
    private static int getMaxStringLength(Collection<String> set)
    {
        int max = 0;
        if (set.size() > 0) {
            for (String string : set) {
                if (string.length() > max)
                    max = string.length();
            }
        }
        return max;
    }

    /* A thin wrapper around System.out.print() for convenience. */
    private static void print(String message)
    {
        System.out.print(message);
    }

    /* A thin wrapper around System.out.println() for convenience. */
    private static void out(String message)
    {
        System.out.println(message);
    }

}
