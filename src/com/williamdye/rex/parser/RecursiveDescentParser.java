package com.williamdye.rex.parser;

import com.williamdye.rex.automata.*;

import java.text.ParseException;

/**
 * Represents a recursive-descent parser.
 * Uses a tokenizer to parse a regular expression with a recursive-descent approach.
 * @author William Dye
 */
public interface RecursiveDescentParser
{

    /**
     * Parses a regular expression and returns an NFA that describes that regular expression.
     * @return an NFA describing the regular expression that was parsed
     * @throws ParseException
     */
    public NFA parse() throws ParseException;

}

