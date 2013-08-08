package edu.gatech.cs3240.project1;

/**
 * Represents a token read from a lexical specification.
 * Has a type and (usually) a string value.
 * @author William Dye
 */
public interface Token
{

    /**
     * Accessor for a token's type.
     * @return the type of the token
     */
	public TokenType getTokenType();

    /**
     * Accessor for a token's string value.
     * @return the value of the token
     */
	public String getTokenString();

}
