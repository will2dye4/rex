package com.williamdye.rex.project1;

/**
 * Abstract implementation of the FiniteStateAutomaton interface.
 * This class may only be instantiated through concrete subclasses.
 * @author William Dye
 */
public abstract class FiniteStateAutomatonImpl implements FiniteStateAutomaton
{

    private String identifier;
    private boolean charClass;
    private boolean deterministic;

    /**
     * Constructs a new <code>FiniteStateAutomatonImpl</code> with the specified identifier
     * and character-class/deterministic flags.
     * @param id the identifier for the automaton
     * @param isChar whether the automaton describes a character class
     * @param isDeterministic whether the automaton is deterministic
     */
    protected FiniteStateAutomatonImpl(String id, boolean isChar, boolean isDeterministic)
    {
        identifier = id;
        charClass = isChar;
        deterministic = isDeterministic;
    }

    @Override
    public String getIdentifier()
    {
        return identifier;
    }

    @Override
    public void setIdentifier(String id)
    {
        identifier = id;
    }

    @Override
    public boolean isCharClass()
    {
        return charClass;
    }

    @Override
    public void setCharClass(boolean isChar)
    {
        charClass = isChar;
    }

    @Override
    public boolean isDeterministic()
    {
        return deterministic;
    }

}
