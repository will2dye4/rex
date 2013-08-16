package com.williamdye.rex.project1;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Stack;
import java.text.ParseException;

/**
 * Implementation of the RecursiveDescentParser interface.
 * @author William Dye
 */
public class RecursiveDescentParserImpl implements RecursiveDescentParser
{

    /* The minimum value for ASCII printable characters (32). */
    public static final char ASCII_MIN = 0x20;

    /* The maximum value for ASCII printable characters (126). */
    public static final char ASCII_MAX = 0x7e;

    /* An array of RE_CHAR characters that may legally follow a backslash ("\"). */
    private static final char[] RE_ESCAPE_CHARS = {' ', '\\', '*', '+', '?', '$', '|', '[', ']', '(', ')', '.', '\'', '\"'};

    /* An array of CLS_CHAR characters that may legally follow a backslash. */
    private static final char[] CLS_ESCAPE_CHARS = {'\\', '^', '-', '[', ']'};

    private Tokenizer tokenizer;
    private Set<NFA> identifiers;
    private Stack<NFA> stack;
    private State current, prev;

    /**
     * Creates a new <code>RecursiveDescentParserImpl</code> that reads from the provided tokenizer
     * and is aware of the identifiers of the NFAs in the specified array list.
     * @param t the tokenizer for the recursive-descent parser to use
     * @param nfas a set of NFAs representing previously-defined identifiers
     */
    public RecursiveDescentParserImpl(Tokenizer t, Set<NFA> nfas)
    {
        tokenizer = t;
        identifiers = nfas;
        stack = new Stack<NFA>();
        stack.push(new NFAImpl());
        current = stack.peek().getStartState();
        prev = null;
    }

    @Override
    public NFA parse() throws ParseException
    {
        reg_ex();
        return stack.pop();
    }

    /* <reg-ex> => <rexp> */
    private void reg_ex() throws ParseException
    {
        rexp();
    }

    /* <rexp> => <rexp1> <rexp'> */
    private void rexp() throws ParseException
    {
        rexp1();
        rexp_();
    }

    /* <rexp1> => <rexp2> <rexp1'> */
    private void rexp1() throws ParseException
    {
        rexp2();
        rexp1_();
    }

    /* <rexp'> => UNION <rexp1> <rexp'> | epsilon */
    private void rexp_() throws ParseException
    {
        if (TokenType.UNION == tokenizer.peek().getTokenType()) {
            tokenizer.consume();   /* consume UNION token */
            handleUnion();
            rexp1();
            rexp_();
        }
    }

    /* <rexp2> => (<rexp>) <rexp2-tail> | RE_CHAR <rexp2-tail> | <rexp3> */
    private void rexp2() throws ParseException
    {
        Token token = tokenizer.peek();
        if (TokenType.LEFT_PAREN == token.getTokenType()) {
            tokenizer.consume();   /* consume LEFT_PAREN token */
            handleNestedRegEx();
            token = tokenizer.peek();
            if (TokenType.RIGHT_PAREN != token.getTokenType())
                throw new ParseException("Opening parenthesis without matching closing parenthesis", -2);
            tokenizer.consume();   /* consume RIGHT_PAREN token */
            rexp2_tail();
        } else if (TokenType.LITERAL == token.getTokenType()) {
            token = tokenizer.getNextToken();
            if (!isRegExToken(token))
                throw new ParseException("Illegal RE_CHAR in rexp2", -3);
            handleConcatenation(getCharFromTokenString(token.getTokenString()));
            rexp2_tail();
        } else
            rexp3();
    }

    /* <rexp1'> => <rexp2> <rexp1'> | epsilon */
    private void rexp1_() throws ParseException
    {
        TokenType type = tokenizer.peek().getTokenType();
        if (TokenType.LEFT_PAREN == type || TokenType.LITERAL == type || TokenType.WILDCARD == type ||
                TokenType.LEFT_BRACKET == type || TokenType.IDENTIFIER == type) {
            rexp2();
            rexp1_();
        }
    }

    /* <rexp2-tail> => * | + | epsilon */
    private void rexp2_tail()
    {
        TokenType type = tokenizer.peek().getTokenType();
        if (TokenType.KLEENE_STAR == type) {
            tokenizer.consume();   /* consume KLEENE_STAR token */
            handleKleeneStar();
        } else if (TokenType.PLUS == type) {
            tokenizer.consume();   /* consume PLUS token */
            handlePlus();
        }
    }

    /* <rexp3> => <char-class> | epsilon */
    private void rexp3() throws ParseException
    {
        TokenType type = tokenizer.peek().getTokenType();
        if (TokenType.WILDCARD == type || TokenType.LEFT_BRACKET == type || TokenType.IDENTIFIER == type)
            char_class();
    }

