package com.williamdye.rex.tokens;

import java.io.File;

import static com.williamdye.rex.parser.RecursiveDescentParserImpl.*;

/**
 * MiniRE-specific implementation of the Tokenizer interface.
 * @author William Dye
 */
public class MiniRETokenizer extends TokenizerImpl implements Tokenizer
{

    private static final char BACKSLASH = '\\';
    private static final char DOUBLE_QUOTE = '\"';
    private static final char SINGLE_QUOTE = '\'';
    private static final char[] BEGIN = {'e', 'g', 'i', 'n'};
    private static final char[] DIFF = {'i', 'f', 'f'};
    private static final char[] END = {'n', 'd'};
    private static final char[] FIND = {'i', 'n', 'd'};
    private static final char[] INTERS = {'t', 'e', 'r', 's'};
    private static final char[] MAX_FREQ_STRING = {'a', 'x', 'f', 'r', 'e', 'q', 's', 't', 'r', 'i', 'n', 'g'};
    private static final char[] PRINT = {'r', 'i', 'n', 't'};
    private static final char[] RECURSIVE_REPLACE = {'c', 'u', 'r', 's', 'i', 'v', 'e', 'r', 'e', 'p', 'l', 'a', 'c', 'e'};
    private static final char[] REPLACE = {'p', 'l', 'a', 'c', 'e'};
    private static final char[] UNION = {'n', 'i', 'o', 'n'};
    private static final char[] WITH = {'i', 't', 'h'};

    /**
     * Constructs a <code>MiniRETokenizer</code> capable of reading from the specified <code>file</code>.
     * @param file the file to be tokenized
     */
    public MiniRETokenizer(File file)
    {
        super(file);
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
        StringBuilder builder = new StringBuilder();
        char next;
        char c = buffer.getNextChar();
        switch (c) {
            case ' ':
            case '\t':
            case '\r':
            case '\f':
                token = readToken();    /* ignore whitespace */
                break;
            case '\n':
                if (advance())
                    token = readToken();
                else
                    token = null;
                break;
            case '=':
                token = new MiniRETokenImpl(MiniRETokenType.ASSIGN);
                break;
            case '#':
                token = new MiniRETokenImpl(MiniRETokenType.COUNT);
                break;
            case '(':
                token = new MiniRETokenImpl(MiniRETokenType.LEFT_PAREN);
                break;
            case ')':
                token = new MiniRETokenImpl(MiniRETokenType.RIGHT_PAREN);
                break;
            case ',':
                token = new MiniRETokenImpl(MiniRETokenType.COMMA);
                break;
            case ';':
                token = new MiniRETokenImpl(MiniRETokenType.SEMICOLON);
                break;
            case '>':
                if ('!' == buffer.peekNextChar()) {
                    buffer.getNextChar();   /* consume '!' */
                    token = new MiniRETokenImpl(MiniRETokenType.REDIRECT);
                } else
                    token = new MiniRETokenImpl(MiniRETokenType.INVALID, String.format("%s", c));
                break;
            case '\"':
                token = getASCIIStringToken(builder);
                break;
            case '\'':
                token = getRegExToken(builder);
                break;
            case 'b':
                builder.append(c);
                token = getReservedWordOrIdentifier(builder, BEGIN, MiniRETokenType.BEGIN);
                break;
            case 'd':
                builder.append(c);
                token = getReservedWordOrIdentifier(builder, DIFF, MiniRETokenType.DIFF);
                break;
            case 'e':
                builder.append(c);
                token = getReservedWordOrIdentifier(builder, END, MiniRETokenType.END);
                break;
            case 'f':
                builder.append(c);
                token = getReservedWordOrIdentifier(builder, FIND, MiniRETokenType.FIND);
                break;
            case 'i':
                builder.append(c);
                next = buffer.peekNextChar();
                if ('n' == next) {
                    builder.append(buffer.getNextChar());
                    next = buffer.peekNextChar();
                    if (' ' == next || DOUBLE_QUOTE == next)
                        token = new MiniRETokenImpl(MiniRETokenType.IN, MiniRETokenType.IN.getString());
                    else
                        token = getReservedWordOrIdentifier(builder, INTERS, MiniRETokenType.INTERS);
                } else
                    token = getIdentifierToken(c);
                break;
            case 'm':
                builder.append(c);
                token = getReservedWordOrIdentifier(builder, MAX_FREQ_STRING, MiniRETokenType.MAX_FREQ_STRING);
                break;
            case 'p':
                builder.append(c);
                token = getReservedWordOrIdentifier(builder, PRINT, MiniRETokenType.PRINT);
                break;
            case 'r':
                builder.append(c);
                next = buffer.peekNextChar();
                if ('e' == next) {
                    builder.append(buffer.getNextChar());
                    next = buffer.peekNextChar();
                    if ('p' == next)
                        token = getReservedWordOrIdentifier(builder, REPLACE, MiniRETokenType.REPLACE);
                    else if ('c' == next)
                        token = getReservedWordOrIdentifier(builder, RECURSIVE_REPLACE, MiniRETokenType.RECURSIVE_REPLACE);
                    else
                        token = getIdentifierToken(builder.toString());
                } else
                    token = getIdentifierToken(c);
                break;
            case 'u':
                builder.append(c);
                token = getReservedWordOrIdentifier(builder, UNION, MiniRETokenType.UNION);
                break;
            case 'w':
                builder.append(c);
                token = getReservedWordOrIdentifier(builder, WITH, MiniRETokenType.WITH);
                break;
            default:
                token = getIdentifierToken(c);
                break;
        }
        return token;
    }

