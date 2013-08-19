package com.williamdye.rex.ast;

/**
 * An enumeration of the valid types of nodes in an AST.
 */
public enum ASTNodeType
{
    /** A "begin" node (the start of a MiniRE script). Has no children. */
    BEGIN,
    /** An "end" node (the end of a MiniRE script). Has no children. */
    END,
    /** An assignment node. Has two children: an identifier and the expression or value to assign to that identifier. */
    ASSIGN,
    /** A "find" node. Has two or three children: a regular expression, a filename, and possibly a set operation. */
    FIND,
    /** A "maxfreqstring" node. Has one child: the identifier for which to determine the most frequent string. */
    MAX_FREQ_STRING,
    /** A "replace" node. Has three children: a regular expression, an ASCII string, and a set of file names. */
    REPLACE,
    /** A "recursivereplace" node. Has three children: a regular expression, an ASCII string, and a set of file names. */
    RECURSIVE_REPLACE,
    /** A "print" statement. Has a variable number of children identifying the expressions to print. */
    PRINT,
    /** A count (#) node. Has one child: the expression to be counted. */
    COUNT,
    /** A set operation (diff, union, or intersect). Has one child, a "find" node. */
    SET_OPERATION,
    /** A set of file names. Has two children: a source file and a destination file. */
    FILE_NAMES,
    /** A filename string. Has no children. */
    FILENAME,
    /** An ASCII string. Has no children. */
    ASCII_STRING,
    /** A regular expression string. Has no children. */
    REG_EX,
    /** An identifier. Has no children. */
    IDENTIFIER
}
