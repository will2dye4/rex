package edu.gatech.cs3240.project1;

import java.io.File;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of the TableWalker interface.
 * @author William Dye
 * @author Taylor Holden
 * @author Matt Hooper
 */
public class TableWalkerImpl implements TableWalker
{
	protected DFA dfa;
    protected CharBuffer buffer;
    protected Map<String, Set<State>> map;
    protected SourceToken current;
    protected boolean paused;
	protected boolean waiting;
    protected char ch;

    /**
     * Creates a new <code>TableWalkerImpl</code> using the specified DFA, reading from
     * the specified file, and using the provided mapping from identifier to accepting states.
     * @param automaton the DFA to be used by the table walker
     * @param file the file for the table walker to read
     * @param mapping a map from identifiers to sets of accepting states
     */
	public TableWalkerImpl(DFA automaton, File file, Map<String, Set<State>> mapping)
	{
		dfa = automaton;
        buffer = new CharBuffer(file);
        map = mapping;
        clearState();
	}

    @Override
    public boolean hasNextToken()
    {
        return (!"<EOF>".equals(peek().getTokenType()));
    }

    @Override
    public SourceToken getNextToken()
    {
        return next(false);
    }

    /* Resets the current token and boolean flags. */
    private void clearState()
    {
        paused = true;
        waiting = false;
        current = null;
        ch = '\0';
    }

    /* Returns (but does not consume) the next token in the file. */
    protected SourceToken peek()
    {
        return next(true);
    }

    /* Helper method for getNextToken() and peek(). */
    protected SourceToken next(boolean peek)
    {
        if (!paused || current == null)
            current = readToken();
        paused = peek;
        return current;
    }

    /* Helper method for next(). Handles reading from the buffer and generating new tokens. */
    protected SourceToken readToken()
    {
        SourceToken token;
        if (waiting)
            waiting = false;
        else
            ch = buffer.getNextChar();
        if (' ' == ch || '\t' == ch || '\r' == ch || '\f' == ch)
            token = readToken();
        else if ('\n' == ch) {
            if (buffer.advance())
                token = readToken();
            else
                token = new SourceToken("<EOF>", null);
        } else {
            State state = dfa.getStartState();
            StringBuilder builder = new StringBuilder();
            while (state.hasTransition(ch)) {
                builder.append(ch);
                state = state.getNextState(ch);
                ch = buffer.getNextChar();
            }
            if (state.isAccepting()) {
                waiting = true;
                token = new SourceToken(getIdentifierFromState(state), builder.toString());
            } else {
                builder.append(ch);
                token = new SourceToken("<INVALID>", builder.toString());
            }
        }
        return token;
    }

    /* Returns the identifier corresponding to the provided accepting state. */
    protected String getIdentifierFromState(State state)
    {
        String id = "<INVALID>";
        for (String string : map.keySet()) {
            if (map.get(string).contains(state)) {
                id = string;
                break;
            }
        }
        return id;
    }
}