package com.williamdye.rex.project2;

import java.text.ParseException;

/**
 * Implementation of the MiniREParser interface.
 * @author William Dye
 */
public class MiniREParserImpl implements MiniREParser
{

    protected MiniRETokenizer tokenizer;
    protected AST ast;
    protected ASTNode current;
    protected MiniREToken token;

    /**
     * Creates a new <code>MiniREParserImpl</code> that reads from the provided tokenizer.
     * @param t the tokenizer for the parser to use
     */
    public MiniREParserImpl(MiniRETokenizer t)
    {
        tokenizer = t;
        ast = new ASTImpl();
        current = ast.getStartNode();
        token = null;
    }

    @Override
    public AST parse() throws ParseException
    {
        MiniRE_program();
        return ast;
    }

    /* <MiniRE-program> => begin <statement-list> end */
    private void MiniRE_program() throws ParseException
    {
        token = getToken();
        if (token == null)
            throw new ParseException("MiniRE_program: Empty input file", -1);
        if (MiniRETokenType.BEGIN != token.getType())
            throw new ParseException("MiniRE_program: Found token \"" + token.getTokenString() + "\"; expected \"begin\"", -2);
        current = new ASTNodeImpl(token, ASTNodeType.BEGIN);
        ast.setStartNode(current);
        statement_list();
        token = getToken();
        if (MiniRETokenType.END != token.getType())
            throw new ParseException("MiniRE_program: Found token \"" + token.getTokenString() + "\"; expected \"end\"", -3);
        current.setNext(new ASTNodeImpl(token, ASTNodeType.END));
    }

    /* <statement-list> => <statement> <statement-list-tail> */
    private void statement_list() throws ParseException
    {
        statement();
        statement_list_tail();
    }

    /* <statement-list-tail> => <statement> <statement-list-tail> | epsilon */
    private void statement_list_tail() throws ParseException
    {
        if (peekToken() == null)
            throw new ParseException("statement_list_tail: Reached EOF before \"end\" token", -4);
        MiniRETokenType type = peekToken().getType();
        if (MiniRETokenType.IDENTIFIER == type || MiniRETokenType.REPLACE == type ||
                MiniRETokenType.RECURSIVE_REPLACE == type || MiniRETokenType.PRINT == type) {
            statement();
            statement_list_tail();
        }
    }

    /* <statement> => ID = exp; */
    /* <statement> => ID = # exp; */
    /* <statement> => ID = maxfreqstring(ID); */
    /* <statement> => replace REGEX with ASCII-STR in <file-names>; */
    /* <statement> => recursivereplace REGEX with ASCII-STR in <file-names>; */
    /* <statement> => print( <exp-list> ); */
    private void statement() throws ParseException
    {
        token = getToken();
        boolean recursive = false;
        switch (token.getType()) {
            case IDENTIFIER:
                handleIdentifierToken();
                break;
            case RECURSIVE_REPLACE:
                recursive = true;
                /* fallthrough intended */
            case REPLACE:
                ASTNode asciiString = handleReplaceToken(recursive);
                file_names(asciiString);
                break;
            case PRINT:
                current.setNext(new ASTNodeImpl(token, ASTNodeType.PRINT));
                current = current.getNext();
                token = getToken();
                if (MiniRETokenType.LEFT_PAREN != token.getType())
                    throw new ParseException("statement: Found token \"" + token.getTokenString() + "\"; expected \"(\"", -5);
                exp_list();
                token = getToken();
                if (MiniRETokenType.RIGHT_PAREN != token.getType())
                    throw new ParseException("statement: Found token \"" + token.getTokenString() + "\"; expected \")\"", -6);
                break;
            default:
                throw new ParseException("statement: Found token \"" + token.getTokenString() + "\"; expected " +
                        "ID, \"replace\", \"recursivereplace\", or \"print\"", -7);
        }
        token = getToken();
        if (MiniRETokenType.SEMICOLON != token.getType())
            throw new ParseException("statement: Found token \"" + token.getTokenString() + "\"; expected \";\"", -8);
    }

