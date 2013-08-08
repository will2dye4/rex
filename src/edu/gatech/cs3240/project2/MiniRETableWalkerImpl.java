package edu.gatech.cs3240.project2;

import java.io.File;

import edu.gatech.cs3240.project1.DFA;
import edu.gatech.cs3240.project1.SourceToken;
import edu.gatech.cs3240.project1.State;
import edu.gatech.cs3240.project1.TableWalkerImpl;

/**
 * MiniRE-specific implementation of the <code>TableWalker</code> interface.
 * @author William Dye
 * @author TJ Harrison
 * @author Ryan McCaffrey
 */
public class MiniRETableWalkerImpl extends TableWalkerImpl
{

    /** The token type for end-of-file (EOF) tokens. */
    public static final String EOF_TOKEN = "<EOF>";
    /** The token type for invalid tokens. */
    public static final String INVALID_TOKEN = "<INVALID>";
    /** The token type for match tokens. */
    public static final String MATCH_TOKEN = "<MATCH>";

    /**
     * Creates a new <code>MiniRETableWalkerImpl</code> using the specified DFA and reading from the specified file.
     * @param automaton the DFA to be used by the table walker
     * @param file the file for the table walker to read
     */
	public MiniRETableWalkerImpl(DFA automaton, File file)
	{
		super(automaton, file, null);
	}

    /* Helper method for next(). Handles reading from the buffer and generating new tokens. */
    protected SourceToken readToken()
    {
        SourceToken token;
        String longestMatch = "";
        StringBuilder builder = new StringBuilder();
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
                token = new SourceToken(EOF_TOKEN, null);
        } else {
            State state = dfa.getStartState();
            while (state.hasTransition(ch)) {
                builder.append(ch);
                state = state.getNextState(ch);
                ch = buffer.getNextChar();
                if(state.isAccepting()){
                	longestMatch = builder.toString();
                }
            }
            if (state.isAccepting()) {
                waiting = true;
                token = new SourceToken(MATCH_TOKEN, builder.toString());
            } else {
                builder.append(ch);
                token = new SourceToken(INVALID_TOKEN, builder.toString());
            }
        }
        if (INVALID_TOKEN.equals(token.getTokenType()) && longestMatch.length() > 0) {
            token = new SourceToken(MATCH_TOKEN, longestMatch);
            buffer.setIndex(buffer.getIndex() - (builder.toString().length() - longestMatch.length() - 1));
        }
        return token;
    }

    /**
     * Returns the current index of the table walker's internal buffer.
     * @return the buffer's index (position within the current line)
     */
    public int getBufferIndex()
	{
		return buffer.getIndex();
	}

    /**
     * Returns the current line number of the table walker's internal buffer.
     * @return the buffer's line number (1-indexed)
     */
    public int getBufferLineNumber()
    {
        return buffer.getLineNumber();
    }
}