    /* <char-class> => . | [<char-class1> | <defined-class> */
    private void char_class() throws ParseException
    {
        Token token = tokenizer.getNextToken();
        if (TokenType.WILDCARD == token.getTokenType())
            handleWildcard();
        else if (TokenType.LEFT_BRACKET == token.getTokenType())
            char_class1();
        else if (TokenType.IDENTIFIER == token.getTokenType())
            handleDefinedClass(token.getTokenString());
        else
            throw new ParseException("Illegal character in char_class", -4);
    }

    /* <char-class1> => <char-set-list> | <exclude-set> */
    private void char_class1() throws ParseException
    {
        TokenType type = tokenizer.peek().getTokenType();
        Set<Character> chars;
        if (TokenType.LITERAL == type || TokenType.RIGHT_BRACKET == type)
            chars = char_set_list(new LinkedHashSet<Character>());
        else if (TokenType.CARET == type)
            chars = exclude_set();
        else
            throw new ParseException("Illegal character in char_class1", -5);
        handleConcatenation(chars);
    }

    /* <char-set-list> => <char-set> <char-set-list> | ] */
    private Set<Character> char_set_list(Set<Character> chars) throws ParseException
    {
        TokenType type = tokenizer.peek().getTokenType();
        Set<Character> result = chars;
        if (TokenType.LITERAL == type) {
            result = char_set(chars);
            result = char_set_list(result);
        } else if (TokenType.RIGHT_BRACKET == type)
            tokenizer.consume();    /* consume right bracket */
        else
            throw new ParseException("Illegal character in char_set_list", -6);
        return result;
    }

    /* <exclude-set> => ^ <char-set>] IN <exclude-set-tail> */
    private Set<Character> exclude_set() throws ParseException
    {
        if (TokenType.CARET != tokenizer.getNextToken().getTokenType())
            throw new ParseException("Illegal character in exclude_set", -7);
        Set<Character> excludedChars = char_set(new LinkedHashSet<Character>());

        if (TokenType.RIGHT_BRACKET != tokenizer.getNextToken().getTokenType())
            throw new ParseException("Illegal character in exclude_set", -8);
        if (TokenType.IN != tokenizer.getNextToken().getTokenType())
            throw new ParseException("Illegal character in exclude_set", -9);

        Set<Character> classChars = exclude_set_tail();
        Iterator<Character> iterator = classChars.iterator();
        while (iterator.hasNext()) {
            if (excludedChars.contains(iterator.next()))
                iterator.remove();
        }
        return classChars;
    }

    /* <char-set> => CLS_CHAR <char-set-tail> */
    private Set<Character> char_set(Set<Character> chars) throws ParseException
    {
        Token token = tokenizer.getNextToken();
        if (!isClassToken(token))
            throw new ParseException("Illegal CLS_CHAR in char_set " + token.getTokenType(), -10);
        return char_set_tail(getCharFromTokenString(token.getTokenString()), chars);
    }

    /* <char-set-tail> => - CLS_CHAR | epsilon */
    private Set<Character> char_set_tail(char first, Set<Character> chars) throws ParseException
    {
        TokenType type = tokenizer.peek().getTokenType();
        if (TokenType.RANGE == type) {
            tokenizer.consume();   /* consume RANGE token */
            Token token = tokenizer.getNextToken();
            if (!isClassToken(token))
                throw new ParseException("Illegal CLS_CHAR in char_set_tail " + token.getTokenType(), -11);
            char last = getCharFromTokenString(token.getTokenString());
            for (char c = first; c <= last; c++)
                chars.add(c);
        }
        return chars;
    }

    /* <exclude-set-tail> => [<char-set>] | <defined-class> */
    private Set<Character> exclude_set_tail() throws ParseException
    {
        Token token = tokenizer.getNextToken();
        Set<Character> result;
        if (TokenType.LEFT_BRACKET == token.getTokenType()) {
            result = char_set(new LinkedHashSet<Character>());
            token = tokenizer.peek();
            if (TokenType.RIGHT_BRACKET != token.getTokenType())
                throw new ParseException("Opening bracket without matching closing bracket", -12);
            tokenizer.consume();   /* consume RIGHT_BRACKET token */
        } else if (TokenType.IDENTIFIER == token.getTokenType()) {
            result = getCharsFromDefinedClass(token.getTokenString());
        } else
            throw new ParseException("Illegal character in exclude_set_tail", -13);
        return result;
    }