    /* Reads from the buffer a string of ASCII printable characters, excluding the surrounding quotation marks. */
    private Token getASCIIStringToken(StringBuilder builder)
    {
        Token token = null;
        char next = buffer.getNextChar();
        while (DOUBLE_QUOTE != next) {
            if (next < ASCII_MIN || next > ASCII_MAX) {
                builder.append(next);
                token = new MiniRETokenImpl(MiniRETokenType.INVALID, builder.toString());
                break;
            } else if (BACKSLASH == next && DOUBLE_QUOTE == buffer.peekNextChar())
                builder.append(buffer.getNextChar());
            else
                builder.append(next);
            next = buffer.getNextChar();
        }
        return (token == null ? new MiniRETokenImpl(MiniRETokenType.ASCII_STRING, builder.toString()) : token);
    }

    /* Reads from the buffer a regular expression string, excluding the surrounding single quotes. */
    private Token getRegExToken(StringBuilder builder)
    {
        Token token = null;
        char next = buffer.getNextChar();
        while (SINGLE_QUOTE != next) {
            if ('\t' == next)
                next = ' ';
            if (next < ASCII_MIN || next > ASCII_MAX) {
                builder.append(next);
                token = new MiniRETokenImpl(MiniRETokenType.INVALID, builder.toString());
                break;
            } else if (BACKSLASH == next && '\'' == buffer.peekNextChar()) {
                buffer.getNextChar();   /* consume escaped single quote */
                builder.append("\\'");
            }
            else
                builder.append(next);
            next = buffer.getNextChar();
        }
        return (token == null ? new MiniRETokenImpl(MiniRETokenType.REG_EX, builder.toString()) : token);
    }

    /* Reads from the buffer and returns an identifier string beginning with the specified first character. */
    private Token getIdentifierToken(Character first)
    {
        Token token;
        if (Character.isLetter(first))
            token = getIdentifierToken(first.toString());
        else
            token = new MiniRETokenImpl(MiniRETokenType.INVALID, first.toString());
        return token;
    }

    /* Reads from the buffer and returns an identifier string beginning with the specified initial string. */
    private Token getIdentifierToken(String start)
    {
        StringBuilder builder = new StringBuilder(start);
        char next = buffer.peekNextChar();
        while (isIdentifierCharacter(next)) {
            builder.append(buffer.getNextChar());
            next = buffer.peekNextChar();
        }
        String result = builder.toString();
        return new MiniRETokenImpl((result.length() > 10 ? MiniRETokenType.INVALID : MiniRETokenType.IDENTIFIER), result);
    }

    /* Reads from the buffer a reserved word (specified by type) or, failing that, an identifier. */
    private Token getReservedWordOrIdentifier(StringBuilder builder, char[] chars, MiniRETokenType type)
    {
        Token token = null;
        char next = buffer.peekNextChar();

        for (char ch : chars) {
            if (ch == next) {
                builder.append(buffer.getNextChar());
                next = buffer.peekNextChar();
            } else {
                token = getIdentifierToken(builder.toString());
                break;
            }
        }

        if (token == null) {
            if(isIdentifierCharacter(next))
                token = getIdentifierToken(builder.toString());
            else
                token = new MiniRETokenImpl(type, type.getString());
        }

        return token;
    }

    /* Returns true if the specified character is a letter, digit, or underscore. */
    private static boolean isIdentifierCharacter(char c)
    {
        return (Character.isLetter(c) || Character.isDigit(c) || '_' == c);
    }

}
