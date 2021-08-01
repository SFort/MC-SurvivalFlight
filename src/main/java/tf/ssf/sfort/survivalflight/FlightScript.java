package tf.ssf.sfort.survivalflight;

import net.minecraft.server.network.ServerPlayerEntity;
import tf.ssf.sfort.survivalflight.script.Default;
import tf.ssf.sfort.survivalflight.script.PredicateProvider;

import java.util.Set;
import java.util.function.Predicate;

public class FlightScript implements PredicateProvider<ServerPlayerEntity> {

    public Predicate<ServerPlayerEntity> getPredicate(String in, String val, Set<String> dejavu){
        return Default.SERVER_PLAYER_ENTITY.getPredicate(in, val, dejavu);
    }

    public Predicate<ServerPlayerEntity> getPredicate(String in, Set<String> dejavu){
        return switch (in) {
            case "beacon" -> {
                Config.hasBeaconCondition = true;
                yield (player) -> (((SPEA) player).bf$hasBeacon());
            }
            default -> Default.SERVER_PLAYER_ENTITY.getPredicate(in, dejavu);
        };
    }
    public static String getHelp(){
        return Default.SERVER_PLAYER_ENTITY.getAllHelp()+
                String.format("\t%-20s%s%n","beacon","- Require beacon");
    }
}