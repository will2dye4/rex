package com.williamdye.rex.project1;

import java.util.*;

/**
 * Contains a class method to convert an NFA to an equivalent DFA.
 * @author William Dye
 * @author TJ Harrison
 * @author Taylor Holden
 */
public class NFAToDFAConverter
{

    private NFAToDFAConverter() { /* prevent instantiation */ }

    /**
     * Converts the specified <code>nfa</code> into an equivalent DFA.
     * Updates the mapping of <code>identifiers</code> to correspond to the generated DFA.
     * @param nfa the NFA to convert to a DFA
     * @param identifiers a mapping from identifiers (token classes) to sets of states
     * @return the DFA generated from the NFA
     */
    public static DFA convert(NFA nfa, Map<String, Set<State>> identifiers)
    {
        /* set up states and transitions */
        DFA dfa = new DFAImpl("<COMBINED>");
        Set<State> startStates = epsilon(nfa.getStartState());
        Map<Set<State>, State> map = new LinkedHashMap<Set<State>, State>();
        map.put(startStates, dfa.getStartState());
        Stack<Set<State>> toConsider = new Stack<Set<State>>();
        toConsider.push(startStates);
        while (toConsider.size() > 0) {
            Set<State> states = toConsider.pop();
            State dfaState = map.get(states);
            Set<Character> chars = new LinkedHashSet<Character>();
            for (State state : states)
                chars.addAll(state.getTransitionChars());
            chars.remove(NFAImpl.EPSILON);
            for (char ch : chars) {
                Set<State> next = new LinkedHashSet<State>();
                for (State state : states) {
                    if (state.hasTransition(ch)) {
                        for (State s : state.getNextStates(ch))
                            next.addAll(epsilon(s));
                    }
                }
                State nextState = map.get(next);
                if (nextState == null) {
                    nextState = dfa.addState(false);
                    map.put(next, nextState);
                    toConsider.push(next);
                }
                dfaState.addTransition(ch, nextState);
            }
        }
        /* update accepting states and identifier mapping */
        Map<String, Set<State>> dfaIdentifiers = new LinkedHashMap<String, Set<State>>();
        for (String id : identifiers.keySet()) {
            Set<State> stateSet = identifiers.get(id);
            for (Set<State> set : map.keySet()) {
                for (State s : stateSet) {
                    boolean found = false;
                    for (State tmp : set) {
                        if (s.equals(tmp)) {
                            found = true;
                            break;
                        }
                    }
                    if (found) {
                        State accept = map.get(set);
                        accept.setAccepting(true);
                        Set<State> dfaStates = dfaIdentifiers.get(id);
                        if (dfaStates == null) {
                            dfaStates = new LinkedHashSet<State>();
                            dfaStates.add(accept);
                            dfaIdentifiers.put(id, dfaStates);
                        } else
                            dfaStates.add(accept);
                    }
                }
            }
        }
        identifiers.clear();
        for (String id : dfaIdentifiers.keySet())
            identifiers.put(id, dfaIdentifiers.get(id));
        return dfa;
    }

    /* Returns the epsilon closure for the specified state. */
    private static Set<State> epsilon(State state)
    {
        return epsilon(state, new LinkedHashSet<State>());
    }

    /* Helper for the epsilon() method. */
    private static Set<State> epsilon(State state, Set<State> result)
    {
        result.add(state);
        if (state.hasTransition(NFAImpl.EPSILON)) {
            Set<State> next = state.getNextStates(NFAImpl.EPSILON);
            for (State s : next) {
                if (!result.contains(s))
                    result = epsilon(s, result);
            }
        }
        return result;
    }

}