    /* <file-names> => <source-file> >! <destination-file> */
    private void file_names(ASTNode asciiNode) throws ParseException
    {
        ASTNode names = new ASTNodeImpl(ASTNodeType.FILE_NAMES);
        asciiNode.setNext(names);
        source_file(names);
        token = getToken();
        if (MiniRETokenType.REDIRECT != token.getType())
            throw new ParseException("file_names: Found token \"" + token.getTokenString() + "\"; expected \">!\"", -9);
        destination_file(names);
    }

    /* <source-file> => ASCII-STR */
    private void source_file(ASTNode parent) throws ParseException
    {
        token = getToken();
        if (MiniRETokenType.ASCII_STRING != token.getType())
            throw new ParseException("source_file: Found token \"" + token.getTokenString() + "\"; expected ASCII_STR", -10);
        parent.setChild(new ASTNodeImpl(token, ASTNodeType.FILENAME));
    }

    /* <destination-file> => ASCII-STR */
    private void destination_file(ASTNode parent) throws ParseException
    {
        token = getToken();
        if (MiniRETokenType.ASCII_STRING != token.getType())
            throw new ParseException("destination_file: Found token \"" + token.getTokenString() + "\"; expected ASCII_STR", -11);
        parent.getChild().setNext(new ASTNodeImpl(token, ASTNodeType.FILENAME));
    }

    /* <exp-list> => <exp> <exp-list-tail> */
    private void exp_list() throws ParseException
    {
        exp(current);
        exp_list_tail(current.getChild());
    }

    /* <exp-list-tail> => , <exp> <exp-list-tail> | epsilon */
    private void exp_list_tail(ASTNode prev) throws ParseException
    {
        MiniRETokenType type = peekToken().getType();
        if (MiniRETokenType.COMMA == type) {
            tokenizer.consume(); /* consume comma token */
            exp(prev);
            exp_list_tail(prev.getNext());
        }
    }

    /* <exp> => ID | ( <exp> ) | <term> <exp-tail> */
    private void exp(ASTNode parent) throws ParseException
    {
        ASTNode expression = null;
        token = getToken();
        if (MiniRETokenType.IDENTIFIER == token.getType())
            expression = new ASTNodeImpl(token, ASTNodeType.IDENTIFIER);
        else if (MiniRETokenType.FIND == token.getType()) {
            expression = term();
            exp_tail(expression);
        } else if (MiniRETokenType.LEFT_PAREN == token.getType()) {
            exp(parent);
            token = getToken();
            if (MiniRETokenType.RIGHT_PAREN != token.getType())
                throw new ParseException("exp: Found token \"" + token.getTokenString() + "\"; expected \")\"", -12);
        } else
            throw new ParseException("exp: Found token \"" + token.getTokenString() + "\"; expected ID, \"(\", or \"find\"", -13);

        if (ASTNodeType.ASSIGN == parent.getNodeType())
            parent.getChild().setNext(expression);
        else if (ASTNodeType.IDENTIFIER == parent.getNodeType() || ASTNodeType.FIND == parent.getNodeType())
            parent.setNext(expression);
        else    /* parent is a print, count or maxfreqstring node */
            parent.setChild(expression);
    }

    /* <exp-tail> => <bin-op> <term> <exp-tail> | epsilon */
    private void exp_tail(ASTNode parent) throws ParseException
    {
        MiniRETokenType type = peekToken().getType();
        if (MiniRETokenType.DIFF == type || MiniRETokenType.INTERS == type || MiniRETokenType.UNION == type) {
            ASTNode op = bin_op();
            token = getToken();
            ASTNode find = term();
            exp_tail(find);
            op.setChild(find);
            /* add op to end of parent's children */
            ASTNode prev = parent.getChild();
            if (prev == null)
                parent.setChild(op);
            else {
                while (prev.getNext() != null)
                    prev = prev.getNext();
                prev.setNext(op);
            }
        }
    }

    /* <term> => find REGEX in <file-name> */
    private ASTNode term() throws ParseException
    {
        if (MiniRETokenType.FIND != token.getType())
            throw new ParseException("term: Found token \"" + token.getTokenString() + "\"; expected \"find\"", -14);
        ASTNode find = new ASTNodeImpl(token, ASTNodeType.FIND);
        token = getToken();
        if (MiniRETokenType.REG_EX != token.getType())
            throw new ParseException("term: Found token \"" + token.getTokenString() + "\"; expected REGEX", -15);
        find.setChild(new ASTNodeImpl(token, ASTNodeType.REG_EX));
        token = getToken();
        if (MiniRETokenType.IN != token.getType())
            throw new ParseException("term: Found token \"" + token.getTokenString() + "\"; expected \"in\"", -16);
        file_name(find.getChild());
        return find;
    }

