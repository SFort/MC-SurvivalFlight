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
        switch (in) {
            case "sf_beacon": case "beacon" : {
                Config.hasBeaconCondition = true;
                return player -> (((SPEA) player).bf$hasBeaconTicks() && ((SPEA)player).bf$hasBeaconPing());
            }
            case "sf_beacon_delayed": case "beacon_delayed" : {
                Config.hasBeaconCondition = true;
                return player -> (((SPEA) player).bf$hasBeaconTicks());
            }
            case "sf_conduit": case "conduit" : {
                Config.hasConduitCondition = true;
                return player -> (((SPEA) player).bf$hasConduitTicks() && ((SPEA)player).bf$hasConduitPing());
            }
            case "sf_conduit_delayed": case "conduit_delayed" : {
                Config.hasConduitCondition = true;
                return player -> (((SPEA) player).bf$hasConduitTicks());
            }
            case "false": return player -> false;
            case "true": return player -> true;
            default: return null;
        }
    }

    public Map<String, String> getHelp(){
        return help;
    }
    public FlightScript() {
        help.put("sf_beacon beacon","Require active beacon");
        help.put("sf_beacon_delayed beacon_delayed","Require active beacon, but it's still valid if outside of range as long as effects apply");
        help.put("sf_conduit conduit","Require active conduit");
        help.put("sf_conduit_delayed conduit_delayed","Require active conduit, but it's still valid if outside of range as long as effects would apply");
        help.put("false","");
        help.put("true","");
    }
}
