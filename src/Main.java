import java.util.*;

class DFA {
    private final Map<String, Map<String, String[]>> transitions;

    public DFA() {
        transitions = new HashMap<>();
        
        addTransition(null, null, "0", new String[]{"0", "0"});
        addTransition(null, null, "1", new String[]{"1", "1"});
        
        addTransition("0", "0", "0", new String[]{"0", "0"});
        addTransition("0", "0", "1", new String[]{"0", "1"});
        addTransition("1", "1", "0", new String[]{"1", "0"});
        addTransition("1", "1", "1", new String[]{"1", "1"});
        
        addTransition("0", "1", "0", new String[]{"0", "0"});
        addTransition("0", "1", "1", new String[]{"0", "1"});
        addTransition("1", "0", "0", new String[]{"1", "0"});
        addTransition("1", "0", "1", new String[]{"1", "1"});
    }
    
    private void addTransition(String firstChar, String lastChar, String input, String[] nextState) {
        String key = firstChar + "," + lastChar;
        transitions.computeIfAbsent(key, k -> new HashMap<>()).put(input, nextState);
    }

    public boolean accepts(String inputChain) {
        if (inputChain.isEmpty()) {
            return true;
        }
        
        String firstChar = null;
        String lastChar = null;

        for (char c : inputChain.toCharArray()) {
            String symbol = String.valueOf(c);
            String key = firstChar + "," + lastChar;
            
            if (!transitions.containsKey(key) || !transitions.get(key).containsKey(symbol)) {
                return false;
            }

            String[] nextState = transitions.get(key).get(symbol);
            firstChar = nextState[0];
            lastChar = nextState[1];
        }

        return Objects.equals(firstChar, lastChar);
    }
}

class NFA {
    private final Set<Integer> currentStates;
    private final Map<Integer, Map<String, Set<Integer>>> transitions;
    private final int windowSize;

    public NFA() {
        this.windowSize = 5;
        this.currentStates = new HashSet<>();
        this.transitions = new HashMap<>();
        
        for (int i = 0; i <= windowSize; i++) {
            transitions.put(i, new HashMap<>());
        }
        
        currentStates.add(0);
        
        for (int state = 0; state < windowSize; state++) {
            transitions.get(state).computeIfAbsent("0", k -> new HashSet<>()).add(state + 1);
            transitions.get(state).computeIfAbsent("1", k -> new HashSet<>()).add(windowSize);
        }
        
        transitions.get(windowSize - 1).get("0").clear();
        transitions.get(windowSize - 1).get("0").add(windowSize - 1);
        
        transitions.get(windowSize).computeIfAbsent("0", k -> new HashSet<>()).add(windowSize);
        transitions.get(windowSize).computeIfAbsent("1", k -> new HashSet<>()).add(windowSize);
    }

    public boolean accepts(String inputChain) {
        Set<Integer> states = new HashSet<>(currentStates);
        
        for (char c : inputChain.toCharArray()) {
            Set<Integer> nextStates = new HashSet<>();
            String symbol = String.valueOf(c);
            
            for (int state : states) {
                if (transitions.get(state).containsKey(symbol)) {
                    nextStates.addAll(transitions.get(state).get(symbol));
                }
            }
            
            states = nextStates;
            if (states.isEmpty()) break;
        }
        
        return states.contains(windowSize);
    }
}

public class Main {
    public static void main(String[] args) {
        DFA dfa = new DFA();
        NFA nfa = new NFA();

        String[] testDfa = {"0", "00", "01", "101", "110", "111"};
        System.out.println("DFA results:");
        for (String chain : testDfa) {
            boolean result = dfa.accepts(chain);
            System.out.printf(" %s -> %s%n", chain, result ? "ACCEPT" : "REJECT");
        }

        String[] testNfa = {"00000", "00001", "00100", "00010", "11111", "00000"};
        System.out.println("\nNFA results:");
        for (String chain : testNfa) {
            boolean result = nfa.accepts(chain);
            System.out.printf(" %s -> %s%n", chain, result ? "ACCEPT" : "REJECT");
        }
    }
}