    /* <file-name> => ASCII-STR */
    private void file_name(ASTNode prev) throws ParseException
    {
        token = getToken();
        if (MiniRETokenType.ASCII_STRING != token.getType())
            throw new ParseException("file_name: Found token \"" + token.getTokenString() + "\"; expected ASCII_STR", -17);
        prev.setNext(new ASTNodeImpl(token, ASTNodeType.FILENAME));
    }

    /* <bin-op> => diff | union | inters */
    private ASTNode bin_op() throws ParseException
    {
        return new ASTNodeImpl(getToken(), ASTNodeType.SET_OPERATION);
    }

    /* Handles "replace" and "recursivereplace" keywords, up to (but not including) the file names.
     * Returns the node corresponding to the ASCII string. */
    private ASTNode handleReplaceToken(boolean recursive) throws ParseException
    {
        current.setNext(new ASTNodeImpl(token, (recursive ? ASTNodeType.RECURSIVE_REPLACE : ASTNodeType.REPLACE)));
        current = current.getNext();
        token = getToken();
        if (MiniRETokenType.REG_EX != token.getType())
            throw new ParseException("statement: Found token \"" + token.getTokenString() + "\"; expected REGEX", -18);
        current.setChild(new ASTNodeImpl(token, ASTNodeType.REG_EX));

        token = getToken();
        if (MiniRETokenType.WITH != token.getType())
            throw new ParseException("statement: Found token \"" + token.getTokenString() + "\"; expected \"with\"", -19);
        token = getToken();
        if (MiniRETokenType.ASCII_STRING != token.getType())
            throw new ParseException("statement: Found token \"" + token.getTokenString() + "\"; expected ASCII_STR", -20);
        ASTNode ascii = new ASTNodeImpl(token, ASTNodeType.ASCII_STRING);
        current.getChild().setNext(ascii);

        token = getToken();
        if (MiniRETokenType.IN != token.getType())
            throw new ParseException("statement: Found token \"" + token.getTokenString() + "\"; expected \"in\"", -21);

        return ascii;
    }

    /* Handles statements of the form "ID = <exp>", "ID = # <exp>", and "ID = maxfreqstring(ID)". */
    private void handleIdentifierToken() throws ParseException
    {
        MiniREToken id = token;
        token = getToken();
        if (MiniRETokenType.ASSIGN != token.getType())
            throw new ParseException("statement: Found token \"" + token.getTokenString() + "\"; expected \"=\"", -22);
        current.setNext(new ASTNodeImpl(token, ASTNodeType.ASSIGN));
        current = current.getNext();
        current.setChild(new ASTNodeImpl(id, ASTNodeType.IDENTIFIER));

        token = peekToken();
        ASTNode next;
        if (MiniRETokenType.COUNT == token.getType()) {
            next = new ASTNodeImpl(getToken(), ASTNodeType.COUNT);
            current.getChild().setNext(next);
            exp(next);
        } else if (MiniRETokenType.MAX_FREQ_STRING == token.getType()) {
            next = new ASTNodeImpl(getToken(), ASTNodeType.MAX_FREQ_STRING);
            current.getChild().setNext(next);
            token = getToken();
            if (MiniRETokenType.LEFT_PAREN != token.getType())
                throw new ParseException("statement: Found token \"" + token.getTokenString() + "\"; expected \"(\"", -23);
            exp(next);
            token = getToken();
            if (MiniRETokenType.RIGHT_PAREN != token.getType())
                throw new ParseException("statement: Found token \"" + token.getTokenString() + "\"; expected \")\"", -24);
        } else
            exp(current);
    }

    /* Returns (and consumes) the next token in the buffer. */
    private MiniREToken getToken()
    {
        return (MiniREToken)tokenizer.getNextToken();
    }

    /* Returns (but does not consume) the next token in the buffer. */
    private MiniREToken peekToken()
    {
        return (MiniREToken)tokenizer.peek();
    }

}
