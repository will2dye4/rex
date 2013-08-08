package edu.gatech.cs3240.project2;

import edu.gatech.cs3240.project1.Token;

/**
 * Represents a token read from a MiniRE script.
 * Has a type and (usually) a string value.
 * @author William Dye
 */
public interface MiniREToken extends Token
{

    /**
     * Accessor for a MiniRE token's type.
     * @return the type of the token
     */
    public MiniRETokenType getType();

}
