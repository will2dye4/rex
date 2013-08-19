package com.williamdye.rex.parser;

import com.williamdye.rex.ast.*;

import java.text.ParseException;

/**
 * Represents a recursive-descent parser for the MiniRE scripting language.
 * @author William Dye
 */
public interface MiniREParser
{

    /**
     * Parses a MiniRE script and returns an AST that describes that script.
     * @return an AST describing the MiniRE script that was parsed
     * @throws ParseException
     */
    public AST parse() throws ParseException;

}
