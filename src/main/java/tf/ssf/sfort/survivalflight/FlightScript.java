package tf.ssf.sfort.survivalflight;

import net.minecraft.server.network.ServerPlayerEntity;
import tf.ssf.sfort.script.Default;
import tf.ssf.sfort.script.Help;
import tf.ssf.sfort.script.PredicateProvider;

import java.util.*;
import java.util.function.Predicate;

public class FlightScript implements PredicateProvider<ServerPlayerEntity>, Help {
    static Set<String> exclude = new HashSet<>();
    public static final Map<String, String> help = new HashMap<>();


    public Predicate<ServerPlayerEntity> getPredicate(String in, String val, Set<Class<?>> dejavu){
        if(exclude.contains(in)) return null;
        return Default.SERVER_PLAYER_ENTITY.getPredicate(in, val, dejavu);
    }

    public Predicate<ServerPlayerEntity> getPredicate(String in, Set<Class<?>> dejavu){
        if(exclude.contains(in)) return null;
        return switch (in) {
            case "beacon" -> {
                Config.hasBeaconCondition = true;
                yield player -> (((SPEA) player).bf$hasBeacon());
            }
            case "false" -> player -> false;
            case "true" -> player -> true;
            default -> Default.SERVER_PLAYER_ENTITY.getPredicate(in, dejavu);
        };
    }
    public Predicate<ServerPlayerEntity> getEmbed(String in, String script, Set<Class<?>> dejavu){
        return Default.SERVER_PLAYER_ENTITY.getEmbed(in, script, dejavu);
    }
    public Predicate<ServerPlayerEntity> getEmbed(String in, String val, String script, Set<Class<?>> dejavu){
        return Default.SERVER_PLAYER_ENTITY.getEmbed(in, val, script, dejavu);
    }
    public Map<String, String> getHelp(){
        return help;
    }
    public List<Help> getImported(){
        return new LinkedList<>(Collections.singleton(Default.SERVER_PLAYER_ENTITY));
    }
    static {
        exclude.add("is_creative");
        exclude.add("climbing");
        exclude.add("height");
        exclude.add("fall_flying");
        exclude.add("swimming");
        exclude.add("width");
        exclude.add("has_vehicle");
        exclude.add("on_ground");

        help.put("beacon","Require beacon");
        help.put("false","");
        help.put("true","");
    }
}
