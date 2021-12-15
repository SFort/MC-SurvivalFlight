package tf.ssf.sfort.survivalflight;

import net.minecraft.server.network.ServerPlayerEntity;
import tf.ssf.sfort.script.Help;
import tf.ssf.sfort.script.PredicateProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class FlightScript implements PredicateProvider<ServerPlayerEntity>, Help {
    public static final FlightScript INSTANCE = new FlightScript();
    public final Map<String, String> help = new HashMap<>();

    public Predicate<ServerPlayerEntity> getPredicate(String in, Set<String> dejavu){
        return switch (in) {
            case "beacon" -> {
                Config.hasBeaconCondition = true;
                yield player -> (((SPEA) player).bf$hasBeaconTicks() && ((SPEA)player).bf$hasBeaconPing());
            }
            case "beacon_delayed" -> {
                Config.hasBeaconCondition = true;
                yield player -> (((SPEA) player).bf$hasBeaconTicks());
            }
            case "false" -> player -> false;
            case "true" -> player -> true;
            default -> null;
        };
    }

    public Map<String, String> getHelp(){
        return help;
    }
    public FlightScript() {
        help.put("beacon","Require beacon");
        help.put("beacon_delayed","Require beacon, but it's still valid if outside of range as long as effects apply");
        help.put("false","");
        help.put("true","");
    }
}
