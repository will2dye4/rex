package com.williamdye.rex.scanner;

import com.williamdye.rex.tokens.*;

/**
 * The table walker program. Reads an input file
 * and generates tokens using a supplied DFA table.
 * @author William Dye
 */
public interface TableWalker
{

    /**
     * Returns <code>true</code> until the table walker reaches end of file (EOF).
     * @return <code>true</code> if the table walker has another token, <code>false</code> otherwise
     */
    public boolean hasNextToken();

    /**
     * Reads and returns a token from the table walker's input file.
     * @return the next token in the file, or <code>null</code> at EOF.
     */
	public SourceToken getNextToken();

}