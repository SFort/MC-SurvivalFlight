package tf.ssf.sfort.survivalflight.script.instance;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import tf.ssf.sfort.survivalflight.script.Help;
import tf.ssf.sfort.survivalflight.script.PredicateProvider;

import java.util.Set;
import java.util.function.Predicate;

public class ServerPlayerEntityScript<T extends ServerPlayerEntity> implements PredicateProvider<T>, Help {
    private final PlayerEntityScript<T> PLAYER_ENTITY = new PlayerEntityScript<>();
    public Predicate<T> getLP(String in){
        return null;
    }
    public Predicate<T> getLP(String in, String val){
        return switch (in){
            case "respawn_distance" ->{
                double arg = Double.parseDouble(val);
                yield player -> {
                    BlockPos pos = player.getSpawnPointPosition();
                    ServerWorld world = player.getServerWorld();
                    RegistryKey<World> dim = player.getSpawnPointDimension();
                    if (pos == null || world == null) return false;
                    return dim.equals(world.getRegistryKey()) && pos.isWithinDistance(player.getPos(), arg);
                };
            }
            case "advancement" -> {
                Identifier arg = new Identifier(val);
                yield player -> {
                    MinecraftServer server = player.getServer();
                    if (server == null) return false;
                    return player.getAdvancementTracker().getProgress(server.getAdvancementLoader().get(arg)).isDone();
                };
            }
            default -> null;
        };
    }
    @Override
    public Predicate<T> getPredicate(String in, String val, Set<Class<?>> dejavu){
        {
            Predicate<T> out = getLP(in, val);
            if (out != null) return out;
        }
        if (dejavu.add(PLAYER_ENTITY.getClass())){
            Predicate<T> out = PLAYER_ENTITY.getPredicate(in, val, dejavu);
            if (out !=null) return out;
        }
        return null;
    }


    @Override
    public Predicate<T> getPredicate(String in, Set<Class<?>> dejavu){
        {
            Predicate<T> out = getLP(in);
            if (out != null) return out;
        }
        if (dejavu.add(PLAYER_ENTITY.getClass())){
            Predicate<T> out = PLAYER_ENTITY.getPredicate(in, dejavu);
            if (out !=null) return out;
        }
        return null;
    }
    @Override
    public String getHelp(){
        return
                String.format("\t%-20s%-70s%s%n","advancement","- Require advancement unlocked","AdvancementID")+
                String.format("\t%-20s%-70s%s%n","respawn_distance","- Require player to be nearby their respawn (usually a bed)","double")
        ;
    }
    @Override
    public String getAllHelp(Set<Class<?>> dejavu){
        return (dejavu.add(PLAYER_ENTITY.getClass())?PLAYER_ENTITY.getAllHelp(dejavu):"")+getHelp();
    }
}
