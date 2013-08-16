package com.williamdye.rex.project1;

import java.util.Set;

/**
 * Represents a generic state in a finite-state automaton.
 * @author William Dye
 */
public interface State
{

    /**
     * Returns <code>true</code> if a state is an accepting (final) state.
     * @return <code>true</code> if the state is accepting, <code>false</code> otherwise
     */
    public boolean isAccepting();

    /**
     * Mutator for a state's accepting flag.
     * @param accepting whether the state is an accepting (final) state
     */
    public void setAccepting(boolean accepting);

    /**
     * Returns <code>true</code> if a state has any transitions on character <code>c</code>.
     * @param c the character to test for transitions
     * @return <code>true</code> if the state has at least one transition on <code>c</code>, <code>false</code> otherwise
     */
    public boolean hasTransition(char c);

    /**
     * Returns the single state to which another state transitions on reading the character <code>c</code>.
     * @param c the character on which to transition
     * @return the single state to which this state transitions upon reading a <code>c</code>
     */
    public State getNextState(char c);

    /**
     * Returns the set of states to which another state may transition on reading the character <code>c</code>.
     * @param c the character on which to transition
     * @return the set of all states to which this state may transition upon reading a <code>c</code>
     */
    public Set<State> getNextStates(char c);

    /**
     * Adds a transition from this state to state <code>to</code> on character <code>c</code>.
     * @param c the character on which to transition
     * @param to the state to which to transition
     * @return <code>true</code> if the transition was added successfully, <code>false</code> otherwise
     */
    public boolean addTransition(char c, State to);

    /**
     * Adds transitions to all the specified <code>states</code> on character <code>c</code>,
     * simultaneously removing any transitions that may have existed previously.
     * @param c the character on which to transition
     * @param states the states to which to transition
     * @return <code>true</code> if the transitions were added successfully, <code>false</code> otherwise
     */
    public boolean setTransition(char c, Set<State> states);

    /**
     * Returns the set of characters for which a state has transitions.
     * @return the set of characters on which the state may legally transition
     */
    public Set<Character> getTransitionChars();

}

