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
        int n = inputChain.length();
        if (n < 2) {
            return false;
        }

        String firstTwo = inputChain.substring(0, 2);
        String lastTwo = inputChain.substring(n - 2);

        return firstTwo.equals(lastTwo);
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
        Deque<Character> window = new ArrayDeque<>();
        
        for (char c : inputChain.toCharArray()) {
            if (window.size() == windowSize) {
                window.pollFirst();
            }
            window.addLast(c);
        }
        
        return window.contains('1');
    }
}

public class Main {
    public static void main(String[] args) {
        DFA dfa = new DFA();
        NFA nfa = new NFA();

        String[] testDfa = {
            "00", "01", "10", "11", "0000", "00100", // true
            "0", "1", "0001", "0110", "110000", // false
            "0", "00", "01", "101", "110", "111"
        };
        System.out.println("DFA results:");
        for (String chain : testDfa) {
            boolean result = dfa.accepts(chain);
            System.out.printf(" %s -> %s%n", chain, result ? "ACCEPT" : "REJECT");
        }

        String[] testNfa = {
            "1", "01", "00001", "101000", "11111", "0001000", "1010101", "000001", // true
            "0", "00", "00000", "100000", "1100000" // false
        };
        System.out.println("\nNFA results:");
        for (String chain : testNfa) {
            boolean result = nfa.accepts(chain);
            System.out.printf(" %s -> %s%n", chain, result ? "ACCEPT" : "REJECT");
        }
    }
}
