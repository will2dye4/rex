package com.williamdye.rex.project1;

/**
 * Represents the valid types of tokens that may appear in a lexical specification.
 * @author William Dye
 */
public enum TokenType
{
    /** A comment in the spec file (line beginning with "<code>%%</code>") */
    COMMENT,
    /** An identifier of a character class or regular expression (begins with "<code>$</code>") */
    IDENTIFIER,
    /** A literal character, such as a letter or digit */
    LITERAL,
    /** The caret character "<code>^</code>", used to exclude characters from a set */
    CARET("^"),
    /** The keyword "<code>IN</code>", used in conjunction with the caret character */
    IN("IN"),
    /** The Kleene star operator "<code>*</code>", used to specify repetition zero or more times */
    KLEENE_STAR("*"),
    /** The left bracket character "<code>[</code>", used to begin a range */
    LEFT_BRACKET("["),
    /** The left parenthesis character "<code>(</code>", used to begin a grouping */
    LEFT_PAREN("("),
    /** The newline character "<code>\n</code>", used to denote the end of a line */
    NEWLINE("\n"),
    /** The plus operator "<code>+</code>", used to denote repetition one or more times */
    PLUS("+"),
    /** The hyphen character "<code>-</code>", used to denote a range of characters */
    RANGE("-"),
    /** The right bracket character "<code>]</code>", used to end a range */
    RIGHT_BRACKET("]"),
    /** The right parenthesis character "<code>)</code>", used to end a grouping */
    RIGHT_PAREN(")"),
    /** The union character "<code>|</code>", used to denote the union of two expressions */
    UNION("|"),
    /** The dot character "<code>.</code>", used as a wildcard to denote any ASCII printable character */
    WILDCARD(".");

    private String string;

    /**
     * Default constructor. Creates a <code>TokenType</code> with <code>null</code> string.
     */
    TokenType()
    {
        this(null);
    }

    /**
     * Constructs a <code>TokenType</code> with the specified <code>value</code>.
     * @param value the value of the token type's string
     */
    TokenType(String value)
    {
        string = value;
    }

    /**
     * Accessor for a token type's string.
     * @return the string value of the token type
     */
    public String getString()
    {
        return string;
    }

}
