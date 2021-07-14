package tf.ssf.sfort.survivalflight;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.function.Predicate;

public interface ScriptParser<T> {
    Predicate<T> getPredicate(String key);
    Predicate<T> getPredicate(String key, String arg);
    default Predicate<T> ScriptParse(String in){
        Deque<Integer> deque = new ArrayDeque<>();
        List<Predicate<T>> list = new ArrayList<>();
        for (int i = 0; i<in.length(); i++) {
            char ch = in.charAt(i);
            switch (ch) {
                case '{', '[', '(' -> {
                    deque.addFirst(i);
                }
                case '}', ']', ')' -> {
                    int indx = deque.removeFirst();
                    String str = in.substring(indx, i + 1);
                    i = indx;
                    in = in.replace(str, "\u0007" + list.size());
                    list.add(getPredicates(str, list));
                }
            }
        }
        boolean negate = in.charAt(0) == '!';
        if (negate)
            in = in.replaceFirst("!", "");
        if (in.charAt(0) == '\u0007') {
            in = in.replaceFirst("\u0007", "");
            return negate ? list.get(Integer.parseInt(in)).negate() : list.get(Integer.parseInt(in));
        }else{
            int colon = in.indexOf(':');
            Predicate<T> predicate = colon == -1 ? getPredicate(in): getPredicate(in.substring(0, colon), in.substring(colon + 1));
            return negate ? predicate.negate() : predicate;
        }
    }
    private Predicate<T> getPredicates(String in, List<Predicate<T>> list){
        Predicate<T> out = null;
        char firstchar = in.charAt(0);
        boolean negate, negate_return = firstchar == '!';
        if(negate_return)
            in = in.replaceFirst("!", "");
        in = in.substring(1, in.length()-1);
        for (String predicateString : in.split(";")) {
            negate = predicateString.charAt(0) == '!';
            if (negate)
                predicateString = predicateString.replaceFirst("!", "");
            if (predicateString.charAt(0) == '\u0007') {
                predicateString = predicateString.replaceFirst("\u0007", "");
                out = BracketMerge(firstchar, out, list.get(Integer.parseInt(predicateString)));
            }else{
                int colon = predicateString.indexOf(':');
                out = BracketMerge(firstchar, out, colon == -1 ? getPredicate(predicateString): getPredicate(predicateString.substring(0, colon), predicateString.substring(colon + 1)));
            }
            if (negate)
                out = out.negate();
        }
        return negate_return && out != null? out.negate() : out;
    }
    private Predicate<T> BracketMerge(char in, Predicate<T> p1, Predicate<T> p2){
        if(p1 == null) return p2;
        return switch (in){
            case '[',']'->p1.and(p2);
            case '{','}'->(player)->p1.test(player) ^ p2.test(player);
            case '(',')'->p1.or(p2);
            default -> throw new IllegalStateException("Unexpected value while flipping brackets: " + in);
        };
    }
}