    /* Returns true if token represents a valid RE_CHAR, false otherwise. */
    private static boolean isRegExToken(Token token)
    {
        boolean valid = (TokenType.LITERAL == token.getTokenType());
        if (valid) {
            char literal = token.getTokenString().charAt(0);
            if ('\\' == literal) {
                literal = token.getTokenString().charAt(1);
                for (char c : RE_ESCAPE_CHARS) {
                    if (c == literal) {
                        valid = true;
                        break;
                    }
                }
            } else
                valid = (literal >= ASCII_MIN && literal <= ASCII_MAX && literal != '?' && literal != '\'' && literal != '\"');
        }
        return valid;
    }

    /* Returns true if token represents a valid CLS_CHAR, false otherwise. */
    private static boolean isClassToken(Token token)
    {
        boolean valid = (TokenType.LITERAL == token.getTokenType());
        if (valid) {
            char literal = token.getTokenString().charAt(0);
            if ('\\' == literal) {
                literal = token.getTokenString().charAt(1);
                for (char c : CLS_ESCAPE_CHARS) {
                    if (c == literal) {
                        valid = true;
                        break;
                    }
                }
            } else
                valid = (literal >= ASCII_MIN && literal <= ASCII_MAX);
        }
        return valid;
    }

    /* Returns the literal character in string, handling the case that the character is escaped. */
    private static char getCharFromTokenString(String string)
    {
        return (string.charAt(0) == '\\' ? string.charAt(1) : string.charAt(0));
    }

    /* Adds a new state to the NFA with an epsilon transition from the start state. */
    private void handleUnion()
    {
        NFA nfa = stack.peek();
        State start = nfa.getStartState();
        State newState = nfa.addState(true);
        start.addTransition(NFAImpl.EPSILON, newState);
        prev = current;
        current = newState;
    }

    /* Adds a new state to the NFA with a transition from the current state on the specified character. */
    private void handleConcatenation(char c)
    {
        Set<Character> list = new LinkedHashSet<Character>();
        list.add(c);
        handleConcatenation(list);
    }

    /* Adds a new state to the NFA with a transition from the current state on all specified characters. */
    private void handleConcatenation(Set<Character> chars)
    {
        if (chars.size() == 0) return;
        State newState = stack.peek().addState(true);
        if (current.isAccepting())
            current.setAccepting(false);
        for (Character ch : chars)
            current.addTransition(ch, newState);
        prev = current;
        current = newState;
    }

    /* Adds a new state to the NFA with a transition from the current state on any valid character. */
    private void handleWildcard()
    {
        State newState = stack.peek().addState(true);
        if (current.isAccepting())
            current.setAccepting(false);
        for (char i = ASCII_MIN; i <= ASCII_MAX; i++)
            current.addTransition(i, newState);
        prev = current;
        current = newState;
    }

    /* Adds a new state to the NFA with a transition from the current state on all characters in the specified class. */
    private void handleDefinedClass(String name) throws ParseException
    {
        Set<Character> alphabet = getCharsFromDefinedClass(name);
        handleConcatenation(alphabet);
    }

    /* Allows zero or more repetitions of the portion of the NFA between prev and current. */
    private void handleKleeneStar()
    {
        prev.addTransition(NFAImpl.EPSILON, current);
        current.addTransition(NFAImpl.EPSILON, prev);
    }

    /* Allows one or more repetitions of the portion of the NFA between prev and current. */
    private void handlePlus()
    {
        current.addTransition(NFAImpl.EPSILON, prev);
    }

    /* Parses a nested regular expression and concatenates the resulting NFA at the current state. */
    private void handleNestedRegEx() throws ParseException
    {
        stack.push(new NFAImpl());  /* push new NFA to parse nested regular expression */
        State oldCurrent = current;
        prev = current;
        current = stack.peek().getStartState();
        rexp();
        NFA result = stack.pop();
        stack.peek().addAllStates(result.getStates());
        oldCurrent.addTransition(NFAImpl.EPSILON, result.getStartState());
        if (oldCurrent.isAccepting())
            oldCurrent.setAccepting(false);
        State end = stack.peek().addState(true);
        for (State state : result.getStates()) {
            if (state.isAccepting()) {
                state.setAccepting(false);
                state.addTransition(NFAImpl.EPSILON, end);
            }
        }
        prev = result.getStartState();
        current = end;
    }

    /* Returns a list of the characters contained in the character class with the specified name. */
    private Set<Character> getCharsFromDefinedClass(String name) throws ParseException
    {
        NFA identifier = null;
        for (NFA nfa : identifiers) {
            if (nfa.getIdentifier().equals(name)) {
                identifier = nfa;
                break;
            }
        }
        if (identifier == null)
            throw new ParseException("Reference to undefined character class", -14);
        return identifier.getAlphabet();
    }

}
