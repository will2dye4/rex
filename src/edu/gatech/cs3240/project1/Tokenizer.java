package edu.gatech.cs3240.project1;

/**
 * Represents a tokenizer, which scans a file and returns tokens upon request.
 * @author William Dye
 */
public interface Tokenizer
{

    /**
     * Consumes and returns the next token.
     * @return the next token in the stream
     */
    public Token getNextToken();

    /**
     * Returns (but does not consume) the next token.
     * @return the next token in the stream
     */
    public Token peek();

    /**
     * Consumes the next token without returning it.
     * Intended to be used in conjunction with <code>peek()</code>.
     */
    public void consume();

    /**
     * Advances the stream to the next line, if possible.
     * @return <code>true</code> if there is another line, <code>false</code> otherwise
     */
    public boolean advance();

}
