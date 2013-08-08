package edu.gatech.cs3240.project2;

/**
 * Represents the valid types of tokens that may appear in a MiniRE script.
 * This enumeration is essentially a superset of the token types defined in edu.gatech.cs3240.project1.TokenType.
 * @author William Dye
 * @see edu.gatech.cs3240.project1.TokenType
 */
public enum MiniRETokenType
{
    /** An ASCII string, enclosed in quotation marks (<code>"</code>) */
    ASCII_STRING,
    /** A variable identifier, matching <code>/[a-zA-Z][a-zA-Z0-9_]{0-9}/</code> */
    IDENTIFIER,
    /** An invalid token (token matching none of the other types) */
    INVALID,
    /** A regular expression, enclosed in single quotes (<code>'</code>) */
    REG_EX,
    /** The equal sign "<code>=</code>", used to assign a value to a variable */
    ASSIGN("="),
    /** The reserved word "<code>begin</code>", used to signal the start of a script */
    BEGIN("begin"),
    /** The comma character "<code>,</code>", used to separate items in a list */
    COMMA(","),
    /** The pound sign "<code>#</code>", used to obtain the size of a list */
    COUNT("#"),
    /** The reserved word "<code>diff</code>", used to find strings in one list but not another */
    DIFF("diff"),
    /** The reserved word "<code>end</code>", used to signal the end of a script */
    END("end"),
    /** The reserved word "<code>find</code>", used to locate strings in a file */
    FIND("find"),
    /** The reserved word "<code>in</code>", used in conjunction with the replacement keywords */
    IN("in"),
    /** The reserved word "<code>inters</code>", used to obtain the intersection of two lists */
    INTERS("inters"),
    /** The left parenthesis "<code>(</code>", used with the <code>maxfreqstring</code> and <code>print</code> keywords */
    LEFT_PAREN("("),
    /** The reserved word "<code>maxfreqstring</code>", used to find the most frequently used string in a list */
    MAX_FREQ_STRING("maxfreqstring"),
    /** The reserved word "<code>print</code>", used to print data to the console */
    PRINT("print"),
    /** The reserved word "<code>recursivereplace</code>", used to recursively replace one string with another */
    RECURSIVE_REPLACE("recursivereplace"),
    /** The redirection operator "<code>>!</code>", used to specify an output file */
    REDIRECT(">!"),
    /** The reserved word "<code>replace</code>", used to replace one string with another */
    REPLACE("replace"),
    /** The right parenthesis "<code>)</code>", used with the <code>maxfreqstring</code> and <code>print</code> keywords */
    RIGHT_PAREN(")"),
    /** The semicolon character "<code>;</code>", used to signal the end of a statement */
    SEMICOLON(";"),
    /** The reserved word "<code>union</code>", used to obtain the union of two lists */
    UNION("union"),
    /** The reserved word "<code>with</code>", used in conjunction with the replacement keywords */
    WITH("with");

    private String string;

    /**
     * Default constructor. Creates a <code>MiniRETokenType</code> with <code>null</code> string.
     */
    MiniRETokenType()
    {
        this(null);
    }

    /**
     * Constructs a <code>MiniRETokenType</code> with the specified <code>value</code>.
     * @param value the value of the token type's string
     */
    MiniRETokenType(String value)
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
