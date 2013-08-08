package edu.gatech.cs3240.project1;

import java.util.Set;

/**
 * Represents a non-deterministic finite-state automaton.
 * @author William Dye
 */
public interface NFA extends FiniteStateAutomaton
{

    /**
     * An NFA's transition function. Given a state <code>from</code>
     * and a character <code>c</code>, returns the set of states to which
     * an NFA may move when starting in <code>from</code> and reading a <code>c</code>.
     * @param c the character on which to transition
     * @param from the state from which to transition
     * @return the set of states to which the NFA may legally transition
     */
    public Set<State> transition(char c, State from);

    /**
     * Accessor for the list of all states in an NFA.
     * @return the list of states in the NFA
     */
    public Set<State> getStates();

}
