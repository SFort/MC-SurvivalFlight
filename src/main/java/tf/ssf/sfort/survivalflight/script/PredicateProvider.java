package tf.ssf.sfort.survivalflight.script;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public interface PredicateProvider<T> {
    //TODO dejavu wont actually work the way it's currently implemented it has to be added pre recursive call getPredicate
    Predicate<T> getPredicate(String key, Set<String> dejavu);
    Predicate<T> getPredicate(String key, String arg, Set<String> dejavu);
    default Predicate<T> getPredicate(String key){
        return getPredicate(key, new HashSet<>());
    }
    default Predicate<T> getPredicate(String key, String arg){
        return getPredicate(key, arg, new HashSet<>());
    }
}
