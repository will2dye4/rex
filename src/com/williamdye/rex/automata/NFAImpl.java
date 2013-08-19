package com.williamdye.rex.automata;

import java.util.*;

/**
 * Implementation of the NFA interface.
 * @author William Dye
 * @author TJ Harrison
 */
public class NFAImpl extends FiniteStateAutomatonImpl implements NFA
{

    /** The epsilon (null) character. */
    public static final char EPSILON = '\0';

    private State start;
    private Set<State> states;

    /**
     * Default constructor. Creates an <code>NFAImpl</code> with <code>null</code> identifier
     * and character-class flag set to <code>true</code>.
     */
    public NFAImpl()
    {
        this(null, true);
    }

    /**
     * Creates an <code>NFAImpl</code> with the specified
     * <code>id</code> and <code>isCharClass</code> flag.
     * @param id the identifier for the NFA
     * @param isCharClass whether the NFA describes a character class
     */
    public NFAImpl(String id, boolean isCharClass)
    {
        super(id, isCharClass, false);
        start = new NFAState(false);
        states = new LinkedHashSet<State>();
        states.add(start);
    }

    @Override
    public int getNumStates()
    {
        return states.size();
    }

    @Override
    public State getStartState()
    {
        return start;
    }

    @Override
    public Set<State> transition(char c, State from)
    {
        if (from == null)
            from = start;
        return from.getNextStates(c);
    }

    @Override
    public State addState(boolean isAccepting)
    {
        State state = new NFAState(isAccepting);
        return (states.add(state) ? state : null);
    }

    @Override
    public Set<State> getStates()
    {
        return states;
    }

    @Override
    public Set<State> getAcceptingStates()
    {
        Set<State> accepting = new LinkedHashSet<State>();
        for (State state : states) {
            if (state.isAccepting())
                accepting.add(state);
        }
        return accepting;
    }

    @Override
    public void addAllStates(Set<State> newStates)
    {
        states.addAll(newStates);
    }

    @Override
    public Set<Character> getAlphabet()
    {
        Set<Character> alphabet = new LinkedHashSet<Character>();
        for (State state : states)
                alphabet.addAll(state.getTransitionChars());
        alphabet.remove(NFAImpl.EPSILON);
        return alphabet;
    }


    /**
     * NFA-specific implementation of the State interface.
     * @author William Dye
     */
    protected class NFAState implements State
    {
        private boolean accepting;
        private LinkedHashMap<Character, Set<State>> transitions;

        /**
         * Default constructor. Creates a new, non-accepting <code>NFAState</code>.
         */
        @SuppressWarnings("unused")
        protected NFAState()
        {
            this(false);
        }

        /**
         * Creates a new <code>NFAState</code> with the specified <code>isAccepting</code> flag.
         * @param isAccepting whether the state is an accepting (final) state
         */
        protected NFAState(boolean isAccepting)
        {
            accepting = isAccepting;
            transitions = new LinkedHashMap<Character, Set<State>>();
        }

        @Override
        public boolean isAccepting()
        {
            return accepting;
        }

        @Override
        public void setAccepting(boolean accept)
        {
            accepting = accept;
        }

        @Override
        public boolean hasTransition(char c)
        {
            return transitions.containsKey(c) && transitions.get(c).size() > 0;
        }

        @Override
        public State getNextState(char c)
        {
            throw new IllegalStateException("getNextState() called on an NFA; use getNextStates() instead");
        }

        @Override
        public Set<State> getNextStates(char c)
        {
            return transitions.get(c);
        }

        @Override
        public boolean addTransition(char c, State to)
        {
            boolean success = false;
            Set<State> nextStates;
            if (transitions.containsKey(c)) {
                nextStates = transitions.get(c);
                success = nextStates.add(to);
            } else {
                nextStates = new LinkedHashSet<State>();
                if (nextStates.add(to)) {
                    transitions.put(c, nextStates);
                    success = true;
                }
            }
            return success;
        }

        @Override
        public boolean setTransition(char c, Set<State> states)
        {
            transitions.put(c, states);
            return true;
        }

        @Override
        public Set<Character> getTransitionChars()
        {
            return new LinkedHashSet<Character>(transitions.keySet());
        }

    }   /* end nested NFAState class */

}   /* end NFAImpl class */
