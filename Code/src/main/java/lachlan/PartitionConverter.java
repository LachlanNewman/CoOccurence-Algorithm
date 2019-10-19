package lachlan;

import java.util.HashMap;
import java.util.Map;

public class PartitionConverter {

    private static final Map<Character,Integer> alphabet2integer =
            new HashMap<Character, Integer>() {{
                put('a', 1);
                put('b', 2);
                put('c', 3);
                put('d', 4);
                put('e', 5);
                put('f', 6);
                put('g', 7);
                put('h', 8);
                put('i', 9);
                put('j', 10);
                put('k', 11);
                put('l', 12);
                put('m', 13);
                put('n', 14);
                put('o', 15);
                put('p', 16);
                put('q', 17);
                put('r', 18);
                put('s', 19);
                put('t', 20);
                put('u', 21);
                put('v', 22);
                put('w', 23);
                put('x', 24);
                put('y', 25);
                put('z', 26);
            }};

    public static Map<Character,Integer> getCharacterIntegerHashMap(){
        return alphabet2integer;
    }
            
}
