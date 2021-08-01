package tf.ssf.sfort.survivalflight.script;

import java.util.HashSet;
import java.util.Set;

public interface Help {
    String getHelp();
    String getAllHelp(Set<String> dejavu);
    default String getAllHelp(){
        return getAllHelp(new HashSet<>());
    }
}
