package com.williamdye.rex.tokens;

/**
 * Represents a token found in a source file.
 * @author William Dye
 */
public class SourceToken
{

    private String type, string;

    /**
     * Creates a new <code>SourceToken</code> with the specified type and string.
     * @param tokenType the type of the token
     * @param tokenString the string (value) of the token
     */
    public SourceToken(String tokenType, String tokenString)
    {
        type = tokenType;
        string = tokenString;
    }

    /**
     * Accessor for a token's type.
     * @return the type of the token
     */
    public String getTokenType()
    {
        return type;
    }

    /**
     * Accessor for a token's string.
     * @return the string (value) of the token
     */
    public String getTokenString()
    {
        return string;
    }

}
