package com.williamdye.rex.automata;

import java.util.Set;

/**
 * Represents a generic finite-state automaton.
 * This interface defines the methods that all NFAs and DFAs must implement.
 * @author William Dye
 * @author TJ Harrison
 * @author Matt Hooper
 */
public interface FiniteStateAutomaton
{

    /**
     * Accessor for an automaton's identifier.
     * @return the identifier (name) associated with the automaton
     */
    public String getIdentifier();

    /**
     * Mutator for an automaton's identifier.
     * @param identifier the new identifier (name) for the automaton
     */
    public void setIdentifier(String identifier);

    /**
     * Accessor for the number of states an automaton contains.
     * @return the number of states the automaton contains
     */
    @SuppressWarnings("unused")
    public int getNumStates();

    /**
     * Accessor for an automaton's starting (initial) state.
     * @return the initial state of the automaton
     */
    public State getStartState();

    /**
     * Accessor for all characters (excluding epsilon) for which
     * an automaton has at least one transition.
     * @return the set of characters for which the automaton has transitions
     */
    public Set<Character> getAlphabet();
    
    /**
     * Accessor for an automaton's accepting (terminal) states.
     * @return the accepting states of the automaton
     */
    public Set<State> getAcceptingStates();
    
    /**
     * Returns <code>true</code> if an automaton defines a character class.
     * @return <code>true</code> if the automaton is a character class, <code>false</code> otherwise
     */
    public boolean isCharClass();

    /**
     * Mutator for an automaton's character-class flag.
     * @param charClass whether to flag the automaton as a character class
     */
    public void setCharClass(boolean charClass);

    /**
     * Returns <code>true</code> if an automaton is deterministic.
     * @return <code>true</code> if the automaton is a DFA, <code>false</code> otherwise
     */
    @SuppressWarnings("unused")
    public boolean isDeterministic();

    /**
     * Adds a new state to an automaton's collection of states.
     * @param isAccepting whether the new state is an accepting (final) state
     * @return the state that was added, or <code>null</code> if an error occurred
     */
    public State addState(boolean isAccepting);

    /**
     * Adds all states in <code>states</code> to an automaton, if the states
     * are not already part of the automaton.
     * @param states the list of states to add
     */
    public void addAllStates(Set<State> states);

}

