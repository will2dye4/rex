package com.williamdye.rex.project2;

import java.io.File;
import java.text.ParseException;

/**
 * The driver for the MiniRE interpreter.
 * @author William Dye
 */
public class Interpreter
{

    /**
     * The entry point for the program.
     * @param args the name of the file containing the MiniRE script to interpret
     */
    public static void main(String[] args)
    {
        if (args.length < 1) {
            out("Usage: java Interpreter <minire-script-file>");
            return;
        }

        File file = new File(args[0]);
        if (!file.exists()) {
            out("Invalid file path: " + args[0]);
            return;
        }

        MiniREParser parser = new MiniREParserImpl(new MiniRETokenizer(file));
        AST tree;
        try {
            tree = parser.parse();
        } catch (ParseException except) {
            out("\n\nCaught parse exception: " + except.getMessage() + " (" + except.getErrorOffset() + ")");
            return;
        }
        ASTEvaluator evaluator = new ASTEvaluator(tree);
        evaluator.evaluate();
    }

    /* A thin wrapper around System.out.println() for convenience. */
    private static void out(String message)
    {
        System.out.println(message);
    }

}
