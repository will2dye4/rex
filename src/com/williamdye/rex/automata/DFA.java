package com.williamdye.rex.automata;

/**
 * Represents a deterministic finite-state automaton.
 * @author TJ Harrison
 */
public interface DFA extends FiniteStateAutomaton
{

	/**
     * A DFA's transition function. Given a state <code>from</code>
     * and a character <code>c</code>, returns the state to which
     * a DFA moves when starting in <code>from</code> and reading a <code>c</code>.
     * @param c the character on which to transition
     * @param from the state from which to transition
     * @return the state to which the DFA transitions
     */
    public State transition(char c, State from);

}
