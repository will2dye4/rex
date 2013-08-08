package edu.gatech.cs3240.project1;

import java.io.File;

/**
 * Implementation of the Tokenizer interface.
 * @author William Dye
 */
public class TokenizerImpl implements Tokenizer
{

    protected CharBuffer buffer;
    protected Token current;
    protected boolean paused;

    /**
     * Constructs a <code>TokenizerImpl</code> capable of reading from the specified <code>file</code>.
     * @param file the file to be tokenized
     */
    public TokenizerImpl(File file)
    {
        buffer = new CharBuffer(file);
        clearState();
    }
    
    /**
     * Constructs a <code>TokenizerImpl</code> capable of reading from the specified <code>String</code>.
     * @param s the String to be tokenized
     */
    public TokenizerImpl(String s)
    {
    	buffer = new CharBuffer(s);
    	clearState();
    }

    @Override
    public Token getNextToken()
    {
        return next(false);
    }

    @Override
    public Token peek()
    {
        return next(true);
    }

    @Override
    public void consume()
    {
        getNextToken();
    }

    @Override
    public boolean advance()
    {
        clearState();
        return buffer.advance();
    }

    /* Resets the current token and paused flag. */
    protected void clearState()
    {
        current = null;
        paused = true;
    }

    /* Helper method for getNextToken() and peekNextToken(). */
    private Token next(boolean peek)
    {
        if (!paused || current == null)
            current = readToken();
        paused = peek;
        return current;
    }

    /* Helper method for next(). Handles reading from the buffer and generation of new tokens. */
    private Token readToken()
    {
        Token token;
        char c = buffer.getNextChar();
        switch (c) {
            case ' ':
            case '\t':
            case '\r':
            case '\f':
                token = readToken();    /* ignore whitespace */
                break;
            case '\n':
                token = new TokenImpl(TokenType.NEWLINE, TokenType.NEWLINE.getString());
                break;
            case '%':
                if ('%' == buffer.peekNextChar())
                    token = new TokenImpl(TokenType.COMMENT);
                else
                    token = new TokenImpl(TokenType.LITERAL, "%");
                break;
            case '$':
                StringBuilder builder = new StringBuilder();
                char tmp = buffer.peekNextChar();
                while (isIdentifier(tmp)) {
                    builder.append(buffer.getNextChar());
                    tmp = buffer.peekNextChar();
                }
                token = new TokenImpl(TokenType.IDENTIFIER, builder.toString());
                break;
            case '(':
                token = new TokenImpl(TokenType.LEFT_PAREN, TokenType.LEFT_PAREN.getString());
                break;
            case ')':
                token = new TokenImpl(TokenType.RIGHT_PAREN, TokenType.RIGHT_PAREN.getString());
                break;
            case '*':
                token = new TokenImpl(TokenType.KLEENE_STAR, TokenType.KLEENE_STAR.getString());
                break;
            case '+':
                token = new TokenImpl(TokenType.PLUS, TokenType.PLUS.getString());
                break;
            case '|':
                token = new TokenImpl(TokenType.UNION, TokenType.UNION.getString());
                break;
            case '.':
                token = new TokenImpl(TokenType.WILDCARD, TokenType.WILDCARD.getString());
                break;
            case '-':
                token = new TokenImpl(TokenType.RANGE, TokenType.RANGE.getString());
                break;
            case '^':
                token = new TokenImpl(TokenType.CARET, TokenType.CARET.getString());
                break;
            case '[':
                token = new TokenImpl(TokenType.LEFT_BRACKET, TokenType.LEFT_BRACKET.getString());
                break;
            case ']':
                token = new TokenImpl(TokenType.RIGHT_BRACKET, TokenType.RIGHT_BRACKET.getString());
                break;
            case 'I':
                if ('N' == buffer.peekNextChar()) {
                    buffer.getNextChar();   /* consume 'N' */
                    token = new TokenImpl(TokenType.IN, TokenType.IN.getString());
                } else
                    token = new TokenImpl(TokenType.LITERAL, "I");
                break;
            case '\\':
                token = new TokenImpl(TokenType.LITERAL, String.format("\\%s", buffer.getNextChar()));
                break;
            default:
                token = new TokenImpl(TokenType.LITERAL, String.format("%s", c));
        }
        return token;
    }

    /* Returns true if the specified character is valid for an identifier. */
    private static boolean isIdentifier(char c)
    {
        return Character.isLetterOrDigit(c) || '-' == c || '_' == c;
    }

}

