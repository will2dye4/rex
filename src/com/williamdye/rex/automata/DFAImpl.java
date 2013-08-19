package com.williamdye.rex.automata;

import java.util.*;

/**
 * Implementation of the DFA interface.
 * @author William Dye
 * @author TJ Harrison
 * @author Taylor Holden
 * @author Ryan McCaffrey
 */
public class DFAImpl extends FiniteStateAutomatonImpl implements DFA
{

	private State start;
    private Set<State> states;

    /**
     * Creates a <code>DFAImpl</code> with no identifier.
     */
    public DFAImpl()
    {
        this(null, false);
    }

    /**
     * Creates a <code>DFAImpl</code> with the specified identifier.
     * @param id the identifier for the DFA
     */
    public DFAImpl(String id)
    {
        this(id, false);
    }

    /**
     * Creates a <code>DFAImpl</code> with the specified
     * <code>id</code> and <code>isChar</code> flag.
     * @param id the identifier for the DFA
     * @param isChar whether the DFA describes a character class
     */
	public DFAImpl(String id, boolean isChar)
	{
		super(id, isChar, true);
        start = new DFAState();
        states = new LinkedHashSet<State>();
        states.add(start);
	}

	@Override
	public int getNumStates() {
		return states.size();
	}

	@Override
	public State getStartState() {
		return start;
	}

	@Override
	public Set<State> getAcceptingStates() {
		Set<State> accepting = new LinkedHashSet<State>();
        for (State state : states) {
            if (state.isAccepting())
                accepting.add(state);
        }
        return accepting;
	}

    @Override
    public Set<Character> getAlphabet()
    {
        Set<Character> alphabet = new LinkedHashSet<Character>();
        for (State state : states)
            alphabet.addAll(state.getTransitionChars());
        return alphabet;
    }

    @Override
    public void addAllStates(Set<State> newStates)
    {
        states.addAll(newStates);
    }

	@Override
	public State transition(char c, State from) {
        if (from == null)
            from = start;
        return from.getNextState(c);
	}
	
    @Override
	public State addState(boolean isAccepting) {
		State newState = new DFAState();
        newState.setAccepting(isAccepting);
		return (states.add(newState) ? newState : null);
	}


    /**
     * DFA-specific implementation of the State interface.
     */
	protected class DFAState implements State
    {
		
		private boolean accepting;
        private LinkedHashMap<Character, State> transitions;

        /**
         * Creates a new, non-accepting <code>DFAState</code>.
         */
		public DFAState()
		{
			accepting = false;
			transitions = new LinkedHashMap<Character, State>();
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
            return transitions.containsKey(c) && transitions.get(c) != null;
		}

		@Override
		public State getNextState(char c)
        {
            return transitions.get(c);
		}

		@Override
		public Set<State> getNextStates(char c)
        {
			throw new IllegalStateException("getNextStates() called on an DFA; use getNextState() instead");
		}

		@Override
		public boolean addTransition(char c, State to)
        {
			boolean success = false;
            if (!transitions.containsKey(c)) {
                transitions.put(c, to);
                success = true;
            }
            return success;
		}

		@Override
		public boolean setTransition(char c, Set<State> states)
        {
			boolean success = false;
            if (states.size() == 1)
                success = addTransition(c, states.iterator().next());
            return success;
		}

		@Override
		public Set<Character> getTransitionChars()
        {
			return new LinkedHashSet<Character>(transitions.keySet());
		}

	}   /* end nested DFAState class */

}   /* end DFAImpl class */